package ui.client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ServerException;
import model.ClientResponse;
import model.JoinRequest;
import model.LoginState;
import server.ServerFacade;
import ui.BoardRenderer;
import websocket.ServerMessageHandler;
import websocket.WebsocketFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

public class GameClient implements ClientInterface {
    private final String authToken;
    private LoginState loginStatus;
    private ChessGame game;
    private WebsocketFacade ws;


    public GameClient(String authToken, WebsocketFacade ws) throws ServerException {
        this.ws = ws;
        this.authToken = authToken;
        loginStatus = LoginState.LOGGED_IN;
    }
    @Override
    public ClientResponse eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            String output = switch (cmd) {
                case "move" -> makeMove(params);
                case "highlight" -> highlightMoves(params);
                case "redraw" -> redrawChessBoard();
                case "resign" -> resign();
                case "leave" -> leave();
                default -> help();
            };
            return new ClientResponse(loginStatus, output, authToken, null);
        } catch (ServerException ex) {
            return new ClientResponse(loginStatus, ex.getMessage(), authToken, null);
        }
    }

    public String makeMove(String... params) throws ServerException {
        if (params.length >= 2) {
            String startPosition = params[0];
            String endPosition = params[1];
            String promote = (params.length >= 3) ? params[2] : "";
            ChessPiece.PieceType promotion = ChessPiece.PieceType.valueOf(promote);
            ChessMove move = new ChessMove(turnCoordinatesToPosition(startPosition),
                    turnCoordinatesToPosition(endPosition), promotion);
            ws.makeMove(move, authToken);
            return "Made Move";
        }
        throw new ServerException(400, "Expected: <gameID> <playerColor>");
    }

    private ChessPosition turnCoordinatesToPosition(String position) throws ServerException {
        char[] coordinates = position.toCharArray();
        int col = switch (coordinates[0]) {
            case 'a' -> 1;
            case 'b' -> 2;
            case 'c' -> 3;
            case 'd' -> 4;
            case 'e' -> 5;
            case 'f' -> 6;
            case 'g' -> 7;
            case 'h' -> 8;
            default -> throw new ServerException(400, "Unexpected value: " + coordinates[0]);
        };
        return new ChessPosition(coordinates[1], col);
    }

    public String highlightMoves(String... params) throws ServerException {
        game = ws.getGame();
        Collection<ChessMove> validMoves = game.validMoves(turnCoordinatesToPosition(params[0]));
        BoardRenderer boardRenderer = new BoardRenderer();
        boardRenderer.setValidMoves(validMoves);
        boardRenderer.render(game.getBoard());
        return  "Available Moves";
    }

    public String resign() throws ServerException {
        ws.resign(authToken);
        game = ws.getGame();
        return "You resigned";
    }

    public String redrawChessBoard() throws ServerException {
        game = ws.getGame();
        BoardRenderer boardRenderer = new BoardRenderer();
        boardRenderer.render(game.getBoard());
        return "";
    }

    public String leave() throws ServerException {
        return "";
    }

    @Override
    public String help() {
        return null;
    }
}

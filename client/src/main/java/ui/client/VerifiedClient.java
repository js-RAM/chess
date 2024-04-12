package ui.client;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ServerException;
import model.*;
import server.ServerFacade;
import ui.BoardRenderer;
import webSocketMessages.userCommands.UserGameCommand;
import websocket.ServerMessageHandler;
import websocket.WebsocketFacade;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import static ui.EscapeSequences.*;

public class VerifiedClient implements ClientInterface {
    private final ServerFacade serverFacade;
    private final String authToken;
    private LoginState loginStatus;
    private final Dictionary<Integer, Integer> gameIDs;

    private WebsocketFacade ws;
    private final String url;
    private final ServerMessageHandler handler;

    public VerifiedClient(String url, String authToken, ServerMessageHandler handler) {
        serverFacade = new ServerFacade(url);
        this.authToken = authToken;
        this.url = url;
        this.handler = handler;
        gameIDs = new Hashtable<>();
        loginStatus = LoginState.LOGGED_IN;
    }
    @Override
    public ClientResponse eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            String output = switch (cmd) {
                case "logout" -> signOut();
                case "games" -> getGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observe(params);
                case "quit" -> "quit";
                default -> help();
            };
            return new ClientResponse(loginStatus, output, authToken, ws);
        } catch (ServerException ex) {
            return new ClientResponse(loginStatus, ex.getMessage(), authToken, null);
        }
    }

    public String signOut() throws ServerException {
        serverFacade.logout(authToken);
        loginStatus = LoginState.LOGGED_OUT;

        return "You signed out!";
    }

    public String getGames() throws ServerException {
        GamesList gamesList = serverFacade.listGames(authToken);
        return printGames(gamesList);
    }

    public String createGame(String... params) throws ServerException {
        if (params.length >= 1) {
            String gameName = params[0];

            serverFacade.addGame(authToken, gameName);
            return "Added " + gameName;
        }
        throw new ServerException(400, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ServerException {
        if (params.length >= 1) {
            String gameNumber = params[0];
            int gameID = gameIDs.get(Integer.valueOf(gameNumber));
            String playerColor = "";
            if (params.length >= 2) playerColor = params[1].toUpperCase();
            serverFacade.joinGame(authToken,new JoinRequest(gameID,playerColor));
            loginStatus = LoginState.IN_GAME;
            ws = new WebsocketFacade(url, handler, new PlayerInfo(gameID, ChessGame.TeamColor.valueOf(playerColor)));
            return "Joined Game";
        }
        throw new ServerException(400, "Expected: <gameID> <playerColor>");
    }

    private String observe(String... params) throws ServerException {
        if (params.length >= 1) {
            return joinGame(params[0]);
        }
        throw new ServerException(400, "Expected: <gameID>");
    }

    public String help() {
        String output = "";
        output += "  " + SET_TEXT_COLOR_BLUE + "create <GAME_NAME>"
                + SET_TEXT_COLOR_MAGENTA + " - to create a new game\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "games"
                + SET_TEXT_COLOR_MAGENTA + " - to view existing games\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "join <GAME_ID> [WHITE|BLACK|<empty>]"
                + SET_TEXT_COLOR_MAGENTA + " - an existing game as white or black\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "observe <GAME_ID>"
                + SET_TEXT_COLOR_MAGENTA + " - a game\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "logout"
                + SET_TEXT_COLOR_MAGENTA + " - when you are done\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "quit"
                + SET_TEXT_COLOR_MAGENTA + " - to leave\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "help"
                + SET_TEXT_COLOR_MAGENTA + " - to list commands\n";
        return output;
    }
    public String printGames(GamesList gamesList) {
        GameData[] games = gamesList.games();
        StringBuilder output = new StringBuilder();
        int listNumber = 1;
        for (GameData game : games) {
            gameIDs.put(listNumber, game.gameID());
            output.append(listNumber).append(": ").append(game.gameName());
            output.append(" " + SET_TEXT_COLOR_WHITE).append(game.whiteUsername() != null ? game.whiteUsername() : "[available]");
            output.append(" " + SET_TEXT_COLOR_LIGHT_GREY).append(game.blackUsername() != null ? game.blackUsername() : "[available]");
            output.append(SET_TEXT_COLOR_BLUE + "\n");
            listNumber++;
        }
        return output.toString();
    }
}

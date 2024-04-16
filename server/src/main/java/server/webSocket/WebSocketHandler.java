package server.webSocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;

import dataAccess.SQLDataAccess;
import exception.ServerException;
import model.GameData;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final HashMap<Integer, ConnectionManager> gameConnections = new HashMap<>();
    private final SQLDataAccess dataAccess = new SQLDataAccess();

    public WebSocketHandler() throws ServerException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String command) throws IOException, ServerException {
        UserGameCommand userGameCommand = new Gson().fromJson(command, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, new Gson().fromJson(command, JoinPlayerCommand.class));
            case JOIN_OBSERVER -> joinObserver(session, new Gson().fromJson(command, JoinObserverCommand.class));
            case LEAVE -> leave(new Gson().fromJson(command, LeaveCommand.class));
            case MAKE_MOVE -> makeMove(new Gson().fromJson(command, MakeMoveCommand.class));
            case RESIGN -> resign(new Gson().fromJson(command, ResignCommand.class));
        }
    }

    public void test(Session session) {
        addGameConnection(1);
        gameConnections.get(1).add("5", session, ChessGame.TeamColor.WHITE);
    }

    public void joinPlayer(Session session, JoinPlayerCommand joinPlayerCommand) throws ServerException, IOException {
        addGameConnection(joinPlayerCommand.getGameID());
        GameData gameData = dataAccess.getGame(joinPlayerCommand.getGameID());
        gameConnections.get(joinPlayerCommand.getGameID()).add(joinPlayerCommand.getAuthString(), session, joinPlayerCommand.getPlayerColor());
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Game does not exist");
            gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), errorMessage);
            gameConnections.get(joinPlayerCommand.getGameID()).remove(joinPlayerCommand.getAuthString());
            return;
        }
        if (dataAccess.getAuth(joinPlayerCommand.getAuthString()) == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid Auth");
            gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), errorMessage);
            gameConnections.get(joinPlayerCommand.getGameID()).remove(joinPlayerCommand.getAuthString());
            return;
        }
        if (joinPlayerCommand.getPlayerColor() == null) {
            ErrorMessage errorMessage = new ErrorMessage("Player requires a color");
            gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), errorMessage);
            gameConnections.get(joinPlayerCommand.getGameID()).remove(joinPlayerCommand.getAuthString());
            return;
        }
        String playerName = dataAccess.getAuth(joinPlayerCommand.getAuthString()).username();
        if (joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.WHITE && !Objects.equals(gameData.whiteUsername(), playerName)) {
            ErrorMessage errorMessage = new ErrorMessage("A player is already playing as white");
            gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), errorMessage);
            gameConnections.get(joinPlayerCommand.getGameID()).remove(joinPlayerCommand.getAuthString());
            return;
        }
        if (joinPlayerCommand.getPlayerColor() == ChessGame.TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), playerName)) {
            ErrorMessage errorMessage = new ErrorMessage("A player is already playing as black");
            gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), errorMessage);
            gameConnections.get(joinPlayerCommand.getGameID()).remove(joinPlayerCommand.getAuthString());
            return;
        }
        ChessGame game = gameData.game();
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        gameConnections.get(joinPlayerCommand.getGameID()).sendMessage(joinPlayerCommand.getAuthString(), loadGameMessage);
        Notification notification = new Notification(playerName + " has joined as " + joinPlayerCommand.getPlayerColor());
        gameConnections.get(joinPlayerCommand.getGameID()).broadcast(joinPlayerCommand.getAuthString(), notification);
    }

    public void joinObserver(Session session, JoinObserverCommand joinObserverCommand) throws ServerException, IOException {
        addGameConnection(joinObserverCommand.getGameID());
        gameConnections.get(joinObserverCommand.getGameID()).add(joinObserverCommand.getAuthString(), session, null);
        GameData gameData = dataAccess.getGame(joinObserverCommand.getGameID());
        if (gameData == null) {
            ErrorMessage errorMessage = new ErrorMessage("Game does not exist");
            gameConnections.get(joinObserverCommand.getGameID()).sendMessage(joinObserverCommand.getAuthString(), errorMessage);
            gameConnections.get(joinObserverCommand.getGameID()).remove(joinObserverCommand.getAuthString());
            return;
        }
        if (dataAccess.getAuth(joinObserverCommand.getAuthString()) == null) {
            ErrorMessage errorMessage = new ErrorMessage("Invalid Auth");
            gameConnections.get(joinObserverCommand.getGameID()).sendMessage(joinObserverCommand.getAuthString(), errorMessage);
            gameConnections.get(joinObserverCommand.getGameID()).remove(joinObserverCommand.getAuthString());
            return;
        }
        ChessGame game = gameData.game();
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        gameConnections.get(joinObserverCommand.getGameID()).sendMessage(joinObserverCommand.getAuthString(), loadGameMessage);

        String playerName = dataAccess.getAuth(joinObserverCommand.getAuthString()).username();
        Notification notification = new Notification(playerName + " has joined as an observer");
        gameConnections.get(joinObserverCommand.getGameID()).broadcast(joinObserverCommand.getAuthString(), notification);
    }

    private void addGameConnection(int gameID) {
        if (gameConnections.get(gameID) == null) {
            gameConnections.put(gameID, new ConnectionManager());
        }
    }

    public void leave(LeaveCommand leaveCommand) throws ServerException, IOException {
        GameData gameData = dataAccess.getGame(leaveCommand.getGameID());
        if (gameConnections.get(leaveCommand.getGameID()).connections.get(leaveCommand.getAuthString()).color == ChessGame.TeamColor.WHITE) {
            gameData = new GameData(gameData.gameID(), "", gameData.blackUsername(), gameData.gameName(), gameData.game());
            dataAccess.updateGame(gameData.gameID(), gameData);
        }
        if (gameConnections.get(leaveCommand.getGameID()).connections.get(leaveCommand.getAuthString()).color == ChessGame.TeamColor.BLACK) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), "", gameData.gameName(), gameData.game());
            dataAccess.updateGame(gameData.gameID(), gameData);
        }
        gameConnections.get(leaveCommand.getGameID()).remove(leaveCommand.getAuthString());
        String playerName = dataAccess.getAuth(leaveCommand.getAuthString()).username();
        Notification notification = new Notification(playerName + " has left");
        gameConnections.get(leaveCommand.getGameID()).broadcast(leaveCommand.getAuthString(), notification);
    }

    public void makeMove(MakeMoveCommand makeMoveCommand) throws ServerException, IOException {
        GameData gameData = dataAccess.getGame(makeMoveCommand.getGameID());
        ChessGame game = gameData.game();
        ChessGame.TeamColor color = gameConnections.get(gameData.gameID()).connections.get(makeMoveCommand.getAuthString()).color;
        if (game.isOver) {
            ErrorMessage notification = new ErrorMessage("The game is over. No more moves can be made");
            gameConnections.get(makeMoveCommand.getGameID()).sendMessage(makeMoveCommand.getAuthString(), notification);
            return;
        }
        if (color != game.getTeamTurn()) {
            ErrorMessage notification = new ErrorMessage("It is not your turn. You cannot make a move");
            gameConnections.get(makeMoveCommand.getGameID()).sendMessage(makeMoveCommand.getAuthString(), notification);
            return;
        }
        try {
            game.makeMove(makeMoveCommand.getMove());
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            dataAccess.updateGame(gameData.gameID(), gameData);
            LoadGameMessage loadGameMessage = new LoadGameMessage(game);
            gameConnections.get(makeMoveCommand.getGameID()).broadcast("", loadGameMessage);

            String playerName = dataAccess.getAuth(makeMoveCommand.getAuthString()).username();
            Notification notification = new Notification(playerName + " has moved");
            gameConnections.get(makeMoveCommand.getGameID()).broadcast(makeMoveCommand.getAuthString(), notification);
            checkGameStatus(gameData);
        } catch (InvalidMoveException e) {
            ErrorMessage notification = new ErrorMessage("Please send a valid move");
            gameConnections.get(makeMoveCommand.getGameID()).sendMessage(makeMoveCommand.getAuthString(), notification);
        }
    }

    private void checkGameStatus(GameData gameData) throws IOException, ServerException {
        ChessGame game = gameData.game();
        Notification notification = null;
        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            notification = new Notification("The game is in stalemate. No one wins");
            game.isOver = true;
        } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            notification = new Notification("White is in checkmate, " + gameData.blackUsername() + " has won!");
            game.isOver = true;
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            notification = new Notification("Black is in checkmate, " + gameData.whiteUsername() + " has won!");
            game.isOver = true;
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            notification = new Notification("White is in check");
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            notification = new Notification("Black is in check");
        }
        if (notification != null){
            gameData=new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            dataAccess.updateGame(gameData.gameID(), gameData);
            gameConnections.get(gameData.gameID()).broadcast("", notification);
        }
    }

    public void resign(ResignCommand resignCommand) throws ServerException, IOException {
        if (gameConnections.get(resignCommand.getGameID()).connections.get(resignCommand.getAuthString()).color == null) {
            ErrorMessage notification = new ErrorMessage("An observer cannot resign");
            gameConnections.get(resignCommand.getGameID()).sendMessage(resignCommand.getAuthString(), notification);
            return;
        }
        GameData gameData = dataAccess.getGame(resignCommand.getGameID());
        ChessGame game = gameData.game();
        if (game.isOver) {
            ErrorMessage notification = new ErrorMessage("Cannot resign. Game is already over");
            gameConnections.get(resignCommand.getGameID()).sendMessage(resignCommand.getAuthString(), notification);
            return;
        }
        game.isOver = true;

        dataAccess.updateGame(gameData.gameID(), new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
        String playerName = dataAccess.getAuth(resignCommand.getAuthString()).username();
        Notification notification = new Notification(playerName + " has resigned");
        gameConnections.get(resignCommand.getGameID()).broadcast("", notification);
    }
}

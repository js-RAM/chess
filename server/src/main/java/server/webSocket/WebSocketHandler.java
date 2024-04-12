package server.webSocket;

import com.google.gson.Gson;

import dataAccess.SQLDataAccess;
import exception.ServerException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;

@WebSocket
public class WebSocketHandler {
    private final HashMap<Integer, ConnectionManager> gameConnections = new HashMap<>();


    @OnWebSocketMessage
    public void onMessage(Session session, String command) throws IOException, ServerException {
        UserGameCommand userGameCommand = new Gson().fromJson(command, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(session, userGameCommand);
            case JOIN_OBSERVER -> joinObserver(session, userGameCommand);
            case LEAVE -> leave();
            case MAKE_MOVE -> makeMove();
            case RESIGN -> resign();
        }
    }

    public void joinPlayer(Session session, UserGameCommand userGameCommand) throws ServerException, IOException {
        JoinPlayerCommand joinPlayerCommand = (JoinPlayerCommand) userGameCommand;
        addGameConnection(joinPlayerCommand.getGameID());
        gameConnections.get(joinPlayerCommand.getGameID()).add(joinPlayerCommand.getAuthString(), session);
        String playerName = new SQLDataAccess().getAuth(joinPlayerCommand.getAuthString()).username();
        Notification notification = new Notification(playerName + " has joined as " + joinPlayerCommand.getColor());
        gameConnections.get(joinPlayerCommand.getGameID()).broadcast(joinPlayerCommand.getAuthString(), notification);
    }

    public void joinObserver(Session session, UserGameCommand userGameCommand) throws ServerException, IOException {
        JoinObserverCommand joinObserverCommand = (JoinObserverCommand) userGameCommand;
        addGameConnection(joinObserverCommand.getGameID());
        gameConnections.get(joinObserverCommand.getGameID()).add(joinObserverCommand.getAuthString(), session);
        String playerName = new SQLDataAccess().getAuth(joinObserverCommand.getAuthString()).username();
        Notification notification = new Notification(playerName + " has joined as an observer");
        gameConnections.get(joinObserverCommand.getGameID()).broadcast(joinObserverCommand.getAuthString(), notification);
    }

    private void addGameConnection(int gameID) {
        if (gameConnections.get(gameID) == null) {
            gameConnections.put(gameID, new ConnectionManager());
        }
    }

    public void leave() {

    }

    public void makeMove() {

    }

    public void resign() {

    }

}

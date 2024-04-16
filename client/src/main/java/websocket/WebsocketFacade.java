package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ServerException;
import model.PlayerInfo;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;

    ChessGame game;

    PlayerInfo playerInfo;

    public WebsocketFacade(String url, ServerMessageHandler serverMessageHandler, PlayerInfo playerInfo) throws ServerException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;
            this.playerInfo = playerInfo;
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        serverMessage = new Gson().fromJson(message, LoadGameMessage.class);
                        game = ((LoadGameMessage) serverMessage).getGame();
                        serverMessageHandler.printGameBoard((LoadGameMessage) serverMessage, playerInfo.color() == ChessGame.TeamColor.BLACK);
                    } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        serverMessage = new Gson().fromJson(message, Notification.class);
                        serverMessageHandler.notify(serverMessage);
                    } else {
                        serverMessage = new Gson().fromJson(message, ErrorMessage.class);
                        serverMessageHandler.notify(serverMessage);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    public ChessGame getGame() {
        return game;
    }
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // Unnecessary
    }

    public void joinAsPlayer(ChessGame.TeamColor color, String authToken) throws ServerException {
        try {
            JoinPlayerCommand cmd = new JoinPlayerCommand(playerInfo.gameID(), color, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    public void joinAsObserver(String authToken) throws ServerException {
        try {
            JoinObserverCommand cmd = new JoinObserverCommand(playerInfo.gameID(), authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    public void makeMove(ChessMove move, String authToken) throws ServerException {
        try {
            MakeMoveCommand cmd = new MakeMoveCommand(playerInfo.gameID(), move, authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    public void resign(String authToken) throws ServerException {
        try {
            ResignCommand cmd = new ResignCommand(playerInfo.gameID(), authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    public void leave(String authToken) throws ServerException {
        try {
            LeaveCommand cmd = new LeaveCommand(playerInfo.gameID(), authToken);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
            this.session.close();
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }
}

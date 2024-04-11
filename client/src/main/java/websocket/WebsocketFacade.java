package websocket;

import com.google.gson.Gson;
import exception.ServerException;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {
    Session session;
    ServerMessageHandler serverMessageHandler;


    public WebsocketFacade(String url, ServerMessageHandler serverMessageHandler) throws ServerException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageHandler = serverMessageHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageHandler.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // Unnecessary
    }

    public void sendCommand(String authToken, UserGameCommand.CommandType commandType) throws ServerException {
        try {
            UserGameCommand cmd = new UserGameCommand(authToken, commandType);
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ServerException(500, ex.getMessage());
        }
    }


}

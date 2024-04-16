package websocket;
import model.PlayerInfo;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;
public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);

    void printGameBoard(LoadGameMessage loadGameMessage, boolean isBlack);
}

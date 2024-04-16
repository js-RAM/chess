package ui;

import model.ClientResponse;
import model.LoginState;
import model.PlayerInfo;
import ui.client.Client;
import ui.client.ClientInterface;
import ui.client.GameClient;
import ui.client.VerifiedClient;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.ServerMessage;
import websocket.ServerMessageHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {

    String url;
    private ClientInterface client;
    private LoginState status;
    public Repl(String url) {
        client = new Client(url);
        status = LoginState.LOGGED_OUT;
        this.url = url;
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE+"Welcome to Chess. Sign in to start.\nUse 'help' to see a list of commands.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                ClientResponse response = client.eval(line);
                result = response.message();
                if (result == null) result = "";
                if (status != response.loginStatus()) {
                    status = response.loginStatus();
                    if (status == LoginState.LOGGED_IN) client = new VerifiedClient(url, response.authToken(), this);
                    else if (status == LoginState.IN_GAME) client = new GameClient(response.authToken(), response.ws());
                    else client = new Client(url);
                }
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        System.out.println(SET_TEXT_COLOR_RED + serverMessage.getMessage());
        printPrompt();
    }

    public void printGameBoard(LoadGameMessage loadGameMessage, boolean isBlack) {
        System.out.println();
        new BoardRenderer().render(loadGameMessage.getGame().getBoard(), isBlack);
        printPrompt();
    }
    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[" + status + "] " + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}

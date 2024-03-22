package ui;

import model.ClientResponse;
import model.LoginState;
import ui.client.Client;
import ui.client.ClientInterface;
import ui.client.VerifiedClient;

import java.util.Scanner;

import static tools.EscapeSequences.*;

public class Repl {

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
                if (status != response.loginStatus()) {
                    status = response.loginStatus();
                    if (status == LoginState.LOGGED_IN) client = new VerifiedClient(url, response.authToken());
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

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[" + status + "] " + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}

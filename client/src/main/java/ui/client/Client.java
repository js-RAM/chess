package ui.client;

import exception.ServerException;
import java.util.Arrays;

import model.*;
import server.ServerFacade;

import static ui.EscapeSequences.*;

public class Client implements ClientInterface {

    private ServerFacade serverFacade;
    private String authToken;
    private LoginState loginStatus;

    public Client(String url) {
        serverFacade = new ServerFacade(url);
        loginStatus = LoginState.LOGGED_OUT;
        authToken = "";
    }

    @Override
    public ClientResponse eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            String output = switch (cmd) {
                case "register" -> register(params);
                case "login" -> signIn(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
            return new ClientResponse(loginStatus, output, authToken);
        } catch (ServerException ex) {
            return new ClientResponse(loginStatus, ex.getMessage(), authToken);
        }
    }

    public String register(String... params) throws ServerException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            authToken = serverFacade.register(new UserData(username, password, email)).authToken();
            loginStatus = LoginState.LOGGED_IN;
            return String.format("You signed in as %s.", username);
        }
        throw new ServerException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String signIn(String... params) throws ServerException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            authToken = serverFacade.login(loginRequest).authToken();
            loginStatus = LoginState.LOGGED_IN;
            return String.format("You signed in as %s.", username);
        }
        throw new ServerException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String clear() throws ServerException {
        serverFacade.clear();
        return "Chess Server Cleared!";
    }

    public String help() {
        String output = "";
        output += "  " + SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>"
                + SET_TEXT_COLOR_MAGENTA + " - to create an account\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>"
                + SET_TEXT_COLOR_MAGENTA + " - to play with an existing account\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "quit"
                + SET_TEXT_COLOR_MAGENTA + " - to leave\n";
        output += "  " + SET_TEXT_COLOR_BLUE + "help"
                + SET_TEXT_COLOR_MAGENTA + " - to list commands\n";
        return output;
    }

}

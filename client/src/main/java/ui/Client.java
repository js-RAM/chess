package ui;

import exception.ServerException;
import java.util.Arrays;

import model.LoginRequest;
import server.ServerFacade;

public class Client {

    private ServerFacade serverFacade;
    public Client(String url) {
        serverFacade = new ServerFacade(url);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "signin" -> signIn(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ServerException ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws ServerException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            serverFacade.login(new LoginRequest(username, password));

            return String.format("You signed in as %s.", username);
        }
        throw new ServerException(400, "Expected: <username> <password>");
    }

    public String help() {
        return "";
    }

}

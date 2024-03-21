package ui;

import exception.ServerException;
import java.util.Arrays;

import model.AuthData;
import model.LoginRequest;
import model.UserData;
import server.ServerFacade;

public class Client {

    private ServerFacade serverFacade;
    private AuthData authData;

    public Client(String url) {
        serverFacade = new ServerFacade(url);
        authData = null;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> signIn(params);
                case "logout" -> signOut();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ServerException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ServerException {
        if (params.length >= 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            authData = serverFacade.register(new UserData(username, password, email));

            return String.format("You signed in as %s.", username);
        }
        throw new ServerException(400, "Expected: <username> <password> <email>");
    }

    public String signIn(String... params) throws ServerException {
        if (params.length >= 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest loginRequest = new LoginRequest(username, password);
            authData = serverFacade.login(loginRequest);

            return String.format("You signed in as %s.", username);
        }
        throw new ServerException(400, "Expected: <username> <password>");
    }

    public String signOut() throws ServerException {
        serverFacade.logout(authData.authToken());

        return "You signed out!";
    }

    public String help() {
        return "";
    }

}

package server;

import com.google.gson.Gson;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import services.GameMgmtService;
import services.LoginService;
import services.RegistrationService;
import exception.ServerException;
import spark.*;

public class Server {
    RegistrationService registrationService;
    LoginService loginService;
    GameMgmtService gameMgmtService;
    public Server() {
        registrationService = new RegistrationService();
        loginService = new LoginService();
        gameMgmtService = new GameMgmtService();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegistrationService registrationService = new RegistrationService();
        LoginService loginService = new LoginService();
        GameMgmtService gameMgmtService = new GameMgmtService();

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);

        Spark.post("/session", this::login);

//        Spark.delete("/session/:authToken", (req, res) -> {
//            String authToken = req.params(":authToken");
//            System.out.println(authToken);
//            return "";
//        });
//
//        Spark.get("/game/:authToken", (req, res) -> {
//            String authToken = req.params(":authToken");
//            return "";
//        });
//
//        Spark.post("/game/:authToken/:gameName", (req, res) -> {
//            String authToken = req.params(":authToken");
//            String gameName = req.params(":gameName");
//            return "";
//        });
//
//        Spark.put("/game/:authToken/:ClientColor/:gameID", (req, res) -> {
//            String authToken = req.params(":authToken");
//            String clientColor = req.params(":ClientColor");
//            String gameID = req.params(":gameID");
//            return "";
//        });
//
        Spark.delete("/db", this::clear);

        Spark.exception(ServerException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ServerException ex, Request req, Response res) {
        res.status(ex.getStatusCode());
        res.body("{ \"message\": \"" + ex.getMessage()+"\" }");
    }

    private Object register(Request req, Response res) throws ServerException {
        UserData userData = new Gson().fromJson(req.body(), UserData.class);
        AuthData authData = registrationService.register(userData);
        res.status(200);
        return new Gson().toJson(authData);
    }

    private Object login(Request req, Response res) throws ServerException {
        LoginRequest loginRequest = new Gson().fromJson(req.body(), LoginRequest.class);
        AuthData authData = loginService.login(loginRequest);
        res.status(200);
        return new Gson().toJson(authData);
    }

    private Object clear(Request req, Response res) throws ServerException {
        gameMgmtService.clear();
        res.status(200);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

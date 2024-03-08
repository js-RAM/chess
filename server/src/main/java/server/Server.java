package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import model.*;
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
        DataAccessInterface dataAccess;
        try {
            dataAccess = new SQLDataAccess();
        } catch (ServerException e) {
            dataAccess = new MemoryDataAccess();
            System.out.println("Can't Connect to db, switching to using memory.\nError: " + e.getMessage());
        }
        registrationService = new RegistrationService(dataAccess);
        loginService = new LoginService(dataAccess);
        gameMgmtService = new GameMgmtService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::addGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.exception(ServerException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ServerException ex, Request req, Response res) {
        System.out.println(ex.getMessage());
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

    private Object logout(Request req, Response res) throws ServerException {
        String authToken = req.headers("authorization");
        loginService.logout(authToken);
        res.status(200);
        return "";
    }
    private Object listGames(Request req, Response res) throws ServerException {
        String authToken = req.headers("authorization");
        GamesList games = gameMgmtService.getGames(authToken);
        res.status(200);
        return new Gson().toJson(games);
    }

    private Object addGame(Request req, Response res) throws ServerException {
        String authToken = req.headers("authorization");
        JsonObject jsonReq = new Gson().fromJson(req.body(), JsonObject.class);
        String gameName;
        if (jsonReq.get("gameName") == null)  gameName = "";
        else gameName = jsonReq.get("gameName").getAsString();
        int gameID = gameMgmtService.createGame(gameName, authToken);
        res.status(200);
        return "{ \"gameID\": \"" + gameID + "\" }";
    }

    private Object joinGame(Request req, Response res) throws ServerException {
        String authToken = req.headers("authorization");
        JsonObject jsonReq = new Gson().fromJson(req.body(), JsonObject.class);
        String playerColor;
        if (jsonReq.get("playerColor") == null) playerColor = null;
        else playerColor = jsonReq.get("playerColor").getAsString();
        gameMgmtService.joinGame(authToken, playerColor, jsonReq.get("gameID").getAsInt());
        res.status(200);
        return "";
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

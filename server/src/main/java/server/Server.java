package server;

import services.GameMgmtService;
import services.LoginService;
import services.RegistrationService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        RegistrationService registrationService = new RegistrationService();
        LoginService loginService = new LoginService();
        GameMgmtService gameMgmtService = new GameMgmtService();

        // Register your endpoints and handle exceptions here.
        Spark.post("/register/:username/:password/:email", (req, res) -> {

            String username = req.params(":username");
            String password = req.params(":password");
            String email = req.params(":email");
            System.out.println(username + password + email);
            return registrationService.register(username, password, email);
        });

        Spark.post("/session/:username/:password", (req, res) -> {
            String username = req.params(":username");
            String password = req.params(":password");
            System.out.println(username + password);
            return registrationService.register(username, password,"");
        });

        Spark.delete("/session/:authToken", (req, res) -> {
            String authToken = req.params(":authToken");
            System.out.println(authToken);
            return registrationService.register(authToken, "", "");
        });

        Spark.get("/game/:authToken", (req, res) -> {
            String authToken = req.params(":authToken");
            return "";
        });

        Spark.post("/game/:authToken/:gameName", (req, res) -> {
            String authToken = req.params(":authToken");
            String gameName = req.params(":gameName");
            return "";
        });

        Spark.put("/game/:authToken/:ClientColor/:gameID", (req, res) -> {
            String authToken = req.params(":authToken");
            String clientColor = req.params(":ClientColor");
            String gameID = req.params(":gameID");
            return "";
        });

        Spark.delete("/db", (req, res) -> {
            return 200;
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

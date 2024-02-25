package server;

import services.RegistrationService;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.post("/user/:username/:password/:email", (req, res) -> {
            RegistrationService registrationService = new RegistrationService();
            String username = req.params(":username");
            String password = req.params(":password");
            String email = req.params(":email");
            return registrationService.register(username, password, email);
        });
        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

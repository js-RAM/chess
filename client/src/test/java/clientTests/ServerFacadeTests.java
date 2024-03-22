package clientTests;

import exception.ServerException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void setUp() throws ServerException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() throws ServerException {
        serverFacade.clear();
        server.stop();
    }

    @Test
    public void registerTest() throws ServerException {
        AuthData response = serverFacade.register(new UserData("username", "password", "email.e"));
        Assertions.assertEquals("username", response.username());
    }

    @Test
    public void failedRegisterTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.register(new UserData("username", "password", "email")));
    }

    @Test
    public void loginTest() throws ServerException {
        serverFacade.register(new UserData("username", "password", "email.e"));
        AuthData response = serverFacade.login(new LoginRequest("username", "password"));
        Assertions.assertEquals("username", response.username());
    }

    @Test
    public void failedLoginTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.login(new LoginRequest("username", "password")));
    }

    @Test
    public void logoutTest() throws ServerException {
        serverFacade.register(new UserData("username", "password", "email.e"));
        AuthData response = serverFacade.login(new LoginRequest("username", "password"));
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(response.authToken()));
    }

    @Test
    public void failedLogoutTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.logout("not_a_token"));
    }
    @Test
    public void listGamesTest() throws ServerException {
        AuthData response = serverFacade.register(new UserData("username", "password", "email.e"));
        Assertions.assertInstanceOf(GamesList.class, serverFacade.listGames(response.authToken()));
    }

    @Test
    public void failedListGamesTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.listGames("not_a_token"));
    }

    @Test
    public void addGameTest() throws ServerException {
        AuthData response = serverFacade.register(new UserData("username", "password", "email.e"));
        Assertions.assertDoesNotThrow(() -> serverFacade.addGame(response.authToken(), "name"));
        GamesList gamesList = serverFacade.listGames(response.authToken());
        Assertions.assertEquals("name", gamesList.games()[0].gameName());
    }

    @Test
    public void failedAddGameTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.addGame("not_a_token", "name"));
    }

    @Test
    public void joinGameTest() throws ServerException {
        AuthData response = serverFacade.register(new UserData("username", "password", "email.e"));
        Assertions.assertDoesNotThrow(() -> serverFacade.addGame(response.authToken(), "name"));
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(response.authToken(), new JoinRequest(1, "WHITE")));
        GamesList gamesList = serverFacade.listGames(response.authToken());
        Assertions.assertEquals("username", gamesList.games()[0].whiteUsername());
    }

    @Test
    public void failedJoinGameTest() {
        Assertions.assertThrows(ServerException.class, () -> serverFacade.joinGame("not_a_token", new JoinRequest(0,"")));
    }

}

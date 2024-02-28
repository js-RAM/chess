package serviceTests;

import chess.ChessGame;
import dataAccess.MemoryDataAccess;
import exception.ServerException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import services.GameMgmtService;
import services.LoginService;
import services.RegistrationService;

class GameMgmtServiceTest {
    static RegistrationService registrationService;
    static GameMgmtService gameMgmtService;
    static String username;
    static String password;
    static String authToken;
    static String game1;
    static String game2;
    @BeforeEach
    void init() throws ServerException {
        registrationService = new RegistrationService();
        gameMgmtService= new GameMgmtService();
        username = "ExistingUser";
        password = "secure";
        game1 = "Game1";
        game2 = "";
        authToken = registrationService.register(new UserData(username,password,"email.email")).authToken();
    }

    @AfterEach
    public void clearDB() {
        gameMgmtService.clear();
    }

    @Test
    @DisplayName("Create Games Test")
    public void testCreateGames() {
        Assertions.assertDoesNotThrow(() -> gameMgmtService.createGame(game1, authToken));
        Assertions.assertDoesNotThrow(() -> gameMgmtService.createGame(game2, authToken));
    }

    @Test
    @DisplayName("Get Games Test")
    public void testGetGames() throws ServerException {
        int gameID1 = gameMgmtService.createGame(game1, authToken);
        int gameID2 = gameMgmtService.createGame(game2, authToken);
        GameData[] expectedData = new GameData[2];
        expectedData[0] = new GameData(gameID1, null, null, game1, new ChessGame());
        expectedData[1] = new GameData(gameID2, null, null, game2, new ChessGame());
        Assertions.assertEquals(expectedData[0], gameMgmtService.getGames(authToken).games()[0]);
        Assertions.assertEquals(expectedData[1], gameMgmtService.getGames(authToken).games()[1]);
    }

    @Test
    @DisplayName("Join Game Test")
    public void testJoinGame() throws ServerException {
        int gameID1 = gameMgmtService.createGame(game1, authToken);
        int gameID2 = gameMgmtService.createGame(game2, authToken);
        gameMgmtService.joinGame(authToken,"BLACK", gameID1);
        gameMgmtService.joinGame(authToken,"WHITE", gameID2);
        GameData[] expectedData = new GameData[2];
        expectedData[0] = new GameData(gameID1, null, username, game1, new ChessGame());
        expectedData[1] = new GameData(gameID2, username, null, game2, new ChessGame());
        Assertions.assertEquals(expectedData[0], gameMgmtService.getGames(authToken).games()[0]);
        Assertions.assertEquals(expectedData[1], gameMgmtService.getGames(authToken).games()[1]);
    }

    @Test
    @DisplayName("Invalid Create Test")
    public void testInvalidCreate() {
        Assertions.assertThrows(ServerException.class, () -> gameMgmtService.createGame("", "fakeID"));
    }

    @Test
    @DisplayName("Invalid Join Game Test")
    public void testInvalidJoin() {
        Assertions.assertThrows(ServerException.class, () -> gameMgmtService.joinGame("fakeID", "WHITE", 1));
    }

    @Test
    @DisplayName("Invalid Get Games Test")
    public void testInvalidGetGames() {
        Assertions.assertThrows(ServerException.class, () -> gameMgmtService.getGames("fakeID"));
    }
}
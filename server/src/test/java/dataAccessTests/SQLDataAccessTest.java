package dataAccessTests;

import chess.*;
import dataAccess.SQLDataAccess;
import exception.ServerException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import tools.AuthTokenGenerator;

import static org.junit.jupiter.api.Assertions.*;

class SQLDataAccessTest {
    static SQLDataAccess dataAccess;
    static String username;
    static String password;
    static String email;

    @BeforeAll
    public static void init() throws ServerException {
        dataAccess = new SQLDataAccess();
        username = "username";
        password = "password";
        email = "email";
    }

    @BeforeEach
    public void clearDb() throws ServerException {
        dataAccess.clearAuth();
        dataAccess.clearUsers();
        dataAccess.clearGames();
    }

    @Test
    @DisplayName("Create User Test")
    public void testCreateUser() {
        assertDoesNotThrow(() -> dataAccess.addUser(new UserData(username,password,email)));
    }

    @Test
    @DisplayName("Find User")
    public void testFindUser() throws ServerException {
        dataAccess.addUser(new UserData(username,password,email));
        assertEquals(new UserData(username,password,email), dataAccess.getUser(username, password));
    }

    @Test
    @DisplayName("Failed Find User")
    public void testFailedFindUser() throws ServerException {
        assertNull(dataAccess.getUser(username));
    }

    @Test
    @DisplayName("Incorrect Password Test")
    public void testIncorrectPassword() throws ServerException {
        dataAccess.addUser(new UserData(username,password,email));
        assertNull(dataAccess.getUser(username, "incorrect"));
    }

    @Test
    @DisplayName("Add Auth Test")
    public void testAddAuth() {
        assertDoesNotThrow(() -> dataAccess.addAuth(new AuthData(new AuthTokenGenerator().generateAuthToken(dataAccess), username)));
    }

    @Test
    @DisplayName("Get Auth Test")
    public void testGetAuth() throws ServerException {
        String authToken = new AuthTokenGenerator().generateAuthToken(dataAccess);
        dataAccess.addAuth(new AuthData(authToken, username));
        assertEquals(new AuthData(authToken, username), dataAccess.getAuth(authToken));
    }

    @Test
    @DisplayName("Incorrect Auth Test")
    public void testIncorrectAuth() throws ServerException {
        String authToken = new AuthTokenGenerator().generateAuthToken(dataAccess);
        dataAccess.addAuth(new AuthData(authToken, username));
        assertNull(dataAccess.getAuth(new AuthTokenGenerator().generateAuthToken(dataAccess)));
    }

    @Test
    @DisplayName("Delete Auth Test")
    public void testDeleteAuth() throws ServerException {
        String authToken = new AuthTokenGenerator().generateAuthToken(dataAccess);
        dataAccess.addAuth(new AuthData(authToken, username));
        assertEquals(new AuthData(authToken, username), dataAccess.getAuth(authToken));
        dataAccess.deleteAuth(new AuthData(authToken, username));
        assertNull(dataAccess.getAuth(authToken));
    }

    @Test
    @DisplayName("Get Games Blank Test")
    public void testGetGamesBlank() throws ServerException {
        assertEquals(0, dataAccess.getGames().length);
    }

    @Test
    @DisplayName("Add Games Test")
    public void testAddGames() throws ServerException {
        assertEquals(1, dataAccess.addGame(new GameData(1,"","","",new ChessGame())));
    }

    @Test
    @DisplayName("Get Valid Game Test")
    public void testGetValidGame() throws ServerException {
        GameData gameData = new GameData(1, "", "", "game", new ChessGame());
        dataAccess.addGame(gameData);
        assertEquals(gameData, dataAccess.getGame(gameData.gameID()));
    }

    @Test
    @DisplayName("Get Invalid Game Test")
    public void testGetInvalidGame() throws ServerException {
        GameData gameData = new GameData(1, "", "", "game", new ChessGame());
        dataAccess.addGame(gameData);
        assertNull(dataAccess.getGame(2));
    }

    @Test
    @DisplayName("Get Multiple Games")
    public void testGetMultipleGames() throws ServerException {
        GameData gameData1 = new GameData(1, "", "", "game1", new ChessGame());
        GameData gameData2 = new GameData(2, "", "", "game2", new ChessGame());
        GameData gameData3 = new GameData(3, "", "", "game3", new ChessGame());
        dataAccess.addGame(gameData1);
        dataAccess.addGame(gameData2);
        dataAccess.addGame(gameData3);
        assertEquals(gameData1, dataAccess.getGames()[0]);
        assertEquals(gameData2, dataAccess.getGames()[1]);
        assertEquals(gameData3, dataAccess.getGames()[2]);
    }

    @Test
    @DisplayName("Update Game Test")
    public void testUpdateGameTest() throws ServerException {
        int gameID = dataAccess.addGame(new GameData(1, "", "", "game1", new ChessGame()));
        assertDoesNotThrow(() -> dataAccess.updateGame(gameID,
                new GameData(1, "something", "", "game1", new ChessGame())));
    }

    @Test
    @DisplayName("Check Update Test")
    public void testCheckUpdate() throws ServerException {
        int gameID = dataAccess.addGame(new GameData(1, "", "", "game1", new ChessGame()));
        dataAccess.updateGame(gameID, new GameData(1, "something", "", "game1", new ChessGame()));
        assertEquals(new GameData(1, "something", "", "game1", new ChessGame()),
                dataAccess.getGame(gameID));
    }

    @Test
    @DisplayName("Make Move Test")
    public void testMakeMove() throws ServerException, InvalidMoveException {
        int gameID = dataAccess.addGame(new GameData(1, "", "", "game1", new ChessGame()));
        ChessGame chessGame = new ChessGame();
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        chessGame.setBoard(chessBoard);
        chessGame.makeMove(new ChessMove(new ChessPosition(2,1), new ChessPosition(3,1), null));
        dataAccess.updateGame(gameID, new GameData(1, "something", "", "game1", chessGame));
        assertEquals(chessGame, dataAccess.getGame(gameID).game());
    }

    @Test
    @DisplayName("Clear Auth Test")
    public void testClearAuth() throws ServerException {
        String authToken = new AuthTokenGenerator().generateAuthToken(dataAccess);
        dataAccess.addAuth(new AuthData(authToken, username));
        assertEquals(new AuthData(authToken, username), dataAccess.getAuth(authToken));
        dataAccess.clearAuth();
        assertNull(dataAccess.getAuth(authToken));
    }

    @Test
    @DisplayName("Clear User Test")
    public void testClearUser() throws ServerException {
        dataAccess.addUser(new UserData(username,password,email));
        assertEquals(new UserData(username,password,email), dataAccess.getUser(username, password));
        dataAccess.clearUsers();
        assertNull(dataAccess.getUser(username));
    }

    @Test
    @DisplayName("Clear Game Test")
    public void testClearGame() throws ServerException {
        GameData gameData = new GameData(1, "", "", "game", new ChessGame());
        dataAccess.addGame(gameData);
        assertEquals(gameData, dataAccess.getGame(gameData.gameID()));
        dataAccess.clearGames();
        assertNull(dataAccess.getGame(1));
    }
}

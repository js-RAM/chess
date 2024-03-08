package dataAccessTests;

import dataAccess.SQLDataAccess;
import exception.ServerException;
import model.AuthData;
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
}

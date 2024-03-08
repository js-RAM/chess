package serviceTests;

import dataAccess.MemoryDataAccess;
import exception.ServerException;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.*;
import services.GameMgmtService;
import services.LoginService;
import services.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;

class LoginServiceTest {
    static RegistrationService registrationService;
    static LoginService loginService;
    static String username;
    static String password;
    static String authToken;
    @BeforeAll
    static void init() throws ServerException {
        registrationService = new RegistrationService(new MemoryDataAccess());
        loginService = new LoginService(new MemoryDataAccess());
        username = "ExistingUser";
        password = "secure";
        authToken = registrationService.register(new UserData(username,password,"email.email")).authToken();
    }

    @AfterAll
    public static void clearDB() throws ServerException {
        new GameMgmtService(new MemoryDataAccess()).clear();
    }

    @Test
    @Order(2)
    @DisplayName("Valid Login Test")
    void testValidLogin() throws ServerException {
        Assertions.assertEquals(username, loginService.login(new LoginRequest(username, password)).username());
    }

    @Test
    @Order(1)
    @DisplayName("Valid Logout Test")
    void testValidLogout() {
        Assertions.assertDoesNotThrow(() -> loginService.logout(authToken));
    }

    @Test
    @Order(3)
    @DisplayName("Test Invalid Logins and Logouts")
    void testInvalid() throws ServerException {
        Assertions.assertThrows(ServerException.class, () -> loginService.login(new LoginRequest("notaUser", "insecure")));
        Assertions.assertThrows(ServerException.class, () -> loginService.logout("fakeToken"));
    }

}
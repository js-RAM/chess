package serviceTests;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import exception.ServerException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import services.GameMgmtService;
import services.RegistrationService;

class RegistrationServiceTest {
    static RegistrationService registrationService;
    @BeforeAll
    public static void init() {
        registrationService = new RegistrationService();
    }

    @AfterEach
    public void clearDB() {
        new GameMgmtService().clear();
    }

    @Test
    @DisplayName("Valid Register")
    public void testRegisterValid() throws ServerException {
        AuthData result = registrationService.register(new UserData("userName", "password", "email.e"));
        Assertions.assertEquals(result.username(), "userName");
    }

    @Test
    @DisplayName("Register: bad request")
    public void testBadRequest() throws ServerException {
        Assertions.assertThrows(ServerException.class, () -> registrationService.register(new UserData(null,
                "password", "email.e")));
        Assertions.assertThrows(ServerException.class, () -> registrationService.register(new UserData("userName",
                null, "email.e")));
        Assertions.assertThrows(ServerException.class, () -> registrationService.register(new UserData("userName",
                "password", null)));
        Assertions.assertThrows(ServerException.class, () -> registrationService.register(new UserData("userName",
                "password", "email")));
    }

    @Test
    @DisplayName("Register: already taken")
    public void testTakenRequest() throws ServerException {
        registrationService.register(new UserData("userName",
                "password", "email.e"));
        Assertions.assertThrows(ServerException.class, () -> registrationService.register(new UserData("userName",
                "password", "email.e")));
    }

    @Test
    @DisplayName("Test Emails")
    public void testIsEmail() {
        Assertions.assertFalse(registrationService.isValidEmail("notValid"));
        Assertions.assertFalse(registrationService.isValidEmail("notValid@"));
        Assertions.assertFalse(registrationService.isValidEmail("notValid@gmail"));
        Assertions.assertFalse(registrationService.isValidEmail("notValid@gmail."));
        Assertions.assertTrue(registrationService.isValidEmail("gmail.com"));
        Assertions.assertTrue(registrationService.isValidEmail("valid@gmail.com"));
    }
}
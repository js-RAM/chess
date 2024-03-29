package serviceTests;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import exception.ServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.AuthTokenGenerator;

class AuthTokenGeneratorTest {
    @Test
    @DisplayName("Uniqueness Test")
    public void testUniqueness() throws ServerException {
        AuthTokenGenerator authTokenGenerator = new AuthTokenGenerator();
        DataAccessInterface dataAccess = new MemoryDataAccess();
        Assertions.assertNotEquals(authTokenGenerator.generateAuthToken(dataAccess), authTokenGenerator.generateAuthToken(dataAccess));
    }
}
package serviceTests;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.AuthTokenGenerator;

class AuthTokenGeneratorTest {
    @Test
    @DisplayName("Uniqueness Test")
    public void testUniqueness() {
        AuthTokenGenerator authTokenGenerator = new AuthTokenGenerator();
        DataAccessInterface dataAccess = new MemoryDataAccess();
        Assertions.assertNotEquals(authTokenGenerator.generateAuthToken(dataAccess), authTokenGenerator.generateAuthToken(dataAccess));
    }
}
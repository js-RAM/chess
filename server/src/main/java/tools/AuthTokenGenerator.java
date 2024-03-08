package tools;

import dataAccess.DataAccessInterface;
import exception.ServerException;

import java.util.Random;

public class AuthTokenGenerator {
    public String generateAuthToken(DataAccessInterface dataAccess) throws ServerException {
        String authToken;
        do {
            authToken = Integer.toString(new Random().nextInt(999999999));
        } while (dataAccess.getAuth(authToken) != null);
        return authToken;
    }
}

package services;

import dataAccess.DataAccessInterface;
import model.AuthData;

import java.util.Random;

public class AuthTokenGenerator {
    public String generateAuthToken(DataAccessInterface dataAccess) {
        String authToken;
        do {
            authToken = Integer.toString(new Random().nextInt(999999999));
        } while (dataAccess.getAuth(authToken) != null);
        return authToken;
    }
}

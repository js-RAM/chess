package services;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import exception.ServerException;
import model.AuthData;
import model.UserData;

/**
 * Manages the registration service
 */
public class RegistrationService {
    DataAccessInterface dataAccess;
    public RegistrationService() {
        dataAccess = new MemoryDataAccess();
    }

    public AuthData register(UserData userData) throws ServerException {
        if (!isValidEmail(userData.email()) || userData.username() == null || userData.password() == null) {
            throw new ServerException(400, "Error: bad request");
        }
        if (dataAccess.getUser(userData.username()) != null) {
            throw new ServerException(403, "Error: already taken");
        }

        dataAccess.addUser(userData);
        String authToken = new AuthTokenGenerator().generateAuthToken(dataAccess);
        AuthData authData = new AuthData(authToken, userData.username());
        dataAccess.addAuth(authData);
        return authData;
    }

    public boolean isValidEmail(String email) {
        return email.matches("(.+)\\.(.+)");
    }
}

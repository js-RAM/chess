package services;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
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

    public String register(String username, String password, String email) throws ServicesException {
        if (isValidEmail(email)) {
            throw new ServicesException("400");
        }
        if (dataAccess.getUser(username) == null) {
            throw new ServicesException("403");
        }

        UserData newUserData = new UserData(username, password, email);
        dataAccess.addUser(newUserData);

        String authToken = new AuthTokenGenerator().generateAuthToken();
        dataAccess.addAuth(new AuthData(authToken, username));
        return authToken;
    }

    public boolean isValidEmail(String email) {
        return email.matches("(.*)@(.*).(.*)");
    }
}

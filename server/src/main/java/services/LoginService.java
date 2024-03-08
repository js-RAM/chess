package services;

import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import exception.ServerException;
import model.AuthData;
import model.LoginRequest;
import tools.AuthTokenGenerator;

public class LoginService {
    DataAccessInterface dataAccess;
    public LoginService(DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData login(LoginRequest loginRequest) throws ServerException {
        if (dataAccess.getUser(loginRequest.username(), loginRequest.password()) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        AuthData authData = new AuthData(new AuthTokenGenerator().generateAuthToken(dataAccess), loginRequest.username());
        dataAccess.addAuth(authData);
        return authData;
    }

    public void logout(String authToken) throws ServerException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        dataAccess.deleteAuth(authData);
    }

}

package dataAccess;

import exception.ServerException;
import model.*;

public interface DataAccessInterface {
    UserData getUser(String username) throws ServerException;

    UserData getUser(String username, String password) throws ServerException;

    int addUser(UserData userData) throws ServerException;

    void addAuth(AuthData authData) throws ServerException;

    AuthData getAuth(String authToken) throws ServerException;

    void deleteAuth(AuthData authData) throws ServerException;

    GameData[] getGames();

    GameData getGame(int gameID);

    void addGame(GameData gameData);

    void updateGame(int gameID, GameData newGameData);

    void clearUsers();

    void clearAuth();

    void clearGames();
}

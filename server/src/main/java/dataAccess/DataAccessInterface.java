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

    GameData[] getGames() throws ServerException;

    GameData getGame(int gameID) throws ServerException;

    int addGame(GameData gameData) throws ServerException;

    void updateGame(int gameID, GameData newGameData) throws ServerException;

    void clearUsers() throws ServerException;

    void clearAuth() throws ServerException;

    void clearGames() throws ServerException;
}

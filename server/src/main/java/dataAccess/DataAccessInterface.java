package dataAccess;

import model.*;

public interface DataAccessInterface {
    UserData getUser(String username);

    UserData getUser(String username, String password);

    void addUser(UserData userData);

    void addAuth(AuthData authData);

    AuthData getAuth(String authToken);

    void deleteAuth(AuthData authData);

    GameData[] getGames();

    GameData getGame(int gameID);

    void addGame(GameData gameData);

    void updateGame(int gameID, GameData newGameData);

    void clearUsers();

    void clearAuth();

    void clearGames();
}

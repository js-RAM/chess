package dataAccess;

import model.*;

public interface DataAccessInterface {
    public UserData getUser(String username);

    public UserData getUser(String username, String password);

    public void addUser(UserData userData);

    public void addAuth(AuthData authData);

    public AuthData getAuth(String authToken);

    public void deleteAuth(AuthData authData);

    public GameData[] getGames(int gameID);

    public void addGame(GameData gameData);

    public void updateGame(int gameID, GameData newGameData);

    public void clearUsers();

    public void clearAuth();

    public void clearGames();
}

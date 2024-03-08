package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Objects;


/**
 * The Memory implementation of the data access interface
 */
public class MemoryDataAccess implements DataAccessInterface{
    static ArrayList<UserData> userDataArray;
    static ArrayList<AuthData> authDataArray;
    static ArrayList<GameData> gameDataArray;

    public MemoryDataAccess() {
        super();
        userDataArray = new ArrayList<>();
        authDataArray = new ArrayList<>();
        gameDataArray = new ArrayList<>();
    }
    @Override
    public UserData getUser(String username) {
        for (UserData userData : userDataArray) {
            if (Objects.equals(userData.username(), username)) return userData;
        }
        return null;
    }

    @Override
    public UserData getUser(String username, String password) {
        for (UserData userData : userDataArray) {
            if (Objects.equals(userData.username(), username) && Objects.equals(userData.password(), password))
                return userData;
        }
        return null;
    }

    @Override
    public int addUser(UserData userData) {
        userDataArray.add(userData);
        return 0;
    }

    @Override
    public void addAuth(AuthData authData) {
        authDataArray.add(authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        for (AuthData authData : authDataArray) {
            if (Objects.equals(authData.authToken(), authToken))
                return authData;
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authDataArray.remove(authData);
    }

    @Override
    public GameData[] getGames() {
        GameData[] gameData = new GameData[gameDataArray.size()];
        gameDataArray.toArray(gameData);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData gameData : gameDataArray) {
            if (Objects.equals(gameData.gameID(), gameID))
                return gameData;
        }
        return null;
    }

    @Override
    public int addGame(GameData gameData) {
        gameDataArray.add(gameData);
        return gameData.gameID();
    }

    @Override
    public void updateGame(int gameID, GameData newGameData) {
        for (int i = 0; i < gameDataArray.size(); i++) {
            if (Objects.equals(gameDataArray.get(i).gameID(), gameID)) {
                gameDataArray.set(i, newGameData);
            }
        }
    }

    @Override
    public void clearUsers() {
        userDataArray = new ArrayList<>();
    }

    @Override
    public void clearAuth() {
        authDataArray = new ArrayList<>();
    }

    @Override
    public void clearGames() {
        gameDataArray = new ArrayList<>();
    }
}

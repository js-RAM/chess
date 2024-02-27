package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * The Memory implementaion of the data access interface
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
    public void addUser(UserData userData) {
        userDataArray.add(userData);
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
    public GameData[] getGames(int gameID) {
        return new GameData[0];
    }

    @Override
    public void addGame(GameData gameData) {

    }

    @Override
    public void updateGame(int gameID, GameData newGameData) {

    }

    @Override
    public void clearUsers() {

    }

    @Override
    public void clearAuth() {

    }

    @Override
    public void clearGames() {

    }
}

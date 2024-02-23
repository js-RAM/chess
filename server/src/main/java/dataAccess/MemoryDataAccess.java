package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;


/**
 * The Memory implementaion of the data access interface
 */
public class MemoryDataAccess implements DataAccessInterface{
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public UserData getUser(String username, String password) {
        return null;
    }

    @Override
    public void addUser(UserData userData) {

    }

    @Override
    public void addAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) {

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

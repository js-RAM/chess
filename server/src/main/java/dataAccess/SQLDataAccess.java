package dataAccess;

import exception.ServerException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.SQLException;

public class SQLDataAccess implements DataAccessInterface {
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
    public GameData[] getGames() {
        return new GameData[0];
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
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

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS AuthData (
              `authToken` int NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws ServerException {
        DatabaseManager.createDatabase();
        try (var conn=DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement=conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ServerException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

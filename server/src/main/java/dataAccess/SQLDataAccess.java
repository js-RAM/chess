package dataAccess;

import com.google.gson.Gson;
import exception.ServerException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDataAccess implements DataAccessInterface {

    public SQLDataAccess() throws ServerException {
        try {
            configureDatabase();
        } catch (Exception e) {
            throw new ServerException(500, e.getMessage());
        }
    }
    @Override
    public UserData getUser(String username) throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username,password,email FROM UserData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString("username"), rs.getString("password"),
                                rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public UserData getUser(String username, String password) throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username,password,email FROM UserData WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (!new BCryptPasswordEncoder().matches(password, rs.getString("password"))) return null;
                        return new UserData(rs.getString("username"), password,
                                rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public int addUser(UserData userData) throws ServerException {
        var statement = "INSERT INTO UserData (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, userData.username());
                ps.setString(2, new BCryptPasswordEncoder().encode(userData.password()));
                ps.setString(3, userData.email());
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public void addAuth(AuthData authData) throws ServerException {
        var statement = "INSERT INTO AuthData (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (!Objects.equals(rs.getString("authToken"), authToken)) return null;
                        return new AuthData(rs.getString("authToken"), rs.getString("username"));
                    }
                    return null;
                }
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public void deleteAuth(AuthData authData) throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM AuthData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    @Override
    public GameData[] getGames() throws ServerException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, gameData FROM GameData";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        GameData[] gameData = new GameData[result.size()];
        result.toArray(gameData);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, gameData FROM GameData WHERE (id=?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public int addGame(GameData gameData) throws ServerException {
        System.out.println("Adding Game: " + gameData.gameName());
        var statement = "INSERT INTO GameData (gameData) VALUES (?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, new Gson().toJson(gameData));
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public void updateGame(int gameID, GameData newGameData) throws ServerException {
        System.out.println("Updating Game: " + newGameData.gameName());
        var statement = "UPDATE GameData SET gameData = ? WHERE id = ?;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, new Gson().toJson(newGameData));
                ps.setInt(2, gameID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public void clearUsers() throws ServerException {
        var statement = "TRUNCATE TABLE UserData;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public void clearAuth() throws ServerException {
        var statement = "TRUNCATE TABLE AuthData;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    @Override
    public void clearGames() throws ServerException {
        var statement = "TRUNCATE TABLE GameData;";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ServerException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        } catch (DataAccessException e) {
            throw new ServerException(500, e.getMessage());
        }
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var id = rs.getInt("id");
        var json = rs.getString("gameData");
        var gameData = new Gson().fromJson(json, GameData.class);
        return gameData.setGameID(id);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS AuthData (
              `authToken` int NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS UserData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` TEXT NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(`username`),
              INDEX(`password`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS GameData (
              `id` int NOT NULL AUTO_INCREMENT,
              `gameData` TEXT NOT NULL,
              PRIMARY KEY (`id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn=DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement=conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

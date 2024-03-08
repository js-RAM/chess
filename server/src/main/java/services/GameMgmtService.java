package services;

import chess.ChessGame;
import dataAccess.DataAccessInterface;
import dataAccess.MemoryDataAccess;
import dataAccess.SQLDataAccess;
import exception.ServerException;
import model.AuthData;
import model.GameData;
import model.GamesList;

import java.util.Objects;

/**
 * Manages the game management service
 */
public class GameMgmtService {
    DataAccessInterface dataAccess;
    public GameMgmtService (DataAccessInterface dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GamesList getGames(String authToken) throws ServerException {
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        return new GamesList(dataAccess.getGames());
    }

    public int createGame(String gameName, String authToken) throws ServerException {
        if (gameName == null || authToken == null) {
            throw new ServerException(400, "Error: bad request");
        }
        if (dataAccess.getAuth(authToken) == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        int gameID = dataAccess.getGames().length+1;
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        return dataAccess.addGame(newGame);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws ServerException {
        GameData game = dataAccess.getGame(gameID);
        if (game == null) {
            throw new ServerException(400, "Error: bad request");
        }
        AuthData authData = dataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ServerException(401, "Error: unauthorized");
        }
        GameData updatedGame;
        if (Objects.equals(playerColor, "WHITE")) {
            if (game.whiteUsername() != null) throw new ServerException(403, "Error: already taken");
            updatedGame = new GameData(gameID, authData.username(), game.blackUsername(), game.gameName(), game.game());
        } else if (Objects.equals(playerColor, "BLACK")) {
            if (game.blackUsername() != null) throw new ServerException(403, "Error: already taken");
            updatedGame = new GameData(gameID, game.whiteUsername(), authData.username(), game.gameName(), game.game());
        } else {
            updatedGame = game;
        }
        dataAccess.updateGame(gameID, updatedGame);
    }

    public void clear() throws ServerException {
        dataAccess.clearGames();
        dataAccess.clearUsers();
        dataAccess.clearAuth();
    }
}

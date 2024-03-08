package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    public GameData setGameID(int id) {
        return new GameData(id, whiteUsername, blackUsername, gameName, game);
    }
}

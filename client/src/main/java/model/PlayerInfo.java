package model;

import chess.ChessGame;

public record PlayerInfo(int gameID, ChessGame.TeamColor color) {
}

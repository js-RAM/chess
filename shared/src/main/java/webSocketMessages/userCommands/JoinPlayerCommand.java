package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    private final int gameID;
    private final ChessGame.TeamColor playerColor;
    public JoinPlayerCommand(int gameID, ChessGame.TeamColor color, String authToken) {
        super(authToken);
        this.commandType=CommandType.JOIN_PLAYER;
        this.gameID=gameID;
        this.playerColor=color;
    }

    public int getGameID() {
        return gameID;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}

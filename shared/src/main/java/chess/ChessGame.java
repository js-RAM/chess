package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    TeamColor turnColor;
    ChessBoard gameBoard;

    public Boolean isOver;

    public ChessGame() {
        turnColor = TeamColor.WHITE;
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        isOver = false;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (gameBoard.getPiece(startPosition) == null) return null;
        ChessBoard testBoard = gameBoard;
        gameBoard = new ChessBoard();
        gameBoard.copyBoard(testBoard);
        Collection<ChessMove> validMoves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> invalidMoves = new HashSet<>();
        for (ChessMove move : validMoves) {
            gameBoard.movePiece(move);
            if (isInCheck(testBoard.getPiece(move.getStartPosition()).getTeamColor())) invalidMoves.add(move);
            gameBoard.copyBoard(testBoard);
        }
        validMoves.removeAll(invalidMoves);
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameBoard.getPiece(move.getStartPosition()) == null) throw new InvalidMoveException();
        if (gameBoard.getPiece(move.getStartPosition()).getTeamColor() != turnColor) throw new InvalidMoveException();
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move)) throw new InvalidMoveException();
        gameBoard.movePiece(move);
        turnColor = turnColor == TeamColor.WHITE?TeamColor.BLACK:TeamColor.WHITE;
    }

    /**
     * A helper method to find the position of the king of the requested team. Returns null if no king is found
     *
     * @param teamColor The color of the king to be found
     * @return The position of the king on the given team
     */
    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 1; i < gameBoard.getBoardSize()+1; i++) {
            for (int j = 1; j < gameBoard.getBoardSize()+1; j++) {
                if (gameBoard.isPiece(j,i)){
                    if (gameBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor && gameBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                        return new ChessPosition(i, j);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition == null) return false;
        for (int i = 1; i < gameBoard.getBoardSize()+1; i++) {
            for (int j = 1; j < gameBoard.getBoardSize()+1; j++) {
                if (gameBoard.isPiece(j,i)) {
                    if (gameBoard.getPiece(new ChessPosition(i, j)).pieceMoves(gameBoard, new ChessPosition(i, j))
                            .contains(new ChessMove(new ChessPosition(i, j), kingPosition, null)))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 0; i < gameBoard.getBoardSize(); i++) {
            for (int j = 0; j < gameBoard.getBoardSize(); j++) {
                if (!gameBoard.isPiece(j,i)) continue;
                if (gameBoard.getPiece(new ChessPosition(i, j)).getTeamColor() != teamColor) continue;
                if (!validMoves(new ChessPosition(i,j)).isEmpty()) return false;
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) return false;
        if (isInCheckmate(teamColor)) return validMoves(getKingPosition(teamColor)).isEmpty();
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame=(ChessGame) o;
        return turnColor == chessGame.turnColor && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turnColor, gameBoard);
    }
}

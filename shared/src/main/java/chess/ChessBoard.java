package chess;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getColumn()-1][position.getRow()-1] =  piece;
    }

    public void movePiece(ChessMove move) {
      addPiece(move.getEndPosition(), getPiece(move.getStartPosition()));
      removePiece(move.getStartPosition());
    }

    private void removePiece(ChessPosition position) {
      board[position.getColumn()-1][position.getRow()-1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getColumn()-1][position.getRow()-1];
    }

    public boolean isPiece(int posX, int posY) {
        return board[posX-1][posY-1] != null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[8][8];
        ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
        int row = 0;
        for (int i = 0; i < 2; i++){
            board[0][row] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            board[1][row] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[2][row] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[5][row] = new ChessPiece(color, ChessPiece.PieceType.BISHOP);
            board[6][row] = new ChessPiece(color, ChessPiece.PieceType.KNIGHT);
            board[7][row] = new ChessPiece(color, ChessPiece.PieceType.ROOK);
            row = (color == ChessGame.TeamColor.WHITE)? row+1:row-1;
            for (int j = 0; j < 8; j++) {
                board[j][row] = new ChessPiece(color, ChessPiece.PieceType.PAWN);
            }
            color = ChessGame.TeamColor.BLACK;
            row = 7;
        }
        board[3][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[4][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        board[3][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[4][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == null && that.board[i][j] == null) continue;
                if (!board[i][j].equals(that.board[i][j])) return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}

package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private static final int BOARD_MIN = 1;
    private static final int BOARD_MAX = 8;
    private ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[BOARD_MAX][BOARD_MAX];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getColumn() - BOARD_MIN][position.getRow() - BOARD_MIN] =  piece;
    }

  /**
   * Implements a move on the board
   * @param move The ChessMove to implement
   */
    public void movePiece(ChessMove move) {
      if (move.getPromotionPiece() == null) addPiece(move.getEndPosition(), getPiece(move.getStartPosition()));
      else addPiece(move.getEndPosition(), new ChessPiece(getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece()));
      removePiece(move.getStartPosition());
    }

  /**
   * Removes a piece at the specified position
   * @param position The ChessPosition to remove a piece at
   */
    private void removePiece(ChessPosition position) {
      board[position.getColumn() - BOARD_MIN][position.getRow() - BOARD_MIN] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getColumn() - BOARD_MIN][position.getRow() - BOARD_MIN];
    }

  /**
   * Returns the size of the board
   *
   * @return the size of the board as type int
   */
    public int getBoardSize() {
        return BOARD_MAX;
    }

    /**
     * Returns whether a piece exists at the location on the chessboard.
     * Parameters must be between 1 and 8, otherwise it will always return false.
     *
     * @param col The column of the piece
     * @param row The row of the piece
     * @return if there is a piece there
     */
    public boolean isPiece(int col, int row) {
        if (col < BOARD_MIN || col > BOARD_MAX || row < BOARD_MIN || row > BOARD_MAX) return false;
        return board[col-1][row-1] != null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[BOARD_MAX][BOARD_MAX];
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

    /**
     * Allows the Chessboard to copy the board from another chessboard
     *
     * @param board The board from which you wish to copy
     */
    public void copyBoard(ChessBoard board) {
        for (int i = 0; i < BOARD_MAX; i++) {
            for (int j = 0; j < BOARD_MAX; j++) {
                this.board[i][j] = board.board[i][j];
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that=(ChessBoard) o;
        for (int i=0; i < BOARD_MAX; i++) {
            for (int j=0; j < BOARD_MAX; j++) {
                if (board[i][j] == null && that.board[i][j] == null) continue;
                if (board[i][j] == null || that.board[i][j] == null) return false;
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

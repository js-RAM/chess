package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor pieceColor;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int myCol = myPosition.getColumn();
        int myRow = myPosition.getRow();
        Collection<ChessMove> chessMoves = new HashSet<>();
        if (type == PieceType.BISHOP) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1,1));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1,1));
            chessMoves.addAll(generateChessMoves(board,myPosition,1,-1));
            chessMoves.addAll(generateChessMoves(board,myPosition,-1,-1));
        }
        if (type == PieceType.ROOK) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, 1));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, -1));
        }

        return chessMoves;
    }

    private Collection<ChessMove> generateChessMoves (ChessBoard board, ChessPosition chessPosition,
                                                      int colRate, int rowRate) {
        Collection<ChessMove> newChessMoves = new HashSet<>();
        int x = chessPosition.getColumn() + colRate;
        int y = chessPosition.getRow() + rowRate;
        while (x > 0 && x <= 8 && y >0 && y <= 8) {
            if (board.isPiece(x, y)) break;
            else {
                newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(y, x),null));
                System.out.println("Added Move: {" + y + "," + x + "}");
            }
            x += colRate;
            y += rowRate;
        }
        return newChessMoves;
    }
}

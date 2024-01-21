package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

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
        Collection<ChessMove> chessMoves = new HashSet<>();
        if (type == PieceType.BISHOP) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1,1));
            chessMoves.addAll(generateChessMoves(board,myPosition,1,-1));
            chessMoves.addAll(generateChessMoves(board,myPosition,-1,-1));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1,1));
        }
        if (type == PieceType.ROOK) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, 1));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, -1));
        }
        if(type == PieceType.KING) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1, 0, false));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1, 0, false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, 1, false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, -1, false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 1,1, false));
            chessMoves.addAll(generateChessMoves(board,myPosition,1,-1, false));
            chessMoves.addAll(generateChessMoves(board,myPosition,-1,-1, false));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1,1, false));
        }
        if (type == PieceType.QUEEN) {
            chessMoves.addAll(generateChessMoves(board, myPosition, 1,1));
            chessMoves.addAll(generateChessMoves(board,myPosition,1,-1));
            chessMoves.addAll(generateChessMoves(board,myPosition,-1,-1));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1,1));
            chessMoves.addAll(generateChessMoves(board, myPosition, 1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1, 0));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, 1));
            chessMoves.addAll(generateChessMoves(board, myPosition, 0, -1));
        }
        if (type == PieceType.KNIGHT) {
            chessMoves.addAll(generateChessMoves(board, myPosition,1,2,false));
            chessMoves.addAll(generateChessMoves(board, myPosition,-1,2,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 1,-2,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, -1,-2,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 2,1,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, -2,1,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, 2,-1,false));
            chessMoves.addAll(generateChessMoves(board, myPosition, -2,-1,false));
        }
        if (type == PieceType.PAWN) {
            int x = myPosition.getColumn();
            int y =myPosition.getRow();
            int moveDist = pieceColor == ChessGame.TeamColor.WHITE?1:-1;
            int startPos = pieceColor == ChessGame.TeamColor.WHITE?2:7;
            if (!board.isPiece(x,y+moveDist)) {
                chessMoves.addAll(addPromotions(board, myPosition,x,y+moveDist));
                if(myPosition.getRow()==startPos && !board.isPiece(x,y+2*moveDist))
                    chessMoves.addAll(addPromotions(board, myPosition,x,y+2*moveDist));
            }
            if (board.isPiece(x+1,y+moveDist) && board.getPiece(new ChessPosition(y+moveDist, x+1)).pieceColor != this.pieceColor) {
                chessMoves.addAll(addPromotions(board, myPosition,x+1,y+moveDist));
            }
            if (board.isPiece(x-1,y+moveDist) && board.getPiece(new ChessPosition(y+moveDist, x-1)).pieceColor != this.pieceColor) {
                chessMoves.addAll(addPromotions(board, myPosition,x-1,y+moveDist));
            }
        }

        return chessMoves;
    }

    private Collection<ChessMove> addPromotions (ChessBoard board, ChessPosition chessPosition, int newCol, int newRow) {
        Collection<ChessMove> newChessMoves = new HashSet<>();
        if (newRow == 1 || newRow == 8){
            newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(newRow, newCol), PieceType.BISHOP));
            newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(newRow, newCol), PieceType.KNIGHT));
            newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(newRow, newCol), PieceType.QUEEN));
            newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(newRow, newCol), PieceType.ROOK));
        } else {
            newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(newRow, newCol), null));
        }
        return newChessMoves;
    }
    private Collection<ChessMove> generateChessMoves (ChessBoard board, ChessPosition chessPosition,
                                                      int colRate, int rowRate) {
        return generateChessMoves(board,chessPosition, colRate, rowRate, true);
    }
    private Collection<ChessMove> generateChessMoves (ChessBoard board, ChessPosition chessPosition,
                                                      int colRate, int rowRate, boolean loop) {
        Collection<ChessMove> newChessMoves = new HashSet<>();
        int x = chessPosition.getColumn() + colRate;
        int y = chessPosition.getRow() + rowRate;
        while (x > 0 && x <= 8 && y >0 && y <= 8) {
            if (board.isPiece(x, y)) {
                if (board.getPiece(new ChessPosition(y,x)).pieceColor != this.pieceColor) {
                    newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(y, x),null));
                    System.out.println("Added Move: {" + y + "," + x + "}");
                }
                break;
            }
            else {
                newChessMoves.add(new ChessMove(chessPosition, new ChessPosition(y, x),null));
                System.out.println("Added Move: {" + y + "," + x + "}");
            }
            if (!loop) break;
            x += colRate;
            y += rowRate;
        }
        return newChessMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that=(ChessPiece) o;
        return type == that.type && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, pieceColor);
    }

}

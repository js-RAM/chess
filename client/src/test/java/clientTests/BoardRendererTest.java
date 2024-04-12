package clientTests;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.BoardRenderer;

class BoardRendererTest {
    private static BoardRenderer boardRenderer;
    @BeforeAll
    public static void init() {
        boardRenderer = new BoardRenderer();
    }

    @Test
    public void render() {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        Assertions.assertDoesNotThrow(() -> boardRenderer.render(chessBoard));
        Assertions.assertDoesNotThrow(() -> boardRenderer.render(chessBoard, true));
    }

    @Test
    public void renderMoves() {
        ChessBoard chessBoard = new ChessBoard();
        chessBoard.resetBoard();
        ChessGame game = new ChessGame();
        game.setBoard(chessBoard);
        var chessMoves = game.validMoves(new ChessPosition(2,1));
        boardRenderer.setValidMoves(chessMoves);
        Assertions.assertDoesNotThrow(() -> boardRenderer.render(chessBoard));
        Assertions.assertDoesNotThrow(() -> boardRenderer.render(chessBoard, true));
    }
}
package clientTests;

import chess.ChessBoard;
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
}
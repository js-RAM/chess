package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Random;

import static tools.EscapeSequences.*;

public class BoardRenderer {

    private final int BOARD_SIZE_IN_SQUARES = 8;
    private boolean isBackgroundBlack;
    private final String[] headers;
    private String[] row_labels;
    private ChessBoard chessBoard;
    private final String X = " X ";
    private final String O = " O ";
    private static final Random rand = new Random();
    private static  PrintStream out;


    public BoardRenderer() {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        headers = new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        row_labels = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        isBackgroundBlack = false;
        chessBoard = new ChessBoard();
    }

    public void render(ChessBoard chessBoard) {
        render(chessBoard,false);
    }

    public void render(ChessBoard chessBoard, boolean reverse) {
        this.chessBoard = chessBoard;

        out.print(ERASE_SCREEN);

        drawHeaders(out);

        if(reverse) {
            setBlack(out);
            drawBoard(out);
        } else {
            setBlack(out);
            drawBoardReversed(out);
        }

        drawHeaders(out);

        out.print(RESET_BG_COLOR);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void drawHeaders(PrintStream out) {

        out.print(SET_TEXT_COLOR_GREEN);
        out.print(SET_BG_COLOR_DARK_GREY);

        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES+2; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }
        out.println(RESET_BG_COLOR);
    }

    private void drawHeader(PrintStream out, String headerText) {
        out.print(" ");
        printHeaderText(out, headerText);
        out.print(" ");
    }

    private void printHeaderText(PrintStream out, String player) {
        out.print(player);
    }

    private void printColumnHeader(PrintStream out, String header) {
        out.print(SET_TEXT_COLOR_GREEN);
        out.print(SET_BG_COLOR_DARK_GREY);
        drawHeader(out, header);
    }

    private void drawBoard(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if(boardRow % 2 == 0) if(isBackgroundBlack) setWhite(out); else setBlack(out);
            printColumnHeader(out, row_labels[boardRow]);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if(isBackgroundBlack) setWhite(out); else setBlack(out);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(boardRow+1, 8-boardCol));
                if (piece != null) printPlayer(out, " " + piece.toString() + " ");
                else printPlayer(out, "   ");
            }
            printColumnHeader(out, row_labels[boardRow]);
            out.println(RESET_BG_COLOR);
            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private void drawBoardReversed(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if(boardRow % 2 == 1) if(isBackgroundBlack) setWhite(out); else setBlack(out);
            printColumnHeader(out, row_labels[7-boardRow]);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if(isBackgroundBlack) setWhite(out); else setBlack(out);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(8-boardRow, boardCol+1));
                if (piece != null) printPlayer(out, " " + piece.toString() + " ");
                else printPlayer(out, "   ");
            }
            printColumnHeader(out, row_labels[boardRow]);
            out.println(RESET_BG_COLOR);
            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private void setWhite(PrintStream out) {
        isBackgroundBlack = false;
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void setRed(PrintStream out) {
        isBackgroundBlack = false;
        out.print(SET_BG_COLOR_RED);
        out.print(SET_TEXT_COLOR_RED);
    }

    private void setBlack(PrintStream out) {
        isBackgroundBlack = true;
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void printPlayer(PrintStream out, String player) {
        out.print(player);
    }
}
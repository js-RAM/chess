package ui;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class BoardRenderer {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private boolean isBackgroundBlack;
    private String[] headers;
    private final String[] rowLabels;
    private ChessBoard chessBoard;
    private static  PrintStream out;

    private int[][] validMoves;


    public BoardRenderer() {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        headers = new String[]{" ", "a", "b", "c", "d", "e", "f", "g", "h", " "};
        rowLabels= new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        isBackgroundBlack = false;
        chessBoard = new ChessBoard();
        validMoves = new int[8][8];
    }

    public void render(ChessBoard chessBoard) {
        render(chessBoard,false);
    }

    public void setValidMoves(Collection<ChessMove> chessMoves) {
        validMoves = new int[8][8];
        for (ChessMove move : chessMoves) {
            ChessPosition startPosition = move.getStartPosition();
            validMoves[startPosition.getRow()-1][startPosition.getColumn()-1] = 2;
            ChessPosition position = move.getEndPosition();
            validMoves[position.getRow()-1][position.getColumn()-1] = 1;
        }
    }

    public void render(ChessBoard chessBoard, boolean reverse) {
        this.chessBoard = chessBoard;
        out.print(ERASE_SCREEN);
        if (reverse) {
            headers = new String[]{" ", "h", "g", "f", "e", "d", "c", "b", "a", " "};
        }
        drawHeaders(out);

        setBlack(out);
        if(reverse) drawBoard(out);
        else drawBoardReversed(out);

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
            if(boardRow % 2 == 1) if(isBackgroundBlack) setWhite(out); else setBlack(out);
            printColumnHeader(out, rowLabels[boardRow]);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if(isBackgroundBlack) setWhite(out); else setBlack(out);
                if (validMoves[boardRow][7-boardCol] == 1) out.print(SET_BG_COLOR_GREEN);
                if (validMoves[boardRow][7-boardCol] == 2) out.print(SET_BG_COLOR_YELLOW);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(boardRow+1, 8-boardCol));
                if (piece != null) printPlayer(out, " " + piece + " ");
                else printPlayer(out, "   ");
            }
            printColumnHeader(out, rowLabels[boardRow]);
            out.println(RESET_BG_COLOR);
            if (boardRow < BOARD_SIZE_IN_SQUARES - 1) {
                setBlack(out);
            }
        }
    }

    private void drawBoardReversed(PrintStream out) {
        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {
            if(boardRow % 2 == 1) if(isBackgroundBlack) setWhite(out); else setBlack(out);
            printColumnHeader(out, rowLabels[7-boardRow]);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if(isBackgroundBlack) setWhite(out); else setBlack(out);
                if (validMoves[7-boardRow][boardCol] == 1) out.print(SET_BG_COLOR_GREEN);
                if (validMoves[7-boardRow][boardCol] == 2) out.print(SET_BG_COLOR_YELLOW);
                ChessPiece piece = chessBoard.getPiece(new ChessPosition(8-boardRow, boardCol+1));
                if (piece != null) printPlayer(out, " " + piece + " ");
                else printPlayer(out, "   ");
            }
            printColumnHeader(out, rowLabels[7-boardRow]);
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

    private void setBlack(PrintStream out) {
        isBackgroundBlack = true;
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void printPlayer(PrintStream out, String player) {
        out.print(player);
    }
}
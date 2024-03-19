package ui;

import static ui.EscapeSequences.*;

public class Repl {
    public Repl(String url) {

    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE+"Welcome to Chess. Sign in to start.\nUse 'help' to see a list of commands.");
    }
}

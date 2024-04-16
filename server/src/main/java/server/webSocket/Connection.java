package server.webSocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String authToken;
    public Session session;
    public ChessGame.TeamColor color;

    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }

    public Connection(String authToken, Session session, ChessGame.TeamColor color) {
        this.authToken = authToken;
        this.session = session;
        this.color = color;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}

package model;

import websocket.WebsocketFacade;

public record ClientResponse(LoginState loginStatus, String message, String authToken, WebsocketFacade ws) {
}

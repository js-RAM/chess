package model;

public record ClientResponse(LoginState loginStatus, String message, String authToken) {
}

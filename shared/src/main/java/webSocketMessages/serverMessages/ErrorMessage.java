package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;
    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = "Error: " + errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String getMessage() { return errorMessage; }
}

package exception;

/**
 * Indicates there was an error connecting to the database
 */
public class ServerException extends Exception{
    private final int statusCode;
    public ServerException(int errorCode, String message) {
        super(message);
        statusCode = errorCode;
    }

    public ServerException(int errorCode) {
        super("");
        statusCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

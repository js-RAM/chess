package services;

/**
 * Indicates there was an error connecting to the database
 */
public class ServicesException extends Exception{
    public ServicesException(String message) {
        super(message);
    }
}

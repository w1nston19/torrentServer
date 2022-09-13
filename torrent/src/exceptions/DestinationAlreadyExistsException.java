package exceptions;

public class DestinationAlreadyExistsException extends Exception {
    public DestinationAlreadyExistsException(String message) {
        super(message);
    }

    public DestinationAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

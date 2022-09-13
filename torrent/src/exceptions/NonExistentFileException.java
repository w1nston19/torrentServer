package exceptions;

public class NonExistentFileException extends Exception {
    public NonExistentFileException(String message) {
        super(message);
    }

    public NonExistentFileException(String message, Throwable cause) {
        super(message, cause);
    }
}

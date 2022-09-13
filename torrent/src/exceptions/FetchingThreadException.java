package exceptions;

public class FetchingThreadException extends RuntimeException {
    public FetchingThreadException(String message, Throwable cause) {
        super(message, cause);
    }
}

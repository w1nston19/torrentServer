package exceptions;

public class UndetectableLocalIPException extends Exception {
    public UndetectableLocalIPException() {
    }

    public UndetectableLocalIPException(String message) {
        super(message);
    }

    public UndetectableLocalIPException(String message, Throwable cause) {
        super(message, cause);
    }
}

package exceptions;

public class UserHasNotRegisteredTorrentsException extends Exception{
    public UserHasNotRegisteredTorrentsException(String message) {
        super(message);
    }

    public UserHasNotRegisteredTorrentsException(String message, Throwable cause) {
        super(message, cause);
    }
}

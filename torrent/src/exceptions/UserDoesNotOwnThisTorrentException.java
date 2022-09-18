package exceptions;

public class UserDoesNotOwnThisTorrentException extends Exception{
    public UserDoesNotOwnThisTorrentException(String message) {
        super(message);
    }

    public UserDoesNotOwnThisTorrentException(String message, Throwable cause) {
        super(message, cause);
    }
}

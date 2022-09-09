package exceptions;

public class FetchingThreadException extends RuntimeException{

    public FetchingThreadException(){}

    public FetchingThreadException(String message){
        super(message);
    }

    public FetchingThreadException(String message, Throwable cause){
        super(message, cause);
    }
}

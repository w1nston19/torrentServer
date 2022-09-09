package exceptions;

public class NonExistentFileException extends RuntimeException{
    public NonExistentFileException(){}

    public NonExistentFileException(String message){
        super(message);
    }

    public NonExistentFileException(String message, Throwable cause){
        super(message, cause);
    }
}

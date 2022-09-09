package logger;


import java.nio.file.Path;
import java.time.LocalDateTime;

public interface Logger {

    void log(LocalDateTime timestamp,Class<?> clazz,  String message);
    /**
     * Gets the Logger's options.
     *
     * @return the Logger's options
     */
    LoggerOptions getOptions();

    /**
     * Gets the current log file path.
     *
     * @return the current log file path
     */
    Path getCurrentFilePath();

}
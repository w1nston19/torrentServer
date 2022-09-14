package logger;

import java.time.LocalDateTime;

public interface Loggable {
    Logger LOGGER = new DefaultLogger(new LoggerOptions("torrent/src/logs"));

    String ERROR =
            "There was a problem with %s Please contact admin and provide the information in <%s>";


    default void handleException(Exception e, String message, Class<?> clazz) {
        System.out.printf((ERROR) + "%n", message,
                LOGGER.getCurrentFilePath().toString());
        log(clazz, e.getMessage());
    }

    private void log(Class<?> clazz, String mssg) {
        LOGGER.log(LocalDateTime.now(),
                clazz,
                mssg);
    }
}

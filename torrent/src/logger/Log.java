package logger;

import java.time.LocalDateTime;

public record Log(LocalDateTime timestamp, String packageName, String message) {
    private static final String FORMAT = "%s|%s|%s" + System.lineSeparator();

    @Override
    public String toString() {
        return String.format(FORMAT, timestamp, packageName, message);
    }
}
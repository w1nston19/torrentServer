package logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class DefaultLogger implements Logger {
    private final String LOG_FORMAT = "logs-%d.txt";
    private static int logID = -1;

    LoggerOptions options;

    Path currentFile;

    BufferedWriter writer;


    public DefaultLogger(LoggerOptions options) {
        this.options = options;
        try {
            makePath();
            openFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void makePath() {
        String name = String.format(LOG_FORMAT, ++logID);
        this.currentFile = Path.of(options.getDirectory(), name);
    }

    public void openFile() throws IOException {
        this.writer = Files.newBufferedWriter(currentFile);
    }

    public boolean canWriteToFile() throws IOException {
        return Files.size(currentFile) < options.getMaxFileSizeBytes();
    }

    @Override
    public void log(LocalDateTime timestamp, Class<?> clazz, String message) {
        validateNotNull(timestamp);
        validateNotNull(message);
        validateNotEmpty(message);

        Log toLog = new Log(timestamp, clazz.getPackageName() + "/" + clazz.getName(), message);

        try {
            if (!canWriteToFile()) {
                closeFile();
                makePath();
                openFile();
            }

            writer.write(toLog.toString());
            writer.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void closeFile() throws IOException {
        writer.close();
    }

    @Override
    public LoggerOptions getOptions() {
        return options;
    }

    @Override
    public Path getCurrentFilePath() {
        return currentFile;
    }

    public void validateNotNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    public void validateNotEmpty(String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }
}

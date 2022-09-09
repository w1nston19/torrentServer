package logger;

public class LoggerOptions {

    private static final long DEFAULT_MAX_FILE_SIZE_BYTES = 1024;
    private final String directory;

    private long maxFileSizeBytes = DEFAULT_MAX_FILE_SIZE_BYTES;

    public LoggerOptions(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public void setMaxFileSizeBytes(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

}
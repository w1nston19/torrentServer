package peer;

import exceptions.FetchingThreadException;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class FetchingThread extends AbstractPeer implements Runnable {
    private final Path log;

    private final static String SERVER_MESSAGE = "fetch";

    private final SocketChannel socketChannel;

    private static final int BUFFER_SIZE = 4096;

    public FetchingThread(Path pathToLog, SocketChannel channel) {
        log = pathToLog;
        socketChannel = channel;
    }

    @Override
    public void run() {
        try {
             ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
             sendMessage(byteBuffer, socketChannel, SERVER_MESSAGE);
             String output = getMessage(byteBuffer, socketChannel);
             Files.write(log, output.getBytes());
        } catch (Exception e) {
            throw new FetchingThreadException(e.getMessage(), e);
        }
    }
}

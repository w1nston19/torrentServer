package peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface Peer {
    int BUFFER_SIZE = 1024;

    String PATH_TO_FILE_FORMAT = "torrent/src/data/%s";

    String DISCONNECT_STR = "disconnect";

    String DOWNLOAD_STR = "download";

    void sendMessage(ByteBuffer buffer, SocketChannel socketChannel, String message) throws IOException;


    String getMessage(ByteBuffer buffer, SocketChannel socketChannel) throws IOException;

}

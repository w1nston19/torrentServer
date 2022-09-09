package peer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public abstract class AbstractPeer implements Peer {
    public void sendMessage(ByteBuffer buffer, SocketChannel socketChannel, String message) throws IOException {
        buffer.clear();

        buffer.put(message.getBytes());
        buffer.flip();

        socketChannel.write(buffer);
    }

    public String getMessage(ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);

        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8);
    }
}

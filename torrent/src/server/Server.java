package server;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public interface Server {
    int BUFFER_SIZE = 1024;
    int DEFAULT_PORT = 6584;

    void accept(Selector selector, SelectionKey selectionKey) throws IOException;

    String getClientInput(SocketChannel socketChannel) throws IOException;

    void writeOutput(ByteBuffer byteBuffer, SocketChannel clientChannel, String output) throws IOException;

    void stopServer(Selector selector);

    InetAddress getIP();

    void establish(ServerSocketChannel serverSocketChannel, InetAddress address, int port) throws IOException;
}

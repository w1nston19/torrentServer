package server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class AbstractServer implements Server {
    protected boolean isServerWorking;
    protected Selector selector;
    protected ByteBuffer byteBuffer;

    @Override
    public void accept(Selector selector, SelectionKey selectionKey) throws IOException {
        ServerSocketChannel socketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel sock = socketChannel.accept();

        sock.configureBlocking(false);
        sock.register(selector, SelectionKey.OP_READ);
    }

    public String getClientInput(SocketChannel socketChannel) throws IOException {
        byteBuffer.clear();

        int readBytes = socketChannel.read(byteBuffer);
        if (readBytes < 0) {
            return null;
        }

        byteBuffer.flip();

        byte[] clientInputBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    @Override
    public void writeOutput(ByteBuffer byteBuffer, SocketChannel clientChannel, String output) throws IOException {
        byteBuffer.clear();
        byteBuffer.put(output.getBytes());
        byteBuffer.flip();

        clientChannel.write(byteBuffer);
    }

    @Override
    public void stopServer(Selector selector) {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    @Override
    public InetAddress getIP() {
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);
            return InetAddress.getByName(datagramSocket.getLocalAddress().getHostAddress());
        } catch (Exception e) {
            //ToDo
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void establish(ServerSocketChannel serverSocketChannel, InetAddress address, int port) throws IOException {
        this.selector = Selector.open();

        serverSocketChannel.bind(new InetSocketAddress(address, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.printf("Server established at IP : %s and port %d%n", address.getHostAddress(), port);
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        isServerWorking = true;
    }
}

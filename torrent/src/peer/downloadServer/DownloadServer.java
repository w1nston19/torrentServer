package peer.downloadServer;

import logger.Loggable;
import server.AbstractServer;
import server.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadServer extends AbstractServer implements Server, Runnable, Loggable {

    InetSocketAddress socketAddress;

    public DownloadServer(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public void run() {
        try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
            establish(socketChannel, socketAddress.getAddress(), socketAddress.getPort());

            while (isServerWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                var selectedKeys = selector.selectedKeys();
                var it = selectedKeys.iterator();

                while (it.hasNext()) {
                    var selectionKey = it.next();

                    if (selectionKey.isReadable()) {
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        String input = getClientInput(sc);

                        if (input == null) {
                            if (sc.isConnected()) {
                                sc.close();
                            }
                            continue;
                        }
                        printFile(input, sc);

                    } else {
                        if (selectionKey.isAcceptable()) {
                            accept(selector, selectionKey);
                        }
                    }
                }
                it.remove();
            }

        } catch (IOException ioException) {
            handleException(ioException,
                    "the download server ",
                    this.getClass()
            );
        }

    }

    public void stop() {
        stopServer(selector);
    }

    public void printFile(String pathStr, SocketChannel channel) throws IOException {
        Path filePath = Path.of(pathStr);

        if (!Files.exists(filePath)) {
            channel.close();
            return;
        }

        byteBuffer.clear();
        try (var inputStream = new FileInputStream(filePath.toFile())) {
            long size = Files.size(filePath);
            long position = 0;
            while (position < size) {
                FileChannel fileChannel = inputStream.getChannel();
                position += fileChannel.transferTo(position, fileChannel.size(), channel);
            }

        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
            /*
            handleException(ioException,
                    "extracting the file",
                    this.getClass()
            );*/
        }

        channel.close();
    }
}

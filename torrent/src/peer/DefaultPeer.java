package peer;

import peer.downloadClient.DefaultDownloadClient;
import peer.downloadClient.DownloadClient;
import peer.downloadServer.DownloadServer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class DefaultPeer extends AbstractPeer implements Peer {

    private int counter = 0;

    private final InetSocketAddress inetSocketAddress;

    private Path PATH_TO_FILE;

    public DefaultPeer() {
        InetAddress ip;
        int port;
        try (final DatagramSocket datagramSocket = new DatagramSocket()) {
            datagramSocket.connect(InetAddress.getByName("8.8.8.8"), 12345);

            ip = InetAddress.getByName(datagramSocket.getLocalAddress().getHostAddress());
            port = datagramSocket.getLocalPort();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        inetSocketAddress = new InetSocketAddress(ip, port);
    }

    public void generateFile() {
        try {
            while (Files.exists(Path.of(PATH_TO_FILE_FORMAT.formatted(counter)))) {
                counter++;
            }
            PATH_TO_FILE = Path.of(PATH_TO_FILE_FORMAT.formatted(counter));
            Files.createFile(PATH_TO_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void start(InetAddress serverAddress, int port) {
        InetSocketAddress server = new InetSocketAddress(serverAddress, port);
        generateFile();

        DownloadClient client = new DefaultDownloadClient(PATH_TO_FILE);

        try (
                SocketChannel socketChannel = SocketChannel.open();
                Scanner input = new Scanner(System.in)
        ) {

            socketChannel.connect(server);
            DownloadServer ds = startDownloadServer();
            FetchingThread fetchingThread = startFetching(socketChannel);
            System.out.println("Successfully connected to the main server");
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                System.out.println("Enter command");
                String inputStr = input.nextLine();

                if (DISCONNECT_STR.equals(inputStr)) {
                    fetchingThread.stop();
                    socketChannel.close();
                    ds.stop();
                    break;
                }

                if (inputStr.startsWith(DOWNLOAD_STR)) {
                    client.download(inputStr);
                    continue;
                }

                inputStr = inputStr + ' ' + inetSocketAddress;
                sendMessage(buffer, socketChannel, inputStr);
                System.out.println(getMessage(buffer, socketChannel));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DownloadServer startDownloadServer() {
        DownloadServer ds = new DownloadServer(inetSocketAddress);
        Thread serverThread = new Thread(ds);
        serverThread.start();
        return ds;
    }

    private FetchingThread startFetching(SocketChannel socketChannel) {
        var fetchingThread = new FetchingThread(PATH_TO_FILE, socketChannel);
        Thread thread = new Thread(fetchingThread);
        thread.start();
        return fetchingThread;
    }

}
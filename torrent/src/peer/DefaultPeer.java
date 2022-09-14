package peer;

import exceptions.DestinationAlreadyExistsException;
import exceptions.FetchingThreadException;
import exceptions.NonExistentFileException;
import logger.Loggable;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DefaultPeer extends AbstractPeer implements Peer, Loggable {
    private static final int DEFAULT_FETCH_TIME = 30;

    private int counter = 0;

    private final InetSocketAddress inetSocketAddress;

    private String name = null;

    private Path pathToFile;

    private static final String NAME_MESSAGE = "name:";

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
            pathToFile = Path.of(PATH_TO_FILE_FORMAT.formatted(counter));
            Files.createFile(pathToFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Path getPathToFile() {
        return pathToFile;
    }


    SocketChannel socketCh;

    public void setSocketCh(SocketChannel socketCh) {
        this.socketCh = socketCh;
    }

    public void start(InetAddress serverAddress, int port) {
        InetSocketAddress server = new InetSocketAddress(serverAddress, port);
        generateFile();

        DownloadClient client = new DefaultDownloadClient(pathToFile);

        try {
            socketCh = SocketChannel.open();
        } catch (IOException ioException) {
            handleException(ioException,
                    "the connection to the main ",
                    this.getClass()
            );
            throw new RuntimeException(ioException);
        }

        try (
                SocketChannel socketChannel = socketCh;
                Scanner input = new Scanner(System.in)
        ) {
            socketChannel.connect(server);
            DownloadServer ds = startDownloadServer();
            ScheduledExecutorService fetchingExecutor = startFetching(socketChannel);

            System.out.println("Successfully connected to the main server");
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (true) {
                System.out.println("Enter command");
                String inputStr = input.nextLine();

                if (DISCONNECT_STR.equals(inputStr)) {
                    socketChannel.close();
                    ds.stop();
                    fetchingExecutor.shutdown();
                    System.out.println("Disconnecting...");
                    break;
                }

                if (inputStr.startsWith(DOWNLOAD_STR)) {
                    try {
                        download(inputStr, client, input, buffer, socketChannel);
                    } catch (RuntimeException runtimeException) {
                        continue;
                    }
                    continue;
                }

                inputStr = inputStr + ' ' + inetSocketAddress;
                sendMessage(buffer, socketChannel, inputStr);
                String output = getMessage(buffer, socketChannel);
                if (output.startsWith(NAME_MESSAGE)) {
                    name = output.split(":")[1];
                    continue;
                }
                System.out.println(output);
            }

        } catch (FetchingThreadException fetchingThreadException) {
            handleException(fetchingThreadException,
                    "fetching data from the server",
                    FetchingThread.class);
            throw new RuntimeException(fetchingThreadException);
        } catch (IOException ioException) {
            handleException(ioException,
                    "the connection to the main ",
                    this.getClass()
            );
            throw new RuntimeException(ioException);
        }
        System.out.println("Disconnected successfully");
    }

    private DownloadServer startDownloadServer() {
        var ds = new DownloadServer(inetSocketAddress);
        Thread thread = new Thread(ds);
        thread.start();
        return ds;
    }

    private ScheduledExecutorService startFetching(SocketChannel socketChannel) {
        FetchingThread fetchingThread = new FetchingThread(pathToFile, socketChannel);
        ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
        es.scheduleAtFixedRate(fetchingThread, 0, DEFAULT_FETCH_TIME, TimeUnit.SECONDS);
        return es;
    }

    private void download(String inputStr, DownloadClient client, Scanner input,
                          ByteBuffer buffer, SocketChannel socketChannel) throws IOException {
        String path;
        try {
            path = client.download(inputStr);
        } catch (DestinationAlreadyExistsException | NonExistentFileException | RuntimeException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        if (this.name == null) {
            System.out.println("You haven't specified your username.");
            System.out.print(NAME_MESSAGE);
            name = input.nextLine();
        }
        String tmp = REGISTER_FORMAT.formatted(name, path, inetSocketAddress);
        System.out.println(tmp);
        sendMessage(buffer, socketChannel, tmp);
        System.out.println(getMessage(buffer, socketChannel));
    }
}
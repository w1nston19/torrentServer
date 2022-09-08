package peer.downloadClient;

import peer.AbstractPeer;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DefaultDownloadClient extends AbstractPeer implements DownloadClient {
    private final Path PATH_TO_FILE;

    public DefaultDownloadClient(Path pathToFile) {
        PATH_TO_FILE = pathToFile;
    }

    public void download(String input) {
        String[] tokens = input.split(COMMAND_DELIMITER);
        if (tokens.length != 4) {
            System.out.println("Wrong usage of command download");
            System.out.println("Usage : download <user> <source> <dest>");
            return;
        }
        download(
                tokens[SOURCE_USER_TOKEN],
                tokens[SOURCE_TOKEN],
                tokens[DESTINATION_TOKEN]);
    }

    public void download(String user, String from, String to) {
        Map.Entry<String, String> ipAddress = getTorrent(user, from);

        if (ipAddress.getKey().equals(ERROR_MESSAGE)) {
            System.out.println(ipAddress.getValue());
            return;
        }

        Path file = Path.of(to);
        if (Files.exists(file)) {
            System.out.println("Destination file already exists");
            return;
        }

        try {
            Files.createFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //might be magic
        String ip = ipAddress.getKey().substring(1);
        int port = Integer.parseInt(ipAddress.getValue());

        try (SocketChannel channel = SocketChannel.open()) {
            System.out.println("Attempt to connect to " + ip + ":" + port);
            channel.connect(new InetSocketAddress(ip, port));
            System.out.println("Connected");
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            sendMessage(buffer, channel, from);

            try (var os = new FileOutputStream(to)) {
                FileChannel fileChannel = os.getChannel();
                fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            }
            channel.close();
            System.out.println("I finished");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map.Entry<String, String> getTorrent(String user, String from) {
        List<String> allFromUser = getTorrentsFromUser(user);
        if (allFromUser.isEmpty()) {
            return Map.entry(ERROR_MESSAGE, "The user hasn't registered any torrents \n");
        }
        List<String[]> allTokens = allFromUser.stream().map(s -> s.split(TORRENT_DELIMITER)).toList();


        List<String[]> neededTokens = allTokens.stream()
                .filter(tokens -> tokens[FILE_TOKEN].equals(from))
                .toList();

        if (neededTokens.isEmpty()) {
            return Map.entry(ERROR_MESSAGE, "There is no torrent with path<%s> connected to user <%s>%n"
                    .formatted(from, user));

        }

        String[] torrent = neededTokens.get(0);
        return Map.entry(torrent[IP_TOKEN], torrent[PORT_TOKEN]);
    }

    private List<String> getTorrentsFromUser(String user) {
        List<String> torrents = new ArrayList<>();
        String torrentLine;

        try (BufferedReader bufferedReader = Files.newBufferedReader(PATH_TO_FILE)) {
            while ((torrentLine = bufferedReader.readLine()) != null) {
                String[] tokens = torrentLine.split(TORRENT_DELIMITER);
                if (user.equals(tokens[USER_TOKEN])) {
                    torrents.add(torrentLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return torrents;
    }
}

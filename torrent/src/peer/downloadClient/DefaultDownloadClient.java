package peer.downloadClient;

import exceptions.DestinationAlreadyExistsException;
import exceptions.NonExistentFileException;
import exceptions.UserDoesNotOwnThisTorrentException;
import exceptions.UserHasNotRegisteredTorrentsException;
import logger.Loggable;
import peer.AbstractPeer;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DefaultDownloadClient extends AbstractPeer implements DownloadClient, Loggable {
    private final Path pathToFile;

    public DefaultDownloadClient(Path pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String download(String input) throws DestinationAlreadyExistsException, NonExistentFileException {
        String[] tokens = input.split(COMMAND_DELIMITER);
        if (tokens.length != 4) {
            System.out.println("Wrong usage of command download");
            System.out.println("Usage : download <user> <source> <dest>");
            throw new RuntimeException("Wrong usage of command download.");
        }
        getFromOtherPeer(
                tokens[SOURCE_USER_TOKEN],
                tokens[SOURCE_TOKEN],
                tokens[DESTINATION_TOKEN]);
        return tokens[DESTINATION_TOKEN];
    }

    public void getFromOtherPeer(String user, String from, String to) throws NonExistentFileException,
            DestinationAlreadyExistsException {
        InetSocketAddress ipAddress;
        try{
            ipAddress = getTorrent(user, from);
        }catch (UserDoesNotOwnThisTorrentException | UserHasNotRegisteredTorrentsException exception){
            throw new NonExistentFileException(exception.getMessage());
        }


        Path file = Path.of(to);
        createDest(file);

        try (SocketChannel channel = SocketChannel.open()) {
            channel.connect(ipAddress);
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            sendMessage(buffer, channel, from);
            if (!channel.isConnected()) {
                throw new NonExistentFileException("the path provided is not valid/the " +
                        "file on this path doesn't exist");
            }
            try (var os = new FileOutputStream(to)) {
                FileChannel fileChannel = os.getChannel();
                fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            }

            if (Files.size(file) == 0) {
                Files.delete(file);
                throw new NonExistentFileException("the file doesn't exist or the writing was not success");
            }
        } catch (Exception e) {
            handleException(e,
                    "downloading the file ",
                    this.getClass()
            );
            throw new NonExistentFileException(e.getMessage(), e);
        }
    }


    private void createDest(Path file) throws DestinationAlreadyExistsException {
        if (Files.exists(file)) {
            System.out.println("Destination file already exists");
            throw new DestinationAlreadyExistsException("Provided destination already exists");
        }

        try {
            Files.createFile(file);
        } catch (IOException ioException) {
            handleException(ioException,
                    "creating a destination file ",
                    this.getClass()
            );
            throw new RuntimeException(ioException);
        }
    }

    private InetSocketAddress getTorrent(String user, String from) throws UserHasNotRegisteredTorrentsException,
            UserDoesNotOwnThisTorrentException {
        List<String> allFromUser = getTorrentsFromUser(user);
        if (allFromUser.isEmpty()) {
            throw new UserHasNotRegisteredTorrentsException("The user does not have any torrents linked with him");
        }

        List<String[]> allTokens = allFromUser.stream().map(s -> s.split(TORRENT_DELIMITER)).toList();
        List<String[]> neededTokens = allTokens.stream()
                .filter(tokens -> tokens[FILE_TOKEN].equals(from))
                .toList();

        if (neededTokens.isEmpty()) {
            throw new UserDoesNotOwnThisTorrentException("There is no torrent with path<%s> connected to user <%s>%n");
        }

        String[] torrent = neededTokens.get(0);
        String ip = torrent[IP_TOKEN].substring(1);
        return new InetSocketAddress(ip, Integer.parseInt(torrent[PORT_TOKEN]));
    }

    private List<String> getTorrentsFromUser(String user) {
        List<String> torrents = new ArrayList<>();
        String torrentLine;

        try (BufferedReader bufferedReader = Files.newBufferedReader(pathToFile)) {
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

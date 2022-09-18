package peer.downloadClient;

import exceptions.DestinationAlreadyExistsException;
import exceptions.NonExistentFileException;

public interface DownloadClient {

    String TORRENT_DELIMITER = ":";
    String ERROR_MESSAGE = "Err";
    int USER_TOKEN = 0;
    int IP_TOKEN = 1;
    int PORT_TOKEN = 2;
    int FILE_TOKEN = 3;

    int SOURCE_USER_TOKEN = 1;
    int SOURCE_TOKEN = 2;
    int DESTINATION_TOKEN = 3;

    String COMMAND_DELIMITER = " ";

    String download(String input) throws DestinationAlreadyExistsException, NonExistentFileException;

    void getFromOtherPeer(String user, String from, String to)
            throws NonExistentFileException, DestinationAlreadyExistsException;

}

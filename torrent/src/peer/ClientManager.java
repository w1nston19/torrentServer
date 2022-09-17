package peer;

import java.net.InetAddress;

public class ClientManager {
    public static void main(String[] args) {
        DefaultPeer client = new DefaultPeer();

        try {
            client.start(InetAddress.getByName("192.168.0.127"), 6584);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

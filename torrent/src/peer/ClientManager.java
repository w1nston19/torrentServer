package peer;

import java.net.InetAddress;

public class ClientManager {
    public static void main(String[] args) {
        DefaultPeer client = new DefaultPeer();

        try {
            client.start(InetAddress.getByName("192.168.0.104"), 6584);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

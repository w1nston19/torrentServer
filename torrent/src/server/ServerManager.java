package server;

import java.util.Scanner;

public class ServerManager {
    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.start();
        String adminInput = null;
        Scanner scanner = new Scanner(System.in);
        while (adminInput == null || !adminInput.equals("quit")) {
            adminInput = scanner.nextLine();
        }
    }
}

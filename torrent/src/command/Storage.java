package command;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {

    private static final String EMPTY_STORAGE = "No items currently available";
    private final Map<String, List<Path>> activeUsers;
    private final Map<String, String> userAddresses;

    public Storage() {
        this.activeUsers = new HashMap<>();
        this.userAddresses = new HashMap<>();
    }

    public String list() {
        StringBuilder builder = new StringBuilder();

        for (String userClient : activeUsers.keySet()) {
            for (Path path : activeUsers.get(userClient)) {
                builder.append(userAddresses.get(userClient)).append(" : ");
                builder.append(path).append("\n");
            }
        }

        return builder.isEmpty() ? EMPTY_STORAGE : builder.toString();
    }

    public String server_info() {
        StringBuilder builder = new StringBuilder();

        for (String userClient : activeUsers.keySet()) {
            for (Path path : activeUsers.get(userClient)) {
                builder.append(userAddresses.get(userClient)).append(":");
                builder.append(userClient).append(":").append(path).append("\n");
            }
        }

        return builder.isEmpty() ? EMPTY_STORAGE : builder.toString();
    }

    public String register(String username, List<Path> paths, String address) {
        if (!activeUsers.containsKey(address)) {
            if (userAddresses.containsValue(username)) {
                return "The username you are trying to register from is already by another ip\n";
            }
            activeUsers.put(address, new ArrayList<>());
            userAddresses.put(address, username);
        } else {
            if (!userAddresses.get(address).equals(username)) {
                return "The ip address <%s> you are trying to register".formatted(address) +
                        " from already is linked with another username <%s> \n"
                                .formatted(userAddresses.get(address));
            }
        }

        activeUsers.get(address).addAll(paths);

        return "Registration successful/n";
    }


    public String unregister(String username, List<Path> paths, String address) {
        if (activeUsers.containsKey(address)) {
            if (!userAddresses.get(address).equals(username)) {
                return "The ip <%s> you are trying to unregister".formatted(address) +
                        " from is linked with another username" +
                        "<%s> (%s provided)%n".formatted(userAddresses.get(address), username);
            }

            StringBuilder builder = new StringBuilder();
            var activeUserPaths = activeUsers.get(address);

            int unregisteredFiles = 0;

            for (Path path : paths) {
                if (activeUserPaths.contains(path)) {
                    activeUserPaths.remove(path);
                    unregisteredFiles++;
                } else {
                    builder.append(("The path <%s> you are trying to unregister " +
                            "is not linked with this user\n").formatted(path.toString()));
                }
            }

            if (activeUsers.get(address).isEmpty()) {
                activeUsers.remove(address);
                userAddresses.remove(address);
            }
            builder.append("Unregistered %d/%d files".formatted(unregisteredFiles, paths.size()));
            return builder.toString();
        }
        return "There is no active user with that ip address\n";
    }

    public void disconnect(String address) {
        if (activeUsers.containsKey(address)) {
            activeUsers.remove(address);
            userAddresses.remove(address);
        }
    }

}

package command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Command(String command, String userClient, List<String> arguments) {
    private final static String delimiter = " ";
    private final static short commandToken = 0;
    private final static short userToken = 1;

    //Used for commands that don't take username or args (list-files/disconnect)
    private final static short shortCommandLen = 1;

    public static Command of(String clientInput) {
        String[] tokens = clientInput.split(delimiter);

        String command = tokens[commandToken];

        if (tokens.length == shortCommandLen) {
            return new Command(command, null, null);
        }

        String user = tokens[userToken];

        List<String> arguments =
                new ArrayList<>(Arrays.asList(tokens)
                        .subList(userToken + 1, tokens.length));

        return new Command(command, user, arguments);
    }
}

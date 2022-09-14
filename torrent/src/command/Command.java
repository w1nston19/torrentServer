package command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Command(String command, String userClient, List<String> arguments) {
    private final static String DELIMITER = " ";
    private final static short COMMAND_TOKEN = 0;
    private final static short USER_TOKEN = 1;

    //Used for commands that don't take username or args (list-files/disconnect)
    private final static short SHORT_COMMAND_LEN = 1;

    public static Command of(String clientInput) {
        String[] tokens = clientInput.split(DELIMITER);

        String command = tokens[COMMAND_TOKEN];

        if (tokens.length == SHORT_COMMAND_LEN) {
            return new Command(command, null, null);
        }

        String user = tokens[USER_TOKEN];

        List<String> arguments =
                new ArrayList<>(Arrays.asList(tokens)
                        .subList(USER_TOKEN + 1, tokens.length));

        return new Command(command, user, arguments);
    }
}

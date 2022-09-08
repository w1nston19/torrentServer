package command;

import java.nio.file.Path;

public class CommandExecutor {

    private final static String LIST_COMMAND = "list-files";
    private final static String REGISTER_COMMAND = "register";
    private final static String UNREGISTER_COMMAND = "unregister";

    private final static String FETCH_MESSAGE = "fetch";

    private final Storage storage;

    public CommandExecutor() {
        this.storage = new Storage();
    }


    public String execute(Command command) {
        return switch (command.command()) {
            case LIST_COMMAND -> list();
            case REGISTER_COMMAND -> register(command);
            case UNREGISTER_COMMAND -> unregister(command);
            default -> "The system does not support the command <%s>%n".formatted(command.command());
        };
    }

    public void remove(String ip) {
        this.storage.disconnect(ip);
    }

    private String list() {
        return storage.list();
    }

    private String register(Command command) {

        if (command.arguments().size() <= 1) {
            return "You need to specify files to register and username\n";
        }

        var address = command.arguments().get(command.arguments().size() - 1);
        command.arguments().remove(address);
        return storage.register(command.userClient(),
                command.arguments().stream().map(Path::of).toList(), address);

    }

    private String unregister(Command command) {

        if (command.arguments().size() == 0) {
            return "You need to specify files to unregister and username\n";
        }

        var address = command.arguments().get(command.arguments().size() - 1);
        command.arguments().remove(address);
        return storage.unregister(command.userClient(),
                command.arguments().stream().map(Path::of).toList(), address);

    }

}

package command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CommandExecutorTest {

    private static final String EMPTY_STORAGE = "No items currently available";
    private final static String LIST_COMMAND = "list-files";
    private final static String REGISTER_COMMAND = "register";
    private final static String UNREGISTER_COMMAND = "unregister";

    private final static String FETCH_COMMAND = "fetch";

    private final String user = "foo";
    private final String ip = "ip";

    private final String file1 = "file1", file2 = "file2";

    private final String FORMAT = " %s   %s   %s %s     ";
    private final String ARGUMENTS = FORMAT.formatted(user, file1, file2, ip);


    private CommandExecutor testedExecutor = new CommandExecutor();

    @BeforeEach
    void empty() {
        testedExecutor = new CommandExecutor();
    }

    @Test
    void testWithUnknownCommand() {
        String command = "dummyCommand";
        String output = "The system does not support the command <%s>%n"
                .formatted(command);

        assertEquals(output, testedExecutor.execute(Command.of(command)),
                "The executor should return message if the command" +
                        "is unknown");
    }


    @Test
    void testWithEmptyStorage() {
        assertEquals(EMPTY_STORAGE, testedExecutor
                        .execute(Command.of(LIST_COMMAND)),
                "<list> should return <%s> on empty storage"
                        .formatted(EMPTY_STORAGE));
        assertEquals(EMPTY_STORAGE, testedExecutor
                        .execute(Command.of(FETCH_COMMAND)),
                "<fetch> should return <%s> on empty storage"
                        .formatted(EMPTY_STORAGE));
    }

    @Test
    void testRegisterInsufficientArgs() {
        String message = "You need to specify files to register and username\n";

        assertEquals(message, testedExecutor.execute(Command.of(
                        REGISTER_COMMAND + " %s".formatted(user)
                )),
                "Register should print error message with no enough arguments");
    }

    @Test
    void testRegisterSuccessful() {
        String message = "name:" + user;

        assertEquals(message, testedExecutor.execute(
                Command.of(REGISTER_COMMAND + ARGUMENTS)
        ), "Successful registration should return <name:...> message");
    }

    void addInfo() {
        testedExecutor.execute(
                Command.of(REGISTER_COMMAND + ARGUMENTS));
    }

    @Test
    void testRegisterUnsuccessful() {
        addInfo();
        String message = "The username you are trying to register from is already by another ip\n";

        assertEquals(message, testedExecutor.execute(
                Command.of(REGISTER_COMMAND + " %s other otherIP".formatted(user))
        ), "Register should return message when registering from another ip");
    }

    @Test
    void testUnregisterInsufficientArgs() {
        String message = "You need to specify files to unregister and username\n";

        assertEquals(message, testedExecutor.execute(Command.of(
                UNREGISTER_COMMAND + " %s".formatted(user)
        )), "Unregister should return message when the arguments are not enough");
    }

    @Test
    void testUnregister() {
        addInfo();

        String message = "Unregistered 2/2 files";

        assertEquals(message, testedExecutor.execute(
                        Command.of(UNREGISTER_COMMAND + ARGUMENTS)),
                "If unregistration is successful should return message");
    }

    @Test
    void testRemove() {
        addInfo();

        testedExecutor.remove(ip);
        Storage testedStorage = testedExecutor.getStorage();

        var users = testedStorage.getActiveUsers();
        var userAddresses = testedStorage.getUserAddresses();

        assertFalse(users.containsKey(ip), "The storage should not contain ip - %s".formatted("ip"));
        assertFalse(userAddresses.containsKey(ip), "The storage should not contain ip - %s".formatted("ip"));
    }
}
package command;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommandTest {
    private final String input = "register foo bar baz";
    Command testedCommand;

    @Test
    void testFactoryMethodOf() {
        testedCommand = Command.of(input);

        assertEquals("register", testedCommand.command(), "The command is different");
        assertEquals("foo", testedCommand.userClient(), "The user is different");
        assertEquals(List.of("bar", "baz"), testedCommand.arguments(), "The command agrs is different");
    }

    @Test
    void testFactoryMethodWithShortCommand() {
        testedCommand = Command.of("list-files");
        assertEquals("list-files", testedCommand.command(), "The command is different");
        assertNull(testedCommand.userClient(), "The user should be null with short command");
        assertNull(testedCommand.arguments(), "The command agrs should be null with short command");
    }
}
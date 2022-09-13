package logger;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogTest {

    @Test
    void testToString() {
        //ToDo :: fix factory method Log
        var timestamp = LocalDateTime.now();
        String packageName = this.getClass().getPackageName();
        String message = "message";
        String expected = "%s|%s|%s%n".formatted(timestamp, packageName, message);

        Log testedLog = new Log(timestamp, packageName, message);
        assertEquals(expected, testedLog.toString(), "The toString method should return correct " +
                "string <%s> expected".formatted(expected));
    }
}
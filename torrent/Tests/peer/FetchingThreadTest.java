package peer;

import exceptions.FetchingThreadException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchingThreadTest {
    private static final SocketChannel socketChannelMock = mock(SocketChannel.class);
    private static final SocketChannel throwingMock = mock(SocketChannel.class);
    private static final Path path = Path.of("torrent/Tests/testFiles/fetchResult");

    @InjectMocks
    private static FetchingThread fetchingThread = new FetchingThread(
            path,
            socketChannelMock
    );

    @InjectMocks
    private static FetchingThread exceptionThread = new FetchingThread(Path.of("tmp"), throwingMock);

    @BeforeAll
    static void set() throws IOException {
        when(throwingMock.read(any(ByteBuffer.class)))
                .thenThrow(new RuntimeException("Exception thrown"));
    }

    private static int counter = 0;
    private static final Thread starter = new Thread(fetchingThread);

    @Test
    void testFetchingThread() {
        try {
            doAnswer(new Answer<>() {
                public Object answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    ((ByteBuffer) args[0]).clear();
                    ((ByteBuffer) args[0]).put("success-%s".formatted(counter++).getBytes());
                    return null;
                }
            }).when(socketChannelMock).read(any(ByteBuffer.class));

            starter.start();
            assertArrayEquals("success-0".getBytes(), Files.readAllBytes(path));
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    void testThrowsException() {
        assertThrows(FetchingThreadException.class, () -> exceptionThread.run());
    }
}
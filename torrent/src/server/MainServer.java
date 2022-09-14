package server;

import command.Command;
import command.CommandExecutor;
import exceptions.UndetectableLocalIPException;
import logger.Loggable;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainServer extends AbstractServer implements Server, Loggable {
    private static CommandExecutor executor;

    static {
        executor = new CommandExecutor();
    }

    private static Map<String, String> addressMapping;

    public MainServer() {
        addressMapping = new HashMap<>();
        //default;
    }

    public MainServer(Selector selector) {
        this.selector = selector;
        addressMapping = new HashMap<>();
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            establish(serverSocketChannel, getIP(), DEFAULT_PORT);

            while (isServerWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                var readyKeys = selector.selectedKeys();
                var iterator = readyKeys.iterator();

                while (iterator.hasNext()) {
                    var selectionKey = iterator.next();

                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        String input = getClientInput(socketChannel);
                        System.out.println(input + "... from ... " + socketChannel.getRemoteAddress().toString());

                        if (input == null || Objects.equals(input, "null")) {
                            String ip = socketChannel.getRemoteAddress().toString();
                            executor.remove(addressMapping.get(ip));
                            addressMapping.remove(ip);
                            continue;
                        }

                        String[] tmp = input.split(" ");
                        String actualIP = tmp[tmp.length - 1];
                        if (!actualIP.equals("fetch") && !actualIP.equals("null")) {
                            addressMapping.put(socketChannel.getRemoteAddress().toString(), actualIP);
                        }

                        String output = executor.execute(Command.of(input));
                        writeOutput(byteBuffer, socketChannel, output);
                    } else {
                        if (selectionKey.isAcceptable()) {
                            accept(selector, selectionKey);
                        }
                    }
                    iterator.remove();
                }

            }
        } catch (IOException ioException) {
            handleException(ioException,
                    " the server",
                    this.getClass());
        } catch (UndetectableLocalIPException ipException) {
            handleException(ipException,
                    "finding the ip the server should be bind to",
                    this.getClass());
        }
    }
}

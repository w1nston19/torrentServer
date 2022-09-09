package server;

import command.Command;
import command.CommandExecutor;
import exceptions.UndetectableLocalIPException;
import logger.Loggable;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class
MainServer extends AbstractServer implements Server, Loggable {
    private static final CommandExecutor executor;

    static {
        executor = new CommandExecutor();
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

                        if (input == null) {
                            String ip = socketChannel.getRemoteAddress().toString();
                            //ToDo :: Fix
                            executor.remove(ip);
                            continue;
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

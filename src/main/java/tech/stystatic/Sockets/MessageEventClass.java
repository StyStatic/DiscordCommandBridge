package tech.stystatic.Sockets;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import tech.stystatic.Main;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.EventListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
public class MessageEventClass extends ListenerAdapter implements EventListener {
    private final AsynchronousSocketChannel clientSocketChannel;

    public MessageEventClass(AsynchronousSocketChannel clientSocketChannel) {
        this.clientSocketChannel = clientSocketChannel;
    }
    public static void sendMessageAcrossSocket(AsynchronousSocketChannel client, String str) throws ExecutionException, InterruptedException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(str.getBytes()); // Put the string into the buffer
        buffer.flip(); // Reset the position and limit of the buffer
        Future<Integer> writeval = client.write(buffer);
        System.out.println("Writing to socket: " + str);
        buffer.clear(); // Reset the buffer for reuse
        writeval.get();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        final AsynchronousSocketChannel clientSocketChannel = this.clientSocketChannel;
        if (event.getAuthor() != event.getJDA().getSelfUser()) {
            String messagetext = "<" + event.getAuthor().getGlobalName() + ">" + " " + event.getMessage().getContentStripped();// Format Text

            // Append Prefix
            if (Main.prefixmode) {
                messagetext = Main.prefix + " " + messagetext;
            }

            // Send Message across socket
            try {
                sendMessageAcrossSocket(clientSocketChannel, messagetext);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            super.onMessageReceived(event);
        }
    }
}

package tech.stystatic.Sockets;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import tech.stystatic.Main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ReadThread extends Thread {
    final AsynchronousSocketChannel clientSocketChannel;
    public static MessageEventClass target;
    public static JDA jda;

    public ReadThread(AsynchronousSocketChannel clientSocketChannel, MessageEventClass target, JDA jda) {
        this.clientSocketChannel = clientSocketChannel;
        ReadThread.target = target;
        ReadThread.jda = jda;
    }

    public void run() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {
                // String Manipulation
                Future<Integer> readval = clientSocketChannel.read(buffer);
                readval.get();

                if (readval.get() >= 0) {
                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    System.out.println("Received message: " + new String(data).trim());

                    TextChannel textChannel = jda.getSelfUser().getJDA().getGuildById(Main.guildid).getTextChannelsByName(Main.channelname, true).get(0);
                    textChannel.sendMessage(new String(data).trim()).queue();

                    buffer.compact();
                }
            }
        } catch (InterruptedException | ExecutionException e) {

            // Log lost connection
            System.out.println("Connection Lost");

            // Unregister event handler
            try {
                clientSocketChannel.close(); // Close the socket
            } catch (NullPointerException | IOException ignored){ignored.printStackTrace();}
        }
    }
}

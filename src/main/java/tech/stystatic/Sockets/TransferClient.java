package tech.stystatic.Sockets;

import tech.stystatic.Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static tech.stystatic.Main.jda;

public class TransferClient extends Thread {
    public static AsynchronousSocketChannel client;
    public static MessageEventClass target; // Store ChatEventClass as target to deregister in the event of a lost connection

    int port;
    String ip;
    public static Boolean debounce;

    public void run() {
        // Get from Config
        port = Integer.parseInt(Main.port);
        ip = Main.ip;

        while (true) {
            try {
                if ((client == null) || (!client.isOpen())) {
                    // Attempt to connect to server
                    client = AsynchronousSocketChannel.open();
                    Future<Void> result = client.connect(new InetSocketAddress(ip, port));
                    result.get();

                    System.out.println("Attempted Connection");

                    // If connection is established
                    if ((client != null) && (client.isOpen())) {

                        // Start a thread to handle reading messages over socket
                        ReadThread readThread = new ReadThread(client, target, jda);
                        readThread.start();

                        // Log connections
                        System.out.println("Connection Established");

                        debounce = false;

                        // Start an Event Handler that reads in game chat and sends to the server over sockets
                        jda.addEventListener(new MessageEventClass(client));
                        ReadThread.target = target;

                    }
                }
            } catch (IOException | InterruptedException | ExecutionException executionException) { // Error Handling
                try {
                    client.close();
                } catch (NullPointerException | IOException ignored){}
                System.out.println("Connection Failed, Retrying...");
            }
        }
    }
}

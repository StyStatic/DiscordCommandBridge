package tech.stystatic.Sockets;

import tech.stystatic.Main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static tech.stystatic.Main.jda;

public class TransferServer extends Thread {

    public static AsynchronousSocketChannel client;
    public static AsynchronousServerSocketChannel server;
    public static boolean clientConnected = false;
    public static MessageEventClass target; // Store ChatEventClass as target to deregister in the event of a lost connection
    public void run() {
        try {
            while (true) {
                if (!clientConnected) {
                    // Starts server
                    server = AsynchronousServerSocketChannel.open();
                    server.bind(new InetSocketAddress(Main.ip, Integer.parseInt(Main.port)));
                    System.out.println("Server started on port " + Main.port);

                    // Accepts a new client
                    Future<AsynchronousSocketChannel> acceptCon = server.accept();
                    client = acceptCon.get();

                    if ((client != null) && (client.isOpen())) { // Checks for a connected client
                        // Log connections
                        System.out.println("New client connected: " + client.getRemoteAddress());
                        System.out.println("Connection Established");

                        // Start an Event Handler that reads in game chat and sends to the client over sockets
                        clientConnected = true;

                        jda.addEventListener(new MessageEventClass(client));
                    }
                    server.close();
                } else {
                    Thread.sleep(1000); // Wait to attempt starting new server
                }
            }
        } catch (IOException | ExecutionException | InterruptedException e) { // Error Handling
            e.printStackTrace();
            clientConnected = false;
            System.out.println("Connection Lost");
        }
    }
}

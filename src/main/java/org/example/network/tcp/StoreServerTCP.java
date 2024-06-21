package org.example.network.tcp;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.network.Processor;
import org.example.network.Receiver;
import org.example.network.Sender;
import org.example.utils.DecryptUtil;
import org.example.utils.EncryptUtil;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class StoreServerTCP {
    private static final int PORT = 2077;
    private static boolean running = true;
    private static final Processor processor;
    private static final byte[] key = "1234567812345678".getBytes();
    private static ServerSocket serverSocket;
    private static MessageHandler messageHandler = new MessageHandler(key);
    private static PacketHandler packetHandler = new PacketHandler(messageHandler);

    static {
        EncryptUtil encryptUtil = new EncryptUtil(key);
        Sender sender = new Sender(packetHandler);
        processor = new Processor(encryptUtil, sender);
    }
    public static final List<Socket> clientSockets = new ArrayList<>();

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("TCP Server started om port: " + PORT);

            while(running) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                synchronized (clientSockets) {
                    clientSockets.add(clientSocket);
                    System.out.println("Client added to the list, total clients: " + clientSockets.size());
                }
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            if(running) {
                System.err.println("Error starting TCP server!");
                e.printStackTrace();
            }
        }
    }

    public static void stopServer() {
        running = false;
        synchronized (clientSockets) {
            for (Socket socket : clientSockets) {
                try {
                    socket.close();
                } catch (Exception e) {
                    System.err.println("Error closing client socket!");
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                DecryptUtil decryptUtil = new DecryptUtil(key);
                Sender sender = new Sender(packetHandler);
                Receiver receiver = new ReceiverTCP(clientSocket, decryptUtil, processor, packetHandler, sender);
                receiver.receiveMessage();
            } catch (Exception e) {
                System.err.println("Error with client communication!");
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    synchronized (clientSockets) {
                        clientSockets.remove(clientSocket);
                        System.out.println("Client removed from the list, total clients: " + clientSockets.size());
                    }
                } catch (Exception e) {
                    System.err.println("Error closing client socket!");
                }
            }
        }
    }
}

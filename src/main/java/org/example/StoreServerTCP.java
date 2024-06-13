package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class StoreServerTCP {
    private static final int PORT = 2077;
    private static final List<Socket> clientSockets = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server started om port: " + PORT);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                synchronized (clientSocket) {
                    clientSockets.add(clientSocket);
                }
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting TCP server!");
            e.printStackTrace();
        }
    }

    public static void closeClientConnection(int clientIndex) {
        synchronized (clientSockets) {
            if (clientIndex >= 0 && clientIndex < clientSockets.size()) {
                try {
                    clientSockets.get(clientIndex).close();
                    System.out.println("Closed connection for client " + (clientIndex + 1));
                } catch (IOException e) {
                    System.err.println("Error closing client connection!");
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PacketHandler packetHandler;
        private byte[] key = "1234567812345678".getBytes();

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.packetHandler = new PacketHandler(new MessageHandler(key));
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

                while(true) {
                    try {
                        // Parse received packet
                        byte[] data = (byte[]) in.readObject();
                        Packet packet = packetHandler.parsePacket(data, key);
                        System.out.println("Received: " + new String(packet.getMessage()));

                        // Build response packet
                        byte[] resMsg = "OK".getBytes();
                        Packet resPacket = new Packet((byte) 0x13, packet.getbSrc(), packet.getbPktId(), resMsg.length, resMsg);
                        byte[] resData = packetHandler.constructPacketBytes(resPacket);
                        out.writeObject(resData);
                        out.flush();
                    } catch (EOFException e) {
                        // End of stream, close connection
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error with creating IO streams in TCP!");
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing TCP client socket!");
                }
            }
        }
    }
}

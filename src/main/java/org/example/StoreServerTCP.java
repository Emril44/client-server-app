package org.example;

import java.io.*;
import java.net.*;

public class StoreServerTCP {
    private static final int PORT = 2077;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("TCP Server started om port: " + PORT);

            while(true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting TCP server!");
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
                    // Parse received packet
                    byte[] data = (byte[]) in.readObject();
                    Packet packet = packetHandler.parsePacket(data, key);
                    System.out.println("Received: " + new String(packet.getMessage()));

                    // Build response packet
                    byte[] resMsg = "OK".getBytes();
                    Packet resPacket = new Packet((byte) 0x13, packet.getbSrc(), packet.getbPktId(), resMsg.length, resMsg);
                    byte[] resData = packetHandler.constructPacketBytes(resPacket);
                    out.writeObject(resData);
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

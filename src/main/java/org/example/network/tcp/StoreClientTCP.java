package org.example.network.tcp;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.models.Packet;

import java.net.*;
import java.io.*;
import java.util.Arrays;

public class StoreClientTCP {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2077;
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final int RETRY_DELAY_MS = 1000;

    private static PacketHandler packetHandler;
    private static byte[] key = "1234567812345678".getBytes();

    public static void main(String[] args) {
        packetHandler = new PacketHandler(new MessageHandler(key));
        connectAndCommunicate();
    }

    public static void connectAndCommunicate() {
        boolean connected = false;
        int attempt = 0;

        while (!connected && attempt < MAX_RETRY_ATTEMPTS) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                connected = true;
                System.out.println("CLIENT Connected to the server.");

                // Initial communication with server
                communicateWithServer(out, in, "GET_AMOUNT:1");
                communicateWithServer(out, in, "DEDUCT_AMOUNT:1:10");
                communicateWithServer(out, in, "ADD_AMOUNT:1:5");
                communicateWithServer(out, in, "SET_PRICE:1:49.99");

                int comNum = 5;
                // Keep communicating with the server
                for (int i = 0; i < comNum; i++) {
                    try {
                        Thread.sleep(3000); // Wait before sending the next message
                        communicateWithServer(out, in, "GET_AMOUNT:1");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Connection lost. Attempting to reconnect...");
                        connected = false; // Exit inner loop to reconnect
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println("Retry interrupted.");
                        connected = false;
                        break;
                    }
                }
            } catch (Exception e) {
                attempt++;
                System.out.println("Failed to connect. Retrying to connect... (Attempt " + attempt + ")");
                if (attempt >= MAX_RETRY_ATTEMPTS) {
                    System.out.println("Max retry attempts reached. Could not connect to the server.");
                    break;
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.out.println("Retry interrupted.");
                    break;
                }
            }
        }
    }

    private static void communicateWithServer(ObjectOutputStream out, ObjectInputStream in, String message) throws Exception {
        byte[] msg = message.getBytes();
        Packet packet = new Packet((byte) 0x13, (byte) 1, 1, msg.length, msg);
        byte[] data = packetHandler.constructPacketBytes(packet);
        out.writeObject(data);
        out.flush();

        try {
            byte[] resData = (byte[]) in.readObject();
            Packet resPacket = packetHandler.parsePacket(resData, key);
            System.out.println("Server response: " + new String(resPacket.getMessage()));
        } catch (Exception e) {
            System.err.println("Error in client receiving or processing packet!");
            e.printStackTrace();
            throw e; // Rethrow exception to handle reconnection logic
        }
    }
}

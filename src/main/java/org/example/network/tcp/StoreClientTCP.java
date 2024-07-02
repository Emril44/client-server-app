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
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public StoreClientTCP() {
        packetHandler = new PacketHandler(new MessageHandler(key));
        connect();
    }

    public void connect() {
        boolean connected = false;
        int attempt = 0;

        while (!connected && attempt < MAX_RETRY_ATTEMPTS) {
            try {
                socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                connected = true;
                System.out.println("CLIENT Connected to the server.");
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

    public String communicateWithServer(String message) throws Exception {
        byte[] msg = message.getBytes();
        Packet packet = new Packet((byte) 0x13, (byte) 1, 1, msg.length, msg);
        byte[] data = packetHandler.constructPacketBytes(packet);
        out.writeObject(data);
        out.flush();

        try {
            byte[] resData = (byte[]) in.readObject();
            Packet resPacket = packetHandler.parsePacket(resData, key);
            return new String(resPacket.getMessage());
        } catch (Exception e) {
            System.err.println("Error in client receiving or processing packet!");
            e.printStackTrace();
            throw e; // Rethrow exception to handle reconnection logic
        }
    }

    public void closeConnection() {
        try {
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket!");
            e.printStackTrace();
        }
    }
}

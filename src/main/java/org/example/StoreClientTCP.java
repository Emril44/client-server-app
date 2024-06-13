package org.example;

import java.net.*;
import java.io.*;

public class StoreClientTCP {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2077;
    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final int RETRY_DELAY_MS = 2000;

    private static PacketHandler packetHandler;
    private static byte[] key = "1234567812345678".getBytes();

    public static void main(String[] args) {
        packetHandler = new PacketHandler(new MessageHandler(key));

        boolean connected = false;
        int attempt = 0;

        while (!connected && attempt < MAX_RETRY_ATTEMPTS) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // connected; good job! :D
                connected = true;
                attempt = 0;

                byte[] msg = "SERVERING IMEDIATELY".getBytes();
                Packet packet = new Packet((byte) 0x13, (byte) 1, 1, msg.length, msg);
                byte[] data = packetHandler.constructPacketBytes(packet);
                out.writeObject(data);

                byte[] resData = (byte[]) in.readObject();
                Packet resPacket = packetHandler.parsePacket(resData, key);
                System.out.println("Server response: " + new String(resPacket.getMessage()));
            }  catch (InterruptedException e) {
                System.out.println("Client interrupted. Attempting to reconnect...");
                attempt++;
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
            } catch (Exception e) {
                attempt++;
                if (attempt >= MAX_RETRY_ATTEMPTS) {
                    System.out.println("Max retry attempts reached. Could not connect to the server.");
                    break;
                }
                System.out.println("Connection lost. Retrying to connect... (Attempt " + attempt + ")");
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
}

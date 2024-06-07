package org.example;

import java.net.*;
import java.io.*;

public class StoreClientTCP {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2077;
    private static PacketHandler packetHandler;
    private static byte[] key = "1234567812345678".getBytes();

    public static void main(String[] args) {
        packetHandler = new PacketHandler(new MessageHandler(key));

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            byte[] msg = "SERVERING IMEDIATELY".getBytes();
            Packet packet = new Packet((byte) 0x13, (byte) 1, 1, msg.length, msg);
            byte[] data = packetHandler.constructPacketBytes(packet);
            out.writeObject(data);

            byte[] resData = (byte[]) in.readObject();
            Packet resPacket = packetHandler.parsePacket(resData, key);
            System.out.println("Server response: " + new String(resPacket.getMessage()));
        } catch (Exception e) {
            System.err.println("Error with TCP client!");
            e.printStackTrace();
        }
    }
}

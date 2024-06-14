package org.example.network.udp;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.models.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreServerUDP {
    private DatagramSocket socket;
    private PacketHandler packetHandler;
    private AtomicBoolean running = new AtomicBoolean(true);

    public StoreServerUDP(int port, byte[] key) throws Exception {
        socket = new DatagramSocket(port);
        MessageHandler messageHandler = new MessageHandler(key);
        packetHandler = new PacketHandler(messageHandler);
    }

    public void start() {
        byte[] buffer = new byte[256];

        while(true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] data = packet.getData();
                Packet receivedPacket = packetHandler.parsePacket(data, "1234567812345678".getBytes());
                System.out.println("Received: " + new String(receivedPacket.getMessage()));

                // response
                byte[] message = "acknowledged".getBytes();
                Packet responsePacket = new Packet((byte) 0x13, (byte) 1, receivedPacket.getbPktId() + 1, message.length, message);
                byte[] resData = packetHandler.constructPacketBytes(responsePacket);
                DatagramPacket res = new DatagramPacket(resData, resData.length, packet.getAddress(), packet.getPort());
                socket.send(res);
            } catch (Exception e) {
                System.err.println("in StoreServerUDP!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        StoreServerUDP server = new StoreServerUDP(2078, "1234567812345678".getBytes());
        server.start();
        System.out.println("UDP server up on port 2078");
    }
}

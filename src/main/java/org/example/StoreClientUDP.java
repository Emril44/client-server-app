package org.example;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class StoreClientUDP {
    private DatagramSocket socket;
    private InetAddress address;
    private PacketHandler packetHandler;
    private long pktId = 0;

    public StoreClientUDP(String hostname, int port, byte[] key) throws Exception {
        socket = new DatagramSocket();
        address = InetAddress.getByName(hostname);
        MessageHandler messageHandler = new MessageHandler(key);
        packetHandler = new PacketHandler(messageHandler);
    }

    public void sendRequest(String req) throws Exception {
        byte[] message = req.getBytes();
        Packet packet = new Packet((byte) 0x13, (byte) 1, pktId++, message.length, message);
        byte[] data = packetHandler.constructPacketBytes(packet);
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, address, 2078);
        socket.send(datagramPacket);

        byte[] buffer = new byte[1024];
        DatagramPacket resPacket = new DatagramPacket(buffer, buffer.length);
        socket.receive(resPacket);
        Packet receivedPacket = packetHandler.parsePacket(resPacket.getData(), "1234567812345678".getBytes());
        System.out.println("Response: " + new String(receivedPacket.getMessage()));
    }

    public static void main(String[] args) throws Exception {
        StoreClientUDP client = new StoreClientUDP("localhost", 2078, "1234567812345678".getBytes());
        client.sendRequest("HI SERVER :DDDD");
    }
}

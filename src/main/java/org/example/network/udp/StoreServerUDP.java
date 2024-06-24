package org.example.network.udp;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.models.Message;
import org.example.models.Packet;
import org.example.network.Processor;
import org.example.network.Sender;
import org.example.utils.EncryptUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class StoreServerUDP {
    private DatagramSocket socket;
    private PacketHandler packetHandler;
    private Processor processor;

    public StoreServerUDP(int port, byte[] key, Processor processor) throws Exception {
        socket = new DatagramSocket(port);
        MessageHandler messageHandler = new MessageHandler(key);
        packetHandler = new PacketHandler(messageHandler);
        this.processor = processor;
    }

    public void start() {
        byte[] buffer = new byte[256];

        while(true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] data = packet.getData();
                Packet receivedPacket = packetHandler.parsePacket(data, "1234567812345678".getBytes());
                String receivedMessage = new String(receivedPacket.getMessage());
                System.out.println("Received: " + receivedMessage);

                Message message = new Message(receivedPacket.getbMagic(), receivedPacket.getbSrc(), receivedMessage.getBytes(), true);

                // response
                processor.process(message, null, packet.getAddress(), packet.getPort());
            } catch (Exception e) {
                System.err.println("in StoreServerUDP!");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        EncryptUtil encryptUtil = new EncryptUtil("1234567812345678".getBytes());
        PacketHandler packetHandler = new PacketHandler(new MessageHandler("1234567812345678".getBytes()));
        Sender sender = new Sender(packetHandler);
        Processor processor = new Processor(encryptUtil, sender);
        StoreServerUDP server = new StoreServerUDP(2078, "1234567812345678".getBytes(), processor);
        server.start();
        System.out.println("UDP server up on port 2078");
    }
}

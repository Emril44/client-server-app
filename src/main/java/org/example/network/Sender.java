package org.example.network;

import org.example.handlers.PacketHandler;
import org.example.models.Packet;

import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
    private final PacketHandler packetHandler;

    public Sender(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public void sendMessageTCP(byte[] message, ObjectOutputStream out) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            System.err.println("Error sending TCP message!");
            e.printStackTrace();
        }
    }

    // Send a message over UDP
    public void sendMessageUDP(byte[] message, InetAddress target, int port) {
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(message, message.length, target, port);
            socket.send(packet);
        } catch (Exception e) {
            System.err.println("Error sending UDP message!");
            e.printStackTrace();
        }
    }
}

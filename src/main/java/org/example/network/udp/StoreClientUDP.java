package org.example.network.udp;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.models.Packet;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class StoreClientUDP {
    private DatagramSocket socket;
    private InetAddress address;
    private PacketHandler packetHandler;
    private long pktId = 0;
    private final int timeout = 2000;
    private final int maxRetries = 3;

    public StoreClientUDP(String hostname, byte[] key) throws Exception {
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

        int retries = 0;
        boolean ack = false;
        while(retries < maxRetries && !ack) {
            socket.send(datagramPacket);

            socket.setSoTimeout(timeout);
            try {
                byte[] buffer = new byte[256];
                DatagramPacket resPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(resPacket);

                Packet res = packetHandler.parsePacket(resPacket.getData(), "1234567812345678".getBytes());
                String resMsg = new String(res.getMessage());
                if(resMsg.equals("acknowledged")) {
                    ack = true;
                    System.out.println("Packet acknowledged by server");
                }
            } catch (SocketTimeoutException e) {
                retries++;
                System.err.println("[" + retries + " OF " + maxRetries + "] No acknowledgement, retrying...");
            }
        }

        if(!ack) {
            System.err.println("Failed to acknowledge after " + maxRetries + "attempts.");
        }
    }

    public static void main(String[] args) throws Exception {
        StoreClientUDP client = new StoreClientUDP("localhost", "1234567812345678".getBytes());
        client.sendRequest("HI SERVER :DDDD");
    }
}

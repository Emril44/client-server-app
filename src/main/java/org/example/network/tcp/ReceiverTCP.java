package org.example.network.tcp;

import org.example.handlers.PacketHandler;
import org.example.models.Message;
import org.example.models.Packet;
import org.example.network.Processor;
import org.example.network.Receiver;
import org.example.network.Sender;
import org.example.utils.DecryptUtil;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ReceiverTCP implements Receiver {
    private final DecryptUtil decryptUtil;
    private final Processor processor;
    private final Socket clientSocket;
    private final PacketHandler packetHandler;
    private final Sender sender;

    public ReceiverTCP(Socket clientSocket, DecryptUtil decryptUtil, Processor processor, PacketHandler packetHandler, Sender sender) {
        this.clientSocket = clientSocket;
        this.decryptUtil = decryptUtil;
        this.processor = processor;
        this.packetHandler = packetHandler;
        this.sender = sender;
    }

    @Override
    public void receiveMessage() {
        try(ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
            while(true) {
                try {
                    byte[] packetBytes = (byte[]) in.readObject();
                    Packet packet = packetHandler.parsePacket(packetBytes, decryptUtil.getKey());
                    Message message = new Message(packet.getbMagic(), packet.getbSrc(), packet.getMessage(), false);
                    processor.process(message, out, null, 0);
                } catch (java.io.EOFException e) {
                    System.out.println("Client disconnected");
                    break; // Exit the loop on client disconnect
                } catch (Exception e) {
                    System.err.println("Error in TcpReceiver processing!");
                    e.printStackTrace();
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in ReceiverTCP setup!");
            e.printStackTrace();
        }
    }
}

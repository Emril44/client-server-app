package org.example;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class PacketHandler {
    // Packet structure constants
    private static final byte MAGIC_BYTE = 0x13;
    private static final int HEADER_LENGTH = 20; // 1+1+8+4+2+4

    // Fields for parsed data
    private byte bMagic;
    private byte bSrc;
    private long bPktId;
    private int wLen;
    private int headerCrc;
    private byte[] encryptedMsg;
    private int messageSrc;

    // Method to parse packet
    public void parsePacket(byte[] packet) throws Exception {
        // Packet length validation
        if(packet.length < HEADER_LENGTH) {
            throw new IllegalArgumentException("Packet is too smol");
        }

        // Extract & validate header fields
        bMagic = packet[0];
        bSrc = packet[1];
        bPktId = ByteBuffer.wrap(packet, 2, 8).getLong();
        wLen = ByteBuffer.wrap(packet, 10, 4).getInt();
        headerCrc = ByteBuffer.wrap(packet, 14, 2).getShort();

        // Verify magic byte
        if(bMagic != MAGIC_BYTE) {
            throw new IllegalArgumentException("Invalid magic byte");
        }

        // Verify header CRC
        byte[] headerBytes = Arrays.copyOfRange(packet, 0, 14);
        if(!verifyCrc16(headerBytes, headerCrc)) {
            throw new IllegalArgumentException("Invalid header CRC");
        }

        // Extract encrypted message and CRC
        encryptedMsg = Arrays.copyOfRange(packet, 16, 16 + wLen);
        messageSrc = ByteBuffer.wrap(packet, 16 + wLen, 2).getShort();

        // Verify message CRC
        if(!verifyCrc16(encryptedMsg, messageSrc)) {
            throw new IllegalArgumentException("Invalid message SRC");
        }
    }

    // CRC16 verification method
    private boolean verifyCrc16(byte[] data, int expectedCrc) {
        // Calculate CRC16 of data
        int calculatedCrc = calculateCrc16(data);
        return calculatedCrc == expectedCrc;
    }

    private int calculateCrc16(byte[] data) {
        // TODO: Implement calculation of CRC16
        return 0; // summon deleto to delete this shit
    }

    public byte[] decryptMessage(byte[] encryptedMsg) throws Exception {
        // TODO: use some encryption algorithm here
        return encryptedMsg; // summon deleto to delete this shit
    }

    public Message parseDecryptedMsg(byte[] decryptedMsg) {
        ByteBuffer buffer = ByteBuffer.wrap(decryptedMsg);
        int cType = buffer.getInt();
        int bUserId = buffer.getInt();
        byte[] payload = new byte[decryptedMsg.length - 8];
        buffer.get(payload);

        // Convert payload to POJO
        String json = new String(payload, StandardCharsets.UTF_8);
        return new Gson().fromJson(json, Message.class);
    }
    public static class Message {
        private int cType;
        private int bUserId;
        private String payload;

        public int getbUserId() {
            return bUserId;
        }
        public int getcType() {
            return cType;
        }
        public String getPayload() {
            return payload;
        }
        public void setbUserId(int bUserId) {
            this.bUserId = bUserId;
        }
        public void setcType(int cType) {
            this.cType = cType;
        }
        public void setPayload(String payload) {
            this.payload = payload;
        }
    }

}

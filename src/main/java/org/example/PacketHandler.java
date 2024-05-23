package org.example;

import com.google.gson.Gson;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
/*
Offset	Length	Mnemonic	Notes
00	    1	    bMagic	    Байт, що вказує на початок пакету - значення 13h (h - значить hex)
01	    1	    bSrc	    Унікальний номер клієнтського застосування
02	    8	    bPktId	    Номер повідомлення. Номер постійно збільшується. В форматі big-endian
10	    4	    wLen	    Довжина пакету даних big-endian
14	    2	    wCrc16	    CRC16 байтів (00-13) big-endian
16	    wLen    bMsq	    Message - корисне повідомлення
16+wLen	2	    wCrc16	    CRC16 байтів (16 до 16+wLen-1) big-endian*/
public class PacketHandler {
    // Packet structure constants
    private static final byte MAGIC_BYTE = 0x13;
    private static final int HEADER_LENGTH = 16; // 1+1+8+4+2

    // Fields for parsed data
    private byte bMagic;
    private byte bSrc;
    private long bPktId;
    private int wLen;
    private int headerCrc;
    private byte[] encryptedMsg;
    private int messageSrc;
    private CipherUtil cipherUtil;

    public PacketHandler(CipherUtil cipherUtil) {
        this.cipherUtil = cipherUtil;
    }

    // Method to parse packet
    public void parsePacket(byte[] packet) throws Exception {
        // Packet length validation
        if(packet.length < HEADER_LENGTH) {
            throw new IllegalArgumentException("Packet is too smol");
        }

        // Extract & validate header fields
        ByteBuffer buffer = ByteBuffer.wrap(packet);
        bMagic = buffer.get();
        bSrc = buffer.get();
        bPktId = buffer.getLong();
        wLen = buffer.getInt();
        headerCrc = buffer.getShort();

        // Verify magic byte
        if(bMagic != MAGIC_BYTE) {
            throw new IllegalArgumentException("Invalid magic byte");
        }

        // Verify header CRC
        byte[] headerBytes = Arrays.copyOfRange(packet, 0, 14);
        if(CRC16.calculate(headerBytes) != headerCrc) {
            throw new IllegalArgumentException("Invalid header CRC");
        }

        // Extract encrypted message and CRC
        encryptedMsg = Arrays.copyOfRange(packet, 16, 16 + wLen);
        messageSrc = ByteBuffer.wrap(packet, 16 + wLen, 2).getShort();

        // Verify message CRC
        if(CRC16.calculate(encryptedMsg) != messageSrc) {
            throw new IllegalArgumentException("Invalid message SRC");
        }
    }

    public byte[] decryptMessage() throws Exception {
        return cipherUtil.decrypt(encryptedMsg);
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
    /*
    Структура повідомлення (message)

    Offset	Length	Mnemonic 	Notes
    00	    4	    cType	    Код команди big-endian
    04	    4	    bUserId	    Від кого надіслане повідомлення. В системі може бути багато клієнтів. А на кожному з цих клієнтів може працювати один з багатьох працівників. big-endian
    08	    wLen-8  message	    корисна інформація, можна покласти JSON як масив байтів big-endian*/
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

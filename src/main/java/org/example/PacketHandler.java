package org.example;

import java.nio.ByteBuffer;

public class PacketHandler {
    private final MessageHandler messageHandler;

    public PacketHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public byte[] constructPacketBytes(Packet pck) throws Exception {
        EncryptUtil encryptUtil = new EncryptUtil("1234567812345678".getBytes());
        byte[] encryptedMsg = encryptUtil.encrypt(pck.getMessage());

        ByteBuffer buffer = ByteBuffer.allocate(18 + encryptedMsg.length);
        buffer.put(pck.getbMagic());
        buffer.put(pck.getbSrc());
        buffer.putLong(pck.getbPktId());
        buffer.putInt(encryptedMsg.length);
        buffer.putShort((short)CRC16.getCRC16(buffer.array(), 0, 14));
        buffer.put(encryptedMsg);
        buffer.putShort((short)CRC16.getCRC16(buffer.array(), 16, encryptedMsg.length));

        return buffer.array();
    }

    public Packet parsePacket(byte[] bytes, byte[] key) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        byte bMagic = buffer.get();
        byte bSrc = buffer.get();
        long bPktId = buffer.getLong();
        int mLen = buffer.getInt();
        short crc1 = buffer.getShort();
        byte[] msg = new byte[mLen];
        buffer.get(msg);
        short crc2 = buffer.getShort();

        if(crc1 != (short)CRC16.getCRC16(bytes, 0, 14) || crc2 != (short)CRC16.getCRC16(bytes, 16, mLen)) {
            throw new Exception("CRC16 check failed!");
        }

        DecryptUtil decryptUtil = new DecryptUtil(key);
        byte[] decodedMsg = decryptUtil.decrypt(msg);

        return new Packet(bMagic, bSrc, bPktId, decodedMsg.length, decodedMsg);
    }
}

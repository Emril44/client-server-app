package org.example;

import java.nio.ByteBuffer;

public class PacketHandler {
    private final MessageHandler messageHandler;

    public PacketHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public byte[] constructPacketBytes(Packet pck) {
        ByteBuffer buffer = ByteBuffer.allocate(18 + pck.getMessage().length);
        buffer.put(pck.getbMagic());
        buffer.put(pck.getbSrc());
        buffer.putLong(pck.getbPktId());
        buffer.putInt(pck.getmLen());
        buffer.putShort((short)CRC16.getCRC16(buffer.array(), 0, 14));
        buffer.put(pck.getMessage());
        buffer.putShort((short)CRC16.getCRC16(buffer.array(), 16, pck.getMessage().length));

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

        DecryptUtil cipherUtil = new DecryptUtil(key);
        byte[] decodedMsg = cipherUtil.decrypt(msg);

        return new Packet(bMagic, bSrc, bPktId, decodedMsg.length, decodedMsg);
    }
}

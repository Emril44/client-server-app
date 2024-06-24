package PacketTest;

import org.example.handlers.MessageHandler;
import org.example.handlers.PacketHandler;
import org.example.models.Message;
import org.example.models.Packet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessagePacketTest {

    @Test
    public void testMsgCipher() throws Exception {
        byte[] key = "1234567812345678".getBytes();
        MessageHandler messageHandler = new MessageHandler(key);

        Message msg = new Message(1, 402, "I like trains".getBytes(), false);

        byte[] encryptedMsg = messageHandler.encryptMessage(msg);
        Message decryptedMsg = messageHandler.decryptMessage(encryptedMsg);

        assertEquals(msg.getcType(), decryptedMsg.getcType());
        assertEquals(msg.getbUserId(), decryptedMsg.getbUserId());
        assertArrayEquals(msg.getMessage(), decryptedMsg.getMessage());
    }

    @Test
    public void testPacketOps() throws Exception {
        byte[] key = "1234567812345678".getBytes();
        MessageHandler messageHandler = new MessageHandler(key);
        PacketHandler packetHandler = new PacketHandler(messageHandler);

        Message message = new Message(1, 402, "I like trains".getBytes(), false);
        byte[] encryptedMsg = messageHandler.encryptMessage(message);

        byte[] msgBytes = messageHandler.toBytes(message);
        Packet origPacket = new Packet((byte) 0x13, (byte) 0x01, 12345L, msgBytes.length, msgBytes);
        Packet origPacket1 = new Packet((byte) 0x13, (byte) 0x01, 12345L, encryptedMsg.length, encryptedMsg);

        byte[] packetBytes = packetHandler.constructPacketBytes(origPacket1);
        Packet parsedPacket = packetHandler.parsePacket(packetBytes, key);

        assertEquals(origPacket1.getbMagic(), parsedPacket.getbMagic());
        assertEquals(origPacket1.getbSrc(), parsedPacket.getbSrc());
        assertEquals(origPacket1.getbPktId(), parsedPacket.getbPktId());
        assertEquals(origPacket1.getmLen(), parsedPacket.getmLen());
        assertArrayEquals(origPacket1.getMessage(), parsedPacket.getMessage());
    }
}

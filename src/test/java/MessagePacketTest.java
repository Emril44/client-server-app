import org.example.Message;
import org.example.MessageHandler;
import org.example.Packet;
import org.example.PacketHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessagePacketTest {

    @Test
    public void testMsgCipher() throws Exception {
        byte[] key = "1028564026592835".getBytes();
        MessageHandler messageHandler = new MessageHandler(key);

        Message msg = new Message(1, 402, "I like trains".getBytes());

        byte[] encryptedMsg = messageHandler.encryptMessage(msg);
        Message decryptedMsg = messageHandler.decryptMessage(encryptedMsg);

        assertEquals(msg.getcType(), decryptedMsg.getcType());
        assertEquals(msg.getbUserId(), decryptedMsg.getbUserId());
        assertArrayEquals(msg.getMessage(), decryptedMsg.getMessage());
    }

    @Test
    public void testPacketOps() throws Exception {
        byte[] key = "1028564026592835".getBytes();
        MessageHandler messageHandler = new MessageHandler(key);
        PacketHandler packetHandler = new PacketHandler(messageHandler);

        Message message = new Message(1, 402, "I like trains".getBytes());
        byte[] encryptedMsg = messageHandler.encryptMessage(message);

        byte[] msgBytes = messageHandler.toBytes(message);
        Packet origPacket = new Packet((byte) 0x13, (byte) 0x01, 12345L, msgBytes.length, msgBytes);
        Packet origPacket1 = new Packet((byte) 0x13, (byte) 0x01, 12345L, encryptedMsg.length, encryptedMsg);

        byte[] packetBytes = packetHandler.constructPacketBytes(origPacket1);
        Packet parsedPacket = packetHandler.parsePacket(packetBytes, key);

        assertEquals(origPacket1.getbMagic(), parsedPacket.getbMagic());
        assertEquals(origPacket1.getbSrc(), parsedPacket.getbSrc());
        assertEquals(origPacket1.getbPktId(), parsedPacket.getbPktId());
        assertEquals(origPacket.getmLen(), parsedPacket.getmLen());
        assertArrayEquals(origPacket.getMessage(), parsedPacket.getMessage());
    }
}

package org.example.handlers;

import org.example.utils.DecryptUtil;
import org.example.utils.EncryptUtil;
import org.example.models.Message;

import java.nio.ByteBuffer;

public class MessageHandler {
    private final EncryptUtil encryptUtil;
    private final DecryptUtil decryptUtil;

    public MessageHandler(byte[] key) {
        this.encryptUtil = new EncryptUtil(key);
        this.decryptUtil = new DecryptUtil(key);
    }

    public byte[] encryptMessage(Message message) throws Exception {
        return encryptUtil.encrypt(toBytes(message));
    }

    public byte[] toBytes(Message message) {
        ByteBuffer buffer = ByteBuffer.allocate(9 + message.getMessage().length);
        buffer.putInt(message.getcType());
        buffer.putInt(message.getbUserId());
        buffer.put((byte) (message.isUDP() ? 1 : 0));
        buffer.put(message.getMessage());
        return buffer.array();
    }

    public Message decryptMessage(byte[] encryptedMsg) throws Exception {
        byte[] decryptedMsg = decryptUtil.decrypt(encryptedMsg);
        ByteBuffer buffer = ByteBuffer.wrap(decryptedMsg);
        int cType = buffer.getInt();
        int bUserId = buffer.getInt();
        boolean isUDP = buffer.get() == 1;
        byte[] msgContents = new byte[decryptedMsg.length - 9];
        buffer.get(msgContents);
        return new Message(cType, bUserId, msgContents, isUDP);
    }
}

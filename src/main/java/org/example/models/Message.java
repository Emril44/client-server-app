package org.example.models;

/*
    Структура повідомлення (message)

    Offset	Length	Mnemonic 	Notes
    00	    4	    cType	    Код команди big-endian
    04	    4	    bUserId	    Від кого надіслане повідомлення. В системі може бути багато клієнтів. А на кожному з цих клієнтів може працювати один з багатьох працівників. big-endian
    08	    wLen-8  message	    корисна інформація, можна покласти JSON як масив байтів big-endian
*/
public class Message {
    private final int cType;
    private final int bUserId;
    private final byte[] message;
    private boolean isUDP;

    public Message(int cType, int bUserID, byte[] payload, boolean isUDP) {
        this.message = payload;
        this.cType = cType;
        this.bUserId = bUserID;
        this.isUDP = isUDP;
    }

    public int getbUserId() {
        return bUserId;
    }
    public int getcType() {
        return cType;
    }
    public byte[] getMessage() {
        return message;
    }

    public boolean isUDP() {
        return isUDP;
    }
}

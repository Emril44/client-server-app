package org.example;

/*
    Структура повідомлення (message)

    Offset	Length	Mnemonic 	Notes
    00	    4	    cType	    Код команди big-endian
    04	    4	    bUserId	    Від кого надіслане повідомлення. В системі може бути багато клієнтів. А на кожному з цих клієнтів може працювати один з багатьох працівників. big-endian
    08	    wLen-8  message	    корисна інформація, можна покласти JSON як масив байтів big-endian
*/
public class Message {
    public final int cType;
    public final int bUserId;
    public final byte[] message;

    public Message(int cType, int bUserID, byte[] payload) {
        this.message = payload;
        this.cType = cType;
        this.bUserId = bUserID;
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
}

package org.example;

/*
Offset	Length	Mnemonic	Notes
00	    1	    bMagic	    Байт, що вказує на початок пакету - значення 13h (h - значить hex)
01	    1	    bSrc	    Унікальний номер клієнтського застосування
02	    8	    bPktId	    Номер повідомлення. Номер постійно збільшується. В форматі big-endian
10	    4	    wLen	    Довжина пакету даних big-endian
14	    2	    wCrc16	    CRC16 байтів (00-13) big-endian
16	    wLen    bMsq	    Message - корисне повідомлення
16+wLen	2	    wCrc16	    CRC16 байтів (16 до 16+wLen-1) big-endian
*/
public class Packet {
    private final byte bMagic;
    private final byte bSrc;
    private final long bPktId;
    private final int mLen;
    private final byte[] message;
    public Packet(byte bMagic, byte bSrc, long bPktId, int mLen, byte[] message) {
        this.bMagic = bMagic;
        this.bSrc = bSrc;
        this.bPktId = bPktId;
        this.mLen = mLen;
        this.message = message;
    }

    public byte getbMagic() {
        return bMagic;
    }
    public byte getbSrc() {
        return bSrc;
    }

    public long getbPktId() {
        return bPktId;
    }

    public int getmLen() {
        return mLen;
    }

    public byte[] getMessage() {
        return message;
    }
}

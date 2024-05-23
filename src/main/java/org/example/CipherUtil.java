package org.example;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class CipherUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORM = "AES/ECB/PKCS5Padding";
    private SecretKey secretKey;

    public CipherUtil() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        secretKey = keyGen.generateKey();
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
}

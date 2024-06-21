package org.example.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;

public class EncryptUtil {
    private Key key;

    public EncryptUtil(byte[] key) {
        this.key = new SecretKeySpec(key, "AES");
    }

    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedData = cipher.doFinal(data);
        System.out.println("Encrypting data: " + Arrays.toString(data));
        System.out.println("Encrypted data: " + Arrays.toString(encryptedData));
        return encryptedData;
    }
}

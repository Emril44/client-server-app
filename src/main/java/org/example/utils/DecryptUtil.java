package org.example.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Arrays;

public class DecryptUtil {
    private Key key;

    public DecryptUtil(byte[] key) {
        this.key = new SecretKeySpec(key, "AES");
    }

    public byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedData = cipher.doFinal(data);
        System.out.println("Decrypting data: " + Arrays.toString(data));
        System.out.println("Decrypted data: " + Arrays.toString(decryptedData));
        return decryptedData;
    }

    public byte[] getKey() {
        return this.key.getEncoded();
    }
}

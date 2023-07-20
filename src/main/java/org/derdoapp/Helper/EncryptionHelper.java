package org.derdoapp.Helper;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionHelper {

    /*private static String KEY = "derdoencryptionkey";
    private static String AES = "RSA/ECB/PKCS1Padding";

    private static Key getAesKey() {
        Key aesKey = new SecretKeySpec(KEY.getBytes(), AES);
        return aesKey;
    }

    private static Cipher getCipher() throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        return cipher;
    }

    public static String encryptMessage(String message) throws Exception {
        Key aesKey = getAesKey();
        Cipher cipher = getCipher();
        // encrypt the text
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(message.getBytes());
        return new String(encrypted);
    }

    public static String decryptMessage(String encryptedMessage) throws Exception {

        Key aesKey = getAesKey();
        Cipher cipher = getCipher();
        // decrypt the text

        byte[] encrypted = encryptedMessage.getBytes();
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        String decrypted = new String(cipher.doFinal(encrypted));

        return decrypted;
    }*/

    private static final String SECRET_KEY_STR = "derdoscretkey";
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final String ALGORITHM = "AES";

    private static void prepareSecreteKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt) {
        try {
            prepareSecreteKey(SECRET_KEY_STR);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public static String decrypt(String strToDecrypt) {
        try {
            prepareSecreteKey(SECRET_KEY_STR);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}

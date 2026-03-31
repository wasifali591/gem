package in.grse.gem.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";

    /**
     * Encrypt plain text using AES-128 ECB PKCS5Padding
     */
    public static String encrypt(String data, String secretKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(getKey(secretKey), ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(data.getBytes());

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting", e);
        }
    }

    /**
     * Decrypt AES-128 ECB PKCS5Padding encrypted string
     */
    public static String decrypt(String encryptedData, String secretKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(getKey(secretKey), ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decoded = Base64.getDecoder().decode(encryptedData);

            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted);

        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting", e);
        }
    }

    /**
     * Ensure key is exactly 16 bytes (AES-128)
     */
    private static byte[] getKey(String key) {
        byte[] keyBytes = new byte[16]; // AES-128 requires 16 bytes
        byte[] parameterKeyBytes = key.getBytes();

        int length = Math.min(parameterKeyBytes.length, keyBytes.length);
        System.arraycopy(parameterKeyBytes, 0, keyBytes, 0, length);

        return keyBytes;
    }
}

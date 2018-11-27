package utils.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class AESUtil {
    private static final String SALT = "COMMON_SALT";
    private static final int ITERATION_CNT = 1000;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] generateKeyByPassword(String password, CryptoUtil.CryptoBit cryptoBit) throws Exception {
        //128bit(16*8bit), 192bit(24), 256bit(32)
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        //password, salt, iteration count, bit
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(), ITERATION_CNT, cryptoBit.toValue());
        return secretKeyFactory.generateSecret(keySpec).getEncoded();
    }

    public static String encryptGCM(String plainText, String password, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] key = generateKeyByPassword(password, cryptoBit);
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));
            //Optional associated data (for instance meta data)
            byte[] associatedData = null;
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }

            return Base64Util.encode(cipher.doFinal(plainText.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptGCM(String base64CipherText, String password, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] cipherText = Base64Util.decode(base64CipherText);
            byte[] key = generateKeyByPassword(password, cryptoBit);
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));

            return new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

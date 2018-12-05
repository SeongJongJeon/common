package com.common.core.utils.crypto.symmetric;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import com.common.core.utils.crypto.Base64Util;
import com.common.core.utils.crypto.CryptoUtil;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Arrays;

import static com.common.core.utils.crypto.CryptoUtil.CHAR_SET;

/**
 * 대칭키 알고리즘.
 * - KEY 및 IV(Initialization Vector)는 랜덤하게 추출하지만 Password 를 이용하여 사용할 수 있도록 함. (PBKDF2 알고리즘 사용)
 * - 운영보드(Operation Mode)는 ECB(사용하면 안), CBC, GCM 중 가장 좋은 GCM 사용.
 */
@Slf4j
public class AESUtil {
    private static final String SALT = "COMMON_SALT";
    private static final int ITERATION_CNT = 1000;  //충분한 반복을 해줘야 안전함.

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] generateKeyByPassword(String password, CryptoUtil.CryptoBit cryptoBit) throws Exception {
        //128bit(16*8bit), 192bit(24), 256bit(32)
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        //PBKDF2 (Password-Based Key Derivation Function 2) : 비밀번호 기반으로 random key 생성.
        //password, salt, iteration count, bit
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), SALT.getBytes(), ITERATION_CNT, cryptoBit.toValue());
        return secretKeyFactory.generateSecret(keySpec).getEncoded();
    }

    public static String encryptGCM(String plainText, String password, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] key = generateKeyByPassword(password, cryptoBit);
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));
            //Optional associated data (for instance meta data)
            byte[] associatedData = null;
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }

            return Base64Util.encode(cipher.doFinal(plainText.getBytes(CHAR_SET)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String decryptGCM(String base64CipherText, String password, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] cipherText = Base64Util.decode(base64CipherText);
            byte[] key = generateKeyByPassword(password, cryptoBit);
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));

            return new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String encryptByKey(String plainText, byte[] key, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));
            //Optional associated data (for instance meta data)
            byte[] associatedData = null;
            if (associatedData != null) {
                cipher.updateAAD(associatedData);
            }

            return Base64Util.encode(cipher.doFinal(plainText.getBytes(CHAR_SET)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String decryptByKey(String base64CipherText, byte[] key, CryptoUtil.CryptoBit cryptoBit) {
        try {
            byte[] cipherText = Base64Util.decode(base64CipherText);
            byte[] iv = Arrays.copyOfRange(key, key.length - 12, key.length);   //For GCM (IV = Initial Vector)

            SecretKey secretKey = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(cryptoBit.toValue(), iv));

            return new String(cipher.doFinal(cipherText), "UTF-8");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

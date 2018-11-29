package utils.crypto.asymmetric;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import utils.crypto.Base64Util;

import javax.crypto.Cipher;
import java.security.PrivateKey;
import java.security.PublicKey;

import static utils.crypto.CryptoUtil.CHAR_SET;

/**
 * 비대칭키 알고리즘.
 * - ECC (Elliptic Curve Cryptography) : 타원곡선 암호
 * - ECDSA (Elliptic Curve Digital Signature Algorithm) : 전자서명 (즉 ECC를 이용하여 전자서명을 위해 사용한 것을 ECDSA라고 함.)
 * - ECDH (Elliptic Curve Diff-Hellman) : 키교환알고즘 (자신의 Private Key와 상대의 Public Key를 이용하여 공통된 Secret키를 생성하여 AES 같은 대칭키 알고리즘으로 메시지를 교환할 수 있다.)
 * - ECIES (Elliptic Curve Integrated Encryption scheme) : 통합암호화 방식 (RSA와 같이 수신자의 Public Key로 암호화 하고 수신자의 Private Key로 복호화 한다.)
 * <p>
 * - 수신자의 Public Key를 이용하여 메시지를 암호화한다.
 * - 수신자의 Private Key를 이용하여 메시지를 복호화한다.
 */
@Slf4j
public class ECIESUtil {
    private static final String provider = BouncyCastleProvider.PROVIDER_NAME;

    public static String encrypt(PublicKey publicKeyOfReceiver, String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("ECIES", provider);
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyOfReceiver);
            return Base64Util.encode(cipher.doFinal(plainText.getBytes(CHAR_SET)));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String decrypt(PrivateKey privKey, String base64CipherText) {
        try {
            byte[] cipherText = Base64Util.decode(base64CipherText);

            Cipher cipher = Cipher.getInstance("ECIES", provider);
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            return new String(cipher.doFinal(cipherText), CHAR_SET);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

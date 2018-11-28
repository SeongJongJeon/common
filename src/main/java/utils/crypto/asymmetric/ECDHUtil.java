package utils.crypto.asymmetric;

import utils.crypto.CryptoUtil;

import javax.crypto.KeyAgreement;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 비대칭키 알고리즘.
 * - ECC (Elliptic Curve Cryptography) : 타원곡선 암호
 * - ECDSA (Elliptic Curve Digital Signature Algorithm) : 전자서명 (즉 ECC를 이용하여 전자서명을 위해 사용한 것을 ECDSA라고 함.)
 * - ECDH (Elliptic Curve Diff-Hellman) : 키교환알고즘 (RSA의 diff-hellman 키교환 알고리즘과 마찬가지로 키교환을 위한 알고리즘 이다.)
 */
public class ECDHUtil {
    public static KeyPair generateKey(CryptoUtil.ECCAlgorithm eccAlgorithm) {
        return ECDSAUtil.generateKey(eccAlgorithm);
    }

    public static PublicKey generatePubStringToPubKey(String pubString) {
        return ECDSAUtil.generatePubStringToPubKey(pubString);
    }

    /**
     * 자신의 Private Key로 다른 사람이 전송한 Public Key를 동의한다.
     *
     * @param privKey
     */
    public static byte[] generateSharedSecret(PrivateKey privKey, PublicKey otherPublicKey) {
        try {
            //자신의 Private Key로 다른 사람이 전송한 Public Key를 동의한다.
            KeyAgreement ka = KeyAgreement.getInstance("ECDH");
            ka.init(privKey);
            ka.doPhase(otherPublicKey, true);

            return ka.generateSecret();
        } catch (Exception e) {

        }
        return null;
    }
}

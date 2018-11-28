package utils.crypto.asymmetric;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.KeyAgreement;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 비대칭키 알고리즘.
 * - ECC (Elliptic Curve Cryptography) : 타원곡선 암호
 * - ECDSA (Elliptic Curve Digital Signature Algorithm) : 전자서명 (즉 ECC를 이용하여 전자서명을 위해 사용한 것을 ECDSA라고 함.)
 * - ECDH (Elliptic Curve Diff-Hellman) : 키교환알고즘 (자신의 Private Key와 상대의 Public Key를 이용하여 공통된 Secret키를 생성하여 AES 같은 대칭키 알고리즘으로 메시지를 교환할 수 있다.)
 * - ECIES (Elliptic Curve Integrated Encryption scheme) : 통합암호화 방식 (RSA와 같이 수신자의 Public Key로 암호화 하고 수신자의 Private Key로 복호화 한다.)
 * <p>
 * - 자신의 Private Key와 상대의 Public Key를 이용하여 공통된 Key 값을 추출한다.
 * - 상대도 상대의 Private Key와 나의 Public Key를 이용하여 같은 Key 값을 추출할 수 있다.
 * - 이 Key를 이용하여 AES와 같이 대칭키 알고리즘을 이용하여 메시지를 암, 복호화 할 수 있다.
 */
@Slf4j
public class ECDHUtil {
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
            log.error(e.getMessage());
        }
        return null;
    }
}

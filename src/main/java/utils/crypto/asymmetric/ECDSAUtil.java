package utils.crypto.asymmetric;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import utils.crypto.CryptoUtil;
import utils.crypto.HexUtil;
import utils.crypto.ShaUtil;

import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 비대칭키 알고리즘.
 * - ECC (Elliptic Curve Cryptography) : 타원곡선 암호
 * - ECDSA (Elliptic Curve Digital Signature Algorithm) : 전자서명 (즉 ECC를 이용하여 전자서명을 위해 사용한 것을 ECDSA라고 함.)
 * - ECDH (Elliptic Curve Diff-Hellman) : 키교환알고즘 (자신의 Private Key와 상대의 Public Key를 이용하여 공통된 Secret키를 생성하여 AES 같은 대칭키 알고리즘으로 메시지를 교환할 수 있다.)
 * - ECIES (Elliptic Curve Integrated Encryption scheme) : 통합암호화 방식 (RSA와 같이 수신자의 Public Key로 암호화 하고 수신자의 Private Key로 복호화 한다.)
 * <p>
 * - 전사서명에 사용
 * - 비밀키로 서명하고 공개키로 검증한다.
 */
@Slf4j
public class ECDSAUtil {
    private static final String provider = BouncyCastleProvider.PROVIDER_NAME;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair generateKey(CryptoUtil.ECCAlgorithm eccAlgorithm) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            //EC, ECDSA, ECDH 모두 동일함
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", provider);
            keyPairGenerator.initialize(new ECGenParameterSpec(eccAlgorithm.toValue()), secureRandom);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * PubKey의 Curve x point 로부터 계정의 주소값을 추출하기 위해 사용함.
     *
     * @param pubKey
     * @param eccAlgorithm
     * @return
     */
    public static ECPoint generateECPoint(PublicKey pubKey, CryptoUtil.ECCAlgorithm eccAlgorithm) {
        if (pubKey instanceof BCECPublicKey) {
            return ((BCECPublicKey) pubKey).getQ();
        } else if (pubKey instanceof ECPublicKey) {
            X9ECParameters params = SECNamedCurves.getByName(eccAlgorithm.toValue());
            ECDomainParameters curve = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());

            java.security.spec.ECPoint publicPointW = ((ECPublicKey) pubKey).getW();

            return curve.getCurve().createPoint(publicPointW.getAffineX(), publicPointW.getAffineY());
        }
        return null;
    }

    /**
     * PubKey 로부터 sha256 변환뒤 뒤의 20자리를 hex 값으로 변환하여 리턴
     *
     * @param pubKey
     * @return
     */
    public static String getHexOfPubKey(byte[] pubKey) {
        int from = 12;
        byte[] shaData = ShaUtil.sha256(pubKey);
        byte[] copy = new byte[shaData.length - from];
        System.arraycopy(shaData, from, copy, 0, shaData.length - from);

        return HexUtil.encodeString(copy);
    }

    /**
     * HexString 값을 PrivateKey 변환
     *
     * @param privateString
     * @return
     */
    public static PrivateKey generatePrivateStringToPrivateKey(String privateString) {
        PrivateKey privateKey = null;
        try {
            KeyFactory fact = KeyFactory.getInstance("EC", provider);
            privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(HexUtil.decodeHex(privateString.toCharArray())));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return privateKey;
    }

    /**
     * HexString 값을 PublicKey 변환
     *
     * @param pubString
     * @return
     */
    public static PublicKey generatePubStringToPubKey(String pubString) {
        PublicKey publicKey = null;
        try {
            KeyFactory fact = KeyFactory.getInstance("EC", provider);
            publicKey = fact.generatePublic(new X509EncodedKeySpec(HexUtil.decodeHex(pubString.toCharArray())));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return publicKey;
    }

    /**
     * public key 로 서명값 검증
     *
     * @param eccSigAlgorithm
     * @param plaintext
     * @param signature
     * @param hexPublicKey
     * @return
     */
    public static boolean verifySignature(CryptoUtil.ECCSigAlgorithm eccSigAlgorithm, String plaintext, String signature, String hexPublicKey) {
        boolean isVerify = false;
        try {
            PublicKey publicKey = generatePubStringToPubKey(hexPublicKey);
            Signature sig = Signature.getInstance(eccSigAlgorithm.toValue());
            sig.initVerify(publicKey);
            sig.update(plaintext.getBytes());
            isVerify = sig.verify(HexUtil.decodeHex(signature.toCharArray()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return isVerify;
    }

    /**
     * private key 로 signature 생성
     *
     * @param eccSigAlgorithm
     * @param plainText
     * @param hexPrivateKey
     * @return
     */
    public static String signature(CryptoUtil.ECCSigAlgorithm eccSigAlgorithm, String plainText, String hexPrivateKey) {
        byte[] signature = null;
        try {
            Signature sig = Signature.getInstance(eccSigAlgorithm.toValue());
            sig.initSign(generatePrivateStringToPrivateKey(hexPrivateKey));
            sig.update(plainText.getBytes());
            signature = sig.sign();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return HexUtil.encodeString(signature);
    }
}

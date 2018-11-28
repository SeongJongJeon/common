package utils.crypto.asymmetric;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import utils.crypto.CryptoUtil;
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
 * - ECDH (Elliptic Curve Diff-Hellman) : 키교환알고즘 (RSA의 diff-hellman 키교환 알고리즘과 마찬가지로 키교환을 위한 알고리즘 이다.)
 * - 전사서명에 사용
 * - 비밀키로 서명하고 공개키로 검증한다.
 */
public class ECDSAUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair generateKey(CryptoUtil.ECCAlgorithm eccAlgorithm) {
        try {
            SecureRandom secureRandom = new SecureRandom();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(new ECGenParameterSpec(eccAlgorithm.toValue()), secureRandom);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
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

        return generateHexString(copy);
    }

    /**
     * PrivateKey 또는 PublicKey 값을 HexString으로 변환
     *
     * @param data
     * @return
     */
    public static String generateHexString(byte[] data) {
        return Hex.encodeHexString(data);
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
            KeyFactory fact = KeyFactory.getInstance("EC");
            privateKey = fact.generatePrivate(new PKCS8EncodedKeySpec(Hex.decodeHex(privateString.toCharArray())));
        } catch (Exception e) {
            e.printStackTrace();
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
            KeyFactory fact = KeyFactory.getInstance("EC");
            publicKey = fact.generatePublic(new X509EncodedKeySpec(Hex.decodeHex(pubString.toCharArray())));
        } catch (Exception e) {
            e.printStackTrace();
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
            isVerify = sig.verify(Hex.decodeHex(signature.toCharArray()));
        } catch (Exception e) {
            e.printStackTrace();
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
            StringBuffer mStringBuffer = new StringBuffer();
            Signature sig = Signature.getInstance(eccSigAlgorithm.toValue());
            sig.initSign(generatePrivateStringToPrivateKey(hexPrivateKey));
            sig.update(plainText.getBytes());
            signature = sig.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Hex.encodeHexString(signature);
    }
}

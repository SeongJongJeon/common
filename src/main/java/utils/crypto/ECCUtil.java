package utils.crypto;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 비대칭키 알고리즘. (=ECDSA)
 * - 전사서명에 사용
 * - 비밀키로 서명하고 공개키로 검증한다.
 */
public class ECCUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair generateKey(CryptoUtil.ECCAlgorithm eccAlgorithm) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(new ECGenParameterSpec(eccAlgorithm.toValue()));
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

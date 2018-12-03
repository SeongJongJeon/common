package utils.crypto.asymmetric;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.HMacDSAKCalculator;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.math.ec.ECPoint;
import utils.crypto.CryptoUtil;
import utils.crypto.HexUtil;
import utils.crypto.ShaUtil;

import java.math.BigInteger;
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
    private static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME;
    //bouncycastle에서는 다음의 3가지를 지원함. secp256r1, secp128k1
    //secp256k1 보다 secp256r1 알고리즘이 좀더 안전하다고 함. (이더리움 및 비트코인은 secp256k1 사용)
    private static final String ECC_ALGORITHM = "secp256k1";

    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final ECDomainParameters CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
    static final BigInteger HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static KeyPair generateKey() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            //EC, ECDSA, ECDH 모두 동일함
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", PROVIDER);
            keyPairGenerator.initialize(new ECGenParameterSpec(ECC_ALGORITHM), secureRandom);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * PubKey의 Curve x point 로부터 계정의 주소값을 추출하기 위해 사용함.
     *
     * @param pubKey
     * @return
     */
    public static ECPoint generateECPoint(PublicKey pubKey) {
        if (pubKey instanceof BCECPublicKey) {
            return ((BCECPublicKey) pubKey).getQ();
        } else if (pubKey instanceof ECPublicKey) {
            X9ECParameters params = SECNamedCurves.getByName(ECC_ALGORITHM);
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
            KeyFactory fact = KeyFactory.getInstance("EC", PROVIDER);
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
            KeyFactory fact = KeyFactory.getInstance("EC", PROVIDER);
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
     * @param plainText       내부적으로 Sha256으로 해시
     * @param hexPrivateKey
     * @return
     */
    public static String signature(CryptoUtil.ECCSigAlgorithm eccSigAlgorithm, String plainText, String hexPrivateKey) {
        byte[] signature = null;
        try {
            Signature sig = Signature.getInstance(eccSigAlgorithm.toValue());
            sig.initSign(generatePrivateStringToPrivateKey(hexPrivateKey));
            sig.update(ShaUtil.sha256(plainText));
            signature = sig.sign();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return HexUtil.encodeString(signature);
    }

    /**
     * private key 로 signature 생성 (메시지 서명의 결과 R,S 리턴)
     *
     * @param plainText     내부적으로 Sha256으로 해시
     * @param hexPrivateKey
     * @return
     */
    public static BigInteger[] signatureToRS(String plainText, String hexPrivateKey) {
        BigInteger[] components = null;
        try {
            BCECPrivateKey privateKey = (BCECPrivateKey) generatePrivateStringToPrivateKey(hexPrivateKey);

            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(privateKey.getD(), CURVE);
            signer.init(true, privKey);
            components = signer.generateSignature(ShaUtil.sha256(plainText));

            BigInteger s = components[1];
            if (s.compareTo(HALF_CURVE_ORDER) <= 0) {
                components[1] = CURVE.getN().subtract(s);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return components;
    }

//    public static BigInteger recoverFromSignature(int recId, ECDSASignature sig, byte[] message) {
//
//    }
}

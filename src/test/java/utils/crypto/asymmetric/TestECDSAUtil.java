package utils.crypto.asymmetric;

import org.junit.Test;
import utils.crypto.CryptoUtil;
import utils.crypto.HexUtil;

import java.math.BigInteger;
import java.security.KeyPair;

import static org.junit.Assert.assertEquals;

public class TestECDSAUtil {
    @Test
    public void signatureWithVerification() {
        KeyPair keyPair = ECDSAUtil.generateKey();
        String hexPrivKey = HexUtil.encodeString(keyPair.getPrivate().getEncoded());
        String hexPubKey = HexUtil.encodeString(keyPair.getPublic().getEncoded());

        String sig = ECDSAUtil.signature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", hexPrivKey);
        boolean isSuccess = ECDSAUtil.verifySignature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", sig, hexPubKey);
        assertEquals(true, isSuccess);

        System.out.println(ECDSAUtil.getHexOfPubKey(keyPair.getPublic().getEncoded()));
    }

    /**
     * ref. https://github.com/web3j/web3j/blob/master/crypto/src/main/java/org/web3j/crypto/ECKeyPair.java
     */
    @Test
    public void recoverPubKeyFromSig() {
        String plainText = "test 1";
        KeyPair keyPair = ECDSAUtil.generateKey();
        String hexPrivKey = HexUtil.encodeString(keyPair.getPrivate().getEncoded());
        String hexPubKey = HexUtil.encodeString(keyPair.getPublic().getEncoded());

        BigInteger[] rs = ECDSAUtil.signatureToRS(plainText, hexPrivKey);
        System.out.println(rs[0]);
        System.out.println(rs[1]);
//        BigInteger privKey = ((BCECPrivateKey) keyPair.getPrivate()).getD();
//        byte[] publicKeyBytes = ((BCECPublicKey) keyPair.getPublic()).getQ().getEncoded(false);
//        BigInteger pubKey = new BigInteger(1, Arrays.copyOfRange(publicKeyBytes, 1, publicKeyBytes.length));
        /*
        String hexPrivKey = HexUtil.encodeString(keyPair.getPrivate().getEncoded());
        String hexPubKey = HexUtil.encodeString(keyPair.getPublic().getEncoded());

        ECDSAUtil.ECDSASignature sig = ECDSAUtil.signatureRaw("아이고~", hexPrivKey);

        PublicKey recoverPubKey = ECDSAUtil.recoverFromSignature(sig, ShaUtil.sha256("아이고~"));
        System.out.println(hexPubKey);
        System.out.println(HexUtil.encodeString(recoverPubKey.getEncoded()));*/
    }
}
package utils.crypto.asymmetric;

import org.junit.Test;
import utils.crypto.CryptoUtil;
import utils.crypto.HexUtil;
import utils.crypto.ShaUtil;

import java.security.KeyPair;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;

public class TestECDSAUtil {
    @Test
    public void signatureWithVerification() {
        KeyPair keyPair = ECDSAUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        String hexPrivKey = HexUtil.encodeString(keyPair.getPrivate().getEncoded());
        String hexPubKey = HexUtil.encodeString(keyPair.getPublic().getEncoded());

        String sig = ECDSAUtil.signature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", hexPrivKey);
        boolean isSuccess = ECDSAUtil.verifySignature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", sig, hexPubKey);
        assertEquals(true, isSuccess);

        System.out.println(ECDSAUtil.getHexOfPubKey(keyPair.getPublic().getEncoded()));
    }

    @Test
    public void recoverPubKeyFromSig() {
        /*KeyPair keyPair = ECDSAUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        String hexPrivKey = HexUtil.encodeString(keyPair.getPrivate().getEncoded());
        String hexPubKey = HexUtil.encodeString(keyPair.getPublic().getEncoded());

        ECDSAUtil.ECDSASignature sig = ECDSAUtil.signatureRaw("아이고~", hexPrivKey);

        PublicKey recoverPubKey = ECDSAUtil.recoverFromSignature(sig, ShaUtil.sha256("아이고~"));
        System.out.println(hexPubKey);
        System.out.println(HexUtil.encodeString(recoverPubKey.getEncoded()));*/
    }
}
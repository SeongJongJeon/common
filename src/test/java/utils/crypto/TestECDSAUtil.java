package utils.crypto;

import org.junit.Test;
import utils.crypto.asymmetric.ECDSAUtil;

import java.security.KeyPair;

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
}

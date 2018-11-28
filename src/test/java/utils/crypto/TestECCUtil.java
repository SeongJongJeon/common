package utils.crypto;

import org.junit.Test;
import utils.crypto.asymmetric.ECCUtil;

import java.security.KeyPair;

import static org.junit.Assert.assertEquals;

public class TestECCUtil {
    @Test
    public void signatureWithVerification() {
        KeyPair keyPair = ECCUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        String hexPrivKey = ECCUtil.generateHexString(keyPair.getPrivate().getEncoded());
        String hexPubKey = ECCUtil.generateHexString(keyPair.getPublic().getEncoded());

        String sig = ECCUtil.signature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", hexPrivKey);
        boolean isSuccess = ECCUtil.verifySignature(CryptoUtil.ECCSigAlgorithm.SHA256, "아이고~", sig, hexPubKey);
        assertEquals(true, isSuccess);
    }
}

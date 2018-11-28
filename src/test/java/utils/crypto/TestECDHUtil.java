package utils.crypto;

import org.junit.Test;
import utils.crypto.asymmetric.ECDHUtil;

import java.security.KeyPair;

public class TestECDHUtil {
    @Test
    public void exchangeKey() {
        KeyPair alice = ECDHUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        KeyPair bob = ECDHUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);

        System.out.println(ECDHUtil.generateSharedSecret(bob.getPrivate(), alice.getPublic()));
        System.out.println(ECDHUtil.generateSharedSecret(alice.getPrivate(), bob.getPublic()));
    }
}

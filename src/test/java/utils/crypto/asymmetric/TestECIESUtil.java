package utils.crypto.asymmetric;

import org.junit.Test;
import utils.crypto.CryptoUtil;

import java.security.KeyPair;

import static org.junit.Assert.assertEquals;

public class TestECIESUtil {
    @Test
    public void encryptWithDecrypt() {
        String plainText = "하하ㅏ";
        KeyPair keyPair = ECDSAUtil.generateKey(CryptoUtil.ECCAlgorithm.MEDIUM);
        String cipherText = ECIESUtil.encrypt(keyPair.getPublic(), plainText);
        String originText = ECIESUtil.decrypt(keyPair.getPrivate(), cipherText);

        assertEquals(plainText, originText);
    }
}

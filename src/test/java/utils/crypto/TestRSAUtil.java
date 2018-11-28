package utils.crypto;

import org.junit.Test;
import utils.crypto.asymmetric.RSAUtil;

import java.security.KeyPair;

import static org.junit.Assert.assertEquals;

public class TestRSAUtil {
    @Test
    public void encryptWithDecrypt() {
        String plainText = "아이고~";

        KeyPair keyPair = RSAUtil.generateKey();
        String cipherText = RSAUtil.encrypt(plainText, keyPair.getPublic());
        String originText = RSAUtil.decrypt(cipherText, keyPair.getPrivate());
        assertEquals(plainText, originText);
    }
}

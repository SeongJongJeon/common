package utils.crypto;

import org.junit.Test;

import java.security.KeyPair;

public class TestRSAUtil {
    @Test
    public void encryptWithDecrypt() {
        KeyPair keyPair = RSAUtil.generateKey();
        String cipherText = RSAUtil.encrypt("아이고~", keyPair.getPublic());
        String plainText = RSAUtil.decrypt(cipherText, keyPair.getPrivate());
        System.out.println(plainText);
    }
}

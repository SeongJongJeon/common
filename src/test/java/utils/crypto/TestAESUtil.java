package utils.crypto;

import org.junit.Test;

public class TestAESUtil {
    @Test
    public void encryptGCM() {
        String cipherText = AESUtil.encryptGCM("test", "1111", CryptoUtil.CryptoBit.SMALL);
        String plainText = AESUtil.decryptGCM(cipherText, "1111", CryptoUtil.CryptoBit.SMALL);
        System.out.println(plainText);
    }
}

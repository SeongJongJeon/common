package utils.crypto;

import org.junit.Test;

public class TestAESUtil {
    @Test
    public void encryptWithDecryptGCM() {
        String cipherText = AESUtil.encryptGCM("하하ㅏ", "1111", CryptoUtil.CryptoBit.SMALL);
        String plainText = AESUtil.decryptGCM(cipherText, "1111", CryptoUtil.CryptoBit.SMALL);
        System.out.println(plainText);
    }
}

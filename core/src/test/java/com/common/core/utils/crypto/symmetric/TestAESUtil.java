package com.common.core.utils.crypto.symmetric;

import org.junit.Test;
import com.common.core.utils.crypto.CryptoUtil;

import static org.junit.Assert.assertEquals;

public class TestAESUtil {
    @Test
    public void encryptWithDecryptGCM() {
        String plainText = "하하ㅏ";
        String cipherText = AESUtil.encryptGCM(plainText, "1111", CryptoUtil.CryptoBit.SMALL);
        String originText = AESUtil.decryptGCM(cipherText, "1111", CryptoUtil.CryptoBit.SMALL);
        assertEquals(plainText, originText);
    }
}

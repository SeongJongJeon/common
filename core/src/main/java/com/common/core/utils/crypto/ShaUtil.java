package com.common.core.utils.crypto;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

import static com.common.core.utils.crypto.CryptoUtil.CHAR_SET;

@Slf4j
public class ShaUtil {
    public static byte[] sha256(String msg) {
        try {
            return sha256(msg.getBytes(CHAR_SET));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static byte[] sha256(byte[] msg) {
        byte[] result = null;

        try {
            Digest digest = new SHA256Digest();
            digest.update(msg, 0, msg.length);
            result = new byte[digest.getDigestSize()];
            digest.doFinal(result, 0);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }
}

package utils.crypto;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;

import static utils.crypto.CryptoUtil.CHAR_SET;

public class ShaUtil {
    public static byte[] sha256(String msg) {
        try {
            return sha256(msg.getBytes(CHAR_SET));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] sha256(byte[] msg) {
        byte[] result = null;

        try {
            Digest digest = new SHA256Digest();
            digest.update(msg, 0, msg.length);
            result = new byte[digest.getDigestSize()];
            digest.doFinal(result, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}

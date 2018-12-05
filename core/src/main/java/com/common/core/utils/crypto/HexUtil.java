package com.common.core.utils.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

@Slf4j
public class HexUtil {
    public static char[] encode(byte[] data) {
        return Hex.encodeHex(data);
    }

    public static String encodeString(byte[] data) {
        return Hex.encodeHexString(data);
    }

    public static byte[] decodeHex(String data) {
        try {
            return Hex.decodeHex(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static byte[] decodeHex(char[] data) {
        try {
            return Hex.decodeHex(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

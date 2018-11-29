package utils.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class CryptoUtil {
    public static final String CHAR_SET = "UTF-8";

    public enum CryptoBit {
        SMALL(128),
        MEDIUM(192),
        LARGE(256);//256bit requires https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html 여기서 다운로드해서 JRE 디렉토리에 넣어야 함.

        private int value;

        CryptoBit(int value) {
            this.value = value;
        }

        @JsonCreator
        public static CryptoBit fromValue(int value) {
            switch (value) {
                case 128:
                    return SMALL;
                case 192:
                    return MEDIUM;
                case 256:
                    return LARGE;
            }
            return SMALL;
        }

        /**
         * Use serialize
         *
         * @return
         */
        @JsonValue
        public int toValue() {
            return this.value;
        }
    }

    public enum ECCAlgorithm {
        SMALL("secp128k1"),
        MEDIUM("secp256k1"),
        LARGE("secp512k1");

        private String value;

        ECCAlgorithm(String value) {
            this.value = value;
        }

        @JsonCreator
        public static ECCAlgorithm fromValue(String value) {
            switch (value) {
                case "secp128k1":
                    return SMALL;
                case "secp256k1":
                    return MEDIUM;
                case "secp512k1":
                    return LARGE;
            }
            return SMALL;
        }

        /**
         * Use serialize
         *
         * @return
         */
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }

    public enum ECCSigAlgorithm {
        SHA128("SHA1withECDSA"),
        SHA256("SHA256withECDSA"),
        SHA512("SHA512withECDSA");

        private String value;

        ECCSigAlgorithm(String value) {
            this.value = value;
        }

        @JsonCreator
        public static ECCSigAlgorithm fromValue(String value) {
            switch (value) {
                case "SHA1withECDSA":
                    return SHA128;
                case "SHA256withECDSA":
                    return SHA256;
                case "SHA512withECDSA":
                    return SHA512;
            }
            return SHA128;
        }

        /**
         * Use serialize
         *
         * @return
         */
        @JsonValue
        public String toValue() {
            return this.value;
        }
    }
}

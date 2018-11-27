package utils.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class CryptoUtil {
    public enum CryptoBit {
        SMALL(128),
        MEDIUM(192),
        BIG(256);//256bit requires https://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html 여기서 다운로드해서 JRE 디렉토리에 넣어야 함.

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
                    return BIG;
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
}

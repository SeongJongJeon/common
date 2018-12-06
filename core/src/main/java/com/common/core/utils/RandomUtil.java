package com.common.core.utils;

import java.util.Random;

/**
 * Created by sv506 on 2016-10-19.
 */
public class RandomUtil {
    private static final String[] RANDOM_ARR = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] COUPON_RANDOM_ARR = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    public static String getRandomStrArr(String[] arr) {
        int idx = new Random().nextInt(arr.length);
        return arr[idx];
    }

    public static char getRandomCharArr(char[] arr) {
        int idx = new Random().nextInt(arr.length);
        return arr[idx];
    }

    public static int getRandomIntArr(int[] arr) {
        int idx = new Random().nextInt(arr.length);
        return arr[idx];
    }

    public static long getRandomLong(long value) {
        return new Random(value).nextLong();
    }

    public static int getRandomInt(int value) {
        return new Random().nextInt(value);
    }

    /**
     *
     * @param min
     * @param max exclusive
     * @return
     */
    public static int getRandomIntBetween(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static String generateRandomValue(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = new Random().nextInt(RANDOM_ARR.length);
            sb.append(RANDOM_ARR[idx]);
        }
        return sb.toString();
    }

    /**
     * 랜덤한 쿠폰을 생성한다
     * ex) delimiter = -, delimiterTotalCnt = 2, delimiterCnt = 5
     * abcde-12345-67890
     *
     * @param delimiter         구분자
     * @param delimiterTotalCnt 총구분자 갯수
     * @param delimiterCnt      각 구분자마다 랜덤갯수
     * @return
     */
    public static String generateCouponNumber(String delimiter, int delimiterTotalCnt, int delimiterCnt) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i <= delimiterTotalCnt; i++) {
            for (int j = 0; j < delimiterCnt; j++) {
                int idx = new Random().nextInt(COUPON_RANDOM_ARR.length);
                result.append(COUPON_RANDOM_ARR[idx].toUpperCase());
            }
            if (i != delimiterTotalCnt) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }
}

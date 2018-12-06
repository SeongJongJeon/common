package com.common.core.utils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingUtil {

    /**
     * 휴대폰 번호 마스킹 처리
     *
     * @param phoneNum
     * @return maskedCellPhoneNumber
     */
    public static String getMaskedPhoneNum(String phoneNum) {

        String regex = "(01[016789])(\\d{3,4})\\d{4}$";
        Matcher matcher = Pattern.compile(regex).matcher(phoneNum);
        if (matcher.find()) {
            String replaceTarget = matcher.group(2);
            char[] c = new char[replaceTarget.length()];
            Arrays.fill(c, '*');
            return phoneNum.replace(replaceTarget, String.valueOf(c));
        }
        return phoneNum;
    }

    /**
     * 사용자 이름 마스킹 처리
     *
     * @param name
     * @return maskedName
     */
    public static String getMaskedUserName(String name) {
        int nameLength = name.length() - 1;
        String replaceTarget = name.substring(1, nameLength);
        if (name.length() == 2) {
            replaceTarget = name.substring(1);
        }
        char[] c = new char[replaceTarget.length()];
        Arrays.fill(c, '*');
        return name.replace(replaceTarget, String.valueOf(c));
    }

    /**
     * 사용자 생일 마스킹처리
     *
     * @param birth
     * @return
     */
    public static String getMaskedBirth(String birth) {
        String replaceTarget = birth.substring(4, 6);

        char[] c = new char[replaceTarget.length()];
        Arrays.fill(c, '*');
        StringBuffer sb = new StringBuffer(birth);
        birth = String.valueOf(sb.replace(4, 6, String.valueOf(c)));

        return birth;
    }

    /**
     * 인증서 txId 마스킹처리
     *
     * @param txId
     * @return
     */
    public static String getMaskedTxId(String txId) {
        int txIdEndLength = txId.length() - 4;
        String replaceTarget = txId.substring(4, txIdEndLength);
        char[] c = new char[4];
        Arrays.fill(c, '.');
        return txId.replace(replaceTarget, String.valueOf(c));
    }

}

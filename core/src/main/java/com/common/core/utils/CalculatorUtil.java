package com.common.core.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by alex on 2016. 11. 6..
 */
public class CalculatorUtil {

    /**
     * number 값을 1,000,000와 같이 콤마를 붙여서 리턴
     *
     * @param val
     * @return
     */
    public static String generateNumberComma(long val) {
        DecimalFormat Commas = new DecimalFormat("#,###");
        return Commas.format(val);
    }

    /**
     * GB로 변환하여 리턴한다.
     *
     * @param source bytes 값
     * @return
     */
    public static String generateGBFloating(Long source) {
        float gb = 1024f * 1024 * 1024;
        if (source == 0) {
            return source + "GB";
        }

        DecimalFormat format = new DecimalFormat(".##");
        if (source.floatValue() < gb) {
            return CalculatorUtil.generateMBFloating(source);
        }
        return format.format(source.floatValue() / gb) + "GB";
    }

    /**
     * MB로 변환하여 리턴한다.
     *
     * @param source bytes 값
     * @return
     */
    public static String generateMBFloating(Long source) {
        float mb = 1024f * 1024;
        if (source == 0) {
            return source + "MB";
        }

        DecimalFormat format = new DecimalFormat(".##");
        if (source.floatValue() / mb < 1) {
            return 0 + format.format(source.floatValue() / mb) + "MB";
        }

        return format.format(source.floatValue() / mb) + "MB";
    }

    /**
     * 3.0, 3.5 와 같이 0.5 단위의 값을 리턴한다
     *
     * @param value
     * @return
     */
    public static float generatePointHalf(double value) {
        return (float) (Math.round(value * 2) / 2.0);
    }

    /**
     * %를 계산한다.
     *
     * @param child  분자
     * @param parent 분모
     * @return
     */
    public static String generatePercentage(Long child, Long parent) {
        DecimalFormat format = new DecimalFormat(".##");

        if (((child.floatValue() / parent.floatValue()) * 100) < 1) {
            return 0 + format.format(((child.floatValue() / parent.floatValue()) * 100)) + "%";
        }

        return format.format(((child.floatValue() / parent.floatValue()) * 100)) + "%";
    }

    /**
     * %를 계산하고 point 갯수만큼의 소숫점 자리아래를 절삭하고 리턴한다
     *
     * @param child
     * @param parent
     * @param point
     * @return
     */
    public static double generatePercentageUntilPoint(Long child, Long parent, int point) {
        //백분률
        double percentage = (child.doubleValue() / parent.doubleValue()) * 100;

        //소숫점 포인트 계산
        String pointStr = ".";
        for (int i = 0; i < point; i++) {
            pointStr += "#";
        }
        DecimalFormat format = new DecimalFormat(pointStr);

        return Double.parseDouble(format.format(percentage));
    }

    /**
     * 소수 값을 point자리 만큼만 계산한다.
     *
     * @param floatVal 소숫 값
     * @param point    소숫 점 자리 수
     * @return
     */
    public static String generateFloatPoint(float floatVal, String point) {
        DecimalFormat format = new DecimalFormat(point);
        if (Float.parseFloat(format.format(floatVal)) < 1) {
            return "0" + format.format(floatVal);
        }
        return format.format(floatVal);
    }

    /**
     * 소수 값을 point자리 만큼만 계산한다.
     *
     * @param floatVal 소숫 값
     * @param point    소숫 점 자리 수
     * @return
     */
    public static String generateFloatPoint(double floatVal, String point) {
        DecimalFormat format = new DecimalFormat(point);
        if (Float.parseFloat(format.format(floatVal)) < 1) {
            return "0" + format.format(floatVal);
        }
        return format.format(floatVal);
    }

    /**
     * 총 실행 시간을 시간, 분, 초로 계산한다.
     *
     * @param startTime 시작 시간 ms
     * @param endTime   종료 시간 ms
     * @return
     */
    public static String generateToSuitableTime(long startTime, long endTime) {
        if (endTime == 0) {
            return "00:00:00";
        }
        long elapsed = endTime - startTime;
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));

        return df.format(new Date(elapsed));
    }

    /**
     * 초당 처리량 계산
     *
     * @param startTime
     * @param endTime
     * @param conductedCnt 처리량 수
     * @return
     */
    public static long calculateThroughputPerSecond(long startTime, long endTime, long conductedCnt) {
        float throughput = 0;
        float duration = endTime - startTime;

        if (duration <= 1000) {
            return conductedCnt;
        }
        float unit = 1000;

        /**
         * 초기 duration의 단위는 1/1000초이다.
         *
         * 1. 1/1000초로 나누고 초단위 처리량을 구하기 위해 1000을 곱한다.
         * 2. 1/1000초로 나눌 정도의 처리량이 아니라면 1/100초로 계산한다.
         * 3. 1/100초로 나눌 정도의 처리량이 아니라면 1/10초로 계산한다.
         * 4. 1/10초로 나눌 정도의 처리량이 아니라면 1초로 계산한다.
         */
        while ((throughput = conductedCnt / duration) <= (1 / unit) && unit > 1) {
            duration = duration / 10;
            unit = unit / 10;
        }
        throughput = throughput * unit;

        return (long) throughput;
    }

    /**
     * 초당 처리량 계산
     *
     * @param duration
     * @param conductedCnt
     * @return
     */
    public static long calculateThroughputPerSecond(long duration, long conductedCnt) {
        float throughput = 0;

        if (duration <= 1000) {
            return conductedCnt;
        }
        float unit = 1000;

        /**
         * 초기 duration의 단위는 1/1000초이다.
         *
         * 1. 1/1000초로 나누고 초단위 처리량을 구하기 위해 1000을 곱한다.
         * 2. 1/1000초로 나눌 정도의 처리량이 아니라면 1/100초로 계산한다.
         * 3. 1/100초로 나눌 정도의 처리량이 아니라면 1/10초로 계산한다.
         * 4. 1/10초로 나눌 정도의 처리량이 아니라면 1초로 계산한다.
         */
        long tempDuration = duration;
        while ((throughput = (float) conductedCnt / tempDuration) <= (1 / unit) && unit > 1) {
            tempDuration = tempDuration / 10;
            unit = unit / 10;
        }
        throughput = throughput * unit;

        return (long) throughput;
    }

    /**
     * 소수점 아래의 마지막 값에 1을 더한다.
     *
     * @param floatNumber
     * @return
     */
    public static double addRedisScorePoint(long number, long floatNumber) {
        int maxFloatCnt = 10;    //10억
        String numberStr = String.valueOf(floatNumber);

        String changedPoint = "";
        for (int i = 0; i < (maxFloatCnt - numberStr.length()); i++) {
            changedPoint += "0";
        }
        changedPoint += numberStr;

        double temp = Double.parseDouble(number + "." + changedPoint);
        return (temp + 0.0000000001);
    }
}

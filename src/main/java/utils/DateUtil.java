package utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static ZoneId zoneId = ZoneId.systemDefault();

    public static LocalDate getLocalDate() {
        return LocalDate.now();
    }

    public static LocalDate getLocalDate(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public static LocalTime getLocalTime() {
        return LocalTime.now();
    }

    public static LocalTime getLocalTime(int hour, int minute, int second, int nanoOfSecond) {
        return LocalTime.of(hour, minute, second, nanoOfSecond);
    }

    /**
     * 현재 날짜와 시간에 대해 System 기본 타임존을 기준으로 생성하여 리턴한다.
     *
     * @return
     */
    public static Date getDate() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getDate(int addMonths, int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime dateTime = LocalDateTime.now().plusMonths(addMonths)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(nanoOfSecond);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    public static Date getDate(int hour, int minute, int second, int nanoOfSecond) {
        LocalDateTime dateTime = LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(nanoOfSecond);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    public static Date getDate(int hour, int minute, int second) {
        LocalDateTime dateTime = LocalDateTime.now()
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * 현재 날짜로부터 몇일 이전 또는 이후의 날짜를 생성하여 리턴한다.
     *
     * @param days
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getBeforeOrAfterDate(int days, int hour, int minute, int second, boolean isBefore) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = isBefore ? dateTime.minusDays(days) : dateTime.plusDays(days);
        dateTime.withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .withNano(0);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * 현재 시간으로부터 이전 또는 이후 시간을 셋팅
     *
     * @param hours
     * @return
     */
    public static Date getBeforeOrAfterDateOfHours(long hours, boolean isBefore) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = isBefore ? dateTime.minusHours(hours) : dateTime.plusHours(hours);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * 현재 시간으로부터 이전 또는 이후 분을 셋팅
     *
     * @param minutes
     * @return
     */
    public static Date getBeforeOrAfterDateOfMinutes(long minutes, boolean isBefore) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = isBefore ? dateTime.minusMinutes(minutes) : dateTime.plusMinutes(minutes);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * 현재 시간으로부터 이전 또는 이후 초를 셋팅
     *
     * @param seconds
     * @return
     */
    public static Date getBeforeOrAfterDateOfSeconds(int seconds, boolean isBefore) {
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = isBefore ? dateTime.minusSeconds(seconds) : dateTime.plusSeconds(seconds);
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * 주어진 포맷으로 날짜를 변환
     *
     * @param format ex) yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String generateStringByDate(Date date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(date);
    }

    /**
     * String Date 값을 Date 변환
     *
     * @param date   ex) 2018-01-01 12:00:00
     * @param format ex) yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date generateDateTimeByString(String date, String format) {
        LocalDateTime dateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
        return Date.from(dateTime.atZone(zoneId).toInstant());
    }

    /**
     * LocalDateTime 주어진 포맷으로 변환
     *
     * @param date
     * @param format ex) yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String generateStringByLocalDateTime(LocalDateTime date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
    }

    /**
     * String Date 값을 LocalDateTime 변환
     *
     * @param date   ex) 2018-01-01 12:00:00
     * @param format ex) yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static LocalDateTime generateLocalDateTimeByString(String date, String format) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
    }
}

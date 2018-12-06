package com.common.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by admin on 2017. 4. 18..
 */
public class ExceptionUtil {
    /**
     * Exception의 StackTrace를 String으로 변환
     *
     * @param throwable
     * @return
     */
    public static String generateStackTraceToString(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
}

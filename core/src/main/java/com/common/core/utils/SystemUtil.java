package com.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class SystemUtil {
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getHostAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static String getHost() {
        String hostname = System.getenv("HOSTNAME");
        if (!StringUtils.isEmpty(hostname)) {
            return hostname;
        }

        String lineStr = "";
        try {
            Process process = Runtime.getRuntime().exec("hostname");
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((lineStr = br.readLine()) != null) {
                hostname = lineStr;
            }
        } catch (IOException e) {
            log.warn(String.format("mag:%s, trace:%s", e.getMessage(), ExceptionUtil.generateStackTraceToString(e)));
        }
        return hostname;
    }
}

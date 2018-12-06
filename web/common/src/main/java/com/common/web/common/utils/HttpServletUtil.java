package com.common.web.common.utils;


import com.common.web.common.dto.ServletReqDto;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HttpServlet 에서 값을 추출하고자 하는 경우에 사용한다.
 *
 * @version 1.0
 * @since 1.0
 */
public class HttpServletUtil {
    public static final int COOKIE_EXPIRE = 60 * 60 * 24 * 59; //59일

    /**
     * 요청 클라이언트의 IP를 추출한다.
     *
     * @param request
     * @return
     */
    public static String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();

        String[] xForwardedForArr = {"X-FORWARDED-FOR", "X-Forwarded-For", "xForwardedFor", "HEADER_X_FORWARDED_FOR"};
        String xForwardedFor = null;
        for (String temp : xForwardedForArr) {
            xForwardedFor = request.getHeader(temp);
            if (!StringUtils.isEmpty(xForwardedFor)) {
                break;
            }
        }

        if (xForwardedFor != null) {
            remoteAddr = xForwardedFor;
            int idx = remoteAddr.indexOf(',');
            if (idx > -1) {
                remoteAddr = remoteAddr.substring(0, idx);
            }
        }
        return remoteAddr;
    }

    /**
     * 요청의 국가별 언어값을 추출한다.
     *
     * @param request
     * @return
     */
    public static String getLanguage(HttpServletRequest request) {
        return request.getLocale().getLanguage();
    }

    /**
     * 브라우져 Agent 값을 추출한다.
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 요청 URL에서 도메인 URL만을 추출한다.
     *
     * @return
     */
    public static String getDomainUrl(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getRequestURI(), "");
    }

    /**
     * 프로토콜 요청을 검사한다
     *
     * @param request
     */
    public static void checkHttps(HttpServletRequest request, Set<String> httpsUrls) {
        if (httpsUrls.contains(request.getRequestURI())) {
            if (!"https".equals(request.getScheme())) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * 쿠키 값을 Map으로 변환
     *
     * @param cookies
     * @return
     */
    public static Map<String, String> generateCookieToMap(Cookie[] cookies) {
        Map<String, String> cookieMap = new HashMap<>();
        for (Cookie cookie : cookies) {
            cookieMap.put(cookie.getName(), cookie.getValue());
        }

        return cookieMap;
    }

    /**
     * 쿠키 값을 쿼리 스트링으로 변환
     *
     * @param cookies
     * @return
     */
    public static String generateCookieToQueryStr(Cookie[] cookies) {
        StringBuilder sb = new StringBuilder();
        for (Cookie cookie : cookies) {
            sb.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        return sb.toString();
    }

    /**
     * 쿠키 삭제
     *
     * @param req
     * @param res
     * @param key
     * @param path
     */
    public static void removeCookie(HttpServletRequest req, HttpServletResponse res, String key, String path) {
        Cookie cookie = new Cookie(key, "");
        cookie.setMaxAge(0);
        cookie.setDomain(req.getServerName());
        if (StringUtils.isEmpty(path)) {
            cookie.setPath("/");
        } else {
            cookie.setPath(path);
        }

        res.addCookie(cookie);
    }

    /**
     * 쿠키 생성
     *
     * @param req
     * @param res
     * @param key
     * @param value
     * @param path
     * @param expire
     */
    public static void createCookie(HttpServletRequest req, HttpServletResponse res, String key, String value, String path, Integer expire) {
        Cookie cookie = null;
        try {
            cookie = new Cookie(key, value);
            //서브 도메인에서도 쿠키에 접근하기 위함 (example.com에서 쿠키를 생성하면 admin.example.com 에서도 접근가능)
//            cookie.setDomain(req.getServerName()); // 익스플로러에서 도메인을 설정하면 저장이 되지 않는 버그가 있음.
            cookie.setPath("/");
            if (!org.apache.commons.lang3.StringUtils.isEmpty(path)) {
                cookie.setPath(path);
            }

            cookie.setMaxAge(expire != null ? expire : COOKIE_EXPIRE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        res.addCookie(cookie);
    }

    /**
     * 키에 해당하는 쿠키값 조회
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookie(HttpServletRequest request, String key) {
        String val = null;

        if (request != null && request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            Cookie cookie = null;

            for (int len = cookies.length; len > 0; len--) {
                if (cookies[len - 1].getName().equals(key)) {
                    cookie = cookies[len - 1];
                }
            }

            if (cookie != null) {
                val = cookie.getValue();
            }
        }

        return val;
    }

    public static ServletReqDto extractServletRequest(HttpServletRequest req) {
        ServletReqDto servletReqDto = new ServletReqDto();
        servletReqDto.setClientIP(getClientIp(req));
        servletReqDto.setLanguage(getLanguage(req));
        servletReqDto.setCookies(generateCookieToMap(req.getCookies()));

        return servletReqDto;
    }
}
package com.common.web.common.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"code\":500, \"message\":\"Required auth key\"}");

        response.setStatus(501);
        response.getWriter().print(sb);
        response.getWriter().flush();
    }
}
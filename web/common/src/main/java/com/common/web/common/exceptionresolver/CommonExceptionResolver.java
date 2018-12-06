package com.common.web.common.exceptionresolver;

import com.common.core.utils.ExceptionUtil;
import com.common.web.common.dto.JsonResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

@Slf4j
public class CommonExceptionResolver {
    @ExceptionHandler({
            Exception.class,
    })
    @ResponseBody
    public JsonResultDto defaultExceptionHandler(HttpServletRequest req, HttpServletResponse res, Exception ex) {
        int code = 500;
        String msg = "알수없는 에러";
        try {
            Field f = ex.getClass().getDeclaredField("code");
            f.setAccessible(true);
            code = (int) f.get(ex);

            f = ex.getClass().getDeclaredField("msg");
            f.setAccessible(true);
            msg = (String) f.get(ex);
        } catch (Exception e1) {
        }
        log.warn(String.format("Unknown exception - remote:%s(%s), msg:%s, stack:%s", req.getRemoteHost(), req.getRemoteAddr(), ex.getMessage(), ExceptionUtil.generateStackTraceToString(ex)));

        return new JsonResultDto(code, msg);
    }
}

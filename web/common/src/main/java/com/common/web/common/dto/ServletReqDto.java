package com.common.web.common.dto;

import lombok.Data;

import java.util.Map;

/**
 * Created by alex.
 * Date: 2018-12-06
 */
@Data
public class ServletReqDto {
    private String clientIP;
    private String language;
    private Map<String, String> cookies;
}

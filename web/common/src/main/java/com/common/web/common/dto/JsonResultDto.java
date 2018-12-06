package com.common.web.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JsonResultDto {
    private int resultCode = 200;
    private String resultMsg = "SUCCESS";

    public JsonResultDto() {
    }

    public JsonResultDto(int resultCode) {
        this.resultCode = resultCode;
    }

    public JsonResultDto(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}

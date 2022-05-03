package com.yunmuq.kingyanplus.model.response;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class CommonErrorResponse {
    private int errorCode;
    private String message;
    private String exception;

    public CommonErrorResponse() {
    }

    public CommonErrorResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.exception = null;
    }

    public CommonErrorResponse(int errorCode, String message, @Nullable Exception e) {
        this.errorCode = errorCode;
        this.message = message;
        if (e != null) this.exception = e.toString();
    }
}

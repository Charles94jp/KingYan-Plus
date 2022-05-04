package com.yunmuq.kingyanplus.model.exception;

/**
 * 用于Controller中抛出异常
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-19
 * @since 1.8
 * @since spring boot 2.6.6
 */
public class CommonRuntimeException extends RuntimeException {
    /**
     * 错误代码及其对应定义见：resource/i18n/
     */
    private int errorCode;

    public CommonRuntimeException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

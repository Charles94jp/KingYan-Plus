package com.yunmuq.kingyanplus.util;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-22
 * @since 1.8
 * @since spring boot 2.6.6
 */
public class LogUtil {
    public static String LogCurrentFileAndLine() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        return "\nat " + stackTraceElement.toString();
    }
}

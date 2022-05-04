package com.yunmuq.kingyanplus.util;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-22
 * @since 1.8
 * @since spring boot 2.6.6
 */
public class LogUtil {
    /**
     * 获取当前代码所在文件和行数
     *
     * @deprecated 不应该只记录当前文件和行数，应该抛出异常，记录整个堆栈。如果不是特别重要的异常，就降低日志等级
     */
    public static String LogCurrentFileAndLine() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        return "\nat " + stackTraceElement.toString();
    }
}

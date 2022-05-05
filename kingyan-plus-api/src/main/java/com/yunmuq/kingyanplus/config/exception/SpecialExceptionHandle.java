package com.yunmuq.kingyanplus.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.alibaba.fastjson.JSONException;
import com.yunmuq.kingyanplus.model.exception.CommonRuntimeException;
import com.yunmuq.kingyanplus.model.response.CommonErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * spring boot异常先过 @ControllerAdvice，再过 implements ErrorController
 * 后者通常用于处理自定义 404 500等错误，以及通用处理异常
 * 前者用于捕获特定的异常
 * ControllerAdvice虽能捕获整个程序的异常却无法自定义404等错误
 * 而 ErrorController 无法捕获进controller之前的消息转换、反序列化的异常，但能接收到404等错误
 * <p>
 * 注意：ControllerAdvice一定要加上ResponseBody注解，否则循环分配请求
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-18
 * @since 1.8
 * @since spring boot 2.6.6
 */
@ControllerAdvice
@ResponseBody
public class SpecialExceptionHandle {
    @Autowired
    private MessageSource messageSource;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * fastjson反序列化错误
     */
    @ExceptionHandler(value = {JSONException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonErrorResponse handleMessageNotReadableException(JSONException e) {
        logger.debug("fastjson序列/反序列化异常，通常消息转换时前端传来的json格式错误，报safeMode是遭受攻击", e);
        final int errorCode = 1001;
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("errorCode." + errorCode, null, locale);
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(1001, msg, e);
        return commonErrorResponse;
    }

    /**
     * sa-token鉴权失败：未登录
     */
    @ExceptionHandler(value = NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonErrorResponse handleNotLoginException(NotLoginException e) {
        logger.trace("用户未登录，请求被拦截", e);
        final int errorCode = 401;
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("errorCode." + errorCode, null, locale);
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(errorCode, msg, e);
        return commonErrorResponse;
    }

    /**
     * sa-token鉴权失败：权限不足
     */
    @ExceptionHandler(value = {NotPermissionException.class, NotRoleException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonErrorResponse handleNotPermissionAndRoleException(Exception e) {
        logger.trace("用户权限不足，请求被拦截", e);
        final int errorCode = 403;
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("errorCode." + errorCode, null, locale);
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(errorCode, msg, e);
        return commonErrorResponse;
    }

    /**
     * 处理程序抛出的指定异常
     */
    @ExceptionHandler(value = {CommonRuntimeException.class})
    public CommonErrorResponse handleCommonRuntimeException(HttpServletResponse response, CommonRuntimeException e) {
        int errorCode = e.getErrorCode();
        Locale locale = LocaleContextHolder.getLocale();
        String msg = messageSource.getMessage("errorCode." + errorCode, null, locale);
        // 错误代码及其对应定义见：resource/i18n/
        switch (errorCode) {
            case 1002:
                response.setStatus(403);
                logger.debug("请求缺少X-XSRF-TOKEN头", e);
                break;
        }
        return new CommonErrorResponse(errorCode, msg);
    }
}

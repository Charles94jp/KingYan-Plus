package com.yunmuq.kingyanplus.config.exception;

import com.yunmuq.kingyanplus.model.response.CommonErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * spring boot报错会跳转到 /error
 * 实现这个必须 implements ErrorController 才不和默认的冲突
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-17
 * @since 1.8
 * @since spring boot 2.6.6
 */
@RestController
public class customErrorController implements ErrorController {
    @Autowired
    private MessageSource messageSource;

    private final static Logger logger = LoggerFactory.getLogger(customErrorController.class);

    /**
     * 代替默认的错误接口，但以下情况的异常无法被捕获：
     * 1. 反序列化异常，前端传的JSON在反序列化时报错，到这是400 HTTP状态，但是javax.servlet.error.exception获取不到异常
     * 2. 序列化异常，如果返回的对象序列化时报错，猜测也无法捕获异常
     * 此方法如果抛出异常，就会使用tomcat的错误页面
     */
    @RequestMapping(value = "/error")
    public CommonErrorResponse error(HttpServletRequest request) {
        try {
            // HTTP状态码，当其他Controller报错跳转至此吗，或者404...时存在此值，当前端直接访问/error时无此值
            Integer httpStatus = (Integer) request.getAttribute("javax.servlet.error.status_code");
            if (httpStatus == null) {
                return new CommonErrorResponse(200, "error");
            }

            String msg = null;
            Locale locale = LocaleContextHolder.getLocale();
            try {
                msg = messageSource.getMessage("errorCode." + httpStatus, null, locale);
            } catch (NoSuchMessageException noSuchMsgE) {
                // 未预先设置i18n错误消息
                // todo: Controller中要抛出指定errorCode和异常时怎么处理
            }
            Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
            return new CommonErrorResponse(httpStatus, msg, exception);
        } catch (Exception errorControllerE) {
            logger.error("ErrorController在处理系统错误时异常，返回默认CommonError", errorControllerE);
            // 返回的errorCode，未定义是0，customErrorController Error是-1
            return new CommonErrorResponse(-1, "customErrorController Error", errorControllerE);
        }
    }
}


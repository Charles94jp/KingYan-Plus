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
import javax.servlet.http.HttpServletResponse;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String error404Html = "<!DOCTYPE html><html lang=\"zh-cmn-Hans\"><head><meta charset=\"UTF-8\"><title>404-找不到页面</title><meta name=\"viewport\"content=\"width=device-width, maximum-scale=1, initial-scale=1\"/><style>html,body{height:100%}body{color:#333;margin:auto;padding:1em;display:table;user-select:none;box-sizing:border-box;font:lighter 20px\"微软雅黑\"}a{color:#3498db;text-decoration:none}h1{margin-top:0;font-size:2em}main{margin:0 auto;text-align:center;display:table-cell;vertical-align:middle}.btn{color:#fff;padding:.75em 1em;background:#3498db;border-radius:1.5em;display:inline-block;transition:opacity.3s,transform.3s}.btn:hover{transform:scale(1.1)}.btn:active{opacity:.7}</style></head><body><main><h1>(ﾟДﾟ≡ﾟдﾟ)!?</h1><p>找不到你要的页面</p><p style=\"display: none;\">你可以去浏览我的<a href=\"\">首页</a>或<a href=\"\">博客</a>~</p><a class=\"btn\"href=\"/\">返回首页</a></main></body></html>";
    private static final String error404HtmlENUS = "<!DOCTYPE html><html lang=\"zh-cmn-Hans\"><head><meta charset=\"UTF-8\"><title>404 Page Not Found</title><meta name=\"viewport\"content=\"width=device-width, maximum-scale=1, initial-scale=1\"/><style>html,body{height:100%}body{color:#333;margin:auto;padding:1em;display:table;user-select:none;box-sizing:border-box;font:lighter 20px\"微软雅黑\"}a{color:#3498db;text-decoration:none}h1{margin-top:0;font-size:2em}main{margin:0 auto;text-align:center;display:table-cell;vertical-align:middle}.btn{color:#fff;padding:.75em 1em;background:#3498db;border-radius:1.5em;display:inline-block;transition:opacity.3s,transform.3s}.btn:hover{transform:scale(1.1)}.btn:active{opacity:.7}</style></head><body><main><h1>(ﾟДﾟ≡ﾟдﾟ)!?</h1><p>Could not find the page you are looking for</p><p style=\"display: none;\">你可以去浏览我的<a href=\"\">首页</a>或<a href=\"\">博客</a>~</p><a class=\"btn\"href=\"/\">Home</a></main></body></html>";

    /**
     * 代替默认的错误接口，但以下情况的异常无法被捕获：
     * 1. 反序列化异常，前端传的JSON在反序列化时报错，到这是400 HTTP状态，但是javax.servlet.error.exception获取不到异常
     * 2. 序列化异常，如果返回的对象序列化时报错，猜测也无法捕获异常
     * 此方法如果抛出异常，就会使用tomcat的错误页面
     */
    @RequestMapping(value = "/error")
    public CommonErrorResponse error(HttpServletRequest request, HttpServletResponse response) {
        Locale locale = LocaleContextHolder.getLocale();
        String msg = null;
        try {
            // HTTP状态码，当其他Controller报错跳转至此吗，或者404...时存在此值，当前端直接访问/error时无此值
            Integer httpStatus = (Integer) request.getAttribute("javax.servlet.error.status_code");
            if (httpStatus == null) {
                return new CommonErrorResponse(200, "error");
            }
            // 如果是浏览器地址栏GET访问 404返回页面而不是json
            if (request.getMethod().equals("GET") && httpStatus == 404) {
                response.setHeader("Content-Type", "text/html");
                msg = locale.equals(Locale.US) ? error404HtmlENUS : error404Html;
                response.getOutputStream().write(msg.getBytes());
                // 乱码
                // resp.getWriter().write(error404Html);
                return null;
            }

            try {
                msg = messageSource.getMessage("errorCode." + httpStatus, null, locale);
            } catch (NoSuchMessageException noSuchMsgE) {
                // 未预先设置i18n错误消息
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


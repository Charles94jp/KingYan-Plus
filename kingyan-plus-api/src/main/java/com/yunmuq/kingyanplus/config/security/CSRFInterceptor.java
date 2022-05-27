package com.yunmuq.kingyanplus.config.security;

import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.model.exception.CommonRuntimeException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-04
 * @since 1.8
 * @since spring boot 2.6.7
 */
public class CSRFInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // handler指明请求来自哪
        // 这样就不会404请求也被拦截，导致本该是404的响应变成被拦截的响应
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String csrfHeader = request.getHeader("X-Xsrf-Token");
        if (csrfHeader != null && csrfHeader.equals(StpUtil.getTokenSession().get("csrfToken"))) {
            // 放行
            return true;
        }
        throw new CommonRuntimeException(1002, "csrf攻击");
        // 直接拦截，响应为200，body为空
        // return false;
    }
}

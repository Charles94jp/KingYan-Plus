package com.yunmuq.kingyanplus.config.security;

import cn.dev33.satoken.interceptor.SaAnnotationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 无@EnableWebMvc不生效
 * todo: 注意EnableWebMvc对其他WebMvcConfigurer是否有负面影响
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    // 注册Sa-Token的注解拦截器，打开注解式鉴权功能
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器，并排除不需要注解鉴权的接口地址 (与登录拦截器无关)
        registry.addInterceptor(new SaAnnotationInterceptor()).addPathPatterns("/**").excludePathPatterns("/login");
    }
}

package com.yunmuq.kingyanplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;


/**
 * 国际化设置
 *
 * @author yunmuq
 * @since 2022-04-16
 * @since 1.8
 * @since spring boot 2.6.6
 * @version v1.0.0
 */
@Component
public class I18nLocaleConfig {
    @Bean
    public LocaleResolver localeResolver() {
        // 也可以换成 SessionLocalResolver, 区别在于国际化的应用范围
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        // Defaults to CookieLocaleResolver.class.getName() + ".LOCALE" if not set
        localeResolver.setCookieName("lang");
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }
}

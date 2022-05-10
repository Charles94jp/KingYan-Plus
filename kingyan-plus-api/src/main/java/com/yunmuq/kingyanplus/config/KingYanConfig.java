package com.yunmuq.kingyanplus.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * KingYan配置类
 * 如果配置文件中没配置。使用默认的数值
 * todo: 优化从配置文件自动装配的方式，比如xxAutoConfiguration、xxProperties，或参考sa-token
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-05
 * @since 1.8
 * @since spring boot 2.6.7
 */
@ConfigurationProperties(prefix = "kingyan")
@Component
@Data
public class KingYanConfig {
    /**
     * 图片验证码
     */
    private KingYanCaptchaConfig captcha;

    public KingYanConfig(KingYanCaptchaConfig captcha) {
        this.captcha = captcha;
    }
}

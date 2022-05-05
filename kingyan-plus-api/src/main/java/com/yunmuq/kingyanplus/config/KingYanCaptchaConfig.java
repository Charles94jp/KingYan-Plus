package com.yunmuq.kingyanplus.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-05
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Component
@Data
public class KingYanCaptchaConfig {
    /**
     * 图片验证码过期时间，单位ms
     */
    private int captchaTimeout = 60000;

    /**
     * 验证码长度
     */
    private int captchaLength = 5;
}

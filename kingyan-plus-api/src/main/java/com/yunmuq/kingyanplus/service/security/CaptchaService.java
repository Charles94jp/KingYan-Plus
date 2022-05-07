package com.yunmuq.kingyanplus.service.security;

import com.yunmuq.kingyanplus.config.KingYanCaptchaConfig;
import com.yunmuq.kingyanplus.model.CheckCaptchaResult;

import java.io.OutputStream;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-07
 * @since 1.8
 * @since spring boot 2.6.7
 */
public interface CaptchaService {
    /**
     * 生成随机验证码
     * @param out 图片输出流
     * @return 验证码的答案
     * @throws Exception
     */
    public String generateCaptcha(OutputStream out) throws Exception;
    public CheckCaptchaResult checkCaptcha(String captcha);
}

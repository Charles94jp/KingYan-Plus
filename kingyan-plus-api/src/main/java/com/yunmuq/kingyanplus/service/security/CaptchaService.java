package com.yunmuq.kingyanplus.service.security;

import com.wf.captcha.base.Captcha;
import com.yunmuq.kingyanplus.model.CheckCaptchaResult;

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
     * @return 验证码的答案
     * @throws Exception
     */
    Captcha generateCaptcha() throws Exception;
    CheckCaptchaResult checkCaptcha(String captcha);
}

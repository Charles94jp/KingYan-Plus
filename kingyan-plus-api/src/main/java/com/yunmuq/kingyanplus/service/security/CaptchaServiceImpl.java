package com.yunmuq.kingyanplus.service.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import com.yunmuq.kingyanplus.config.KingYanConfig;
import com.yunmuq.kingyanplus.model.CheckCaptchaResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-07
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Service
public class CaptchaServiceImpl implements CaptchaService{
    @Autowired
    private KingYanConfig kingyanConfig;

    @Autowired
    private MessageSource messageSource;

    /**
     * 生成gif随机验证码，验证码配置见KingYanConfig
     * @param out 图片输出流
     * @return 验证码的答案
     * @throws Exception
     */
    @Override
    public Captcha generateCaptcha() throws Exception {
        // 三个参数分别为宽、高、位数
        GifCaptcha captcha = new GifCaptcha(130, 48, kingyanConfig.getCaptcha().getCaptchaLength());
        // 设置字体
        // captcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        captcha.setFont(com.wf.captcha.base.Captcha.FONT_8);
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(com.wf.captcha.base.Captcha.TYPE_NUM_AND_UPPER);
        // 输出图片流
        // captcha.out(out);
        return captcha;
    }

    @Override
    public CheckCaptchaResult checkCaptcha(String captcha) {
        Locale locale = LocaleContextHolder.getLocale();

        CheckCaptchaResult result = new CheckCaptchaResult();
        final int captchaLength = kingyanConfig.getCaptcha().getCaptchaLength();
        final int captchaTimeout = kingyanConfig.getCaptcha().getCaptchaTimeout();
        String tips = messageSource.getMessage("captcha.fail", null, locale);
        // 短路或，左边是true右边就不会运算
        if (captcha == null || captcha.length() != captchaLength) {
            result.setMsg(tips);
            return result;
        }
        SaSession tokenSession = StpUtil.getTokenSession();
        Long captchaCreateTime = (Long) tokenSession.get("captchaCreateTime");
        if (captchaCreateTime == null) {
            tips = messageSource.getMessage("captcha.missing", null, locale);
            result.setMsg(tips);
            return result;
        }
        if (System.currentTimeMillis() - captchaCreateTime > captchaTimeout) {
            tips = messageSource.getMessage("captcha.timeout", null, locale);
            tips = tips.replace("{}", String.valueOf(captchaTimeout / 1000));
            result.setMsg(tips);
            return result;
        }
        if (!captcha.toLowerCase().equals(tokenSession.get("captcha"))) {
            result.setMsg(tips);
            return result;
        }
        result.setSuccess(true);
        return result;
    }
}

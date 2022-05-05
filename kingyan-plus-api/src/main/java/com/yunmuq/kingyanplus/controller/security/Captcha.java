package com.yunmuq.kingyanplus.controller.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.GifCaptcha;
import com.yunmuq.kingyanplus.config.KingYanConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-05
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
@RequestMapping("/auth")
public class Captcha {
    @Autowired
    private KingYanConfig kingyanConfig;

    @GetMapping("/getCaptchaImg")
    public void getCaptchaImg(HttpServletResponse response) throws Exception {
        final int captchaLength = kingyanConfig.getCaptcha().getCaptchaLength();

        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        GifCaptcha captcha = new GifCaptcha(130, 48, captchaLength);
        // 设置字体
        // captcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        captcha.setFont(com.wf.captcha.base.Captcha.FONT_8);
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(com.wf.captcha.base.Captcha.TYPE_NUM_AND_UPPER);
        // 存入satoken的session，而非Spring的HTTPSession
        // StpUtil.getSession是必须要登录才能用
        // getTokenSession配置文件开启tokenSessionCheckLogin: false
        SaSession session = StpUtil.getTokenSession();
        session.set("captcha", captcha.text().toLowerCase());
        session.set("captchaCreateTime", System.currentTimeMillis());
        // 输出图片流
        captcha.out(response.getOutputStream());

        // 使用默认工具，使用http session完成
        //CaptchaUtil.out(request, response);
    }
}

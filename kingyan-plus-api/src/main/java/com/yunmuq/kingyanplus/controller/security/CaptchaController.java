package com.yunmuq.kingyanplus.controller.security;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.base.Captcha;
import com.yunmuq.kingyanplus.service.security.CaptchaService;
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
@RequestMapping("/sec")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/getCaptchaImg")
    public void getCaptchaImg(HttpServletResponse response) throws Exception {

        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-store,no-cache,must-revalidate,post-check=0,pre-check=0,max-age=0");
        response.setDateHeader("Expires", 0);

        Captcha captcha = captchaService.generateCaptcha();

        // 存入satoken的session，而非Spring的HTTPSession
        // StpUtil.getSession是必须要登录才能用
        // getTokenSession配置文件开启tokenSessionCheckLogin: false
        SaSession session = StpUtil.getTokenSession();
        session.set("captcha", captcha.text().toLowerCase());
        session.set("captchaCreateTime", System.currentTimeMillis());

        // 输出图片流
        // 一定要在最后，否则StpUtil Cookie没设置好就返回数据了
        captcha.out(response.getOutputStream());

        // 使用默认工具，使用http session完成
        //CaptchaUtil.out(request, response);
    }
}

package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import com.yunmuq.kingyanplus.config.LoginConfigEntity;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.request.LoginRequest;
import com.yunmuq.kingyanplus.model.response.CommonResponse;
import com.yunmuq.kingyanplus.model.response.LoginConfigResponse;
import com.yunmuq.kingyanplus.model.response.LoginResponse;
import com.yunmuq.kingyanplus.service.security.UserPassword;
import com.yunmuq.kingyanplus.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.Locale;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-02
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
@RequestMapping("/auth")
public class Login {

    @Autowired
    LoginConfigEntity loginConfigEntity;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserPassword userPassword;

    @Autowired
    private MessageSource messageSource;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 图片验证码过期时间，单位ms
     */
    private final int captchaTimeout = 60000;

    /**
     * 验证码长度
     */
    private final int captchaLength = 5;

    @GetMapping("/getLoginConfig")
    public LoginConfigResponse getLoginConfig() {
        LoginConfigResponse loginConfigResponse = new LoginConfigResponse(loginConfigEntity.getPublicKeyHex());
        return loginConfigResponse;
    }

    @GetMapping("/getCaptchaImg")
    public void captcha(HttpServletResponse response) throws Exception {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        GifCaptcha captcha = new GifCaptcha(130, 48, captchaLength);
        // 设置字体
        // captcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        captcha.setFont(Captcha.FONT_8);
        // 设置类型，纯数字、纯字母、字母数字混合
        captcha.setCharType(Captcha.TYPE_NUM_AND_UPPER);
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

    /**
     * 未勾选记住我则session不会添加时间属性，浏览器默认在下次启动后清空cookie。<b>后端仍保存此cookie</b>
     * 勾选后，Set-Cookie时添加过期时间，关闭浏览器不会丢失cookie
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginMsg) {
        LoginResponse loginResponse = new LoginResponse();
        Locale locale = LocaleContextHolder.getLocale();

        ////// 1.校验验证码
        String tips = messageSource.getMessage("login.captcha-fail", null, locale);
        String captcha = loginMsg.getCaptcha();
        // 短路或，左边是true右边就不会运算
        if (captcha == null || captcha.length() != captchaLength) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        SaSession tokenSession = StpUtil.getTokenSession();
        Long captchaCreateTime = (Long) tokenSession.get("captchaCreateTime");
        if (captchaCreateTime == null) {
            tips = messageSource.getMessage("login.captcha-missing", null, locale);
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        if (System.currentTimeMillis() - captchaCreateTime > captchaTimeout) {
            tips = messageSource.getMessage("login.captcha-timeout", null, locale);
            tips = tips.replace("{}", String.valueOf(captchaTimeout/1000));
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        if (!captcha.toLowerCase().equals(tokenSession.get("captcha"))) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        StpUtil.getTokenSession().clear();
        loginResponse.setCaptchaCheck(true);

        ////// 2.查用户
        tips = messageSource.getMessage("login.fail", null, locale);
        String userName = loginMsg.getUserName();
        String userPwd = loginMsg.getPassword();
        if (userName == null || userName.equals("") || userPwd == null || userPwd.equals("")) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        User user = userMapper.selectUserByUserName(userName);
        if (user == null) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }

        ////// 3.校验密码
        boolean matchPwd = false;
        try {
            matchPwd = userPassword.matchUserPassword(user.getPassword(), userPwd);
        } catch (Exception e) {
            tips = messageSource.getMessage("login.invalid-pwd", null, locale);
            logger.info("login输入非法密码，服务器可能遭受攻击", e);
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        if (!matchPwd) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }

        ////// 4.验证通过，刷新session
        // 先登出再登录，刷新会话
        StpUtil.logout();
        // id参数必须是用户名，后续使用这个查数据库获取用户角色
        StpUtil.login(loginMsg.getUserName(), new SaLoginModel()
                .setIsLastingCookie(loginMsg.isRememberMe())   // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
        );
        // 密码不要给前端
        user.setPassword(null);
        tips = messageSource.getMessage("login.success", null, locale);
        loginResponse.setMsg(tips);
        loginResponse.setSuccess(StpUtil.isLogin());
        return loginResponse;
    }

    @GetMapping("/logout")
    public CommonResponse logout() {
        StpUtil.logout();
        Locale locale = LocaleContextHolder.getLocale();
        String tips = messageSource.getMessage("logout.success", null, locale);
        return new CommonResponse(!StpUtil.isLogin(), tips);
    }

    @GetMapping("/hello")
    @SaCheckRole("admin")
    public String hello() {
        return "hello";
    }
}

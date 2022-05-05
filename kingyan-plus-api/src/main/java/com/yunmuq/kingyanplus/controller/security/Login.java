package com.yunmuq.kingyanplus.controller.security;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.base.Captcha;
import com.yunmuq.kingyanplus.config.KingYanConfig;
import com.yunmuq.kingyanplus.config.LoginConfigEntity;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.request.LoginRequest;
import com.yunmuq.kingyanplus.model.response.CommonResponse;
import com.yunmuq.kingyanplus.model.response.LoginConfigResponse;
import com.yunmuq.kingyanplus.model.response.LoginResponse;
import com.yunmuq.kingyanplus.service.security.UserPassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.UUID;

/**
 * 登录登出是操作HTTP的，没法写进service中，但是也没有删、改查操作。而且也不算业务，是安全方面的代码
 * 注册更类似于业务
 *
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

    @Autowired
    KingYanConfig kingyanConfig;

    @GetMapping("/getLoginConfig")
    public LoginConfigResponse getLoginConfig() {
        LoginConfigResponse loginConfigResponse = new LoginConfigResponse(loginConfigEntity.getPublicKeyHex());
        return loginConfigResponse;
    }

    /**
     * 未勾选记住我则session不会添加时间属性，浏览器默认在下次启动后清空cookie。<b>后端仍保存此cookie</b>
     * 勾选后，Set-Cookie时添加过期时间，关闭浏览器不会丢失cookie
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginMsg, HttpServletResponse response) {
        LoginResponse loginResponse = new LoginResponse();
        Locale locale = LocaleContextHolder.getLocale();

        ////// 1.校验验证码
        final int captchaLength = kingyanConfig.getCaptcha().getCaptchaLength();
        final int captchaTimeout = kingyanConfig.getCaptcha().getCaptchaTimeout();
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
            tips = tips.replace("{}", String.valueOf(captchaTimeout / 1000));
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
        String uuid = UUID.randomUUID().toString();
        StpUtil.getTokenSession().set("csrfToken", uuid);
        // 使用X-XSRF-TOKEN，校验头，前端axios会自动添加到header中
        Cookie csrfCookie = new Cookie("X-XSRF-TOKEN", uuid);
        // long转int
        csrfCookie.setMaxAge((int) SaManager.getConfig().getTimeout());
        // todo: 添加一个不存在的path，让浏览器不在Cookie header中带上，但是axios自动带上，节省网络开销
        csrfCookie.setPath("/");
        response.addCookie(csrfCookie);
        // 密码不要给前端
        user.setPassword(null);
        tips = messageSource.getMessage("login.success", null, locale);
        loginResponse.setMsg(tips);
        loginResponse.setSuccess(StpUtil.isLogin());
        return loginResponse;
    }

    @GetMapping("/logout")
    public CommonResponse logout() {
        StpUtil.getTokenSession().clear();
        StpUtil.logout();
        Locale locale = LocaleContextHolder.getLocale();
        String tips = messageSource.getMessage("logout.success", null, locale);
        return new CommonResponse(!StpUtil.isLogin(), tips);
    }
}

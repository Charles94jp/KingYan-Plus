package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.config.LoginConfigEntity;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.request.LoginRequest;
import com.yunmuq.kingyanplus.model.response.LoginConfigResponse;
import com.yunmuq.kingyanplus.model.response.LoginResponse;
import com.yunmuq.kingyanplus.service.security.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-02
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
public class Login {

    @Autowired
    LoginConfigEntity loginConfigEntity;

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserPassword userPassword;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/getLoginConfig")
    public LoginConfigResponse getLoginConfig() {
        LoginConfigResponse loginConfigResponse = new LoginConfigResponse(loginConfigEntity.getPublicKeyHex());
        return loginConfigResponse;
    }

    /**
     * 未勾选记住我则session不会添加时间属性，浏览器默认在下次启动后清空cookie。<b>后端仍保存此cookie</b>
     * 勾选后，Set-Cookie时添加过期时间，关闭浏览器不会丢失cookie
     * todo: 验证码、图片base64
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginMsg) {
        User user = userMapper.selectUserByUserName(loginMsg.getUserName());
        Locale locale = LocaleContextHolder.getLocale();
        String tips = messageSource.getMessage("login.fail", null, locale);
        if (user == null) {
            return new LoginResponse(false, tips, null);
        }
        if (!userPassword.matchUserPassword(user.getPassword(), loginMsg.getPassword())) {
            return new LoginResponse(false, tips, null);
        }
        // 先登出再登录，刷新会话
        StpUtil.logout();
        // id参数必须是用户名，后续使用这个查数据库获取用户角色
        StpUtil.login(loginMsg.getUserName(), new SaLoginModel()
                .setIsLastingCookie(loginMsg.isRememberMe())   // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
        );
        // 密码不要给前端
        user.setPassword(null);
        tips = messageSource.getMessage("login.success", null, locale);
        return new LoginResponse(StpUtil.isLogin(), tips, user);
    }

    @GetMapping("/logout")
    public LoginResponse logout() {
        StpUtil.logout();
        Locale locale = LocaleContextHolder.getLocale();
        String tips = messageSource.getMessage("logout.fail", null, locale);
        return new LoginResponse(!StpUtil.isLogin(), tips, null);
    }

    @GetMapping("/hello")
    @SaCheckRole("admin")
    public String hello(){
        return "hello";
    }
}

package com.yunmuq.kingyanplus.controller.security;

import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.CheckCaptchaResult;
import com.yunmuq.kingyanplus.model.request.RegistrationRequest;
import com.yunmuq.kingyanplus.model.response.LoginResponse;
import com.yunmuq.kingyanplus.service.security.CaptchaService;
import com.yunmuq.kingyanplus.service.security.UserPassword;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * todo: feat
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-05
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class Registration {

    private final UserMapper userMapper;

    private final UserPassword userPassword;

    private final MessageSource messageSource;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CaptchaService captchaService;

    private static final Pattern userNamePattern = Pattern.compile("^[a-zA-Z0-9]{3,}$");

    @PostMapping("/register")
    public LoginResponse register(@RequestBody RegistrationRequest requestParam) {
        LoginResponse loginResponse = new LoginResponse();
        Locale locale = LocaleContextHolder.getLocale();

        ////// 1.校验验证码
        CheckCaptchaResult checkCaptchaResult = captchaService.checkCaptcha(requestParam.getCaptcha());
        if (!checkCaptchaResult.isSuccess()) {
            loginResponse.setMsg(checkCaptchaResult.getMsg());
            return loginResponse;
        }
        StpUtil.getTokenSession().clear();
        loginResponse.setCaptchaCheck(true);

        ////// 2.查用户，不能重复，校验用户名合法性
        String tips = messageSource.getMessage("registration.user-name-failed", null, locale);
        String userName = requestParam.getUserName();
        String userPwd = requestParam.getPassword();
        if (userName == null || userName.equals("") || userPwd == null || userPwd.equals("")) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        try {
            userName = new String(userPassword.decryptSM2(userName));
        } catch (Exception e) {
            tips = messageSource.getMessage("login.invalid-pwd", null, locale);
            logger.info("register输入非法用户名，服务器可能遭受攻击", e);
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        if (!userNamePattern.matcher(userName).find()) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }
        User user = userMapper.selectUserByUserNameWithoutAuth(userName);
        if (user != null) {
            loginResponse.setMsg(tips);
            return loginResponse;
        }

        ////// 3.处理密码：解密SM2，再加密SM3准备入库
        try {
            userPwd = userPassword.sm3Hash(userPassword.decryptSM2(userPwd));
        } catch (Exception e) {
            tips = messageSource.getMessage("login.invalid-pwd", null, locale);
            logger.info("login输入非法密码，服务器可能遭受攻击", e);
            loginResponse.setMsg(tips);
            return loginResponse;
        }

        ////// 4.校验通过，存入数据库
        User insertedUser = new User();
        insertedUser.setName(userName);
        insertedUser.setPassword(userPwd);
        insertedUser.setEmail(requestParam.getEmail());
        userMapper.insertUser(insertedUser);

        tips = messageSource.getMessage("registration.success", null, locale);
        loginResponse.setMsg(tips);
        loginResponse.setSuccess(true);
        return loginResponse;
    }
}

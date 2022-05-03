package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.Requests.LoginRequest;
import com.yunmuq.kingyanplus.model.Responses.LoginResponse;
import com.yunmuq.kingyanplus.service.security.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    UserMapper userMapper;

    @Autowired
    UserPassword userPassword;

    /**
     * 未勾选记住我则session不会添加时间属性，浏览器默认在下次启动后清空cookie。<b>后端仍保存此cookie</b>
     * 勾选后，Set-Cookie时添加过期时间，关闭浏览器不会丢失cookie
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginMsg) {
        User user = userMapper.selectUserByUserName(loginMsg.getUserName());
        if (user == null) {
            return new LoginResponse(false, "用户名或密码错误", null);
        }
        if (!userPassword.matchUserPassword(user.getPassword(), loginMsg.getPassword())) {
            return new LoginResponse(false, "用户名或密码错误", null);
        }
        // 先登出再登录，刷新会话
        StpUtil.logout();
        // id参数必须是用户名
        StpUtil.login(loginMsg.getUserName(), new SaLoginModel()
                .setIsLastingCookie(loginMsg.isRememberMe())        // 是否为持久Cookie（临时Cookie在浏览器关闭时会自动删除，持久Cookie在重新打开后依然存在）
        );
        // 密码不要给前端
        user.setPassword(null);
        return new LoginResponse(StpUtil.isLogin(), "登录成功", user);
    }

    @GetMapping("/logout")
    public LoginResponse logout() {
        StpUtil.logout();
        return new LoginResponse(!StpUtil.isLogin(), "登出成功", null);
    }

    @GetMapping("/hello")
    @SaCheckRole("admin")
    public String hello(){
        return "hello";
    }
}

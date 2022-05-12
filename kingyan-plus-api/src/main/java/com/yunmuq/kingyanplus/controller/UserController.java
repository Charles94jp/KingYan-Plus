package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.dto.User;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-10
 * @since 1.8
 * @since spring boot 2.6.6
 */
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/getUserInfo")
    @SaCheckLogin
    public User getUserInfo(){
        String userName = (String) StpUtil.getLoginId();
        User user = userMapper.selectUserByUserName(userName);
        // 密码不要给前端
        // 如果mapper开启二级缓存会污染缓存
        user.setPassword(null);
        return user;
    }
}

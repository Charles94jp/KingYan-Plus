package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.yunmuq.kingyanplus.mapper.UserMapper;
import com.yunmuq.kingyanplus.model.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-04
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
@RequiredArgsConstructor
public class Hello {

    private final UserMapper userMapper;

    @GetMapping("/all/hello")
    @SaCheckLogin
    public CommonResponse helloToAll() {
        return new CommonResponse(true, "Hello");
    }

    @GetMapping("/test/hello")
    @SaCheckPermission(value = {"hello-test", "hello-admin"}, mode = SaMode.OR)
    public CommonResponse helloToTest() {
        return new CommonResponse(true, "Hello, you have test role");
    }

    @GetMapping("/admin/hello")
    @SaCheckPermission("hello-admin")
    public CommonResponse helloToAdmin() {
        return new CommonResponse(true, "Hello, you have admin role");
    }

    @PostMapping("/addTestRole")
    @SaCheckLogin
    public CommonResponse addTestRole() {
        List<String> permissions = (List<String>) StpUtil.getSession().get("permissions");
        boolean success = false;
        if (!permissions.contains("hello-test")) {
            String userName = (String) StpUtil.getLoginId();
            success = userMapper.addUserPermission(userName, "hello-test") > 0;
            if(success){
                permissions.add("hello-test");
            }
        }
        return new CommonResponse(success, "");
    }

    @PostMapping("/deleteTestRole")
    @SaCheckPermission("hello-test")
    public CommonResponse deleteTestRole() {
        String userName = (String) StpUtil.getLoginId();
        boolean success = userMapper.deleteUserPermission(userName, "hello-test") > 0;
        if (success){
            List<String> permissions = (List<String>) StpUtil.getSession().get("permissions");
            permissions.remove("hello-test");
        }
        return new CommonResponse(success, "");
    }
}

package com.yunmuq.kingyanplus.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-04
 * @since 1.8
 * @since spring boot 2.6.7
 */
@RestController
public class Hello {
    @GetMapping("/hello")
    @SaCheckRole("admin")
    public String hello() {
        return "hello";
    }
}

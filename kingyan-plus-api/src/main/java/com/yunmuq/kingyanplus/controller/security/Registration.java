package com.yunmuq.kingyanplus.controller.security;

import com.yunmuq.kingyanplus.model.request.RegistrationRequest;
import com.yunmuq.kingyanplus.model.response.CommonResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class Registration {
    @PostMapping("/register")
    public CommonResponse register(@RequestBody RegistrationRequest registrationMsg) {
        return new CommonResponse();
    }
}

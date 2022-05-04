package com.yunmuq.kingyanplus.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-02
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String captcha;
    private String userName;
    private String password;
    private boolean rememberMe;
    //private String captcha;
}

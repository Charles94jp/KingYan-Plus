package com.yunmuq.kingyanplus.model.request;

import lombok.Data;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-07
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Data
public class RegistrationRequest {
    public String captcha;
    public String verificatioCode;
    public String userName;
    public String password;
}

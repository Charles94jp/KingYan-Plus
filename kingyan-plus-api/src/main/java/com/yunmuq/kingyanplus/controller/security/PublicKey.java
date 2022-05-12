package com.yunmuq.kingyanplus.controller.security;

import com.yunmuq.kingyanplus.config.LoginConfigEntity;
import com.yunmuq.kingyanplus.model.response.LoginConfigResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-12
 * @since 1.8
 * @since spring boot 2.6.6
 */
@RestController
@RequestMapping("/sec")
@RequiredArgsConstructor
public class PublicKey {

    private final LoginConfigEntity loginConfigEntity;

    @GetMapping("/getPublicKey")
    public LoginConfigResponse getPublicKey() {
        LoginConfigResponse loginConfigResponse = new LoginConfigResponse(loginConfigEntity.getPublicKeyHex());
        if (loginConfigEntity.isDynamicKeyPair()) {
            loginConfigResponse.setTimeout(loginConfigEntity.getCreateTime() + loginConfigEntity.getTimeout());
        }
        return loginConfigResponse;
    }
}

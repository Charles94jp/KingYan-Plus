package com.yunmuq.kingyanplus.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-20
 * @since 1.8
 * @since spring boot 2.6.6
 */
@Data
@NoArgsConstructor
public class LoginConfigResponse {
    private String publicKey;

    public LoginConfigResponse(String publicKey) {
        this.publicKey = publicKey;
    }
}

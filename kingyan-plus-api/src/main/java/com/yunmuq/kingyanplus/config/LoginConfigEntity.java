package com.yunmuq.kingyanplus.config;

import com.yunmuq.kingyanplus.util.sm.SMCrypto;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * springboot @Value @Autowired 都不支持static
 * 用@Autowired获取，不会存在性能问题，不会每个请求都构建一次
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-20
 * @since 1.8
 * @since spring boot 2.6.6
 */
@Component
public class LoginConfigEntity {
    @Value("${sm2-key-pair.public-key-hex}")
    private String publicKeyHex;
    @Value("${sm2-key-pair.private-key-hex}")
    private String privateKeyHex;

    private ECPrivateKeyParameters privateKeyParameters;

    /**
     * @deprecated use getPrivateKeyParameters
     */
    public String getPrivateKeyHex() {
        return privateKeyHex;
    }

    public String getPublicKeyHex() {
        return publicKeyHex;
    }

    public ECPrivateKeyParameters getPrivateKeyParameters() {
        if (privateKeyParameters==null){
            privateKeyParameters = SMCrypto.SM2.buildECPrivateKeyParameters(privateKeyHex);
        }
        return privateKeyParameters;
    }
}

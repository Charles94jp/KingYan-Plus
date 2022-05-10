package com.yunmuq.kingyanplus.config;

import com.yunmuq.kingyanplus.util.sm.KeyPairHex;
import com.yunmuq.kingyanplus.util.sm.SMCrypto;
import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "kingyan.sm2-key-pair")
public class LoginConfigEntity {
    @Getter
    @Setter
    private String publicKeyHex;
    @Getter
    @Setter
    private String privateKeyHex;
    @Getter
    @Setter
    private boolean dynamicKeyPair = true;

    @Getter
    private long createTime = System.currentTimeMillis();

    /** 到期自动更新key(ms) */
    @Getter
    @Setter
    private long timeout = 86400_000;

    @Getter
    private ECPrivateKeyParameters privateKeyParameters;

    /**
     * @deprecated use getPrivateKeyParameters
     */
    public String getPrivateKeyHex() {
        handlingExpiredKey();
        return privateKeyHex;
    }

    public String getPublicKeyHex() {
        handlingExpiredKey();
        return publicKeyHex;
    }

    public ECPrivateKeyParameters getPrivateKeyParameters() {
        if (privateKeyParameters == null) {
            privateKeyParameters = SMCrypto.SM2.buildECPrivateKeyParameters(privateKeyHex);
        }
        handlingExpiredKey();
        return privateKeyParameters;
    }

    /**
     * 检查密钥对是否过期，过期则更新密钥对
     * 动态密钥对带来的问题：前端需要从载入登录页面获取公钥变为登录一次后获取公钥。或者获取公钥时给予一个过期时间
     */
    private void handlingExpiredKey() {
        long now = System.currentTimeMillis();
        // publicKeyHex == null则相当于充当构造函数了
        if (dynamicKeyPair && (publicKeyHex == null || now - createTime > timeout)) {
            KeyPairHex keyPairHex = SMCrypto.SM2.generateKeyPairHex();
            publicKeyHex = keyPairHex.getPublicKey();
            privateKeyHex = keyPairHex.getPrivateKey();
            privateKeyParameters = SMCrypto.SM2.buildECPrivateKeyParameters(privateKeyHex);
            createTime = now;
        }
    }
}

package com.yunmuq.kingyanplus.util.sm;

/**
 * this is required by {@link SMCrypto}
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-17
 * @since 1.8
 * @since spring boot 2.6.6
 */
public class KeyPairHex {
    private String privateKey;
    private String publicKey;

    public KeyPairHex() {
    }

    public KeyPairHex(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}

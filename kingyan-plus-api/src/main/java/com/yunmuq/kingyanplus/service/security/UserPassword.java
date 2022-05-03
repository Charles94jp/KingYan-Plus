package com.yunmuq.kingyanplus.service.security;

/**
 * 包含了KingYan-plus的密码加密解密逻辑，数据库中密码格式
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
public interface UserPassword {
    /**
     * 将前端的密码解密为明文，明文后续用于对比或入库
     *
     * @param PwdEncryptedBySM2 前端传来的密文
     * @return 明文，二进制
     */
    public byte[] decryptSM2(String PwdEncryptedBySM2);

    /**
     * 对比数据库中的密码和用户传的密码是否一致
     *
     * @param PwdHashedBySM3 数据库中SM3加密过的密码
     * @param PwdEncryptedBySM2 前端传来的密文
     * @return
     */
    public boolean matchUserPassword(String PwdHashedBySM3,String PwdEncryptedBySM2);

    /**
     * 将明文进行sm3加密，存储到数据库
     *
     * @param plainPassword
     * @return
     */
    public String sm3Hash(byte[] plainPassword);
}

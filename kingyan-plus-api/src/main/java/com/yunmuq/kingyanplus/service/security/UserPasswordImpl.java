package com.yunmuq.kingyanplus.service.security;

import com.yunmuq.kingyanplus.config.LoginConfigEntity;
import com.yunmuq.kingyanplus.util.LogUtil;
import com.yunmuq.kingyanplus.util.gmhelper.SM3Util;
import com.yunmuq.kingyanplus.util.sm.SMCrypto;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * 包含了KingYan-plus的密码加密解密逻辑，数据库中密码格式
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-05-03
 * @since 1.8
 * @since spring boot 2.6.7
 */
@Service
public class UserPasswordImpl implements UserPassword {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LoginConfigEntity loginConfigEntity;

    /**
     * 盐值长度：设置为和sm3 hash结果长度相同
     */
    private static final int saltLength = 32;

    /**
     * 将前端的密码解密为明文，明文后续用于对比或入库
     *
     * @param PwdEncryptedBySM2 前端传来的密文，sm-crypto库加密后的hex str to Uint8Array to base64
     * @return 明文，二进制
     * @throws InvalidCipherTextException 密码格式错误
     * @throws DecoderException           密码格式错误
     */
    @Override
    public byte[] decryptSM2(String PwdEncryptedBySM2) throws InvalidCipherTextException {
        return SMCrypto.SM2.doDecryptBase64(PwdEncryptedBySM2, loginConfigEntity.getPrivateKeyParameters());
    }

    /**
     * 对比数据库中的密码和用户传的密码是否一致
     *
     * @param PwdHashedBySM3    数据库中SM3加密过的密码
     * @param PwdEncryptedBySM2 前端传来的密文
     * @return
     * @throws InvalidCipherTextException 密码格式错误
     * @throws DecoderException           密码格式错误
     */
    @Override
    public boolean matchUserPassword(String PwdHashedBySM3, String PwdEncryptedBySM2) throws InvalidCipherTextException {
        byte[] plainPwd = decryptSM2(PwdEncryptedBySM2);
        byte[] encodedPasswordSM3Hash = Base64.decode(PwdHashedBySM3);
        if (encodedPasswordSM3Hash.length != saltLength * 2) {
            logger.error("SM3密码密文格式错误，可能是数据库被污染",
                    new Exception("SM3密文长度和盐值长度不相等"));
            return false;
        }
        // 前面是盐值，后面是真的hash
        byte[] realEncodedPasswordSM3Hash = new byte[saltLength];
        System.arraycopy(encodedPasswordSM3Hash, saltLength, realEncodedPasswordSM3Hash, 0, saltLength);

        byte[] join = new byte[saltLength + plainPwd.length];
        System.arraycopy(encodedPasswordSM3Hash, 0, join, 0, saltLength);
        System.arraycopy(plainPwd, 0, join, saltLength, plainPwd.length);

        byte[] sm3Hash = SM3Util.hash(join);
        return Arrays.equals(sm3Hash, realEncodedPasswordSM3Hash);
    }

    /**
     * 将明文进行sm3 hash，同时将hash值和盐值一起保存到数据库
     *
     * @param plainPassword
     * @return
     */
    @Override
    public String sm3Hash(byte[] plainPassword) {
        // 生成盐值，和sm3 hash结果长度相同
        byte[] salt = (new SecureRandom()).generateSeed(saltLength);

        // 加盐：盐值+密码
        byte[] join = new byte[saltLength + plainPassword.length];
        System.arraycopy(salt, 0, join, 0, saltLength);
        System.arraycopy(plainPassword, 0, join, saltLength, plainPassword.length);
        byte[] sm3Hash = SM3Util.hash(join);

        if (sm3Hash.length != saltLength) {
            logger.error("SM3加密用户密码时出错",
                    new Exception("SM3密文长度和盐值长度不相等"));
            return null;
        }
        // 把盐值也保存，才能校验
        byte[] result = new byte[saltLength * 2];
        System.arraycopy(salt, 0, result, 0, saltLength);
        System.arraycopy(sm3Hash, 0, result, saltLength, saltLength);

        return Base64.toBase64String(result);
    }
}

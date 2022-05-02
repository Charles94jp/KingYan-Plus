package com.yunmuq.kingyanplus.util.sm;

import com.yunmuq.kingyan.util.gmhelper.BCECUtil;
import com.yunmuq.kingyan.util.gmhelper.SM2Util;
import com.yunmuq.kingyan.util.gmhelper.SM3Util;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


/**
 * 此类将Java国密组件gmhelper和前端国密组件sm-crypto适配，主要是sm-crypto不合规范，否则gmhelper可直接使用，无需再封装
 * 实际情景中，后端只需使用到 SM2私钥解密以及 SM3 算法
 * sm2公私钥可从部署时的配置文件读取，HexString，私钥可提前转为ECPrivateKeyParameters存储，公钥返回前端
 * <p>
 * package gmhelper is shaded from<a href="https://github.com/ZZMarquis/gmhelper">gmhelper</a>
 * gmhelper使用方法参见其单元测试，它依赖bouncycastle的bcprov、bcpkix两个库
 * 前端国密使用<a href="https://www.npmjs.com/package/sm-crypto">sm-crypto</a>，它有js引擎实现的Java版
 * 这里使用gmhelper封装一个对接前端的原生Java工具
 * 暂时只实现sm2的生成密钥对、加解密，sm3加密功能
 *
 * @author yunmuq
 * @version v1.0.0
 * @since 2022-04-17
 * @since 1.8
 * @since spring boot 2.6.6
 */
public class SMCrypto {
    public static class SM2 {
        public static KeyPairHex generateKeyPairHex() {
            AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPairParameter();
            ECPrivateKeyParameters priKey = (ECPrivateKeyParameters) keyPair.getPrivate();
            ECPublicKeyParameters pubKey = (ECPublicKeyParameters) keyPair.getPublic();
            String priKeyStr = Hex.toHexString(priKey.getD().toByteArray()).toUpperCase();
            String pubKeyStr = Hex.toHexString(pubKey.getQ().getEncoded(false)).toUpperCase();
            return new KeyPairHex(priKeyStr, pubKeyStr);
        }

        /**
         * 无需mode，默认是C1C3C2顺序的结构
         * 手动实现从publicKeyHex生成 ECPublicKeyParameters
         *
         * @return 即使入参相同，多次运行结果也不同
         */
        public static String doEncrypt(String msg, String publicKeyHex) throws InvalidCipherTextException {
            /**
             * 难点在于publicKeyHex转ECPublicKeyParameters
             * bc库的ECPoint：pubKey.getQ()可以{@link org.bouncycastle.math.ec.ECPoint#getEncoded}，却不支持decode
             * gmhelper的{@link BCECUtil}，从私钥构Parameters造公钥Parameters，可以从公钥 X Y值构造ECPublicKeyParameters
             * 却不支持从publicKeyHex构造，那只能根据{@link org.bouncycastle.math.ec.ECPoint#getEncoded}的逻辑
             * 从publicKeyHex中提取 X Y 值来构造
             *
             * publicKeyHex转byte[]，第一个字节是 0x04 ，后面是 X 和 Y，X和Y长度相同
             */
            byte[] encodedPubHex = Hex.decode(publicKeyHex);
            int xyLength = (encodedPubHex.length - 1) / 2;
            byte[] x1 = new byte[xyLength];
            byte[] y1 = new byte[xyLength];
            System.arraycopy(encodedPubHex, 1, x1, 0, xyLength);
            System.arraycopy(encodedPubHex, xyLength + 1, y1, 0, xyLength);
            ECPublicKeyParameters publicKey = BCECUtil.createECPublicKeyParameters(
                    x1, y1, SM2Util.CURVE, SM2Util.DOMAIN_PARAMS);

            byte[] result = SM2Util.encrypt(publicKey, msg.getBytes());
            return Hex.toHexString(result);
        }

        /**
         * 前端sm-crypto库密文不带 0x04 ，需要前端拼接上，或者后端拼接
         *
         * @param privateKeyHex 后端{@link #generateKeyPairHex}生成的 key。一是不可能由前端生成私钥，二使用前端sm-crypto库的 key 可能会报错
         * @deprecated 生产中使用 {@link #doDecrypt(String encryptDataHex, ECPrivateKeyParameters privateKey)}
         */
        public static String doDecrypt(String encryptDataHex, String privateKeyHex) throws InvalidCipherTextException {
            ECPrivateKeyParameters priKey = buildECPrivateKeyParameters(privateKeyHex);
            return new String(doDecrypt(encryptDataHex, priKey));
        }

        /**
         * 生产中使用这个
         */
        public static byte[] doDecrypt(String encryptDataHex, ECPrivateKeyParameters privateKey) throws InvalidCipherTextException {
            byte[] cipher = Hex.decode(encryptDataHex);
            byte[] realCipher = cipher;
            if (cipher[0] != 0x04) {
                realCipher = new byte[cipher.length + 1];
                realCipher[0] = 0x04;
                System.arraycopy(cipher, 0, realCipher, 1, cipher.length);
            }
            return SM2Util.decrypt(privateKey, realCipher);
        }

        /**
         * 提前将配置文件中privateKeyHex转为ECPrivateKeyParameters缓存
         */
        public static ECPrivateKeyParameters buildECPrivateKeyParameters(String privateKeyHex) {
            return new ECPrivateKeyParameters(new BigInteger(Hex.decode(privateKeyHex)), SM2Util.DOMAIN_PARAMS);
        }
    }

    public static String sm3(String msg) {
        byte[] result = SM3Util.hash(msg.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(result);
    }
}

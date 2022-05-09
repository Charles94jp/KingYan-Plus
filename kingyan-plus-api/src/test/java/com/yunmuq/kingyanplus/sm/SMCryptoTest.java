package com.yunmuq.kingyanplus.sm;

import com.yunmuq.kingyanplus.util.sm.KeyPairHex;
import com.yunmuq.kingyanplus.util.sm.SMCrypto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 需要结合前端库 sm-crypto 进行测试
 * <b>部署时不要使用本文件中的密钥对，需要运行单元测试生成</b>
 */
class SMCryptoTest {

    /**
     * 测试生成密钥、加解密
     */
    @Test
    public void testGenerateKeyAndEncryptAndDecrypt() throws Exception {
        KeyPairHex keyPairHex = SMCrypto.SM2.generateKeyPairHex();
        String privateKey = keyPairHex.getPrivateKey();
        String publicKey = keyPairHex.getPublicKey();
        System.out.println("privateKey:\n" + privateKey);
        System.out.println("publicKey:\n" + publicKey);

        final String plainText = "Admin@123";
        System.out.println("plaintext:\n" + plainText);
        String cipherText = SMCrypto.SM2.doEncrypt(plainText, publicKey);
        System.out.println("ciphertext:\n" + cipherText);
        String decryptResult = SMCrypto.SM2.doDecrypt(cipherText, privateKey);
        System.out.println("decrypt result:\n" + decryptResult);
        assertEquals(plainText, decryptResult);
    }

    /**
     * 使用前端sm-crypto生成的密钥，无法通过测试
     */
    @Test
    public void testEncryptAndDecryptKeyFromFrontEnd() throws Exception {
        final String privateKey = "9eee1e6b9e305fd78257a41bcb3ad48421b40c1a11957742759d1560b991b05e";
        final String publicKey = "048e45fb3d7574f8a4a36f42bacc1da3aa22c0d388ba65a809178be387832b55bec7c4f026431f2edec3285b9d1bd2cff5f55b5dff2f9f9422619e86470a949857";
        System.out.println("privateKey:\n" + privateKey);
        System.out.println("publicKey:\n" + publicKey);

        final String plainText = "Admin@123";
        System.out.println("plaintext:\n" + plainText);
        String cipherText = SMCrypto.SM2.doEncrypt(plainText, publicKey);
        System.out.println("ciphertext:\n" + cipherText);
        String decryptResult = SMCrypto.SM2.doDecrypt(cipherText, privateKey);
        System.out.println("decrypt result:\n" + decryptResult);
        assertEquals(plainText, decryptResult);
    }

    /**
     * 使用后端的Key
     * 前端sm-crypto使用后端的公钥加密后，后端来解密
     */
    @Test
    public void testEncryptAndDecryptCipherFromFrontEnd() throws Exception {
        // 后端生成的Key
        final String privateKey = "008DEF31DB9A71AF4FFA89C58781AC529449FB9B3B82066CF02A9E909DB1FA36F7";
        final String publicKey = "0441235EF7DE5457F26AAFBF7FBA48F586A30B11F027F5F74A73DB3AE1EA06CD07B841B4ABE8202FCDDAD1D1A84E2D581FBFE7257BAA49D1EF2353AC719BED5E21";
        final String plainText = "12345h";
        // 前端sm-crypto使用后端的公钥加密
        String cipherText = "a9e6d9a525011008546d90d0726495f728eee291c142475653ab744b86c3e951f02fc6678c5deee0f90603017c16e51285452a4648526d0084384ce513c794fbb7bdd1b837f3403bf561eb295e0a9a25c841730de511dbbb5afb74bded3d1590baa9bc9087b1";

        System.out.println("privateKey:\n" + privateKey);
        System.out.println("publicKey:\n" + publicKey);
        System.out.println("plaintext:\n" + plainText);
        System.out.println("ciphertext:\n" + cipherText);

        String decryptResult = SMCrypto.SM2.doDecrypt(cipherText, privateKey);
        System.out.println("decrypt result:\n" + decryptResult);
        assertEquals(plainText, decryptResult);
    }

    /**
     * 使用后端的Key
     * 前端sm-crypto使用后端的公钥加密后，转字节数组转base64，后端来解密
     */
    @Test
    public void testEncryptAndDecryptCipherFromFrontEndBase64() throws Exception {
        // 后端生成的Key
        final String privateKey = "00D35658E871A7183F9248C7DAFF30330EB0CFBE7FFDDAFECDADF022E8A9A41588";
        final String publicKey = "04AFA94981BA33B3543951EF7DC675C02295004BD90298B70119639508B74899BF92C063DD6765898986655111D706EF9D4CA8DB94328981856633B834E49998AE";
        final String plainText = "123@Test";
        // 前端sm-crypto使用后端的公钥加密
        // node.js命令行，vue中不可使用Buffer，会在浏览器中报错
        // const sm2 = require('sm-crypto').sm2;
        // const {Base64} = require('js-base64');
        // b=sm2.doEncrypt('123@Test',publicKey,1);
        // b=Base64.fromUint8Array(Uint8Array.from(Buffer.from(b,'hex')));
        String cipherText = "XSsw80VtmsYKtZsfW6MF10+JpmWxVL0a0L6bhDpixY/oGPi5TUrj0wEqr9qkTQ8C+4GspQmnyED9gYNs/cwbcAi2Tvfaft6JRITM6EEuxHtwITou5VJXR+pHHVOf/Ejjgv7lFkneZTI=";

        System.out.println("privateKey:\n" + privateKey);
        System.out.println("publicKey:\n" + publicKey);
        System.out.println("plaintext:\n" + plainText);
        System.out.println("ciphertext:\n" + cipherText);

        byte[] decryptResultByte = SMCrypto.SM2.doDecryptBase64(cipherText, SMCrypto.SM2.buildECPrivateKeyParameters(privateKey));
        String decryptResult = new String(decryptResultByte);
        System.out.println("decrypt result:\n" + decryptResult);
        assertEquals(plainText, decryptResult);
    }
}
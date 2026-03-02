package net.ooder.util.crypto;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加密工具类
 * 提供 RSA 加密/解密/签名/验签功能
 *
 * @author ooder
 * @since 2.3
 */
public class RSAUtil {

    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 生成密钥对
     *
     * @param keySize 密钥长度（1024/2048/4096）
     * @return 密钥对
     */
    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(keySize);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new CryptoException("生成RSA密钥对失败", e);
        }
    }

    /**
     * 公钥加密
     *
     * @param plainText 明文
     * @param publicKey Base64编码的公钥
     * @return Base64编码的密文
     */
    public static String encryptByPublicKey(String plainText, String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PublicKey key = factory.generatePublic(spec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CryptoException("RSA公钥加密失败", e);
        }
    }

    /**
     * 私钥解密
     *
     * @param cipherText Base64编码的密文
     * @param privateKey Base64编码的私钥
     * @return 明文
     */
    public static String decryptByPrivateKey(String cipherText, String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = factory.generatePrivate(spec);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encrypted = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException("RSA私钥解密失败", e);
        }
    }

    /**
     * 私钥签名
     *
     * @param data       待签名数据
     * @param privateKey Base64编码的私钥
     * @return Base64编码的签名
     */
    public static String sign(String data, String privateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey key = factory.generatePrivate(spec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signed = signature.sign();

            return Base64.getEncoder().encodeToString(signed);
        } catch (Exception e) {
            throw new CryptoException("RSA签名失败", e);
        }
    }

    /**
     * 公钥验签
     *
     * @param data      原始数据
     * @param sign      Base64编码的签名
     * @param publicKey Base64编码的公钥
     * @return 是否验证通过
     */
    public static boolean verify(String data, String sign, String publicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PublicKey key = factory.generatePublic(spec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(key);
            signature.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signBytes = Base64.getDecoder().decode(sign);

            return signature.verify(signBytes);
        } catch (Exception e) {
            throw new CryptoException("RSA验签失败", e);
        }
    }

    /**
     * 将密钥对转为 Base64 字符串
     *
     * @param keyPair 密钥对
     * @return [公钥, 私钥]
     */
    public static String[] encodeKeyPair(KeyPair keyPair) {
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return new String[]{publicKey, privateKey};
    }
}

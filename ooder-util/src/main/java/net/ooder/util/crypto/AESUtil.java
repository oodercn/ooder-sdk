package net.ooder.util.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES 加密工具类
 * 提供 AES/CBC/PKCS5Padding 加密/解密功能
 *
 * @author ooder
 * @since 2.3
 */
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int IV_LENGTH = 16;

    /**
     * 加密
     *
     * @param plainText 明文
     * @param key       密钥 (16/24/32字节)
     * @return Base64编码的密文（包含IV）
     */
    public static String encrypt(String plainText, String key) {
        try {
            // 生成随机IV
            byte[] iv = new byte[IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 创建密钥
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 加密
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // 组合IV和密文
            byte[] combined = new byte[IV_LENGTH + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new CryptoException("AES加密失败", e);
        }
    }

    /**
     * 解密
     *
     * @param cipherText Base64编码的密文（包含IV）
     * @param key        密钥
     * @return 明文
     */
    public static String decrypt(String cipherText, String key) {
        try {
            byte[] combined = Base64.getDecoder().decode(cipherText);

            // 提取IV
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 提取密文
            byte[] encrypted = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

            // 创建密钥
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);

            // 解密
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(encrypted);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException("AES解密失败", e);
        }
    }

    /**
     * 生成随机密钥
     *
     * @param keySize 密钥长度（128/192/256）
     * @return Base64编码的密钥
     */
    public static String generateKey(int keySize) {
        try {
            int length = keySize / 8;
            byte[] key = new byte[length];
            new SecureRandom().nextBytes(key);
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            throw new CryptoException("生成密钥失败", e);
        }
    }
}

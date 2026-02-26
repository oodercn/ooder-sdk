package net.ooder.sdk.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * 安全管理器接口
 *
 * <p>管理 Skill 的安全验证,包括完整性校验和签名验证</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface SecurityManager {

    /**
     * 计算文件 SHA256 哈希
     *
     * @param data 文件数据
     * @return SHA256 哈希值 (十六进制字符串)
     */
    String calculateSHA256(byte[] data);

    /**
     * 验证文件完整性
     *
     * @param data 文件数据
     * @param expectedHash 期望的哈希值
     * @return 是否验证通过
     */
    boolean verifyIntegrity(byte[] data, String expectedHash);

    /**
     * 验证 GPG 签名
     *
     * @param data 文件数据
     * @param signature 签名数据
     * @param publicKey 公钥
     * @return 是否验证通过
     */
    boolean verifySignature(byte[] data, byte[] signature, String publicKey);

    /**
     * 验证 Skill 包
     *
     * @param skillId Skill ID
     * @param packageData 包数据
     * @param metadata 元数据 (包含哈希和签名)
     * @return 验证结果
     */
    VerificationResult verifySkillPackage(String skillId, byte[] packageData, Map<String, Object> metadata);

    /**
     * 添加受信任的公钥
     *
     * @param keyId 密钥ID
     * @param publicKey 公钥
     */
    void addTrustedKey(String keyId, String publicKey);

    /**
     * 移除受信任的公钥
     *
     * @param keyId 密钥ID
     */
    void removeTrustedKey(String keyId);

    /**
     * 检查密钥是否受信任
     *
     * @param keyId 密钥ID
     * @return 是否受信任
     */
    boolean isKeyTrusted(String keyId);

    /**
     * 验证结果
     */
    class VerificationResult {
        private boolean success;
        private String skillId;
        private boolean integrityVerified;
        private boolean signatureVerified;
        private String message;
        private long timestamp;

        public VerificationResult() {
            this.timestamp = System.currentTimeMillis();
        }

        public static VerificationResult success(String skillId) {
            VerificationResult result = new VerificationResult();
            result.success = true;
            result.skillId = skillId;
            result.integrityVerified = true;
            result.signatureVerified = true;
            result.message = "Verification successful";
            return result;
        }

        public static VerificationResult failure(String skillId, String message) {
            VerificationResult result = new VerificationResult();
            result.success = false;
            result.skillId = skillId;
            result.message = message;
            return result;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public boolean isIntegrityVerified() { return integrityVerified; }
        public void setIntegrityVerified(boolean integrityVerified) { this.integrityVerified = integrityVerified; }

        public boolean isSignatureVerified() { return signatureVerified; }
        public void setSignatureVerified(boolean signatureVerified) { this.signatureVerified = signatureVerified; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}

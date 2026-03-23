package net.ooder.scene.core.activation.executor;

import net.ooder.scene.core.activation.model.ActivationProcess;
import net.ooder.scene.core.spi.ActivationStepExecutor;
import net.ooder.scene.core.template.ActivationStepConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

public class GetKeyExecutor implements ActivationStepExecutor {

    private static final Logger log = LoggerFactory.getLogger(GetKeyExecutor.class);

    private static final String DEFAULT_KEY_PREFIX = "SK";
    private static final int DEFAULT_KEY_LENGTH = 32;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final SecureRandom random = new SecureRandom();

    @Override
    public String getStepType() {
        return "GET_KEY";
    }

    @Override
    public boolean canExecute(ActivationStepConfig stepConfig) {
        return "GET_KEY".equalsIgnoreCase(stepConfig.getStepType()) ||
               "get-key".equalsIgnoreCase(stepConfig.getStepType());
    }

    @Override
    public StepResult execute(
            ActivationStepConfig stepConfig, 
            ActivationProcess process, 
            Map<String, Object> context) {
        
        log.info("[execute] Executing GET_KEY step for scene: {}, step: {}", 
            process.getSceneId(), stepConfig.getStepId());
        
        try {
            Map<String, Object> config = stepConfig.getConfig();
            if (config == null) {
                config = new HashMap<>();
            }
            
            String keyType = (String) config.getOrDefault("keyType", "ACCESS");
            int keyLength = config.containsKey("keyLength") 
                ? ((Number) config.get("keyLength")).intValue() 
                : DEFAULT_KEY_LENGTH;
            long validityMs = config.containsKey("validityMs")
                ? ((Number) config.get("validityMs")).longValue()
                : 365L * 24 * 60 * 60 * 1000;
            
            String key = generateKey(keyLength);
            
            SceneKey sceneKey = new SceneKey();
            sceneKey.setKeyId(UUID.randomUUID().toString());
            sceneKey.setKey(key);
            sceneKey.setKeyType(keyType);
            sceneKey.setSceneId(process.getSceneId());
            sceneKey.setUserId(process.getUserId());
            sceneKey.setRoleId(process.getRoleId());
            sceneKey.setCreatedAt(System.currentTimeMillis());
            sceneKey.setExpiresAt(System.currentTimeMillis() + validityMs);
            sceneKey.setActive(true);
            
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) config.get("permissions");
            sceneKey.setPermissions(permissions != null ? permissions : new ArrayList<>());
            
            Map<String, Object> data = new HashMap<>();
            data.put("keyId", sceneKey.getKeyId());
            data.put("key", sceneKey.getKey());
            data.put("keyType", sceneKey.getKeyType());
            data.put("expiresAt", sceneKey.getExpiresAt());
            
            log.info("[execute] Key generated successfully: keyId={}, keyType={}", 
                sceneKey.getKeyId(), keyType);
            
            return StepResult.success("密钥生成成功", data);
            
        } catch (Exception e) {
            log.error("[execute] Failed to generate key", e);
            return StepResult.failure("密钥生成失败: " + e.getMessage());
        }
    }

    private String generateKey(int length) {
        StringBuilder sb = new StringBuilder(DEFAULT_KEY_PREFIX);
        sb.append("_");
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        
        return sb.toString();
    }

    public static class SceneKey {
        private String keyId;
        private String key;
        private String keyType;
        private String sceneId;
        private String userId;
        private String roleId;
        private long createdAt;
        private long expiresAt;
        private List<String> permissions;
        private boolean active;

        public String getKeyId() { return keyId; }
        public void setKeyId(String keyId) { this.keyId = keyId; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getKeyType() { return keyType; }
        public void setKeyType(String keyType) { this.keyType = keyType; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getRoleId() { return roleId; }
        public void setRoleId(String roleId) { this.roleId = roleId; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        public long getExpiresAt() { return expiresAt; }
        public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }
    }
}

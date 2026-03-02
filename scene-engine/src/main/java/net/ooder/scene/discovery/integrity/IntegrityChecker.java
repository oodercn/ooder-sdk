package net.ooder.scene.discovery.integrity;

import net.ooder.scene.discovery.api.DiscoveryService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 完整性检查器接口
 * 
 * 检查Skill包的完整性：
 * - 文件完整性（哈希校验）
 * - 依赖完整性
 * - 元数据完整性
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface IntegrityChecker {
    
    /**
     * 检查Skill完整性
     *
     * @param skillId Skill ID
     * @return 检查结果
     */
    CompletableFuture<DiscoveryService.IntegrityCheckResult> check(String skillId);
    
    /**
     * 检查Skill完整性（指定路径）
     *
     * @param skillId Skill ID
     * @param skillPath Skill路径
     * @return 检查结果
     */
    CompletableFuture<DiscoveryService.IntegrityCheckResult> check(String skillId, String skillPath);
    
    /**
     * 批量检查
     *
     * @param skillIds Skill ID列表
     * @return 检查结果列表
     */
    CompletableFuture<List<DiscoveryService.IntegrityCheckResult>> checkBatch(List<String> skillIds);
    
    /**
     * 修复完整性问题
     *
     * @param skillId Skill ID
     * @return 修复结果
     */
    CompletableFuture<RepairResult> repair(String skillId);
    
    // ========== 数据类定义 ==========
    
    /**
     * 修复结果
     */
    class RepairResult {
        private boolean success;
        private String skillId;
        private List<String> repairedFiles;
        private List<String> failedFiles;
        private String message;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public List<String> getRepairedFiles() { return repairedFiles; }
        public void setRepairedFiles(List<String> repairedFiles) { this.repairedFiles = repairedFiles; }
        public List<String> getFailedFiles() { return failedFiles; }
        public void setFailedFiles(List<String> failedFiles) { this.failedFiles = failedFiles; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

package net.ooder.skills.api.installer;

import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.api.rag.RagConfig;

import java.util.List;
import java.util.Map;

/**
 * 知识库安装器接口
 *
 * <p>负责安装 Skill 时自动构建向量索引</p>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public interface KnowledgeBaseInstaller {

    /**
     * 安装 Skill 知识库
     *
     * <p>功能要求：</p>
     * <ul>
     *   <li>解析 Skill 元数据中的 ragConfig</li>
     *   <li>扫描 knowledgeDocuments 和外部文件</li>
     *   <li>文档切分（支持多种策略：固定长度、语义切分、递归切分）</li>
     *   <li>生成向量嵌入</li>
     *   <li>构建向量索引</li>
     *   <li>生成安装报告</li>
     * </ul>
     *
     * @param skillPackage Skill 包
     * @return 安装结果
     */
    InstallResult install(SkillPackage skillPackage);

    /**
     * 卸载 Skill 知识库
     *
     * @param skillId Skill ID
     * @return 是否成功
     */
    boolean uninstall(String skillId);

    /**
     * 更新知识库
     *
     * @param skillPackage Skill 包
     * @return 更新结果
     */
    InstallResult update(SkillPackage skillPackage);

    /**
     * 检查知识库是否已安装
     *
     * @param skillId Skill ID
     * @return 是否已安装
     */
    boolean isInstalled(String skillId);

    /**
     * 获取知识库状态
     *
     * @param skillId Skill ID
     * @return 状态信息
     */
    KnowledgeBaseStatus getStatus(String skillId);

    /**
     * 安装结果
     */
    class InstallResult {
        private boolean success;
        private String skillId;
        private String indexId;
        private int documentCount;
        private int chunkCount;
        private long processingTime;
        private String message;
        private Map<String, Object> metadata;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getIndexId() { return indexId; }
        public void setIndexId(String indexId) { this.indexId = indexId; }

        public int getDocumentCount() { return documentCount; }
        public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }

        public int getChunkCount() { return chunkCount; }
        public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }

        public long getProcessingTime() { return processingTime; }
        public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public static InstallResult success(String skillId, String indexId) {
            InstallResult result = new InstallResult();
            result.setSuccess(true);
            result.setSkillId(skillId);
            result.setIndexId(indexId);
            return result;
        }

        public static InstallResult failure(String skillId, String message) {
            InstallResult result = new InstallResult();
            result.setSuccess(false);
            result.setSkillId(skillId);
            result.setMessage(message);
            return result;
        }
    }

    /**
     * 知识库状态
     */
    class KnowledgeBaseStatus {
        private String skillId;
        private String indexId;
        private Status status;
        private int documentCount;
        private int chunkCount;
        private long lastUpdated;
        private String errorMessage;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getIndexId() { return indexId; }
        public void setIndexId(String indexId) { this.indexId = indexId; }

        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }

        public int getDocumentCount() { return documentCount; }
        public void setDocumentCount(int documentCount) { this.documentCount = documentCount; }

        public int getChunkCount() { return chunkCount; }
        public void setChunkCount(int chunkCount) { this.chunkCount = chunkCount; }

        public long getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public enum Status {
            INITIALIZING,
            INDEXING,
            READY,
            ERROR,
            UPDATING
        }
    }
}

package net.ooder.skills.api;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 部署服务接口
 *
 * 整合场景模板解析、依赖检查、版本兼容性检查、能力绑定等流程
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface DeploymentService {
    
    /**
     * 一键部署场景模板
     *
     * @param template 场景模板
     * @return 部署结果
     */
    CompletableFuture<DeploymentResult> deploy(SceneTemplate template);
    
    /**
     * 一键部署场景模板（带选项）
     *
     * @param template 场景模板
     * @param options 部署选项
     * @return 部署结果
     */
    CompletableFuture<DeploymentResult> deploy(SceneTemplate template, DeploymentOptions options);
    
    /**
     * 从模板ID部署
     *
     * @param templateId 模板ID
     * @return 部署结果
     */
    CompletableFuture<DeploymentResult> deployFromTemplate(String templateId);
    
    /**
     * 从模板ID部署（带选项）
     *
     * @param templateId 模板ID
     * @param options 部署选项
     * @return 部署结果
     */
    CompletableFuture<DeploymentResult> deployFromTemplate(String templateId, DeploymentOptions options);
    
    /**
     * 预览部署
     *
     * @param template 场景模板
     * @return 部署预览
     */
    CompletableFuture<DeploymentPreview> previewDeploy(SceneTemplate template);
    
    /**
     * 验证部署配置
     *
     * @param template 场景模板
     * @return 验证结果
     */
    CompletableFuture<ValidationResult> validate(SceneTemplate template);
    
    /**
     * 回滚部署
     *
     * @param deploymentId 部署ID
     * @return 回滚结果
     */
    CompletableFuture<RollbackResult> rollback(String deploymentId);
    
    /**
     * 获取部署状态
     *
     * @param deploymentId 部署ID
     * @return 部署状态
     */
    CompletableFuture<DeploymentStatus> getStatus(String deploymentId);
    
    /**
     * 列出部署历史
     *
     * @return 部署历史列表
     */
    CompletableFuture<List<DeploymentRecord>> listDeployments();
    
    /**
     * 列出场景的部署历史
     *
     * @param sceneId 场景ID
     * @return 部署历史列表
     */
    CompletableFuture<List<DeploymentRecord>> listDeploymentsByScene(String sceneId);
    
    /**
     * 添加部署监听器
     *
     * @param listener 监听器
     */
    void addDeploymentListener(DeploymentListener listener);
    
    /**
     * 移除部署监听器
     *
     * @param listener 监听器
     */
    void removeDeploymentListener(DeploymentListener listener);
    
    // ========== 数据类定义 ==========
    
    /**
     * 部署选项
     */
    class DeploymentOptions {
        private boolean dryRun;  // 仅预览，不实际部署
        private boolean skipDependencyCheck;  // 跳过依赖检查
        private boolean skipVersionCheck;  // 跳过版本检查
        private boolean autoRollbackOnFailure;  // 失败时自动回滚
        private boolean autoActivate;  // 自动激活
        private Map<String, Object> configOverrides;  // 配置覆盖
        private long timeout;  // 超时时间（毫秒）
        
        public DeploymentOptions() {
            this.dryRun = false;
            this.skipDependencyCheck = false;
            this.skipVersionCheck = false;
            this.autoRollbackOnFailure = true;
            this.autoActivate = false;
            this.timeout = 300000;  // 默认5分钟
        }
        
        // Getters and Setters
        public boolean isDryRun() { return dryRun; }
        public void setDryRun(boolean dryRun) { this.dryRun = dryRun; }
        public boolean isSkipDependencyCheck() { return skipDependencyCheck; }
        public void setSkipDependencyCheck(boolean skipDependencyCheck) { this.skipDependencyCheck = skipDependencyCheck; }
        public boolean isSkipVersionCheck() { return skipVersionCheck; }
        public void setSkipVersionCheck(boolean skipVersionCheck) { this.skipVersionCheck = skipVersionCheck; }
        public boolean isAutoRollbackOnFailure() { return autoRollbackOnFailure; }
        public void setAutoRollbackOnFailure(boolean autoRollbackOnFailure) { this.autoRollbackOnFailure = autoRollbackOnFailure; }
        public boolean isAutoActivate() { return autoActivate; }
        public void setAutoActivate(boolean autoActivate) { this.autoActivate = autoActivate; }
        public Map<String, Object> getConfigOverrides() { return configOverrides; }
        public void setConfigOverrides(Map<String, Object> configOverrides) { this.configOverrides = configOverrides; }
        public long getTimeout() { return timeout; }
        public void setTimeout(long timeout) { this.timeout = timeout; }
    }
    
    /**
     * 部署结果
     */
    class DeploymentResult {
        private boolean success;
        private String deploymentId;
        private String sceneId;
        private String message;
        private DeploymentStatus status;
        private List<String> installedSkills;
        private List<String> boundCapabilities;
        private long startTime;
        private long endTime;
        private List<DeploymentStep> steps;
        
        public DeploymentResult() {
            this.installedSkills = new java.util.ArrayList<>();
            this.boundCapabilities = new java.util.ArrayList<>();
            this.steps = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getDeploymentId() { return deploymentId; }
        public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public DeploymentStatus getStatus() { return status; }
        public void setStatus(DeploymentStatus status) { this.status = status; }
        public List<String> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(List<String> installedSkills) { this.installedSkills = installedSkills; }
        public List<String> getBoundCapabilities() { return boundCapabilities; }
        public void setBoundCapabilities(List<String> boundCapabilities) { this.boundCapabilities = boundCapabilities; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public List<DeploymentStep> getSteps() { return steps; }
        public void setSteps(List<DeploymentStep> steps) { this.steps = steps; }
        
        public void addStep(DeploymentStep step) {
            if (this.steps == null) {
                this.steps = new java.util.ArrayList<>();
            }
            this.steps.add(step);
        }
        
        public static DeploymentResult success(String deploymentId, String sceneId) {
            DeploymentResult result = new DeploymentResult();
            result.setSuccess(true);
            result.setDeploymentId(deploymentId);
            result.setSceneId(sceneId);
            result.setStatus(DeploymentStatus.COMPLETED);
            result.setMessage("部署成功");
            return result;
        }
        
        public static DeploymentResult failure(String deploymentId, String message) {
            DeploymentResult result = new DeploymentResult();
            result.setSuccess(false);
            result.setDeploymentId(deploymentId);
            result.setStatus(DeploymentStatus.FAILED);
            result.setMessage(message);
            return result;
        }
    }
    
    /**
     * 部署步骤
     */
    class DeploymentStep {
        private String stepName;
        private StepStatus status;
        private String message;
        private long startTime;
        private long endTime;
        
        public enum StepStatus {
            PENDING,    // 待执行
            RUNNING,    // 执行中
            COMPLETED,  // 完成
            FAILED,     // 失败
            SKIPPED     // 跳过
        }
        
        // Getters and Setters
        public String getStepName() { return stepName; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public StepStatus getStatus() { return status; }
        public void setStatus(StepStatus status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
    }
    
    /**
     * 部署预览
     */
    class DeploymentPreview {
        private boolean canDeploy;
        private String message;
        private List<String> skillsToInstall;
        private List<String> capabilitiesToBind;
        private List<String> dependencies;
        private List<CompatibilityIssue> compatibilityIssues;
        private List<String> warnings;
        private long estimatedTime;
        
        public DeploymentPreview() {
            this.skillsToInstall = new java.util.ArrayList<>();
            this.capabilitiesToBind = new java.util.ArrayList<>();
            this.dependencies = new java.util.ArrayList<>();
            this.compatibilityIssues = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isCanDeploy() { return canDeploy; }
        public void setCanDeploy(boolean canDeploy) { this.canDeploy = canDeploy; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<String> getSkillsToInstall() { return skillsToInstall; }
        public void setSkillsToInstall(List<String> skillsToInstall) { this.skillsToInstall = skillsToInstall; }
        public List<String> getCapabilitiesToBind() { return capabilitiesToBind; }
        public void setCapabilitiesToBind(List<String> capabilitiesToBind) { this.capabilitiesToBind = capabilitiesToBind; }
        public List<String> getDependencies() { return dependencies; }
        public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
        public List<CompatibilityIssue> getCompatibilityIssues() { return compatibilityIssues; }
        public void setCompatibilityIssues(List<CompatibilityIssue> compatibilityIssues) { this.compatibilityIssues = compatibilityIssues; }
        public List<String> getWarnings() { return warnings; }
        public void setWarnings(List<String> warnings) { this.warnings = warnings; }
        public long getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(long estimatedTime) { this.estimatedTime = estimatedTime; }
    }
    
    /**
     * 验证结果
     */
    class ValidationResult {
        private boolean valid;
        private String message;
        private List<ValidationError> errors;
        private List<ValidationWarning> warnings;
        
        public ValidationResult() {
            this.errors = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
        }
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<ValidationError> getErrors() { return errors; }
        public void setErrors(List<ValidationError> errors) { this.errors = errors; }
        public List<ValidationWarning> getWarnings() { return warnings; }
        public void setWarnings(List<ValidationWarning> warnings) { this.warnings = warnings; }
    }
    
    /**
     * 验证错误
     */
    class ValidationError {
        private String field;
        private String message;
        
        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 验证警告
     */
    class ValidationWarning {
        private String field;
        private String message;
        
        public ValidationWarning(String field, String message) {
            this.field = field;
            this.message = message;
        }
        
        // Getters and Setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    /**
     * 回滚结果
     */
    class RollbackResult {
        private boolean success;
        private String message;
        private long rollbackTime;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getRollbackTime() { return rollbackTime; }
        public void setRollbackTime(long rollbackTime) { this.rollbackTime = rollbackTime; }
    }
    
    /**
     * 部署状态
     */
    enum DeploymentStatus {
        PENDING,      // 待部署
        VALIDATING,   // 验证中
        INSTALLING,   // 安装中
        BINDING,      // 绑定中
        ACTIVATING,   // 激活中
        COMPLETED,    // 完成
        FAILED,       // 失败
        ROLLING_BACK, // 回滚中
        ROLLED_BACK   // 已回滚
    }
    
    /**
     * 部署记录
     */
    class DeploymentRecord {
        private String deploymentId;
        private String sceneId;
        private String templateId;
        private DeploymentStatus status;
        private long deployTime;
        private String deployedBy;
        
        // Getters and Setters
        public String getDeploymentId() { return deploymentId; }
        public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }
        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public DeploymentStatus getStatus() { return status; }
        public void setStatus(DeploymentStatus status) { this.status = status; }
        public long getDeployTime() { return deployTime; }
        public void setDeployTime(long deployTime) { this.deployTime = deployTime; }
        public String getDeployedBy() { return deployedBy; }
        public void setDeployedBy(String deployedBy) { this.deployedBy = deployedBy; }
    }
    
    /**
     * 部署监听器
     */
    interface DeploymentListener {
        void onDeploymentStarted(String deploymentId, SceneTemplate template);
        void onDeploymentStep(String deploymentId, String stepName, DeploymentStep.StepStatus status);
        void onDeploymentCompleted(String deploymentId, DeploymentResult result);
        void onDeploymentFailed(String deploymentId, String error);
        void onDeploymentRolledBack(String deploymentId);
    }
}

package net.ooder.scene.core.dependency;

import net.ooder.scene.core.spi.DependencyChecker;
import net.ooder.scene.core.spi.DependencyChecker.CheckResult;
import net.ooder.scene.core.spi.DependencyChecker.HealthStatus;
import net.ooder.scene.core.template.DependenciesConfig;
import net.ooder.scene.core.template.DependenciesConfig.DependencyItem;
import net.ooder.scene.core.template.SceneTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 依赖检查引擎
 * 
 * <p>负责场景技能的依赖检查、验证和解决</p>
 *
 * <h3>功能：</h3>
 * <ul>
 *   <li>检查必需依赖是否满足</li>
 *   <li>检查可选依赖状态</li>
 *   <li>支持多种依赖类型（SKILL, SERVICE, CONFIG）</li>
 *   <li>提供依赖解决方案</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class DependencyCheckEngine {

    private static final Logger log = LoggerFactory.getLogger(DependencyCheckEngine.class);

    private final Map<String, DependencyChecker> checkers = new ConcurrentHashMap<>();
    private final Map<String, DependencyCheckResult> cachedResults = new ConcurrentHashMap<>();
    private final List<DependencyCheckListener> listeners = new ArrayList<>();

    public DependencyCheckEngine() {
    }

    /**
     * 注册依赖检查器
     * 
     * @param checker 检查器
     */
    public void registerChecker(DependencyChecker checker) {
        if (checker != null && checker.getDependencyType() != null) {
            checkers.put(checker.getDependencyType(), checker);
            log.info("[registerChecker] Registered checker for type: {}", checker.getDependencyType());
        }
    }

    /**
     * 注销依赖检查器
     * 
     * @param dependencyType 依赖类型
     */
    public void unregisterChecker(String dependencyType) {
        checkers.remove(dependencyType);
        log.info("[unregisterChecker] Unregistered checker for type: {}", dependencyType);
    }

    /**
     * 检查场景模板的依赖
     * 
     * @param template 场景模板
     * @return 检查结果
     */
    public CompletableFuture<DependencyCheckResult> checkDependencies(SceneTemplate template) {
        return CompletableFuture.supplyAsync(() -> {
            DependencyCheckResult result = new DependencyCheckResult();
            result.setTemplateId(template.getTemplateId());
            result.setCheckTime(System.currentTimeMillis());

            DependenciesConfig dependencies = template.getDependencies();
            if (dependencies == null) {
                result.setAllSatisfied(true);
                result.setMessage("无依赖配置");
                return result;
            }

            List<DependencyCheckItem> requiredResults = new ArrayList<>();
            List<DependencyCheckItem> optionalResults = new ArrayList<>();

            List<DependencyItem> required = dependencies.getRequired();
            if (required != null) {
                for (DependencyItem item : required) {
                    DependencyCheckItem checkItem = checkDependencyItem(item);
                    checkItem.setRequired(true);
                    requiredResults.add(checkItem);
                }
            }

            List<DependencyItem> optional = dependencies.getOptional();
            if (optional != null) {
                for (DependencyItem item : optional) {
                    DependencyCheckItem checkItem = checkDependencyItem(item);
                    checkItem.setRequired(false);
                    optionalResults.add(checkItem);
                }
            }

            result.setRequiredChecks(requiredResults);
            result.setOptionalChecks(optionalResults);

            boolean allRequiredSatisfied = requiredResults.stream()
                .allMatch(DependencyCheckItem::isSatisfied);
            result.setAllSatisfied(allRequiredSatisfied);

            if (!allRequiredSatisfied) {
                long failedCount = requiredResults.stream()
                    .filter(r -> !r.isSatisfied())
                    .count();
                result.setMessage(failedCount + " 个必需依赖未满足");
            } else {
                result.setMessage("所有必需依赖已满足");
            }

            cachedResults.put(template.getTemplateId(), result);
            notifyCheckComplete(template.getTemplateId(), result);

            log.info("[checkDependencies] Check completed for template: {}, satisfied: {}", 
                template.getTemplateId(), result.isAllSatisfied());

            return result;
        });
    }

    /**
     * 检查单个依赖项
     */
    private DependencyCheckItem checkDependencyItem(DependencyItem item) {
        DependencyCheckItem checkItem = new DependencyCheckItem();
        checkItem.setDependencyId(item.getDependencyId());
        checkItem.setDependencyName(item.getDependencyName());
        checkItem.setDependencyType(item.getDependencyType());

        DependencyChecker checker = checkers.get(item.getDependencyType());
        if (checker == null) {
            checkItem.setSatisfied(false);
            checkItem.setMessage("未找到依赖类型 " + item.getDependencyType() + " 的检查器");
            checkItem.setStatus(DependencyCheckStatus.CHECKER_NOT_FOUND);
            return checkItem;
        }

        try {
            CheckResult checkResult = checker.check(item);
            checkItem.setSatisfied(checkResult.isSatisfied());
            checkItem.setMessage(checkResult.getMessage());
            checkItem.setDetails(checkResult.getDetails());
            checkItem.setStatus(checkResult.isSatisfied() 
                ? DependencyCheckStatus.SATISFIED 
                : DependencyCheckStatus.UNSATISFIED);

            HealthStatus healthStatus = checker.healthCheck(item.getDependencyId());
            checkItem.setHealthStatus(healthStatus);

        } catch (Exception e) {
            log.error("[checkDependencyItem] Check failed for: {}", item.getDependencyId(), e);
            checkItem.setSatisfied(false);
            checkItem.setMessage("检查异常: " + e.getMessage());
            checkItem.setStatus(DependencyCheckStatus.ERROR);
        }

        return checkItem;
    }

    /**
     * 获取缓存的检查结果
     * 
     * @param templateId 模板ID
     * @return 检查结果
     */
    public DependencyCheckResult getCachedResult(String templateId) {
        return cachedResults.get(templateId);
    }

    /**
     * 清除缓存
     * 
     * @param templateId 模板ID
     */
    public void clearCache(String templateId) {
        cachedResults.remove(templateId);
    }

    /**
     * 获取依赖解决方案
     * 
     * @param template 场景模板
     * @return 解决方案列表
     */
    public List<DependencySolution> getSolutions(SceneTemplate template) {
        List<DependencySolution> solutions = new ArrayList<>();

        DependenciesConfig dependencies = template.getDependencies();
        if (dependencies == null) {
            return solutions;
        }

        List<DependencyItem> required = dependencies.getRequired();
        if (required != null) {
            for (DependencyItem item : required) {
                DependencyChecker checker = checkers.get(item.getDependencyType());
                if (checker != null) {
                    CheckResult result = checker.check(item);
                    if (!result.isSatisfied()) {
                        DependencySolution solution = new DependencySolution();
                        solution.setDependencyId(item.getDependencyId());
                        solution.setDependencyName(item.getDependencyName());
                        solution.setDependencyType(item.getDependencyType());
                        solution.setAutoInstall(item.isAutoInstall());
                        solution.setSuggestedAction(getSuggestedAction(item, result));
                        solutions.add(solution);
                    }
                }
            }
        }

        return solutions;
    }

    private String getSuggestedAction(DependencyItem item, CheckResult result) {
        if (item.isAutoInstall()) {
            return "自动安装 " + item.getDependencyName();
        } else {
            return "手动安装 " + item.getDependencyName() + " 或联系管理员";
        }
    }

    /**
     * 添加检查监听器
     */
    public void addListener(DependencyCheckListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * 移除检查监听器
     */
    public void removeListener(DependencyCheckListener listener) {
        listeners.remove(listener);
    }

    private void notifyCheckComplete(String templateId, DependencyCheckResult result) {
        for (DependencyCheckListener listener : listeners) {
            try {
                listener.onCheckComplete(templateId, result);
            } catch (Exception e) {
                log.error("[notifyCheckComplete] Listener error", e);
            }
        }
    }

    /**
     * 依赖检查监听器
     */
    public interface DependencyCheckListener {
        void onCheckComplete(String templateId, DependencyCheckResult result);
    }

    /**
     * 依赖检查状态
     */
    public enum DependencyCheckStatus {
        SATISFIED("已满足"),
        UNSATISFIED("未满足"),
        CHECKER_NOT_FOUND("检查器未找到"),
        ERROR("检查错误"),
        PENDING("待检查");

        private final String displayName;

        DependencyCheckStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * 依赖检查结果
     */
    public static class DependencyCheckResult {
        private String templateId;
        private long checkTime;
        private boolean allSatisfied;
        private String message;
        private List<DependencyCheckItem> requiredChecks;
        private List<DependencyCheckItem> optionalChecks;

        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }
        public long getCheckTime() { return checkTime; }
        public void setCheckTime(long checkTime) { this.checkTime = checkTime; }
        public boolean isAllSatisfied() { return allSatisfied; }
        public void setAllSatisfied(boolean allSatisfied) { this.allSatisfied = allSatisfied; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public List<DependencyCheckItem> getRequiredChecks() { return requiredChecks; }
        public void setRequiredChecks(List<DependencyCheckItem> requiredChecks) { this.requiredChecks = requiredChecks; }
        public List<DependencyCheckItem> getOptionalChecks() { return optionalChecks; }
        public void setOptionalChecks(List<DependencyCheckItem> optionalChecks) { this.optionalChecks = optionalChecks; }

        public List<DependencyCheckItem> getUnsatisfiedRequired() {
            if (requiredChecks == null) {
                return Collections.emptyList();
            }
            return requiredChecks.stream()
                .filter(r -> !r.isSatisfied())
                .collect(java.util.stream.Collectors.toList());
        }

        public int getRequiredCount() {
            return requiredChecks != null ? requiredChecks.size() : 0;
        }

        public int getSatisfiedCount() {
            if (requiredChecks == null) {
                return 0;
            }
            return (int) requiredChecks.stream()
                .filter(DependencyCheckItem::isSatisfied)
                .count();
        }
    }

    /**
     * 依赖检查项
     */
    public static class DependencyCheckItem {
        private String dependencyId;
        private String dependencyName;
        private String dependencyType;
        private boolean required;
        private boolean satisfied;
        private String message;
        private DependencyCheckStatus status;
        private HealthStatus healthStatus;
        private Map<String, Object> details;

        public String getDependencyId() { return dependencyId; }
        public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
        public String getDependencyName() { return dependencyName; }
        public void setDependencyName(String dependencyName) { this.dependencyName = dependencyName; }
        public String getDependencyType() { return dependencyType; }
        public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public boolean isSatisfied() { return satisfied; }
        public void setSatisfied(boolean satisfied) { this.satisfied = satisfied; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public DependencyCheckStatus getStatus() { return status; }
        public void setStatus(DependencyCheckStatus status) { this.status = status; }
        public HealthStatus getHealthStatus() { return healthStatus; }
        public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }

        @Override
        public String toString() {
            return String.format("%s[%s]: %s - %s", 
                dependencyName, dependencyType, 
                satisfied ? "✓" : "✗", message);
        }
    }

    /**
     * 依赖解决方案
     */
    public static class DependencySolution {
        private String dependencyId;
        private String dependencyName;
        private String dependencyType;
        private boolean autoInstall;
        private String suggestedAction;
        private String installUrl;
        private String documentationUrl;

        public String getDependencyId() { return dependencyId; }
        public void setDependencyId(String dependencyId) { this.dependencyId = dependencyId; }
        public String getDependencyName() { return dependencyName; }
        public void setDependencyName(String dependencyName) { this.dependencyName = dependencyName; }
        public String getDependencyType() { return dependencyType; }
        public void setDependencyType(String dependencyType) { this.dependencyType = dependencyType; }
        public boolean isAutoInstall() { return autoInstall; }
        public void setAutoInstall(boolean autoInstall) { this.autoInstall = autoInstall; }
        public String getSuggestedAction() { return suggestedAction; }
        public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }
        public String getInstallUrl() { return installUrl; }
        public void setInstallUrl(String installUrl) { this.installUrl = installUrl; }
        public String getDocumentationUrl() { return documentationUrl; }
        public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }
    }
}

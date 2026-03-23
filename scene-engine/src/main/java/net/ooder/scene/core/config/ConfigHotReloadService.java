package net.ooder.scene.core.config;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import net.ooder.scene.core.lifecycle.SkillStateMachine;
import net.ooder.scene.core.template.ActivationStepConfig;
import net.ooder.scene.core.template.RoleConfig;
import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.skill.install.SceneConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 配置热更新服务
 * 
 * <p>提供场景配置的热更新能力，支持：</p>
 * <ul>
 *   <li>配置变更检测</li>
 *   <li>配置版本管理</li>
 *   <li>配置回滚</li>
 *   <li>配置变更通知</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class ConfigHotReloadService {

    private static final Logger log = LoggerFactory.getLogger(ConfigHotReloadService.class);

    private final SceneConfigLoader configLoader;
    private final SkillStateMachine stateMachine;
    private final Map<String, ConfigVersion> configVersions = new ConcurrentHashMap<>();
    private final Map<String, SceneTemplate> currentConfigs = new ConcurrentHashMap<>();
    private final List<ConfigChangeListener> listeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private boolean enabled = false;
    private long checkIntervalMs = 30000;
    private int maxVersions = 10;

    public ConfigHotReloadService(SceneConfigLoader configLoader, SkillStateMachine stateMachine) {
        this.configLoader = configLoader;
        this.stateMachine = stateMachine;
    }

    /**
     * 启动热更新服务
     */
    public void start() {
        if (enabled) {
            return;
        }
        enabled = true;
        scheduler.scheduleAtFixedRate(
            this::checkConfigChanges, 
            checkIntervalMs, 
            checkIntervalMs, 
            TimeUnit.MILLISECONDS
        );
        log.info("[start] Config hot reload service started, check interval: {}ms", checkIntervalMs);
    }

    /**
     * 停止热更新服务
     */
    public void stop() {
        enabled = false;
        scheduler.shutdown();
        log.info("[stop] Config hot reload service stopped");
    }

    /**
     * 注册配置
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param template 场景模板
     */
    public void registerConfig(String sceneId, String skillId, SceneTemplate template) {
        String key = buildKey(sceneId, skillId);
        currentConfigs.put(key, template);
        
        ConfigVersion version = new ConfigVersion();
        version.setVersionId(generateVersionId());
        version.setTemplate(template);
        version.setTimestamp(System.currentTimeMillis());
        version.setSource("initial");
        
        configVersions.put(key, version);
        
        log.info("[registerConfig] Config registered: {} in scene: {}", skillId, sceneId);
    }

    /**
     * 更新配置
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param newTemplate 新模板
     * @return 更新结果
     */
    public ConfigUpdateResult updateConfig(String sceneId, String skillId, SceneTemplate newTemplate) {
        ConfigUpdateResult result = new ConfigUpdateResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);
        result.setUpdateTime(System.currentTimeMillis());

        String key = buildKey(sceneId, skillId);
        SceneTemplate oldTemplate = currentConfigs.get(key);

        if (oldTemplate == null) {
            result.setSuccess(false);
            result.setMessage("配置未注册");
            return result;
        }

        ConfigChangeDetector detector = new ConfigChangeDetector();
        List<ConfigChange> changes = detector.detectChanges(oldTemplate, newTemplate);
        result.setChanges(changes);

        if (changes.isEmpty()) {
            result.setSuccess(true);
            result.setMessage("无配置变更");
            return result;
        }

        if (!canApplyChanges(sceneId, skillId, changes)) {
            result.setSuccess(false);
            result.setMessage("当前状态不允许更新配置");
            return result;
        }

        ConfigVersion oldVersion = configVersions.get(key);
        ConfigVersion newVersion = new ConfigVersion();
        newVersion.setVersionId(generateVersionId());
        newVersion.setTemplate(newTemplate);
        newVersion.setTimestamp(System.currentTimeMillis());
        newVersion.setPreviousVersion(oldVersion.getVersionId());
        newVersion.setSource("update");

        currentConfigs.put(key, newTemplate);
        configVersions.put(key, newVersion);

        notifyConfigChange(sceneId, skillId, oldTemplate, newTemplate, changes);

        result.setSuccess(true);
        result.setMessage("配置更新成功");
        result.setOldVersion(oldVersion.getVersionId());
        result.setNewVersion(newVersion.getVersionId());

        log.info("[updateConfig] Config updated: {} in scene: {}, version: {} -> {}", 
            skillId, sceneId, oldVersion.getVersionId(), newVersion.getVersionId());

        return result;
    }

    /**
     * 回滚配置
     * 
     * @param sceneId 场景ID
     * @param skillId 技能ID
     * @param targetVersionId 目标版本ID
     * @return 回滚结果
     */
    public ConfigUpdateResult rollbackConfig(String sceneId, String skillId, String targetVersionId) {
        ConfigUpdateResult result = new ConfigUpdateResult();
        result.setSceneId(sceneId);
        result.setSkillId(skillId);

        String key = buildKey(sceneId, skillId);
        ConfigVersion targetVersion = findVersion(key, targetVersionId);

        if (targetVersion == null) {
            result.setSuccess(false);
            result.setMessage("目标版本不存在: " + targetVersionId);
            return result;
        }

        SceneTemplate currentTemplate = currentConfigs.get(key);
        SceneTemplate targetTemplate = targetVersion.getTemplate();

        ConfigVersion currentVersion = configVersions.get(key);
        ConfigVersion newVersion = new ConfigVersion();
        newVersion.setVersionId(generateVersionId());
        newVersion.setTemplate(targetTemplate);
        newVersion.setTimestamp(System.currentTimeMillis());
        newVersion.setPreviousVersion(currentVersion.getVersionId());
        newVersion.setSource("rollback");

        currentConfigs.put(key, targetTemplate);
        configVersions.put(key, newVersion);

        ConfigChangeDetector detector = new ConfigChangeDetector();
        List<ConfigChange> changes = detector.detectChanges(currentTemplate, targetTemplate);

        notifyConfigChange(sceneId, skillId, currentTemplate, targetTemplate, changes);

        result.setSuccess(true);
        result.setMessage("配置回滚成功");
        result.setOldVersion(currentVersion.getVersionId());
        result.setNewVersion(newVersion.getVersionId());
        result.setChanges(changes);

        log.info("[rollbackConfig] Config rolled back: {} in scene: {}, to version: {}", 
            skillId, sceneId, targetVersionId);

        return result;
    }

    /**
     * 获取当前配置
     */
    public SceneTemplate getCurrentConfig(String sceneId, String skillId) {
        return currentConfigs.get(buildKey(sceneId, skillId));
    }

    /**
     * 获取配置版本历史
     */
    public List<ConfigVersion> getVersionHistory(String sceneId, String skillId) {
        String key = buildKey(sceneId, skillId);
        List<ConfigVersion> versions = new ArrayList<>();
        ConfigVersion current = configVersions.get(key);
        
        while (current != null && versions.size() < maxVersions) {
            versions.add(current);
            if (current.getPreviousVersion() != null) {
                current = findVersion(key, current.getPreviousVersion());
            } else {
                break;
            }
        }
        
        return versions;
    }

    /**
     * 添加配置变更监听器
     */
    public void addListener(ConfigChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * 移除配置变更监听器
     */
    public void removeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }

    private String buildKey(String sceneId, String skillId) {
        return sceneId + ":" + skillId;
    }

    private String generateVersionId() {
        return "v" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private ConfigVersion findVersion(String key, String versionId) {
        ConfigVersion current = configVersions.get(key);
        while (current != null) {
            if (current.getVersionId().equals(versionId)) {
                return current;
            }
            if (current.getPreviousVersion() != null) {
                String prevKey = key + ":" + current.getPreviousVersion();
                current = configVersions.get(prevKey);
            } else {
                break;
            }
        }
        return null;
    }

    private boolean canApplyChanges(String sceneId, String skillId, List<ConfigChange> changes) {
        if (stateMachine == null) {
            return true;
        }

        boolean hasCriticalChanges = changes.stream()
            .anyMatch(c -> c.getSeverity() == ChangeSeverity.CRITICAL);

        if (hasCriticalChanges) {
            return stateMachine.canTransition(sceneId, skillId, SkillLifecycleState.UPDATING);
        }

        return true;
    }

    private void checkConfigChanges() {
        if (!enabled) {
            return;
        }

        log.debug("[checkConfigChanges] Checking for config changes...");
    }

    private void notifyConfigChange(String sceneId, String skillId, 
                                     SceneTemplate oldTemplate, 
                                     SceneTemplate newTemplate,
                                     List<ConfigChange> changes) {
        ConfigChangeEvent event = new ConfigChangeEvent();
        event.setSceneId(sceneId);
        event.setSkillId(skillId);
        event.setOldTemplate(oldTemplate);
        event.setNewTemplate(newTemplate);
        event.setChanges(changes);
        event.setTimestamp(System.currentTimeMillis());

        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChange(event);
            } catch (Exception e) {
                log.error("[notifyConfigChange] Listener error", e);
            }
        }
    }

    /**
     * 配置变更监听器
     */
    public interface ConfigChangeListener {
        void onConfigChange(ConfigChangeEvent event);
    }

    /**
     * 配置变更事件
     */
    public static class ConfigChangeEvent {
        private String sceneId;
        private String skillId;
        private SceneTemplate oldTemplate;
        private SceneTemplate newTemplate;
        private List<ConfigChange> changes;
        private long timestamp;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public SceneTemplate getOldTemplate() { return oldTemplate; }
        public void setOldTemplate(SceneTemplate oldTemplate) { this.oldTemplate = oldTemplate; }
        public SceneTemplate getNewTemplate() { return newTemplate; }
        public void setNewTemplate(SceneTemplate newTemplate) { this.newTemplate = newTemplate; }
        public List<ConfigChange> getChanges() { return changes; }
        public void setChanges(List<ConfigChange> changes) { this.changes = changes; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    /**
     * 配置版本
     */
    public static class ConfigVersion {
        private String versionId;
        private SceneTemplate template;
        private long timestamp;
        private String previousVersion;
        private String source;

        public String getVersionId() { return versionId; }
        public void setVersionId(String versionId) { this.versionId = versionId; }
        public SceneTemplate getTemplate() { return template; }
        public void setTemplate(SceneTemplate template) { this.template = template; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public String getPreviousVersion() { return previousVersion; }
        public void setPreviousVersion(String previousVersion) { this.previousVersion = previousVersion; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    /**
     * 配置更新结果
     */
    public static class ConfigUpdateResult {
        private String sceneId;
        private String skillId;
        private boolean success;
        private String message;
        private String oldVersion;
        private String newVersion;
        private List<ConfigChange> changes;
        private long updateTime;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getOldVersion() { return oldVersion; }
        public void setOldVersion(String oldVersion) { this.oldVersion = oldVersion; }
        public String getNewVersion() { return newVersion; }
        public void setNewVersion(String newVersion) { this.newVersion = newVersion; }
        public List<ConfigChange> getChanges() { return changes; }
        public void setChanges(List<ConfigChange> changes) { this.changes = changes; }
        public long getUpdateTime() { return updateTime; }
        public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }
    }

    /**
     * 配置变更
     */
    public static class ConfigChange {
        private String field;
        private Object oldValue;
        private Object newValue;
        private ChangeType type;
        private ChangeSeverity severity;

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public Object getOldValue() { return oldValue; }
        public void setOldValue(Object oldValue) { this.oldValue = oldValue; }
        public Object getNewValue() { return newValue; }
        public void setNewValue(Object newValue) { this.newValue = newValue; }
        public ChangeType getType() { return type; }
        public void setType(ChangeType type) { this.type = type; }
        public ChangeSeverity getSeverity() { return severity; }
        public void setSeverity(ChangeSeverity severity) { this.severity = severity; }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s (%s)", field, oldValue, newValue, type);
        }
    }

    /**
     * 变更类型
     */
    public enum ChangeType {
        ADD, MODIFY, DELETE
    }

    /**
     * 变更严重程度
     */
    public enum ChangeSeverity {
        MINOR,   // 轻微变更，如描述修改
        MODERATE, // 中等变更，如菜单调整
        CRITICAL  // 严重变更，如角色、激活步骤变更
    }

    /**
     * 配置变更检测器
     */
    public static class ConfigChangeDetector {
        
        public List<ConfigChange> detectChanges(SceneTemplate oldTemplate, SceneTemplate newTemplate) {
            List<ConfigChange> changes = new ArrayList<>();
            
            if (oldTemplate == null || newTemplate == null) {
                return changes;
            }

            detectFieldChange(changes, "templateName", 
                oldTemplate.getTemplateName(), newTemplate.getTemplateName(), ChangeSeverity.MINOR);
            
            detectFieldChange(changes, "sceneType", 
                oldTemplate.getSceneType(), newTemplate.getSceneType(), ChangeSeverity.CRITICAL);
            
            detectFieldChange(changes, "visibility", 
                oldTemplate.getVisibility(), newTemplate.getVisibility(), ChangeSeverity.MODERATE);

            detectRolesChange(changes, oldTemplate, newTemplate);

            detectActivationStepsChange(changes, oldTemplate, newTemplate);

            detectMenusChange(changes, oldTemplate, newTemplate);

            return changes;
        }

        private void detectFieldChange(List<ConfigChange> changes, String field,
                                        Object oldValue, Object newValue, 
                                        ChangeSeverity severity) {
            if (!Objects.equals(oldValue, newValue)) {
                ConfigChange change = new ConfigChange();
                change.setField(field);
                change.setOldValue(oldValue);
                change.setNewValue(newValue);
                change.setType(ChangeType.MODIFY);
                change.setSeverity(severity);
                changes.add(change);
            }
        }

        private void detectRolesChange(List<ConfigChange> changes, 
                                        SceneTemplate oldTemplate, 
                                        SceneTemplate newTemplate) {
            List<RoleConfig> oldRoles = oldTemplate.getRoles();
            List<RoleConfig> newRoles = newTemplate.getRoles();

            if (!Objects.equals(oldRoles, newRoles)) {
                ConfigChange change = new ConfigChange();
                change.setField("roles");
                change.setOldValue(oldRoles != null ? oldRoles.size() : 0);
                change.setNewValue(newRoles != null ? newRoles.size() : 0);
                change.setType(ChangeType.MODIFY);
                change.setSeverity(ChangeSeverity.CRITICAL);
                changes.add(change);
            }
        }

        private void detectActivationStepsChange(List<ConfigChange> changes,
                                                  SceneTemplate oldTemplate,
                                                  SceneTemplate newTemplate) {
            Map<String, List<ActivationStepConfig>> oldSteps = oldTemplate.getActivationSteps();
            Map<String, List<ActivationStepConfig>> newSteps = newTemplate.getActivationSteps();

            if (!Objects.equals(oldSteps, newSteps)) {
                ConfigChange change = new ConfigChange();
                change.setField("activationSteps");
                change.setOldValue(oldSteps != null ? oldSteps.size() : 0);
                change.setNewValue(newSteps != null ? newSteps.size() : 0);
                change.setType(ChangeType.MODIFY);
                change.setSeverity(ChangeSeverity.CRITICAL);
                changes.add(change);
            }
        }

        private void detectMenusChange(List<ConfigChange> changes,
                                        SceneTemplate oldTemplate,
                                        SceneTemplate newTemplate) {
            Map<String, List<net.ooder.scene.ui.MenuConfig>> oldMenus = oldTemplate.getMenus();
            Map<String, List<net.ooder.scene.ui.MenuConfig>> newMenus = newTemplate.getMenus();

            if (!Objects.equals(oldMenus, newMenus)) {
                ConfigChange change = new ConfigChange();
                change.setField("menus");
                change.setOldValue(oldMenus != null ? oldMenus.size() : 0);
                change.setNewValue(newMenus != null ? newMenus.size() : 0);
                change.setType(ChangeType.MODIFY);
                change.setSeverity(ChangeSeverity.MODERATE);
                changes.add(change);
            }
        }
    }
}

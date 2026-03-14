package net.ooder.scene.skill.engine.context;

import java.util.Map;

/**
 * Skill 切换处理器
 * 统一管理 Skills 切换时的状态保存和恢复逻辑
 *
 * <p>变更说明: 新增接口，处理切换前后的状态同步，解决 Skills 切换时上下文丢失问题</p>
 *
 * @author ooder
 * @since 2.3.1
 */
public interface SkillSwitchHandler {

    /**
     * 切换前处理
     * - 保存当前 Skill 的页面状态
     * - 保存当前 Skill 的上下文
     * - 更新会话的 currentSkill
     *
     * @param fromSkillId 当前 Skill ID
     * @param toSkillId 目标 Skill ID
     * @param sessionId 会话ID
     * @return 切换前上下文，用于后续恢复
     */
    Map<String, Object> beforeSwitch(String fromSkillId, String toSkillId, String sessionId);

    /**
     * 切换后处理
     * - 恢复目标 Skill 的上下文
     * - 恢复目标 Skill 的页面状态
     * - 更新菜单高亮
     *
     * @param fromSkillId 原 Skill ID
     * @param toSkillId 当前 Skill ID
     * @param sessionId 会话ID
     * @param context 切换前保存的上下文
     */
    void afterSwitch(String fromSkillId, String toSkillId, String sessionId, Map<String, Object> context);

    /**
     * 获取全局共享上下文
     * - 用户信息
     * - 系统配置
     * - 已安装 Skills 列表
     *
     * @param userId 用户ID
     * @return 全局上下文
     */
    GlobalContext getGlobalContext(String userId);

    /**
     * 执行完整的切换流程
     *
     * @param request 切换请求
     * @return 切换结果
     */
    SwitchResult switchSkill(SwitchRequest request);

    /**
     * 切换请求
     */
    class SwitchRequest {
        private String fromSkillId;
        private String toSkillId;
        private String sessionId;
        private String userId;
        private Map<String, Object> currentPageState;
        private String currentPageId;

        // Getters and Setters
        public String getFromSkillId() { return fromSkillId; }
        public void setFromSkillId(String fromSkillId) { this.fromSkillId = fromSkillId; }

        public String getToSkillId() { return toSkillId; }
        public void setToSkillId(String toSkillId) { this.toSkillId = toSkillId; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Map<String, Object> getCurrentPageState() { return currentPageState; }
        public void setCurrentPageState(Map<String, Object> currentPageState) { this.currentPageState = currentPageState; }

        public String getCurrentPageId() { return currentPageId; }
        public void setCurrentPageId(String currentPageId) { this.currentPageId = currentPageId; }
    }

    /**
     * 切换结果
     */
    class SwitchResult {
        private boolean success;
        private String message;
        private String targetUrl;
        private GlobalContext globalContext;
        private Map<String, Object> restoredContext;
        private Map<String, Object> restoredPageState;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getTargetUrl() { return targetUrl; }
        public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

        public GlobalContext getGlobalContext() { return globalContext; }
        public void setGlobalContext(GlobalContext globalContext) { this.globalContext = globalContext; }

        public Map<String, Object> getRestoredContext() { return restoredContext; }
        public void setRestoredContext(Map<String, Object> restoredContext) { this.restoredContext = restoredContext; }

        public Map<String, Object> getRestoredPageState() { return restoredPageState; }
        public void setRestoredPageState(Map<String, Object> restoredPageState) { this.restoredPageState = restoredPageState; }
    }

    /**
     * 全局上下文
     */
    class GlobalContext {
        private String userId;
        private String userName;
        private Map<String, Object> userPreferences;
        private Map<String, Object> systemConfig;
        private Map<String, SkillInfo> installedSkills;
        private Map<String, Object> permissions;

        // Getters and Setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }

        public Map<String, Object> getUserPreferences() { return userPreferences; }
        public void setUserPreferences(Map<String, Object> userPreferences) { this.userPreferences = userPreferences; }

        public Map<String, Object> getSystemConfig() { return systemConfig; }
        public void setSystemConfig(Map<String, Object> systemConfig) { this.systemConfig = systemConfig; }

        public Map<String, SkillInfo> getInstalledSkills() { return installedSkills; }
        public void setInstalledSkills(Map<String, SkillInfo> installedSkills) { this.installedSkills = installedSkills; }

        public Map<String, Object> getPermissions() { return permissions; }
        public void setPermissions(Map<String, Object> permissions) { this.permissions = permissions; }
    }

    /**
     * Skill 信息
     */
    class SkillInfo {
        private String skillId;
        private String name;
        private String version;
        private String description;
        private String icon;
        private boolean isActive;

        // Getters and Setters
        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
    }
}

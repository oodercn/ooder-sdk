package net.ooder.scene.skill.engine.context.impl;

import net.ooder.scene.skill.engine.context.ContextStorageService;
import net.ooder.scene.skill.engine.context.SkillSwitchHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Skill 切换处理器实现
 * 处理 Skills 切换时的状态保存和恢复
 *
 * <p>变更说明: 新增实现类，解决 Skills 切换时上下文丢失问题</p>
 *
 * @author ooder
 * @since 2.3.1
 */
@Service
public class SkillSwitchHandlerImpl implements SkillSwitchHandler {

    private static final Logger logger = LoggerFactory.getLogger(SkillSwitchHandlerImpl.class);

    @Autowired
    private ContextStorageService contextStorageService;

    @Override
    public Map<String, Object> beforeSwitch(String fromSkillId, String toSkillId, String sessionId) {
        logger.info("Before switch from {} to {}, session: {}", fromSkillId, toSkillId, sessionId);

        Map<String, Object> context = new HashMap<>();

        try {
            // 1. 加载当前会话上下文
            Map<String, Object> sessionContext = contextStorageService.loadSessionContext(sessionId);

            // 2. 保存当前 Skill 的上下文
            if (fromSkillId != null) {
                Map<String, Object> skillContext = contextStorageService.loadSkillContext(fromSkillId, sessionId);
                skillContext.put("lastActiveAt", Instant.now().toEpochMilli());
                skillContext.put("isActive", false);
                contextStorageService.saveSkillContext(fromSkillId, sessionId, skillContext);
                context.put("fromSkillContext", skillContext);
            }

            // 3. 更新会话的 currentSkill
            sessionContext.put("currentSkill", toSkillId);
            sessionContext.put("lastSwitchAt", Instant.now().toEpochMilli());
            sessionContext.put("previousSkill", fromSkillId);
            contextStorageService.saveSessionContext(sessionId, sessionContext);

            // 4. 记录切换历史
            recordSwitchHistory(sessionId, fromSkillId, toSkillId);

            context.put("sessionContext", sessionContext);

            logger.info("Successfully saved state before switch");
        } catch (Exception e) {
            logger.error("Error during beforeSwitch", e);
            throw new RuntimeException("Failed to save state before switch", e);
        }

        return context;
    }

    @Override
    public void afterSwitch(String fromSkillId, String toSkillId, String sessionId, Map<String, Object> context) {
        logger.info("After switch from {} to {}, session: {}", fromSkillId, toSkillId, sessionId);

        try {
            // 1. 恢复目标 Skill 的上下文
            Map<String, Object> toSkillContext = contextStorageService.loadSkillContext(toSkillId, sessionId);
            if (toSkillContext.isEmpty()) {
                // 首次进入，初始化上下文
                toSkillContext = initializeSkillContext(toSkillId, sessionId);
            }
            toSkillContext.put("isActive", true);
            toSkillContext.put("activatedAt", Instant.now().toEpochMilli());
            contextStorageService.saveSkillContext(toSkillId, sessionId, toSkillContext);

            // 2. 恢复页面状态（如果有）
            String defaultPageId = (String) toSkillContext.getOrDefault("defaultPageId", "index");
            Map<String, Object> pageState = contextStorageService.loadPageState(sessionId, defaultPageId);

            // 3. 更新会话上下文
            Map<String, Object> sessionContext = contextStorageService.loadSessionContext(sessionId);
            sessionContext.put("currentPage", defaultPageId);
            sessionContext.put("switchCompletedAt", Instant.now().toEpochMilli());
            contextStorageService.saveSessionContext(sessionId, sessionContext);

            logger.info("Successfully restored state after switch");
        } catch (Exception e) {
            logger.error("Error during afterSwitch", e);
            throw new RuntimeException("Failed to restore state after switch", e);
        }
    }

    @Override
    public GlobalContext getGlobalContext(String userId) {
        GlobalContext globalContext = new GlobalContext();

        try {
            // 1. 加载用户上下文
            Map<String, Object> userContext = contextStorageService.loadUserContext(userId);
            globalContext.setUserId(userId);
            globalContext.setUserName((String) userContext.getOrDefault("userName", ""));
            globalContext.setUserPreferences((Map<String, Object>) userContext.getOrDefault("preferences", new HashMap<>()));

            // 2. 加载系统配置
            globalContext.setSystemConfig(loadSystemConfig());

            // 3. 加载已安装的 Skills
            globalContext.setInstalledSkills(loadInstalledSkills(userId));

            // 4. 加载权限信息
            globalContext.setPermissions((Map<String, Object>) userContext.getOrDefault("permissions", new HashMap<>()));

            logger.debug("Loaded global context for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error loading global context for user: {}", userId, e);
        }

        return globalContext;
    }

    @Override
    public SwitchResult switchSkill(SwitchRequest request) {
        SwitchResult result = new SwitchResult();
        String fromSkillId = request.getFromSkillId();
        String toSkillId = request.getToSkillId();
        String sessionId = request.getSessionId();

        try {
            // 1. 切换前处理
            Map<String, Object> context = beforeSwitch(fromSkillId, toSkillId, sessionId);

            // 2. 保存当前页面状态（如果提供）
            if (request.getCurrentPageState() != null && request.getCurrentPageId() != null) {
                contextStorageService.savePageState(sessionId, request.getCurrentPageId(), request.getCurrentPageState());
                logger.debug("Saved page state for page: {}", request.getCurrentPageId());
            }

            // 3. 切换后处理
            afterSwitch(fromSkillId, toSkillId, sessionId, context);

            // 4. 构建返回结果
            result.setSuccess(true);
            result.setMessage("Switch successful");
            result.setTargetUrl(buildTargetUrl(toSkillId));
            result.setGlobalContext(getGlobalContext(request.getUserId()));
            result.setRestoredContext(contextStorageService.loadSkillContext(toSkillId, sessionId));
            result.setRestoredPageState(contextStorageService.loadPageState(sessionId, "index"));

            logger.info("Skill switch completed successfully: {} -> {}", fromSkillId, toSkillId);
        } catch (Exception e) {
            logger.error("Skill switch failed: {} -> {}", fromSkillId, toSkillId, e);
            result.setSuccess(false);
            result.setMessage("Switch failed: " + e.getMessage());
        }

        return result;
    }

    /**
     * 初始化 Skill 上下文
     */
    private Map<String, Object> initializeSkillContext(String skillId, String sessionId) {
        Map<String, Object> context = new HashMap<>();
        context.put("skillId", skillId);
        context.put("sessionId", sessionId);
        context.put("createdAt", Instant.now().toEpochMilli());
        context.put("isActive", false);
        context.put("visitCount", 0);
        context.put("defaultPageId", "index");
        return context;
    }

    /**
     * 记录切换历史
     */
    private void recordSwitchHistory(String sessionId, String fromSkillId, String toSkillId) {
        try {
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("fromSkillId", fromSkillId);
            historyEntry.put("toSkillId", toSkillId);
            historyEntry.put("timestamp", Instant.now().toEpochMilli());

            // 这里可以保存到专门的切换历史存储中
            logger.debug("Recorded switch history: {} -> {}", fromSkillId, toSkillId);
        } catch (Exception e) {
            logger.warn("Failed to record switch history", e);
        }
    }

    /**
     * 加载系统配置
     */
    private Map<String, Object> loadSystemConfig() {
        Map<String, Object> config = new HashMap<>();
        // 从配置中心或配置文件加载
        config.put("version", "2.3.1");
        config.put("theme", "default");
        config.put("language", "zh-CN");
        return config;
    }

    /**
     * 加载已安装的 Skills
     */
    private Map<String, SkillInfo> loadInstalledSkills(String userId) {
        Map<String, SkillInfo> skills = new HashMap<>();

        // 这里应该从 Skill 注册中心或数据库加载
        // 暂时返回空 map，实际实现需要集成 Skill 管理模块

        return skills;
    }

    /**
     * 构建目标 URL
     */
    private String buildTargetUrl(String skillId) {
        return "/console/skills/" + skillId + "/pages/index.html";
    }
}

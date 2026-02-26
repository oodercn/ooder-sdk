package net.ooder.scene.core.impl;

import net.ooder.engine.ConnectInfo;
import net.ooder.scene.core.*;
import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.SceneEventType;
import net.ooder.scene.provider.HeartbeatProvider;
import net.ooder.scene.provider.SceneProvider;
import net.ooder.scene.provider.UserSettingsProvider;
import net.ooder.scene.session.SessionInfo;
import net.ooder.scene.session.SessionManager;
import net.ooder.scene.skill.SkillService;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SceneEngine 实现类
 * 整合 Skills 生命周期管理
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SceneEngineImpl implements SceneEngine {

    private final String engineName = "OoderSceneEngine";
    private final String engineVersion = "2.3.0";

    private SessionManager sessionManager;
    private SkillService skillService;
    private SceneEventPublisher eventPublisher;
    private SceneProvider sceneProvider;
    private HeartbeatProvider heartbeatProvider;
    private UserSettingsProvider userSettingsProvider;

    // Skills 管理 - SkillId -> SkillHolder
    private final Map<String, SkillHolder> skillRegistry = new ConcurrentHashMap<>();

    // 全局 ConnectInfo - 由 JDSServer 注入
    private ConnectInfo globalConnectInfo;

    private EngineStatus status = EngineStatus.STOPPED;

    public SceneEngineImpl() {
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setSkillService(SkillService skillService) {
        this.skillService = skillService;
    }

    public SkillService getSkillService() {
        return skillService;
    }

    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setSceneProvider(SceneProvider sceneProvider) {
        this.sceneProvider = sceneProvider;
    }

    public SceneProvider getSceneProvider() {
        return sceneProvider;
    }

    public void setHeartbeatProvider(HeartbeatProvider heartbeatProvider) {
        this.heartbeatProvider = heartbeatProvider;
    }

    public HeartbeatProvider getHeartbeatProvider() {
        return heartbeatProvider;
    }

    public void setUserSettingsProvider(UserSettingsProvider userSettingsProvider) {
        this.userSettingsProvider = userSettingsProvider;
    }

    public UserSettingsProvider getUserSettingsProvider() {
        return userSettingsProvider;
    }

    /**
     * 设置全局 ConnectInfo (由 JDSServer 注入)
     * @param connectInfo 全局连接信息
     */
    public void setGlobalConnectInfo(ConnectInfo connectInfo) {
        this.globalConnectInfo = connectInfo;
    }

    /**
     * 获取全局 ConnectInfo
     */
    public ConnectInfo getGlobalConnectInfo() {
        return globalConnectInfo;
    }

    // ==================== SceneEngine 接口实现 ====================

    @Override
    public SceneClient login(String username, String password) {
        // 创建 Session
        SessionInfo session = sessionManager.createSession(
            null, username, null, null
        );

        // 发布登录事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.security.LoginEvent(
                this, SceneEventType.LOGIN_SUCCESS, session.getUserId(), username, true, null
            ));
        }

        return new SceneClientImpl(session, this);
    }

    @Override
    public SceneClient login(String token) {
        // Token 验证逻辑
        return null;
    }

    @Override
    public AdminClient adminLogin(String username, String password) {
        // 管理员登录逻辑
        return null;
    }

    @Override
    public void logout(String sessionId) {
        sessionManager.destroySession(sessionId);

        // 发布登出事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.security.LogoutEvent(
                this, SceneEventType.LOGOUT, sessionId
            ));
        }
    }

    @Override
    public SessionInfo getSession(String sessionId) {
        return sessionManager.getSession(sessionId);
    }

    @Override
    public boolean validateSession(String sessionId) {
        return sessionManager.validateSession(sessionId);
    }

    @Override
    public SessionInfo refreshSession(String sessionId) {
        return sessionManager.refreshSession(sessionId);
    }

    @Override
    public EngineStatus getStatus() {
        return status;
    }

    @Override
    public void start() {
        this.status = EngineStatus.RUNNING;

        // 启动所有已注册的 Skills
        for (SkillHolder holder : skillRegistry.values()) {
            if (holder.isAutoStart()) {
                startSkill(holder.getSkillId());
            }
        }

        // 发布引擎启动事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.engine.EngineEvent(
                this, SceneEventType.ENGINE_STARTED, engineName, "SceneEngine started"
            ));
        }
    }

    @Override
    public void stop() {
        // 停止所有 Skills
        for (SkillHolder holder : skillRegistry.values()) {
            if (holder.getStatus() == SkillStatus.RUNNING) {
                stopSkill(holder.getSkillId());
            }
        }

        this.status = EngineStatus.STOPPED;

        // 发布引擎停止事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.engine.EngineEvent(
                this, SceneEventType.ENGINE_STOPPED, engineName, "SceneEngine stopped"
            ));
        }
    }

    @Override
    public String getName() {
        return engineName;
    }

    @Override
    public String getVersion() {
        return engineVersion;
    }

    // ==================== Skills 生命周期管理 ====================

    /**
     * 注册 Skill
     * @param skillId Skill ID
     * @param skill Skill 实例
     * @param connectInfo 连接信息
     */
    public void registerSkill(String skillId, Object skill, ConnectInfo connectInfo) {
        SkillHolder holder = new SkillHolder(skillId, skill, connectInfo);
        skillRegistry.put(skillId, holder);

        // 发布 Skill 注册事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.skill.SkillEvent(
                this, SceneEventType.SKILL_INSTALLED, skillId, "Skill registered: " + skillId
            ));
        }
    }

    /**
     * 卸载 Skill
     * @param skillId Skill ID
     */
    public void unregisterSkill(String skillId) {
        SkillHolder holder = skillRegistry.remove(skillId);
        if (holder != null && holder.getStatus() == SkillStatus.RUNNING) {
            stopSkill(skillId);
        }

        // 发布 Skill 卸载事件
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new net.ooder.scene.event.skill.SkillEvent(
                this, SceneEventType.SKILL_UNINSTALLED, skillId, "Skill unregistered: " + skillId
            ));
        }
    }

    /**
     * 启动 Skill
     * @param skillId Skill ID
     */
    public void startSkill(String skillId) {
        SkillHolder holder = skillRegistry.get(skillId);
        if (holder != null) {
            holder.setStatus(SkillStatus.RUNNING);

            // 发布 Skill 启动事件
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new net.ooder.scene.event.skill.SkillEvent(
                    this, SceneEventType.SKILL_STARTED, skillId, "Skill started: " + skillId
                ));
            }
        }
    }

    /**
     * 停止 Skill
     * @param skillId Skill ID
     */
    public void stopSkill(String skillId) {
        SkillHolder holder = skillRegistry.get(skillId);
        if (holder != null) {
            holder.setStatus(SkillStatus.STOPPED);

            // 发布 Skill 停止事件
            if (eventPublisher != null) {
                eventPublisher.publishEvent(new net.ooder.scene.event.skill.SkillEvent(
                    this, SceneEventType.SKILL_STOPPED, skillId, "Skill stopped: " + skillId
                ));
            }
        }
    }

    /**
     * 暂停 Skill
     * @param skillId Skill ID
     */
    public void pauseSkill(String skillId) {
        SkillHolder holder = skillRegistry.get(skillId);
        if (holder != null) {
            holder.setStatus(SkillStatus.PAUSED);
        }
    }

    /**
     * 恢复 Skill
     * @param skillId Skill ID
     */
    public void resumeSkill(String skillId) {
        SkillHolder holder = skillRegistry.get(skillId);
        if (holder != null) {
            holder.setStatus(SkillStatus.RUNNING);
        }
    }

    /**
     * 获取 Skill 状态
     * @param skillId Skill ID
     * @return Skill 状态
     */
    public SkillStatus getSkillStatus(String skillId) {
        SkillHolder holder = skillRegistry.get(skillId);
        return holder != null ? holder.getStatus() : SkillStatus.UNKNOWN;
    }

    /**
     * 获取已注册的 Skills
     * @return Skills 映射
     */
    public Map<String, SkillHolder> getRegisteredSkills() {
        return new ConcurrentHashMap<>(skillRegistry);
    }

    // ==================== SkillHolder 内部类 ====================

    /**
     * Skill 持有者
     * 封装 Skill 实例和生命周期状态
     */
    public static class SkillHolder {
        private final String skillId;
        private final Object skill;
        private final ConnectInfo connectInfo;
        private SkillStatus status = SkillStatus.INITIALIZED;
        private boolean autoStart = true;

        public SkillHolder(String skillId, Object skill, ConnectInfo connectInfo) {
            this.skillId = skillId;
            this.skill = skill;
            this.connectInfo = connectInfo;
        }

        public String getSkillId() { return skillId; }
        public Object getSkill() { return skill; }
        public ConnectInfo getConnectInfo() { return connectInfo; }
        public SkillStatus getStatus() { return status; }
        public void setStatus(SkillStatus status) { this.status = status; }
        public boolean isAutoStart() { return autoStart; }
        public void setAutoStart(boolean autoStart) { this.autoStart = autoStart; }
    }

    /**
     * Skill 状态枚举
     */
    public enum SkillStatus {
        INITIALIZED,
        RUNNING,
        PAUSED,
        STOPPED,
        ERROR,
        UNKNOWN
    }
}

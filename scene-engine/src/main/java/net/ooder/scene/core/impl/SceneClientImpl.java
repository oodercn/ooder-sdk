package net.ooder.scene.core.impl;

import net.ooder.scene.core.*;
import net.ooder.scene.provider.HeartbeatProvider;
import net.ooder.scene.provider.SceneProvider;
import net.ooder.scene.provider.UserSettingsProvider;
import net.ooder.scene.session.SessionInfo;
import net.ooder.scene.skill.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * SceneClient 实现类
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.0
 */
public class SceneClientImpl implements SceneClient {

    private final SessionInfo session;
    private final SceneEngine engine;

    public SceneClientImpl(SessionInfo session, SceneEngine engine) {
        this.session = session;
        this.engine = engine;
    }

    @Override
    public String getSessionId() {
        return session != null ? session.getSessionId() : null;
    }

    @Override
    public String getUserId() {
        return session != null ? session.getUserId() : null;
    }

    @Override
    public String getUsername() {
        return session != null ? session.getUsername() : null;
    }

    @Override
    public String getToken() {
        return session != null ? session.getToken() : null;
    }

    @Override
    public Object findSkill(String skillId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.findSkill(skillId);
        }
        return null;
    }

    @Override
    public List<Object> searchSkills(SkillQuery query) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.searchSkills(query);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Object> listMySkills() {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            return skillService.listInstalledSkills(getUserId());
        }
        return new ArrayList<>();
    }

    @Override
    public Object installSkill(String skillId) {
        return installSkill(skillId, null);
    }

    @Override
    public Object installSkill(String skillId, Map<String, Object> config) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            boolean result = skillService.installSkill(skillId, "latest", config);
            return result;
        }
        return false;
    }

    @Override
    public Object uninstallSkill(String skillId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            return skillService.uninstallSkill(skillId, getUserId());
        }
        return false;
    }

    @Override
    public Object getInstallProgress(String installId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            int progress = skillService.getInstallProgress(installId);
            return progress;
        }
        return 0;
    }

    @Override
    public List<Object> listAvailableScenes() {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.listAvailableScenes();
        }
        return new ArrayList<>();
    }

    @Override
    public Object joinSceneGroup(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            boolean result = sceneProvider.joinSceneGroup(sceneId, getUserId());
            return result;
        }
        return null;
    }

    @Override
    public Object joinSceneGroup(String sceneId, String inviteCode) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            boolean result = sceneProvider.joinSceneGroup(sceneId, getUserId());
            return result;
        }
        return null;
    }

    @Override
    public void leaveSceneGroup(String groupId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            sceneProvider.leaveSceneGroup(groupId, getUserId());
        }
    }

    @Override
    public List<Object> listMySceneGroups() {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            return sceneProvider.listMySceneGroups(getUserId());
        }
        return new ArrayList<>();
    }

    @Override
    public Object getSceneGroup(String groupId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.getSceneGroup(groupId);
        }
        return null;
    }

    @Override
    public Object invokeCapability(String skillId, String capability, Map<String, Object> params) {
        // 通过引擎获取 SkillSDKAdapter 调用能力
        try {
            Object adapter = engine.getService("skillSDKAdapter", Object.class);
            if (adapter != null) {
                // 使用反射调用 SkillSDKAdapter.invokeCapability
                java.lang.reflect.Method method = adapter.getClass().getMethod(
                    "invokeCapability", String.class, String.class, String.class, Map.class);
                return method.invoke(adapter, getUserId(), skillId, capability, params);
            }
        } catch (Exception e) {
            // 降级处理：返回空结果
        }
        return null;
    }

    @Override
    public List<Object> listCapabilities(String skillId) {
        // 通过引擎获取 CapabilityDiscoveryService 查询能力
        try {
            Object discoveryService = engine.getService(
                net.ooder.scene.discovery.CapabilityDiscoveryService.class);
            if (discoveryService != null) {
                java.lang.reflect.Method method = discoveryService.getClass().getMethod(
                    "getAvailableSkills", String.class);
                Object result = method.invoke(discoveryService, skillId);
                if (result instanceof java.util.concurrent.CompletableFuture) {
                    return (List<Object>) ((java.util.concurrent.CompletableFuture<?>) result).get();
                }
            }
        } catch (Exception e) {
            // 降级处理：返回空列表
        }
        return new ArrayList<>();
    }

    @Override
    public Object getSettings() {
        UserSettingsProvider provider = engine.getUserSettingsProvider();
        if (provider != null && getUserId() != null) {
            return provider.getUserSettings(getUserId());
        }
        return null;
    }

    @Override
    public void updateSettings(Object settings) {
        UserSettingsProvider provider = engine.getUserSettingsProvider();
        if (provider != null && getUserId() != null) {
            // 将 settings 转换为 Map 并更新
            if (settings instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> settingsMap = (Map<String, Object>) settings;
                provider.updateUserSettings(getUserId(), settingsMap);
            }
        }
    }

    @Override
    public Object getIdentity() {
        // 从 Session 获取身份信息
        if (session == null) {
            return null;
        }
        
        // 构建身份信息对象
        IdentityInfo identity = new IdentityInfo();
        identity.setUserId(session.getUserId());
        identity.setUsername(session.getUsername());
        identity.setSessionId(session.getSessionId());
        identity.setDomain(session.getDomain());
        identity.setClientIp(session.getClientIp());
        identity.setUserAgent(session.getUserAgent());
        identity.setStatus(session.getStatus());
        identity.setCreatedAt(session.getCreatedAt());
        identity.setExpiresAt(session.getExpiresAt());
        
        return identity;
    }

    @Override
    public CompletableFuture<Object> startHeartbeat(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            // 注册客户端并发送心跳
            provider.registerClient(getUserId(), groupId);
            provider.sendHeartbeat(getUserId());
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void stopHeartbeat(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            // 注销客户端停止心跳
            provider.unregisterClient(getUserId());
        }
    }

    @Override
    public Object getHeartbeatStatus(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            return provider.checkClientStatus(getUserId());
        }
        return null;
    }

    @Override
    public boolean activateScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.updateSceneStatus(sceneId, "active");
        }
        return false;
    }

    @Override
    public boolean deactivateScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.updateSceneStatus(sceneId, "inactive");
        }
        return false;
    }

    @Override
    public String getSceneState(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            Object status = sceneProvider.getSceneStatus(sceneId);
            return status != null ? status.toString() : null;
        }
        return null;
    }

    @Override
    public Object getScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.getSceneStatus(sceneId);
        }
        return null;
    }
}

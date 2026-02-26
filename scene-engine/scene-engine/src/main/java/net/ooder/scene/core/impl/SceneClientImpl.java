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
 * @version 2.3
 * @since 2.3.0
 */
public class SceneClientImpl implements SceneClient {

    private final SessionInfo session;
    private final SceneEngineImpl engine;

    public SceneClientImpl(SessionInfo session, SceneEngineImpl engine) {
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
    public SkillInfo findSkill(String skillId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.findSkill(skillId);
        }
        return null;
    }

    @Override
    public List<SkillInfo> searchSkills(SkillQuery query) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.searchSkills(query);
        }
        return new ArrayList<>();
    }

    @Override
    public List<InstalledSkillInfo> listMySkills() {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            return skillService.listInstalledSkills(getUserId());
        }
        return new ArrayList<>();
    }

    @Override
    public SkillInstallResult installSkill(String skillId) {
        return installSkill(skillId, null);
    }

    @Override
    public SkillInstallResult installSkill(String skillId, Map<String, Object> config) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            if (config != null) {
                return skillService.installSkill(getUserId(), skillId, config);
            } else {
                return skillService.installSkill(getUserId(), skillId);
            }
        }
        return SkillInstallResult.failed(skillId, "SkillService not available or user not authenticated");
    }

    @Override
    public SkillUninstallResult uninstallSkill(String skillId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            return skillService.uninstallSkill(getUserId(), skillId);
        }
        return SkillUninstallResult.failed(skillId, "SkillService not available or user not authenticated");
    }

    @Override
    public SkillInstallProgress getInstallProgress(String installId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.getInstallProgress(installId);
        }
        return null;
    }

    @Override
    public List<SceneInfo> listAvailableScenes() {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.listAvailableScenes();
        }
        return new ArrayList<>();
    }

    @Override
    public SceneGroupInfo joinSceneGroup(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            return sceneProvider.joinSceneGroup(getUserId(), sceneId);
        }
        return null;
    }

    @Override
    public SceneGroupInfo joinSceneGroup(String sceneId, String inviteCode) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            return sceneProvider.joinSceneGroup(getUserId(), sceneId, inviteCode);
        }
        return null;
    }

    @Override
    public void leaveSceneGroup(String groupId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            sceneProvider.leaveSceneGroup(getUserId(), groupId);
        }
    }

    @Override
    public List<SceneGroupInfo> listMySceneGroups() {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null && getUserId() != null) {
            return sceneProvider.listMySceneGroups(getUserId());
        }
        return new ArrayList<>();
    }

    @Override
    public SceneGroupInfo getSceneGroup(String groupId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.getSceneGroup(groupId);
        }
        return null;
    }

    @Override
    public Object invokeCapability(String skillId, String capability, Map<String, Object> params) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null && getUserId() != null) {
            return skillService.invokeCapability(getUserId(), skillId, capability, params);
        }
        return null;
    }

    @Override
    public List<CapabilityInfo> listCapabilities(String skillId) {
        SkillService skillService = engine.getSkillService();
        if (skillService != null) {
            return skillService.listCapabilities(skillId);
        }
        return new ArrayList<>();
    }

    @Override
    public UserSettings getSettings() {
        UserSettingsProvider provider = engine.getUserSettingsProvider();
        if (provider != null && getUserId() != null) {
            return provider.getSettings(getUserId());
        }
        return null;
    }

    @Override
    public void updateSettings(UserSettings settings) {
        UserSettingsProvider provider = engine.getUserSettingsProvider();
        if (provider != null && getUserId() != null) {
            provider.updateSettings(getUserId(), settings);
        }
    }

    @Override
    public IdentityInfo getIdentity() {
        // TODO: 实现获取身份信息 - 需要 IdentityProvider
        return null;
    }

    @Override
    public CompletableFuture<HeartbeatResult> startHeartbeat(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            return provider.startHeartbeat(getUserId(), groupId);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void stopHeartbeat(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            provider.stopHeartbeat(getUserId(), groupId);
        }
    }

    @Override
    public HeartbeatStatus getHeartbeatStatus(String groupId) {
        HeartbeatProvider provider = engine.getHeartbeatProvider();
        if (provider != null && getUserId() != null) {
            return provider.getHeartbeatStatus(getUserId(), groupId);
        }
        return null;
    }

    @Override
    public boolean activateScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.activateScene(sceneId);
        }
        return false;
    }

    @Override
    public boolean deactivateScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.deactivateScene(sceneId);
        }
        return false;
    }

    @Override
    public String getSceneState(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.getSceneState(sceneId);
        }
        return null;
    }

    @Override
    public SceneInfo getScene(String sceneId) {
        SceneProvider sceneProvider = engine.getSceneProvider();
        if (sceneProvider != null) {
            return sceneProvider.getScene(sceneId);
        }
        return null;
    }
}

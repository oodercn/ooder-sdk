package net.ooder.sdk.agent.command;

import net.ooder.sdk.a2a.A2ACommand;
import net.ooder.sdk.common.enums.MemberRole;

import java.util.HashMap;
import java.util.Map;

/**
 * 场景协作命令定义
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class SceneCollaborationCommands {

    public static final String SCENE_INSTALL = "scene.install";
    public static final String SCENE_ACTIVATE = "scene.activate";
    public static final String SCENE_DEACTIVATE = "scene.deactivate";
    public static final String SCENE_UNINSTALL = "scene.uninstall";
    public static final String SCENE_STATUS = "scene.status";
    public static final String SCENE_SYNC = "scene.sync";

    /**
     * 创建场景安装命令
     */
    public static A2ACommand createInstallCommand(String sceneId, String targetAgentId, Map<String, Object> params) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        body.put("params", params);
        
        return A2ACommand.builder()
            .commandType(SCENE_INSTALL)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 创建场景激活命令
     */
    public static A2ACommand createActivateCommand(String sceneId, String targetAgentId, MemberRole role) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        body.put("role", role.name());
        
        return A2ACommand.builder()
            .commandType(SCENE_ACTIVATE)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 创建场景停用命令
     */
    public static A2ACommand createDeactivateCommand(String sceneId, String targetAgentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        
        return A2ACommand.builder()
            .commandType(SCENE_DEACTIVATE)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 创建场景卸载命令
     */
    public static A2ACommand createUninstallCommand(String sceneId, String targetAgentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        
        return A2ACommand.builder()
            .commandType(SCENE_UNINSTALL)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 创建场景状态查询命令
     */
    public static A2ACommand createStatusCommand(String sceneId, String targetAgentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        
        return A2ACommand.builder()
            .commandType(SCENE_STATUS)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 创建场景状态同步命令
     */
    public static A2ACommand createSyncCommand(String sceneId, String targetAgentId, SceneStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("sceneId", sceneId);
        body.put("status", status.name());
        
        return A2ACommand.builder()
            .commandType(SCENE_SYNC)
            .targetAgentId(targetAgentId)
            .payload(body)
            .build();
    }

    /**
     * 场景状态枚举
     */
    public enum SceneStatus {
        INITIALIZING,
        INSTALLING,
        CONFIGURING,
        ACTIVATING,
        ACTIVE,
        DEACTIVATING,
        DEACTIVATED,
        UNINSTALLING,
        UNINSTALLED,
        ERROR
    }
}

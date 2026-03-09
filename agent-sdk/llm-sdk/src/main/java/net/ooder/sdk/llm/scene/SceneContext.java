package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SceneContext {

    /**
     * 场景ID
     */
    private String sceneId;

    /**
     * 场景名称
     */
    private String sceneName;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 场景描述
     */
    private String description;

    /**
     * 场景配置
     */
    private Map<String, Object> config;

    /**
     * 场景角色
     */
    private List<SceneRole> roles;

    /**
     * 场景变量
     */
    @Builder.Default
    private Map<String, Object> variables = new HashMap<>();

    /**
     * 父场景ID（用于场景继承）
     */
    private String parentSceneId;

    /**
     * 场景元数据
     */
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();

    /**
     * 场景角色
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneRole {
        private String roleId;
        private String roleName;
        private String roleType;
        private String description;
        private Map<String, Object> permissions;
    }
}

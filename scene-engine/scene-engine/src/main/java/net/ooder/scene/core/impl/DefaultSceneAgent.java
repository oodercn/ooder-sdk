package net.ooder.scene.core.impl;

import net.ooder.scene.core.SceneAgent;
import net.ooder.scene.core.SceneAgentState;
import net.ooder.scene.core.SceneConfig;
import net.ooder.scene.core.SceneContext;
import net.ooder.scene.core.CapRequest;
import net.ooder.scene.core.CapResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class DefaultSceneAgent implements SceneAgent {
    private String agentId;
    private SceneAgentState state;
    private SceneContext context;
    private Map<String, Object> skills;

    public DefaultSceneAgent() {
        this.agentId = "scene-agent-" + UUID.randomUUID().toString();
        this.state = SceneAgentState.INITIALIZED;
        this.context = new SceneContext(agentId);
        this.skills = new ConcurrentHashMap<>();
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public SceneAgentState getState() {
        return state;
    }

    @Override
    public void initialize(SceneConfig config) {
        if (state == SceneAgentState.INITIALIZED) {
            // 初始化场景配置
            context.setAttribute("config", config);
            state = SceneAgentState.RUNNING;
        }
    }

    @Override
    public void mountSkill(String skillId, SceneConfig config) {
        if (state == SceneAgentState.RUNNING) {
            // 挂载技能
            context.addSkillConfig(skillId, config);
            // 这里可以添加技能初始化逻辑
            skills.put(skillId, new Object()); // 占位符，实际应该是技能实例
        }
    }

    @Override
    public void unmountSkill(String skillId) {
        if (state == SceneAgentState.RUNNING) {
            // 卸载技能
            context.removeSkillConfig(skillId);
            skills.remove(skillId);
        }
    }

    @Override
    public CapResponse invokeCap(String capId, CapRequest request) {
        if (state != SceneAgentState.RUNNING) {
            return CapResponse.failure(request.getRequestId(), capId, "SceneAgent is not running");
        }

        // 这里应该实现能力调用的路由逻辑
        // 暂时返回一个模拟的成功响应
        return CapResponse.success(request.getRequestId(), capId, "Capability invoked successfully");
    }

    @Override
    public void shutdown() {
        if (state != SceneAgentState.SHUTDOWN) {
            // 清理资源
            skills.clear();
            context.clear();
            state = SceneAgentState.SHUTDOWN;
        }
    }

    public SceneContext getContext() {
        return context;
    }

    public Map<String, Object> getSkills() {
        return skills;
    }
}

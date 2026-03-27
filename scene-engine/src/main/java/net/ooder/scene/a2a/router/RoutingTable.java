package net.ooder.scene.a2a.router;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由表
 *
 * <p>维护 Agent 与场景组的映射关系。</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class RoutingTable {

    private final Map<String, String> agentToScene = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sceneToAgents = new ConcurrentHashMap<>();
    private final Map<String, String> defaultTargets = new ConcurrentHashMap<>();

    public void register(String agentId, String sceneGroupId) {
        if (agentId == null) {
            return;
        }
        
        String oldScene = agentToScene.get(agentId);
        if (oldScene != null && !oldScene.equals(sceneGroupId)) {
            Set<String> agents = sceneToAgents.get(oldScene);
            if (agents != null) {
                agents.remove(agentId);
            }
        }
        
        agentToScene.put(agentId, sceneGroupId);
        
        if (sceneGroupId != null) {
            sceneToAgents.computeIfAbsent(sceneGroupId, k -> ConcurrentHashMap.newKeySet()).add(agentId);
        }
    }

    public void unregister(String agentId) {
        if (agentId == null) {
            return;
        }
        
        String sceneGroupId = agentToScene.remove(agentId);
        if (sceneGroupId != null) {
            Set<String> agents = sceneToAgents.get(sceneGroupId);
            if (agents != null) {
                agents.remove(agentId);
            }
        }
        
        defaultTargets.values().removeIf(agentId::equals);
    }

    public String getScene(String agentId) {
        return agentToScene.get(agentId);
    }

    public List<String> getAgentsInScene(String sceneGroupId) {
        if (sceneGroupId == null) {
            return new ArrayList<>();
        }
        
        Set<String> agents = sceneToAgents.get(sceneGroupId);
        return agents != null ? new ArrayList<>(agents) : new ArrayList<>();
    }

    public void setDefaultTarget(String sceneGroupId, String agentId) {
        if (sceneGroupId != null) {
            if (agentId != null) {
                defaultTargets.put(sceneGroupId, agentId);
            } else {
                defaultTargets.remove(sceneGroupId);
            }
        }
    }

    public String getDefaultTarget(String sceneGroupId) {
        return sceneGroupId != null ? defaultTargets.get(sceneGroupId) : null;
    }

    public boolean containsAgent(String agentId) {
        return agentToScene.containsKey(agentId);
    }

    public int getAgentCount() {
        return agentToScene.size();
    }

    public int getSceneCount() {
        return sceneToAgents.size();
    }

    public void clear() {
        agentToScene.clear();
        sceneToAgents.clear();
        defaultTargets.clear();
    }
}

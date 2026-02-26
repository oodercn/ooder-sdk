package net.ooder.engine.scene.store;

import net.ooder.sdk.api.scene.store.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DualSceneStore implements SceneStore, GroupStore, SkillStore, LinkStore, AgentStore {
    
    private static final Logger log = LoggerFactory.getLogger(DualSceneStore.class);
    
    private final LocalSceneStore localStore;
    private final String basePath;
    private final ConflictStrategy conflictStrategy;
    
    private final Map<String, PendingSync> pendingSyncs;
    private final List<SyncListener> syncListeners;
    private final StorageStatus status;
    
    private volatile boolean remoteAvailable = false;
    
    public enum ConflictStrategy {
        LOCAL_WINS,
        REMOTE_WINS,
        MERGE,
        LATEST_WINS
    }
    
    public DualSceneStore(LocalSceneStore localStore, String basePath) {
        this(localStore, basePath, ConflictStrategy.REMOTE_WINS);
    }
    
    public DualSceneStore(LocalSceneStore localStore, String basePath, ConflictStrategy conflictStrategy) {
        this.localStore = localStore;
        this.basePath = basePath;
        this.conflictStrategy = conflictStrategy;
        this.pendingSyncs = new ConcurrentHashMap<>();
        this.syncListeners = new CopyOnWriteArrayList<>();
        this.status = new StorageStatus();
    }
    
    // ==================== SceneStore ====================
    
    @Override
    public void saveScene(String sceneId, Map<String, Object> config) {
        localStore.saveScene(sceneId, config);
        log.debug("Scene saved to local store: {}", sceneId);
    }
    
    @Override
    public Map<String, Object> loadScene(String sceneId) {
        return localStore.loadScene(sceneId);
    }
    
    @Override
    public void deleteScene(String sceneId) {
        localStore.deleteScene(sceneId);
        log.debug("Scene deleted from local store: {}", sceneId);
    }
    
    @Override
    public List<String> listScenes() {
        return localStore.listScenes();
    }
    
    @Override
    public boolean sceneExists(String sceneId) {
        return localStore.sceneExists(sceneId);
    }
    
    @Override
    public void updateSceneConfig(String sceneId, String key, Object value) {
        localStore.updateSceneConfig(sceneId, key, value);
    }
    
    @Override
    public Object getSceneConfigValue(String sceneId, String key) {
        return localStore.getSceneConfigValue(sceneId, key);
    }
    
    // ==================== GroupStore ====================
    
    @Override
    public void saveGroup(String sceneId, String groupId, Map<String, Object> config) {
        String fullGroupId = sceneId + ":" + groupId;
        localStore.saveScene(fullGroupId, config);
    }
    
    @Override
    public Map<String, Object> loadGroup(String sceneId, String groupId) {
        String fullGroupId = sceneId + ":" + groupId;
        return localStore.loadScene(fullGroupId);
    }
    
    @Override
    public void deleteGroup(String sceneId, String groupId) {
        String fullGroupId = sceneId + ":" + groupId;
        localStore.deleteScene(fullGroupId);
    }
    
    @Override
    public List<String> listGroups(String sceneId) {
        List<String> allKeys = localStore.listScenes();
        List<String> groups = new ArrayList<>();
        String prefix = sceneId + ":";
        for (String key : allKeys) {
            if (key.startsWith(prefix)) {
                groups.add(key);
            }
        }
        return groups;
    }
    
    @Override
    public boolean groupExists(String sceneId, String groupId) {
        return localStore.sceneExists(sceneId + ":" + groupId);
    }
    
    @Override
    public void updateGroupConfig(String sceneId, String groupId, String key, Object value) {
        Map<String, Object> config = loadGroup(sceneId, groupId);
        if (config == null) {
            config = new LinkedHashMap<>();
        }
        config.put(key, value);
        saveGroup(sceneId, groupId, config);
    }
    
    @Override
    public Object getGroupConfigValue(String sceneId, String groupId, String key) {
        Map<String, Object> config = loadGroup(sceneId, groupId);
        return config != null ? config.get(key) : null;
    }
    
    @Override
    public void addSkillToGroup(String sceneId, String groupId, String skillId) {
        Map<String, Object> config = loadGroup(sceneId, groupId);
        if (config == null) {
            config = new LinkedHashMap<>();
        }
        @SuppressWarnings("unchecked")
        List<String> skills = (List<String>) config.get("skillIds");
        if (skills == null) {
            skills = new ArrayList<>();
            config.put("skillIds", skills);
        }
        if (!skills.contains(skillId)) {
            skills.add(skillId);
            saveGroup(sceneId, groupId, config);
        }
    }
    
    @Override
    public void removeSkillFromGroup(String sceneId, String groupId, String skillId) {
        Map<String, Object> config = loadGroup(sceneId, groupId);
        if (config != null) {
            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) config.get("skillIds");
            if (skills != null) {
                skills.remove(skillId);
                saveGroup(sceneId, groupId, config);
            }
        }
    }
    
    @Override
    public List<String> getGroupSkills(String sceneId, String groupId) {
        Map<String, Object> config = loadGroup(sceneId, groupId);
        if (config != null) {
            @SuppressWarnings("unchecked")
            List<String> skills = (List<String>) config.get("skillIds");
            return skills != null ? new ArrayList<>(skills) : new ArrayList<>();
        }
        return new ArrayList<>();
    }
    
    // ==================== SkillStore ====================
    
    private final Map<String, SkillRegistration> skillCache = new ConcurrentHashMap<>();
    
    @Override
    public void saveSkill(SkillRegistration registration) {
        if (registration == null || registration.getSkillId() == null) {
            return;
        }
        
        skillCache.put(registration.getSkillId(), registration);
        
        Map<String, Object> config = skillToMap(registration);
        localStore.saveScene("skill:" + registration.getSkillId(), config);
    }
    
    @Override
    public SkillRegistration loadSkill(String skillId) {
        SkillRegistration cached = skillCache.get(skillId);
        if (cached != null) {
            return cached;
        }
        
        Map<String, Object> config = localStore.loadScene("skill:" + skillId);
        if (config != null) {
            SkillRegistration registration = mapToSkill(config);
            skillCache.put(skillId, registration);
            return registration;
        }
        
        return null;
    }
    
    @Override
    public void deleteSkill(String skillId) {
        skillCache.remove(skillId);
        localStore.deleteScene("skill:" + skillId);
    }
    
    @Override
    public List<SkillRegistration> listSkills(String sceneId, String groupId) {
        List<SkillRegistration> result = new ArrayList<>();
        for (SkillRegistration reg : skillCache.values()) {
            if ((sceneId == null || sceneId.equals(reg.getSceneId())) &&
                (groupId == null || groupId.equals(reg.getGroupId()))) {
                result.add(reg);
            }
        }
        return result;
    }
    
    @Override
    public List<SkillRegistration> listSkillsByScene(String sceneId) {
        return listSkills(sceneId, null);
    }
    
    @Override
    public List<SkillRegistration> listSkillsByType(String sceneId, String skillType) {
        List<SkillRegistration> result = new ArrayList<>();
        for (SkillRegistration reg : skillCache.values()) {
            if ((sceneId == null || sceneId.equals(reg.getSceneId())) &&
                (skillType == null || skillType.equals(reg.getSkillType()))) {
                result.add(reg);
            }
        }
        return result;
    }
    
    @Override
    public boolean skillExists(String skillId) {
        return skillCache.containsKey(skillId) || localStore.sceneExists("skill:" + skillId);
    }
    
    @Override
    public void updateSkillHeartbeat(String skillId, long timestamp) {
        SkillRegistration registration = loadSkill(skillId);
        if (registration != null) {
            registration.setLastHeartbeat(timestamp);
            saveSkill(registration);
        }
    }
    
    @Override
    public Long getSkillLastHeartbeat(String skillId) {
        SkillRegistration registration = loadSkill(skillId);
        return registration != null ? registration.getLastHeartbeat() : null;
    }
    
    @Override
    public void updateSkillStatus(String skillId, String status) {
        SkillRegistration registration = loadSkill(skillId);
        if (registration != null) {
            registration.setStatus(status);
            saveSkill(registration);
        }
    }
    
    @Override
    public void updateSkillEndpoints(String skillId, Map<String, Object> endpoints) {
        SkillRegistration registration = loadSkill(skillId);
        if (registration != null) {
            registration.setEndpoints(endpoints);
            saveSkill(registration);
        }
    }
    
    // ==================== LinkStore ====================
    
    private final Map<String, LinkConfig> linkCache = new ConcurrentHashMap<>();
    
    @Override
    public void saveLink(LinkConfig link) {
        if (link == null || link.getLinkId() == null) {
            return;
        }
        
        linkCache.put(link.getLinkId(), link);
        
        Map<String, Object> config = linkToMap(link);
        localStore.saveScene("link:" + link.getLinkId(), config);
    }
    
    @Override
    public LinkConfig loadLink(String linkId) {
        LinkConfig cached = linkCache.get(linkId);
        if (cached != null) {
            return cached;
        }
        
        Map<String, Object> config = localStore.loadScene("link:" + linkId);
        if (config != null) {
            LinkConfig link = mapToLink(config);
            linkCache.put(linkId, link);
            return link;
        }
        
        return null;
    }
    
    @Override
    public void deleteLink(String linkId) {
        linkCache.remove(linkId);
        localStore.deleteScene("link:" + linkId);
    }
    
    @Override
    public List<LinkConfig> listLinks(String sceneId) {
        List<LinkConfig> result = new ArrayList<>();
        for (LinkConfig link : linkCache.values()) {
            if (sceneId == null || sceneId.equals(link.getSceneId())) {
                result.add(link);
            }
        }
        return result;
    }
    
    @Override
    public List<LinkConfig> listAllLinks() {
        return new ArrayList<>(linkCache.values());
    }
    
    @Override
    public List<LinkConfig> getLinksBySource(String sourceId) {
        List<LinkConfig> result = new ArrayList<>();
        for (LinkConfig link : linkCache.values()) {
            if (sourceId.equals(link.getSourceId())) {
                result.add(link);
            }
        }
        return result;
    }
    
    @Override
    public List<LinkConfig> getLinksByTarget(String targetId) {
        List<LinkConfig> result = new ArrayList<>();
        for (LinkConfig link : linkCache.values()) {
            if (targetId.equals(link.getTargetId())) {
                result.add(link);
            }
        }
        return result;
    }
    
    @Override
    public List<LinkConfig> getLinksByType(String sceneId, String linkType) {
        List<LinkConfig> result = new ArrayList<>();
        for (LinkConfig link : linkCache.values()) {
            if ((sceneId == null || sceneId.equals(link.getSceneId())) &&
                (linkType == null || linkType.equals(link.getLinkType()))) {
                result.add(link);
            }
        }
        return result;
    }
    
    @Override
    public boolean linkExists(String linkId) {
        return linkCache.containsKey(linkId) || localStore.sceneExists("link:" + linkId);
    }
    
    @Override
    public void updateLinkStatus(String linkId, String status) {
        LinkConfig link = loadLink(linkId);
        if (link != null) {
            link.setStatus(status);
            saveLink(link);
        }
    }
    
    // ==================== AgentStore ====================
    
    private final Map<String, AgentRegistration> agentCache = new ConcurrentHashMap<>();
    
    @Override
    public void saveAgent(AgentRegistration registration) {
        if (registration == null || registration.getAgentId() == null) {
            return;
        }
        
        agentCache.put(registration.getAgentId(), registration);
        
        Map<String, Object> config = agentToMap(registration);
        localStore.saveScene("agent:" + registration.getAgentId(), config);
    }
    
    @Override
    public AgentRegistration loadAgent(String agentId) {
        AgentRegistration cached = agentCache.get(agentId);
        if (cached != null) {
            return cached;
        }
        
        Map<String, Object> config = localStore.loadScene("agent:" + agentId);
        if (config != null) {
            AgentRegistration registration = mapToAgent(config);
            agentCache.put(agentId, registration);
            return registration;
        }
        
        return null;
    }
    
    @Override
    public void deleteAgent(String agentId) {
        agentCache.remove(agentId);
        localStore.deleteScene("agent:" + agentId);
    }
    
    @Override
    public List<AgentRegistration> listAgents(String sceneId) {
        List<AgentRegistration> result = new ArrayList<>();
        for (AgentRegistration reg : agentCache.values()) {
            if (sceneId == null || sceneId.equals(reg.getSceneId())) {
                result.add(reg);
            }
        }
        return result;
    }
    
    @Override
    public List<AgentRegistration> listAgentsByGroup(String sceneId, String groupId) {
        List<AgentRegistration> result = new ArrayList<>();
        for (AgentRegistration reg : agentCache.values()) {
            if ((sceneId == null || sceneId.equals(reg.getSceneId())) &&
                (groupId == null || groupId.equals(reg.getGroupId()))) {
                result.add(reg);
            }
        }
        return result;
    }
    
    @Override
    public List<AgentRegistration> listAgentsByRole(String sceneId, String role) {
        List<AgentRegistration> result = new ArrayList<>();
        for (AgentRegistration reg : agentCache.values()) {
            if ((sceneId == null || sceneId.equals(reg.getSceneId())) &&
                (role == null || role.equals(reg.getRole()))) {
                result.add(reg);
            }
        }
        return result;
    }
    
    @Override
    public boolean agentExists(String agentId) {
        return agentCache.containsKey(agentId) || localStore.sceneExists("agent:" + agentId);
    }
    
    @Override
    public void updateAgentHeartbeat(String agentId, long timestamp) {
        AgentRegistration registration = loadAgent(agentId);
        if (registration != null) {
            registration.setLastHeartbeat(timestamp);
            saveAgent(registration);
        }
    }
    
    @Override
    public Long getAgentLastHeartbeat(String agentId) {
        AgentRegistration registration = loadAgent(agentId);
        return registration != null ? registration.getLastHeartbeat() : null;
    }
    
    @Override
    public void updateAgentStatus(String agentId, String status) {
        AgentRegistration registration = loadAgent(agentId);
        if (registration != null) {
            registration.setStatus(status);
            saveAgent(registration);
        }
    }
    
    @Override
    public void updateAgentRole(String agentId, String role) {
        AgentRegistration registration = loadAgent(agentId);
        if (registration != null) {
            registration.setRole(role);
            saveAgent(registration);
        }
    }
    
    // ==================== Helper Methods ====================
    
    private Map<String, Object> skillToMap(SkillRegistration skill) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("skillId", skill.getSkillId());
        map.put("sceneId", skill.getSceneId());
        map.put("groupId", skill.getGroupId());
        map.put("skillType", skill.getSkillType());
        map.put("endpoints", skill.getEndpoints());
        map.put("registerTime", skill.getRegisterTime());
        map.put("lastHeartbeat", skill.getLastHeartbeat());
        map.put("status", skill.getStatus());
        map.put("metadata", skill.getMetadata());
        return map;
    }
    
    private SkillRegistration mapToSkill(Map<String, Object> map) {
        SkillRegistration skill = new SkillRegistration();
        skill.setSkillId((String) map.get("skillId"));
        skill.setSceneId((String) map.get("sceneId"));
        skill.setGroupId((String) map.get("groupId"));
        skill.setSkillType((String) map.get("skillType"));
        skill.setRegisterTime(getLong(map, "registerTime"));
        skill.setLastHeartbeat(getLong(map, "lastHeartbeat"));
        skill.setStatus((String) map.get("status"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> endpoints = (Map<String, Object>) map.get("endpoints");
        if (endpoints != null) {
            skill.setEndpoints(endpoints);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            skill.setMetadata(metadata);
        }
        
        return skill;
    }
    
    private Map<String, Object> linkToMap(LinkConfig link) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("linkId", link.getLinkId());
        map.put("sceneId", link.getSceneId());
        map.put("sourceId", link.getSourceId());
        map.put("targetId", link.getTargetId());
        map.put("linkType", link.getLinkType());
        map.put("direction", link.getDirection() != null ? link.getDirection().name() : null);
        map.put("config", link.getConfig());
        map.put("createTime", link.getCreateTime());
        map.put("updateTime", link.getUpdateTime());
        map.put("status", link.getStatus());
        return map;
    }
    
    private LinkConfig mapToLink(Map<String, Object> map) {
        LinkConfig link = new LinkConfig();
        link.setLinkId((String) map.get("linkId"));
        link.setSceneId((String) map.get("sceneId"));
        link.setSourceId((String) map.get("sourceId"));
        link.setTargetId((String) map.get("targetId"));
        link.setLinkType((String) map.get("linkType"));
        link.setCreateTime(getLong(map, "createTime"));
        link.setUpdateTime(getLong(map, "updateTime"));
        link.setStatus((String) map.get("status"));
        
        String direction = (String) map.get("direction");
        if (direction != null) {
            link.setDirection(LinkConfig.LinkDirection.valueOf(direction));
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) map.get("config");
        if (config != null) {
            link.setConfig(config);
        }
        
        return link;
    }
    
    private Map<String, Object> agentToMap(AgentRegistration agent) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("agentId", agent.getAgentId());
        map.put("agentName", agent.getAgentName());
        map.put("sceneId", agent.getSceneId());
        map.put("groupId", agent.getGroupId());
        map.put("endpoint", agent.getEndpoint());
        map.put("role", agent.getRole());
        map.put("status", agent.getStatus());
        map.put("composition", agent.getComposition());
        map.put("capabilities", agent.getCapabilities());
        map.put("registerTime", agent.getRegisterTime());
        map.put("lastHeartbeat", agent.getLastHeartbeat());
        map.put("metadata", agent.getMetadata());
        return map;
    }
    
    private AgentRegistration mapToAgent(Map<String, Object> map) {
        AgentRegistration agent = new AgentRegistration();
        agent.setAgentId((String) map.get("agentId"));
        agent.setAgentName((String) map.get("agentName"));
        agent.setSceneId((String) map.get("sceneId"));
        agent.setGroupId((String) map.get("groupId"));
        agent.setEndpoint((String) map.get("endpoint"));
        agent.setRole((String) map.get("role"));
        agent.setStatus((String) map.get("status"));
        agent.setRegisterTime(getLong(map, "registerTime"));
        agent.setLastHeartbeat(getLong(map, "lastHeartbeat"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> composition = (Map<String, Object>) map.get("composition");
        if (composition != null) {
            agent.setComposition(composition);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> capabilities = (Map<String, Object>) map.get("capabilities");
        if (capabilities != null) {
            agent.setCapabilities(capabilities);
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            agent.setMetadata(metadata);
        }
        
        return agent;
    }
    
    private long getLong(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }
    
    // ==================== Listener Methods ====================
    
    public void addSyncListener(SyncListener listener) {
        syncListeners.add(listener);
    }
    
    public void removeSyncListener(SyncListener listener) {
        syncListeners.remove(listener);
    }
    
    public StorageStatus getStatus() {
        return status;
    }
    
    public boolean isRemoteAvailable() {
        return remoteAvailable;
    }
    
    public void setRemoteAvailable(boolean available) {
        this.remoteAvailable = available;
        status.setRemoteAvailable(available);
    }
    
    private static class PendingSync {
        String syncId;
        String configType;
        String configId;
        Map<String, Object> config;
        long timestamp;
        int retryCount;
    }
}

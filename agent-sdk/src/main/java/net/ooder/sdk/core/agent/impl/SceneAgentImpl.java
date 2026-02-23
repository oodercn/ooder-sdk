package net.ooder.sdk.core.agent.impl;

import net.ooder.sdk.api.agent.SceneAgent;
import net.ooder.sdk.api.agent.Agent;
import net.ooder.sdk.api.skill.Capability;
import net.ooder.sdk.api.skill.SkillService;
import net.ooder.sdk.common.enums.AgentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class SceneAgentImpl implements SceneAgent {
    
    private static final Logger log = LoggerFactory.getLogger(SceneAgentImpl.class);
    
    private final String agentId;
    private final String agentName;
    private final String sceneId;
    private final AgentType agentType;
    private final String endpoint;
    
    private final AtomicReference<AgentState> state = new AtomicReference<>(AgentState.CREATED);
    private final AtomicReference<SceneAgentStatus> sceneStatus = new AtomicReference<>(SceneAgentStatus.INITIALIZING);
    private SceneAgentType sceneAgentType;
    
    private final List<Capability> capabilities = new CopyOnWriteArrayList<>();
    private final Map<String, SkillService> mountedSkills = new ConcurrentHashMap<>();
    private final Map<String, Object> sharedState = new ConcurrentHashMap<>();
    
    private String sceneGroupId;
    private String currentTaskId;
    
    public SceneAgentImpl(String sceneId, String agentName) {
        this.sceneId = sceneId;
        this.agentName = agentName;
        this.agentId = generateAgentId(sceneId, agentName);
        this.agentType = AgentType.SCENE;
        this.endpoint = "scene://" + sceneId + "/" + agentId;
        this.sceneAgentType = SceneAgentType.PRIMARY;
        
        log.info("SceneAgent created: {} for scene {}", agentId, sceneId);
    }
    
    public SceneAgentImpl(String sceneId, String agentName, SceneAgentType type) {
        this(sceneId, agentName);
        this.sceneAgentType = type;
    }
    
    private String generateAgentId(String sceneId, String agentName) {
        return "scene-" + sceneId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
    
    @Override
    public String getAgentName() {
        return agentName;
    }
    
    @Override
    public AgentType getAgentType() {
        return agentType;
    }
    
    @Override
    public String getEndpoint() {
        return endpoint;
    }
    
    @Override
    public AgentState getState() {
        return state.get();
    }
    
    @Override
    public void start() {
        log.info("Starting SceneAgent: {}", agentId);
        
        if (!transitionState(AgentState.CREATED, AgentState.INITIALIZING) &&
            !transitionState(AgentState.STOPPED, AgentState.INITIALIZING)) {
            log.warn("Cannot start agent from state: {}", state.get());
            return;
        }
        
        try {
            for (SkillService skill : mountedSkills.values()) {
                skill.start();
            }
            
            transitionState(AgentState.INITIALIZING, AgentState.STARTING);
            transitionState(AgentState.STARTING, AgentState.RUNNING);
            sceneStatus.set(SceneAgentStatus.ACTIVE);
            
            log.info("SceneAgent started: {}", agentId);
        } catch (Exception e) {
            log.error("Failed to start SceneAgent: {}", agentId, e);
            state.set(AgentState.ERROR);
            sceneStatus.set(SceneAgentStatus.ERROR);
            throw new RuntimeException("Failed to start agent", e);
        }
    }
    
    @Override
    public void stop() {
        log.info("Stopping SceneAgent: {}", agentId);
        
        if (!transitionState(AgentState.RUNNING, AgentState.STOPPING)) {
            log.warn("Cannot stop agent from state: {}", state.get());
            return;
        }
        
        try {
            for (SkillService skill : mountedSkills.values()) {
                skill.stop();
            }
            
            transitionState(AgentState.STOPPING, AgentState.STOPPED);
            sceneStatus.set(SceneAgentStatus.STOPPED);
            
            log.info("SceneAgent stopped: {}", agentId);
        } catch (Exception e) {
            log.error("Failed to stop SceneAgent: {}", agentId, e);
            state.set(AgentState.ERROR);
            throw new RuntimeException("Failed to stop agent", e);
        }
    }
    
    @Override
    public boolean isHealthy() {
        return state.get() == AgentState.RUNNING && 
               sceneStatus.get() != SceneAgentStatus.ERROR;
    }
    
    @Override
    public String getSceneId() {
        return sceneId;
    }
    
    @Override
    public SceneAgentType getSceneAgentType() {
        return sceneAgentType;
    }
    
    @Override
    public SceneAgentStatus getSceneStatus() {
        return sceneStatus.get();
    }
    
    @Override
    public List<Capability> getCapabilities() {
        return new ArrayList<>(capabilities);
    }
    
    @Override
    public List<SkillService> getMountedSkills() {
        return new ArrayList<>(mountedSkills.values());
    }
    
    @Override
    public void mountSkill(SkillService skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }
        
        String skillId = skill.getSkillId();
        if (mountedSkills.containsKey(skillId)) {
            log.warn("Skill already mounted: {}", skillId);
            return;
        }
        
        mountedSkills.put(skillId, skill);
        
        if (state.get() == AgentState.RUNNING) {
            skill.start();
        }
        
        log.info("Skill mounted: {} on agent {}", skillId, agentId);
    }
    
    @Override
    public void unmountSkill(String skillId) {
        SkillService skill = mountedSkills.remove(skillId);
        if (skill != null) {
            if (state.get() == AgentState.RUNNING) {
                skill.stop();
            }
            log.info("Skill unmounted: {} from agent {}", skillId, agentId);
        }
    }
    
    @Override
    public CompletableFuture<Object> invokeCapability(String capId, Map<String, Object> params) {
        if (state.get() != AgentState.RUNNING) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("Agent is not running: " + state.get()));
        }
        
        for (SkillService skill : mountedSkills.values()) {
            Map<String, Object> caps = skill.getCapabilities();
            if (caps != null && caps.containsKey(capId)) {
                log.debug("Invoking capability {} via skill {}", capId, skill.getSkillId());
                return skill.executeAsync(new net.ooder.sdk.api.skill.SkillRequest(capId, params));
            }
        }
        
        return CompletableFuture.failedFuture(
            new IllegalArgumentException("Capability not found: " + capId));
    }
    
    @Override
    public void joinSceneGroup(String groupId) {
        this.sceneGroupId = groupId;
        log.info("Agent {} joined scene group: {}", agentId, groupId);
    }
    
    @Override
    public void leaveSceneGroup() {
        log.info("Agent {} leaving scene group: {}", agentId, sceneGroupId);
        this.sceneGroupId = null;
    }
    
    @Override
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    @Override
    public boolean isPrimary() {
        return sceneAgentType == SceneAgentType.PRIMARY;
    }
    
    @Override
    public boolean isBackup() {
        return sceneAgentType == SceneAgentType.BACKUP;
    }
    
    @Override
    public void promoteToPrimary() {
        if (sceneAgentType == SceneAgentType.BACKUP) {
            sceneAgentType = SceneAgentType.PRIMARY;
            log.info("Agent {} promoted to PRIMARY", agentId);
        }
    }
    
    @Override
    public void demoteToBackup() {
        if (sceneAgentType == SceneAgentType.PRIMARY) {
            sceneAgentType = SceneAgentType.BACKUP;
            log.info("Agent {} demoted to BACKUP", agentId);
        }
    }
    
    @Override
    public void suspend() {
        if (sceneStatus.get() == SceneAgentStatus.ACTIVE) {
            sceneStatus.set(SceneAgentStatus.SUSPENDED);
            log.info("Agent {} suspended", agentId);
        }
    }
    
    @Override
    public void resume() {
        if (sceneStatus.get() == SceneAgentStatus.SUSPENDED) {
            sceneStatus.set(SceneAgentStatus.ACTIVE);
            log.info("Agent {} resumed", agentId);
        }
    }
    
    @Override
    public Map<String, Object> getSharedState() {
        return new HashMap<>(sharedState);
    }
    
    @Override
    public void updateSharedState(Map<String, Object> state) {
        if (state != null) {
            sharedState.putAll(state);
            log.debug("Agent {} shared state updated with {} entries", agentId, state.size());
        }
    }
    
    private boolean transitionState(AgentState from, AgentState to) {
        return state.compareAndSet(from, to);
    }
    
    public void addCapability(Capability capability) {
        if (capability != null) {
            capabilities.add(capability);
            log.debug("Capability added: {} to agent {}", capability.getCapId(), agentId);
        }
    }
    
    public void removeCapability(String capId) {
        capabilities.removeIf(cap -> cap.getCapId().equals(capId));
        log.debug("Capability removed: {} from agent {}", capId, agentId);
    }
    
    @Override
    public String toString() {
        return String.format("SceneAgent{id=%s, scene=%s, type=%s, state=%s, status=%s}",
            agentId, sceneId, sceneAgentType, state.get(), sceneStatus.get());
    }
}

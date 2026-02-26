package net.ooder.sdk.core.agent.impl;

import net.ooder.sdk.api.agent.WorkerAgent;
import net.ooder.sdk.api.skill.SkillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class WorkerAgentImpl implements WorkerAgent {
    
    private static final Logger log = LoggerFactory.getLogger(WorkerAgentImpl.class);
    
    private final String agentId;
    private final String workerName;
    private final String description;
    private final String sceneId;
    private final String skillId;
    private final List<String> capabilities;
    
    private final AtomicReference<AgentState> agentState = new AtomicReference<>(AgentState.CREATED);
    private final AtomicReference<WorkerAgentStatus> workerStatus = new AtomicReference<>(WorkerAgentStatus.IDLE);
    
    private String preferredDevice;
    private String currentTaskId;
    private SkillService skill;
    
    public WorkerAgentImpl(String sceneId, String workerName, String skillId, List<String> capabilities) {
        this.sceneId = sceneId;
        this.workerName = workerName;
        this.skillId = skillId;
        this.capabilities = new CopyOnWriteArrayList<>(capabilities != null ? capabilities : new ArrayList<>());
        this.agentId = generateWorkerId(sceneId, workerName);
        this.description = "Worker Agent for " + workerName;
        this.workerStatus.set(WorkerAgentStatus.IDLE);
        
        log.info("WorkerAgent created: {} for scene {}", agentId, sceneId);
    }
    
    private String generateWorkerId(String sceneId, String name) {
        return "worker-" + sceneId + "-" + name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
    
    @Override
    public String getAgentName() {
        return workerName;
    }
    
    @Override
    public net.ooder.sdk.common.enums.AgentType getAgentType() {
        return net.ooder.sdk.common.enums.AgentType.WORKER;
    }
    
    @Override
    public String getEndpoint() {
        return "worker://" + sceneId + "/" + workerName;
    }
    
    @Override
    public void start() {
        if (transitionState(AgentState.CREATED, AgentState.INITIALIZING) ||
            transitionState(AgentState.STOPPED, AgentState.INITIALIZING)) {
            
            try {
                if (skill != null) {
                    skill.start();
                }
                transitionState(AgentState.INITIALIZING, AgentState.RUNNING);
                workerStatus.set(WorkerAgentStatus.IDLE);
                log.info("WorkerAgent {} started", agentId);
            } catch (Exception e) {
                transitionState(AgentState.INITIALIZING, AgentState.ERROR);
                workerStatus.set(WorkerAgentStatus.ERROR);
                log.error("Failed to start WorkerAgent {}", agentId, e);
            }
        }
    }
    
    @Override
    public void stop() {
        if (transitionState(AgentState.RUNNING, AgentState.STOPPING)) {
            try {
                if (skill != null) {
                    skill.stop();
                }
                transitionState(AgentState.STOPPING, AgentState.STOPPED);
                workerStatus.set(WorkerAgentStatus.OFFLINE);
                log.info("WorkerAgent {} stopped", agentId);
            } catch (Exception e) {
                transitionState(AgentState.STOPPING, AgentState.ERROR);
                log.error("Failed to stop WorkerAgent {}", agentId, e);
            }
        }
    }
    
    @Override
    public boolean isHealthy() {
        return agentState.get() == AgentState.RUNNING && 
               workerStatus.get() != WorkerAgentStatus.ERROR;
    }
    
    @Override
    public AgentState getState() {
        return agentState.get();
    }
    
    @Override
    public String getWorkerName() {
        return workerName;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public String getSceneId() {
        return sceneId;
    }
    
    @Override
    public String getSkillId() {
        return skillId;
    }
    
    @Override
    public List<String> getCapabilities() {
        return new ArrayList<>(capabilities);
    }
    
    @Override
    public WorkerAgentStatus getWorkerStatus() {
        return workerStatus.get();
    }
    
    @Override
    public String getPreferredDevice() {
        return preferredDevice;
    }
    
    @Override
    public void setPreferredDevice(String deviceId) {
        this.preferredDevice = deviceId;
        log.debug("WorkerAgent {} preferred device set to: {}", agentId, deviceId);
    }
    
    @Override
    public CompletableFuture<Object> execute(String capId, Map<String, Object> params) {
        if (!isHealthy()) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("WorkerAgent is not healthy: " + agentState.get()));
            return future;
        }
        
        if (!capabilities.contains(capId)) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Capability not supported: " + capId));
            return future;
        }
        
        if (skill == null) {
            CompletableFuture<Object> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("No skill mounted"));
            return future;
        }
        
        workerStatus.set(WorkerAgentStatus.BUSY);
        currentTaskId = UUID.randomUUID().toString();
        
        log.debug("WorkerAgent {} executing capability {}", agentId, capId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Object result = skill.execute(new net.ooder.sdk.api.skill.SkillRequest());
                workerStatus.set(WorkerAgentStatus.IDLE);
                return result;
            } catch (Exception e) {
                workerStatus.set(WorkerAgentStatus.ERROR);
                log.error("Failed to execute capability {} on WorkerAgent {}", capId, agentId, e);
                throw e;
            }
        });
    }
    
    @Override
    public CompletableFuture<Object> executeAsync(String capId, Map<String, Object> params) {
        return execute(capId, params);
    }
    
    @Override
    public void setIdle() {
        workerStatus.set(WorkerAgentStatus.IDLE);
        currentTaskId = null;
        log.debug("WorkerAgent {} set to IDLE", agentId);
    }
    
    @Override
    public void setBusy() {
        workerStatus.set(WorkerAgentStatus.BUSY);
        log.debug("WorkerAgent {} set to BUSY", agentId);
    }
    
    @Override
    public void setError(String errorMessage) {
        workerStatus.set(WorkerAgentStatus.ERROR);
        log.error("WorkerAgent {} set to ERROR: {}", agentId, errorMessage);
    }
    
    @Override
    public boolean isIdle() {
        return workerStatus.get() == WorkerAgentStatus.IDLE;
    }
    
    @Override
    public boolean isBusy() {
        return workerStatus.get() == WorkerAgentStatus.BUSY;
    }
    
    @Override
    public boolean hasError() {
        return workerStatus.get() == WorkerAgentStatus.ERROR;
    }
    
    @Override
    public String getCurrentTaskId() {
        return currentTaskId;
    }
    
    @Override
    public void setCurrentTaskId(String taskId) {
        this.currentTaskId = taskId;
    }
    
    @Override
    public SkillService getSkill() {
        return skill;
    }
    
    @Override
    public void setSkill(SkillService skill) {
        this.skill = skill;
        log.debug("Skill {} mounted to WorkerAgent {}", 
            skill != null ? skill.getSkillId() : "null", agentId);
    }
    
    private boolean transitionState(AgentState from, AgentState to) {
        return agentState.compareAndSet(from, to);
    }
    
    @Override
    public String toString() {
        return String.format("WorkerAgent{id=%s, name=%s, scene=%s, state=%s, status=%s}",
            agentId, workerName, sceneId, agentState.get(), workerStatus.get());
    }
}

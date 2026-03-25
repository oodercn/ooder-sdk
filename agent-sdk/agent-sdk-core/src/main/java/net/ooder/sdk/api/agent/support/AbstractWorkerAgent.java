package net.ooder.sdk.api.agent.support;

import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.api.agent.WorkerAgent;
import net.ooder.sdk.common.enums.AgentType;
import net.ooder.skills.api.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * WorkerAgent 抽象基类
 * 提供工作状态管理、能力列表等通用实现
 *
 * @version 3.0.0
 * @since 3.0.0
 */
@PublicAPI
public abstract class AbstractWorkerAgent extends AbstractAgent implements WorkerAgent {

    protected final String sceneId;
    protected final String skillId;
    protected final List<String> capabilities = new CopyOnWriteArrayList<>();

    protected volatile WorkerAgentStatus workerStatus = WorkerAgentStatus.IDLE;
    protected volatile String currentTaskId;
    protected volatile SkillService skill;
    protected volatile String errorMessage;

    public AbstractWorkerAgent(String sceneId, String workerName, String skillId, List<String> capabilities) {
        super(generateWorkerId(sceneId, workerName), workerName, AgentType.WORKER);
        this.sceneId = sceneId;
        this.skillId = skillId;
        if (capabilities != null) {
            this.capabilities.addAll(capabilities);
        }
    }

    private static String generateWorkerId(String sceneId, String name) {
        return "worker-" + sceneId + "-" + name + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public String getWorkerName() {
        return getAgentName();
    }

    @Override
    public String getDescription() {
        return "Worker Agent: " + getAgentName();
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
        return workerStatus;
    }

    @Override
    public String getPreferredDevice() {
        return null;
    }

    @Override
    public void setPreferredDevice(String deviceId) {
    }

    @Override
    public boolean isIdle() {
        return workerStatus == WorkerAgentStatus.IDLE;
    }

    @Override
    public boolean isBusy() {
        return workerStatus == WorkerAgentStatus.BUSY;
    }

    @Override
    public boolean hasError() {
        return workerStatus == WorkerAgentStatus.ERROR;
    }

    @Override
    public void setIdle() {
        workerStatus = WorkerAgentStatus.IDLE;
        currentTaskId = null;
    }

    @Override
    public void setBusy() {
        workerStatus = WorkerAgentStatus.BUSY;
    }

    @Override
    public void setError(String errorMessage) {
        workerStatus = WorkerAgentStatus.ERROR;
        this.errorMessage = errorMessage;
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
    }

    public void addCapability(String capability) {
        if (capability != null && !capabilities.contains(capability)) {
            capabilities.add(capability);
        }
    }

    public void removeCapability(String capability) {
        capabilities.remove(capability);
    }

    public boolean hasCapability(String capability) {
        return capabilities.contains(capability);
    }

    @Override
    public abstract CompletableFuture<Object> execute(String capId, Map<String, Object> params);

    @Override
    public CompletableFuture<Object> executeAsync(String capId, Map<String, Object> params) {
        return execute(capId, params);
    }
}

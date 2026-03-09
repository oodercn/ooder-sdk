package net.ooder.scene.core.init;

import net.ooder.scene.core.*;
import net.ooder.sdk.api.capability.Capability;
import net.ooder.sdk.api.scene.SceneGroupManager;

import java.util.*;

/**
 * 初始化上下文
 *
 * <p>保存场景组初始化过程中的所有状态和数据。</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class InitContext {

    private final String initId;
    private final long createTime;
    private final SceneGroupInitializer.InitRequest request;

    private SceneGroupInitializer.InitStatus status = SceneGroupInitializer.InitStatus.CREATED;
    private String errorMessage;

    private SceneGroupManager.SceneGroupConfig groupConfig;
    private final List<SceneAgentCore> agents = new ArrayList<>();
    private final List<SceneMemberInfo> members = new ArrayList<>();
    private final List<Capability> requiredCapabilities = new ArrayList<>();
    private final List<Capability> optionalCapabilities = new ArrayList<>();
    private final Map<String, List<SceneGroupInitializer.SkillMatch>> skillMatches = new HashMap<>();
    private final List<SkillBinding> skillBindings = new ArrayList<>();

    public InitContext(SceneGroupInitializer.InitRequest request) {
        this.initId = "init-" + UUID.randomUUID().toString().substring(0, 8);
        this.createTime = System.currentTimeMillis();
        this.request = request;
    }

    public String getInitId() {
        return initId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public SceneGroupInitializer.InitRequest getRequest() {
        return request;
    }

    public SceneGroupInitializer.InitStatus getStatus() {
        return status;
    }

    public void setStatus(SceneGroupInitializer.InitStatus status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SceneGroupManager.SceneGroupConfig getGroupConfig() {
        return groupConfig;
    }

    public void setGroupConfig(SceneGroupManager.SceneGroupConfig groupConfig) {
        this.groupConfig = groupConfig;
    }

    public List<SceneAgentCore> getAgents() {
        return agents;
    }

    public void addAgent(SceneAgentCore agent) {
        this.agents.add(agent);
    }

    public List<SceneMemberInfo> getMembers() {
        return members;
    }

    public void addMember(SceneMemberInfo member) {
        this.members.add(member);
    }

    public List<Capability> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    public void addRequiredCapability(Capability capability) {
        this.requiredCapabilities.add(capability);
    }

    public List<Capability> getOptionalCapabilities() {
        return optionalCapabilities;
    }

    public void addOptionalCapability(Capability capability) {
        this.optionalCapabilities.add(capability);
    }

    public Map<String, List<SceneGroupInitializer.SkillMatch>> getSkillMatches() {
        return skillMatches;
    }

    public void addSkillMatches(String capId, List<SceneGroupInitializer.SkillMatch> matches) {
        this.skillMatches.put(capId, matches);
    }

    public List<SkillBinding> getSkillBindings() {
        return skillBindings;
    }

    public void addSkillBinding(SkillBinding binding) {
        this.skillBindings.add(binding);
    }

    public SceneAgentCore getPrimaryAgent() {
        return agents.stream()
            .filter(SceneAgentCore::isPrimary)
            .findFirst()
            .orElse(null);
    }

    public List<SceneAgentCore> getBackupAgents() {
        return agents.stream()
            .filter(SceneAgentCore::isBackup)
            .collect(java.util.stream.Collectors.toList());
    }
}

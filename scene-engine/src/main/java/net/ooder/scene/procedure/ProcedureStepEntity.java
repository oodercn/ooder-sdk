package net.ooder.scene.procedure;

import net.ooder.sdk.api.procedure.ProcedureStep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 规范步骤定义实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class ProcedureStepEntity implements ProcedureStep {

    private static final long serialVersionUID = 1L;

    private String stepId;
    private String name;
    private String description;
    private String stepType;
    private int order;
    private List<String> roleIds = new ArrayList<>();
    private boolean required = true;
    private boolean skippable;
    private String executorType;
    private Map<String, Object> config = new HashMap<>();
    private List<String> dependencies = new ArrayList<>();
    private Map<String, Object> extensions = new HashMap<>();

    public ProcedureStepEntity() {
    }

    public ProcedureStepEntity(String stepId, String name) {
        this.stepId = stepId;
        this.name = name;
    }

    @Override
    public String getStepId() {
        return stepId;
    }

    @Override
    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getStepType() {
        return stepType;
    }

    @Override
    public void setStepType(String stepType) {
        this.stepType = stepType;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public List<String> getRoleIds() {
        return roleIds;
    }

    @Override
    public void setRoleIds(List<String> roleIds) {
        this.roleIds = roleIds != null ? roleIds : new ArrayList<>();
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean isSkippable() {
        return skippable;
    }

    @Override
    public void setSkippable(boolean skippable) {
        this.skippable = skippable;
    }

    @Override
    public String getExecutorType() {
        return executorType;
    }

    @Override
    public void setExecutorType(String executorType) {
        this.executorType = executorType;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }

    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies != null ? dependencies : new ArrayList<>();
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }

    @Override
    public String toString() {
        return "ProcedureStepEntity{" +
                "stepId='" + stepId + '\'' +
                ", name='" + name + '\'' +
                ", stepType='" + stepType + '\'' +
                ", order=" + order +
                '}';
    }
}

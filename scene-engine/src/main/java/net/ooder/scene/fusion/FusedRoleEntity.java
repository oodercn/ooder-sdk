package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.FusedRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FusedRoleEntity implements FusedRole {

    private static final long serialVersionUID = 1L;

    private String roleId;
    private String name;
    private String description;
    private int priority;
    private boolean required;
    private int minCount = 1;
    private int maxCount = 1;
    private List<String> requiredCapabilities = new ArrayList<>();
    private String source;
    private Map<String, Object> extensions = new HashMap<>();

    public FusedRoleEntity() {
    }

    @Override
    public String getRoleId() {
        return roleId;
    }

    @Override
    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
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
    public int getMinCount() {
        return minCount;
    }

    @Override
    public void setMinCount(int minCount) {
        this.minCount = minCount;
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    @Override
    public List<String> getRequiredCapabilities() {
        return requiredCapabilities;
    }

    @Override
    public void setRequiredCapabilities(List<String> requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities != null ? requiredCapabilities : new ArrayList<>();
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        this.extensions = extensions != null ? extensions : new HashMap<>();
    }
}

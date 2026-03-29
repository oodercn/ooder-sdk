package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.CapabilityBindingDef;

import java.util.HashMap;
import java.util.Map;

public class CapabilityBindingDefEntity implements CapabilityBindingDef {

    private static final long serialVersionUID = 1L;

    private String capabilityId;
    private String capabilityName;
    private String bindingType;
    private String roleId;
    private Map<String, Object> config = new HashMap<>();

    public CapabilityBindingDefEntity() {
    }

    @Override
    public String getCapabilityId() {
        return capabilityId;
    }

    @Override
    public void setCapabilityId(String capabilityId) {
        this.capabilityId = capabilityId;
    }

    @Override
    public String getCapabilityName() {
        return capabilityName;
    }

    @Override
    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }

    @Override
    public String getBindingType() {
        return bindingType;
    }

    @Override
    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
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
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
}

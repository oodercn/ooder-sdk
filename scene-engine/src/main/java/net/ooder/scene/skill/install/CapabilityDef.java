package net.ooder.scene.skill.install;

import java.util.HashMap;
import java.util.Map;

/**
 * 能力定义
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class CapabilityDef {

    private String capId;
    private String name;
    private String description;
    private String type;
    private Map<String, Object> parameters;
    private Map<String, Object> config;

    public CapabilityDef() {
        this.parameters = new HashMap<>();
        this.config = new HashMap<>();
    }

    public CapabilityDef(String capId, String name, String description) {
        this();
        this.capId = capId;
        this.name = name;
        this.description = description;
    }

    public String getCapId() {
        return capId;
    }

    public void setCapId(String capId) {
        this.capId = capId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config != null ? config : new HashMap<>();
    }
}

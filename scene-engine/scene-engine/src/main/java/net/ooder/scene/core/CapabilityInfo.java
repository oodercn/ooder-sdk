package net.ooder.scene.core;

public class CapabilityInfo {
    private String capId;
    private String name;
    private String version;
    private String category;
    private String description;
    private String skillId;
    private String method;

    public CapabilityInfo(String capId, String name, String version, String category, String description, String skillId, String method) {
        this.capId = capId;
        this.name = name;
        this.version = version;
        this.category = category;
        this.description = description;
        this.skillId = skillId;
        this.method = method;
    }

    public String getCapId() {
        return capId;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getSkillId() {
        return skillId;
    }

    public String getMethod() {
        return method;
    }
}

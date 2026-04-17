package net.ooder.skill.install.dto;

public class InstallSkillDTO {
    private String id;
    private String name;
    private String desc;
    private String version;
    private boolean required;
    private boolean currentSystem;

    public InstallSkillDTO() {}

    public InstallSkillDTO(String id, String name, String desc, String version, 
                          boolean required, boolean currentSystem) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.version = version;
        this.required = required;
        this.currentSystem = currentSystem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isCurrentSystem() {
        return currentSystem;
    }

    public void setCurrentSystem(boolean currentSystem) {
        this.currentSystem = currentSystem;
    }
}

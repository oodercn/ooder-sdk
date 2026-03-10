package net.ooder.scene.llm.config.skillsmd;

/**
 * 场景定义
 *
 * @author ooder
 * @since 2.4
 */
public class ScenarioDefinition {

    private String name;
    private String description;
    private String triggerCapability;

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

    public String getTriggerCapability() {
        return triggerCapability;
    }

    public void setTriggerCapability(String triggerCapability) {
        this.triggerCapability = triggerCapability;
    }
}

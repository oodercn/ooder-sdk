package net.ooder.skill.install.dto;

public class InstallStepDTO {
    private String stepId;
    private String name;
    private String description;
    private boolean required;
    private boolean recommended;
    private int order;

    public InstallStepDTO() {}

    public InstallStepDTO(String stepId, String name, String description, 
                         boolean required, boolean recommended, int order) {
        this.stepId = stepId;
        this.name = name;
        this.description = description;
        this.required = required;
        this.recommended = recommended;
        this.order = order;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

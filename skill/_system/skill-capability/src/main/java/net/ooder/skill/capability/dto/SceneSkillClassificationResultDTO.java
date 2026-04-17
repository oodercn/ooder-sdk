package net.ooder.skill.capability.dto;

public class SceneSkillClassificationResultDTO {
    
    private String capabilityId;
    private String skillForm;
    private String sceneType;
    private Integer businessSemanticsScore;

    public static SceneSkillClassificationResultDTO from(String capabilityId, String skillForm, String sceneType, Integer score) {
        SceneSkillClassificationResultDTO result = new SceneSkillClassificationResultDTO();
        result.setCapabilityId(capabilityId);
        result.setSkillForm(skillForm);
        result.setSceneType(sceneType);
        result.setBusinessSemanticsScore(score);
        return result;
    }

    public String getCapabilityId() { return capabilityId; }
    public void setCapabilityId(String capabilityId) { this.capabilityId = capabilityId; }
    public String getSkillForm() { return skillForm; }
    public void setSkillForm(String skillForm) { this.skillForm = skillForm; }
    public String getSceneType() { return sceneType; }
    public void setSceneType(String sceneType) { this.sceneType = sceneType; }
    public Integer getBusinessSemanticsScore() { return businessSemanticsScore; }
    public void setBusinessSemanticsScore(Integer businessSemanticsScore) { this.businessSemanticsScore = businessSemanticsScore; }
}

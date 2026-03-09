package net.ooder.scene.skill.classification;

/**
 * 场景技能分类异常
 * 
 * <p>在场景技能分类检测过程中发生异常时抛出</p>
 *
 * @author ooder Team
 * @since 2.3
 */
public class SceneSkillClassificationException extends RuntimeException {
    
    private final String skillId;
    private final String standardName;
    
    public SceneSkillClassificationException(String message) {
        super(message);
        this.skillId = null;
        this.standardName = null;
    }
    
    public SceneSkillClassificationException(String message, Throwable cause) {
        super(message, cause);
        this.skillId = null;
        this.standardName = null;
    }
    
    public SceneSkillClassificationException(String skillId, String message) {
        super("技能 [" + skillId + "] 分类检测失败: " + message);
        this.skillId = skillId;
        this.standardName = null;
    }
    
    public SceneSkillClassificationException(String skillId, String standardName, String message) {
        super("技能 [" + skillId + "] 标准 [" + standardName + "] 检查失败: " + message);
        this.skillId = skillId;
        this.standardName = standardName;
    }
    
    public SceneSkillClassificationException(String skillId, String standardName, String message, Throwable cause) {
        super("技能 [" + skillId + "] 标准 [" + standardName + "] 检查失败: " + message, cause);
        this.skillId = skillId;
        this.standardName = standardName;
    }
    
    public SceneSkillClassificationException(String skillId, String message, Throwable cause) {
        super("技能 [" + skillId + "] 分类检测失败: " + message, cause);
        this.skillId = skillId;
        this.standardName = null;
    }
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getStandardName() {
        return standardName;
    }
}

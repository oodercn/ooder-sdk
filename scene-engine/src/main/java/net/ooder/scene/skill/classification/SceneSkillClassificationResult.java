package net.ooder.scene.skill.classification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场景技能分类结果
 * 
 * <p>包含分类检测的完整结果，包括：</p>
 * <ul>
 *   <li>检测到的分类</li>
 *   <li>四项标准的检查结果</li>
 *   <li>业务语义评分详情</li>
 *   <li>检测详情和原因</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 */
public class SceneSkillClassificationResult {
    
    private final String skillId;
    private final String skillName;
    private SceneSkillCategory category;
    private boolean sceneSkill;
    private int businessSemanticsScore;
    private final Map<String, StandardCheckResult> standardChecks;
    private final Map<String, Integer> scoreDetails;
    private final List<String> messages;
    private final long detectTime;
    
    public SceneSkillClassificationResult(String skillId, String skillName) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.standardChecks = new HashMap<>();
        this.scoreDetails = new HashMap<>();
        this.messages = new ArrayList<>();
        this.detectTime = System.currentTimeMillis();
        this.category = SceneSkillCategory.NOT_SCENE_SKILL;
        this.sceneSkill = false;
        this.businessSemanticsScore = 0;
    }
    
    /**
     * 从 RichSkill 场景能力字段创建分类结果
     * 
     * <p>用于 InstallCoordinator 与 RichSkill 深度集成</p>
     * 
     * @param category 分类
     * @param businessSemanticsScore 业务语义评分
     * @param hasMainFirst 是否有自驱能力
     * @param hasDriverConditions 是否有驱动条件
     * @param hasParticipants 是否有参与者
     * @param isPublic 是否公开
     * @param hasCollaboration 是否有协作
     * @param hasBusinessTags 是否有业务标签
     */
    public SceneSkillClassificationResult(
            SceneSkillCategory category,
            int businessSemanticsScore,
            boolean hasMainFirst,
            boolean hasDriverConditions,
            boolean hasParticipants,
            boolean isPublic,
            boolean hasCollaboration,
            boolean hasBusinessTags) {
        this.skillId = "unknown";
        this.skillName = "unknown";
        this.standardChecks = new HashMap<>();
        this.scoreDetails = new HashMap<>();
        this.messages = new ArrayList<>();
        this.detectTime = System.currentTimeMillis();
        this.category = category;
        this.sceneSkill = category.isSceneSkill();
        this.businessSemanticsScore = businessSemanticsScore;
        
        // 添加标准检查结果
        addStandardCheck("mainFirst", hasMainFirst, 
            hasMainFirst ? "具备自驱能力" : "不具备自驱能力");
        addStandardCheck("driverConditions", hasDriverConditions, 
            hasDriverConditions ? "有驱动条件" : "无驱动条件");
        addStandardCheck("participants", hasParticipants, 
            hasParticipants ? "有参与者定义" : "无参与者定义");
        addStandardCheck("visibility", isPublic, 
            isPublic ? "公开可见" : "非公开");
        addStandardCheck("collaboration", hasCollaboration, 
            hasCollaboration ? "有协作属性" : "无协作属性");
        addStandardCheck("businessTags", hasBusinessTags, 
            hasBusinessTags ? "有业务标签" : "无业务标签");
    }
    
    /**
     * 添加标准检查结果
     */
    public void addStandardCheck(String standardName, boolean passed, String message) {
        standardChecks.put(standardName, new StandardCheckResult(passed, message));
        if (!passed) {
            messages.add("[" + standardName + "] " + message);
        }
    }
    
    /**
     * 添加评分详情
     */
    public void addScoreDetail(String itemName, int score) {
        scoreDetails.put(itemName, score);
        businessSemanticsScore += score;
    }
    
    /**
     * 设置检测分类
     */
    public void setCategory(SceneSkillCategory category) {
        this.category = category;
        this.sceneSkill = category.isSceneSkill();
    }
    
    /**
     * 设置业务语义评分
     */
    public void setBusinessSemanticsScore(int score) {
        this.businessSemanticsScore = score;
    }
    
    /**
     * 添加消息
     */
    public void addMessage(String message) {
        messages.add(message);
    }
    
    // Getters
    
    public String getSkillId() {
        return skillId;
    }
    
    public String getSkillName() {
        return skillName;
    }
    
    public SceneSkillCategory getCategory() {
        return category;
    }
    
    public boolean isSceneSkill() {
        return sceneSkill;
    }
    
    public int getBusinessSemanticsScore() {
        return businessSemanticsScore;
    }
    
    public Map<String, StandardCheckResult> getStandardChecks() {
        return standardChecks;
    }
    
    public Map<String, Integer> getScoreDetails() {
        return scoreDetails;
    }
    
    public List<String> getMessages() {
        return messages;
    }
    
    public long getDetectTime() {
        return detectTime;
    }
    
    /**
     * 检查是否满足指定标准
     */
    public boolean isStandardPassed(String standardName) {
        StandardCheckResult result = standardChecks.get(standardName);
        return result != null && result.isPassed();
    }
    
    /**
     * 获取检测摘要
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("技能 ").append(skillName).append("(").append(skillId).append(") 分类检测结果:\n");
        sb.append("  分类: ").append(category.getName()).append(" (").append(category.getEnglishName()).append(")\n");
        sb.append("  场景技能: ").append(sceneSkill ? "是" : "否").append("\n");
        sb.append("  业务语义评分: ").append(businessSemanticsScore).append("/10\n");
        sb.append("  标准检查:\n");
        for (Map.Entry<String, StandardCheckResult> entry : standardChecks.entrySet()) {
            sb.append("    - ").append(entry.getKey()).append(": ")
              .append(entry.getValue().isPassed() ? "通过" : "未通过")
              .append(" (").append(entry.getValue().getMessage()).append(")\n");
        }
        sb.append("  评分详情:\n");
        for (Map.Entry<String, Integer> entry : scoreDetails.entrySet()) {
            sb.append("    - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("分\n");
        }
        return sb.toString();
    }
    
    /**
     * 标准检查结果
     */
    public static class StandardCheckResult {
        private final boolean passed;
        private final String message;
        
        public StandardCheckResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }
        
        public boolean isPassed() {
            return passed;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

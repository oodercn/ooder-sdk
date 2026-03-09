package net.ooder.scene.skill.classification;

import net.ooder.skills.api.SkillPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 场景技能分类器实现
 * 
 * <p>根据四项标准和业务语义评分对技能包进行分类。</p>
 * 
 * <p>v2.3.1 修订：</p>
 * <ul>
 *   <li>统一使用 sceneSkill 字段标识场景技能（兼容 type 字段）</li>
 *   <li>统一自驱能力判定逻辑：mainFirst + mainFirstConfig + driverConditions</li>
 *   <li>统一使用 businessTags 字段（兼容 tags 字段）</li>
 *   <li>修复 getMetadata() 返回 null 的问题</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 */
public class SceneSkillClassifierImpl implements SceneSkillClassifier {
    
    private static final Logger log = LoggerFactory.getLogger(SceneSkillClassifierImpl.class);
    
    public static final String STANDARD_1_SCENE_SKILL = "Standard-1-SceneSkill";
    public static final String STANDARD_2_CAPABILITIES = "Standard-2-Capabilities";
    public static final String STANDARD_3_SELF_DRIVE = "Standard-3-SelfDrive";
    public static final String STANDARD_4_SEMANTICS = "Standard-4-Semantics";
    
    public static final String SCORE_DRIVER_CONDITIONS = "driverConditions";
    public static final String SCORE_PARTICIPANTS = "participants";
    public static final String SCORE_VISIBILITY = "visibility";
    public static final String SCORE_COLLABORATION = "collaboration";
    public static final String SCORE_BUSINESS_TAGS = "businessTags";
    
    private static final int SCORE_THRESHOLD_HIGH = 8;
    private static final int SCORE_THRESHOLD_LOW = 3;
    
    @Override
    public SceneSkillClassificationResult detectCategory(SkillPackage skillPackage) {
        log.debug("开始检测技能 [{}] 的场景技能分类", skillPackage.getSkillId());
        
        SceneSkillClassificationResult result = new SceneSkillClassificationResult(
            skillPackage.getSkillId(), 
            skillPackage.getName()
        );
        
        try {
            Map<String, Object> metadata = getMetadata(skillPackage);
            
            boolean standard1Passed = checkStandard1(skillPackage);
            result.addStandardCheck(STANDARD_1_SCENE_SKILL, standard1Passed, 
                standard1Passed ? "是场景技能" : "不是场景技能");
            
            boolean standard2Passed = checkStandard2(skillPackage);
            result.addStandardCheck(STANDARD_2_CAPABILITIES, standard2Passed,
                standard2Passed ? "sceneCapabilities 非空" : "sceneCapabilities 为空");
            
            if (!standard1Passed || !standard2Passed) {
                result.setCategory(SceneSkillCategory.NOT_SCENE_SKILL);
                log.debug("技能 [{}] 不满足场景技能基本标准，分类为: NOT_SCENE_SKILL", skillPackage.getSkillId());
                return result;
            }
            
            boolean hasSelfDrive = checkSelfDriveCapability(metadata);
            result.addStandardCheck(STANDARD_3_SELF_DRIVE, hasSelfDrive,
                hasSelfDrive ? "有自驱能力" : "无自驱能力");
            
            boolean standard4Passed = checkStandard4(skillPackage);
            result.addStandardCheck(STANDARD_4_SEMANTICS, standard4Passed,
                standard4Passed ? "有业务语义" : "无业务语义");
            
            int businessScore = calculateBusinessSemanticsScore(skillPackage);
            result.setCategory(determineCategory(hasSelfDrive, businessScore));
            
            log.info("技能 [{}] 场景技能分类检测结果: {}, 业务语义评分: {}", 
                skillPackage.getSkillId(), result.getCategory().getName(), businessScore);
            
        } catch (Exception e) {
            log.error("技能 [{}] 分类检测失败", skillPackage.getSkillId(), e);
            result.setCategory(SceneSkillCategory.NOT_SCENE_SKILL);
            result.addMessage("检测过程发生异常: " + e.getMessage());
        }
        
        return result;
    }
    
    @Override
    public int calculateBusinessSemanticsScore(SkillPackage skillPackage) {
        int score = 0;
        Map<String, Object> metadata = getMetadata(skillPackage);
        
        if (hasDriverConditions(metadata)) {
            score += 3;
            log.debug("driverConditions 非空: +3分");
        }
        
        if (hasParticipants(metadata)) {
            score += 3;
            log.debug("participants 非空: +3分");
        }
        
        if (isPublicVisibility(metadata)) {
            score += 2;
            log.debug("visibility = public: +2分");
        }
        
        if (hasCollaborationCapability(skillPackage)) {
            score += 1;
            log.debug("有协作能力: +1分");
        }
        
        if (hasBusinessTags(metadata)) {
            score += 1;
            log.debug("有业务标签: +1分");
        }
        
        return score;
    }
    
    @Override
    public boolean checkStandard1(SkillPackage skillPackage) {
        Map<String, Object> metadata = getMetadata(skillPackage);
        if (metadata == null) {
            return false;
        }
        
        if (Boolean.TRUE.equals(metadata.get("sceneSkill"))) {
            return true;
        }
        
        Object type = metadata.get("type");
        return type != null && "scene-skill".equals(type.toString());
    }
    
    @Override
    public boolean checkStandard2(SkillPackage skillPackage) {
        List<?> sceneCapabilities = getSceneCapabilities(skillPackage);
        return sceneCapabilities != null && !sceneCapabilities.isEmpty();
    }
    
    @Override
    public boolean checkStandard3(SkillPackage skillPackage) {
        Map<String, Object> metadata = getMetadata(skillPackage);
        return checkSelfDriveCapability(metadata);
    }
    
    @Override
    public boolean checkStandard4(SkillPackage skillPackage) {
        Map<String, Object> metadata = getMetadata(skillPackage);
        return hasDriverConditions(metadata) || hasParticipants(metadata);
    }
    
    @Override
    public boolean isSceneSkill(SkillPackage skillPackage) {
        return checkStandard1(skillPackage) && checkStandard2(skillPackage);
    }
    
    /**
     * 统一的自驱能力检查
     * 
     * <p>必须同时满足：</p>
     * <ol>
     *   <li>mainFirst = true</li>
     *   <li>mainFirstConfig 存在且非空</li>
     *   <li>driverConditions 非空</li>
     * </ol>
     *
     * @param metadata 技能元数据
     * @return 是否有自驱能力
     */
    private boolean checkSelfDriveCapability(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        
        if (!Boolean.TRUE.equals(metadata.get("mainFirst"))) {
            return false;
        }
        
        Object config = metadata.get("mainFirstConfig");
        if (config == null) {
            return false;
        }
        if (config instanceof Map && ((Map<?, ?>) config).isEmpty()) {
            return false;
        }
        
        return hasDriverConditions(metadata);
    }
    
    /**
     * 简化的分类判定
     */
    private SceneSkillCategory determineCategory(boolean hasSelfDrive, int businessScore) {
        if (hasSelfDrive) {
            return businessScore >= SCORE_THRESHOLD_HIGH ? SceneSkillCategory.ABS : SceneSkillCategory.ASS;
        } else {
            return businessScore >= SCORE_THRESHOLD_HIGH ? SceneSkillCategory.TBS : SceneSkillCategory.NOT_SCENE_SKILL;
        }
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 获取技能元数据
     * 
     * <p>v2.3.1 修复：正确获取 SkillPackage 的 metadata</p>
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMetadata(SkillPackage skillPackage) {
        if (skillPackage == null) {
            return Collections.emptyMap();
        }
        
        try {
            Map<String, Object> metadata = skillPackage.getMetadata();
            return metadata != null ? metadata : Collections.emptyMap();
        } catch (Exception e) {
            log.warn("获取技能 [{}] 的 metadata 失败: {}", skillPackage.getSkillId(), e.getMessage());
            return Collections.emptyMap();
        }
    }
    
    /**
     * 获取场景能力列表
     */
    @SuppressWarnings("unchecked")
    private List<?> getSceneCapabilities(SkillPackage skillPackage) {
        Map<String, Object> metadata = getMetadata(skillPackage);
        if (metadata == null) {
            return Collections.emptyList();
        }
        
        Object capabilities = metadata.get("sceneCapabilities");
        if (capabilities instanceof List) {
            return (List<?>) capabilities;
        }
        return Collections.emptyList();
    }
    
    /**
     * 检查是否有驱动条件
     */
    private boolean hasDriverConditions(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        Object driverConditions = metadata.get("driverConditions");
        if (driverConditions instanceof List) {
            return !((List<?>) driverConditions).isEmpty();
        }
        return driverConditions != null;
    }
    
    /**
     * 检查是否有参与者
     */
    private boolean hasParticipants(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        Object participants = metadata.get("participants");
        if (participants == null) {
            return false;
        }
        if (participants instanceof List) {
            return !((List<?>) participants).isEmpty();
        }
        if (participants instanceof Map) {
            return !((Map<?, ?>) participants).isEmpty();
        }
        return true;
    }
    
    /**
     * 检查是否公开可见
     */
    private boolean isPublicVisibility(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        Object visibility = metadata.get("visibility");
        return visibility != null && "public".equals(visibility.toString());
    }
    
    /**
     * 检查是否有协作能力
     */
    private boolean hasCollaborationCapability(SkillPackage skillPackage) {
        List<?> sceneCapabilities = getSceneCapabilities(skillPackage);
        if (sceneCapabilities == null || sceneCapabilities.isEmpty()) {
            return false;
        }
        
        for (Object cap : sceneCapabilities) {
            if (cap instanceof Map) {
                Map<?, ?> capMap = (Map<?, ?>) cap;
                Object type = capMap.get("type");
                if (type != null && "collaboration".equals(type.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 检查是否有业务标签
     * 
     * <p>v2.3.1 修订：优先使用 businessTags，兼容 tags</p>
     */
    private boolean hasBusinessTags(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        
        Object tags = metadata.get("businessTags");
        if (tags == null) {
            tags = metadata.get("tags");
        }
        
        if (tags instanceof List) {
            List<?> tagList = (List<?>) tags;
            return !tagList.isEmpty();
        }
        return false;
    }
}

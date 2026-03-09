package net.ooder.scene.skill.classification;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 元数据兼容层
 * 
 * <p>v2.3.1 新增：提供向后兼容的字段访问方法</p>
 * 
 * <p>解决的问题：</p>
 * <ul>
 *   <li>sceneSkill vs type 字段兼容</li>
 *   <li>businessTags vs tags 字段兼容</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public final class MetadataCompat {
    
    private MetadataCompat() {
    }
    
    /**
     * 兼容获取场景技能标识
     * 
     * <p>优先使用 sceneSkill 字段，p>
     * <p>兼容 type 字段（值为 "scene-skill"）</p>
     *
     * @param metadata 元数据
     * @return 是否为场景技能
     */
    public static boolean isSceneSkill(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        
        if (Boolean.TRUE.equals(metadata.get("sceneSkill"))) {
            return true;
        }
        
        Object type = metadata.get("type");
        return type != null && "scene-skill".equals(type.toString());
    }
    
    /**
     * 兼容获取业务标签
     * 
     * <p>优先使用 businessTags 字段</p>
     * <p>兼容 tags 字段</p>
     *
     * @param metadata 元数据
     * @return 业务标签列表，     */
    @SuppressWarnings("unchecked")
    public static List<String> getBusinessTags(Map<String, Object> metadata) {
        if (metadata == null) {
            return Collections.emptyList();
        }
        
        Object tags = metadata.get("businessTags");
        if (tags instanceof List) {
            return (List<String>) tags;
        }
        
        tags = metadata.get("tags");
        if (tags instanceof List) {
            return (List<String>) tags;
        }
        
        return Collections.emptyList();
    }
    
    /**
     * 兼容检查是否有业务标签
     * 
     * @param metadata 元数据
     * @return 是否有业务标签
     */
    public static boolean hasBusinessTags(Map<String, Object> metadata) {
        List<String> tags = getBusinessTags(metadata);
        return tags != null && !tags.isEmpty();
    }
    
    /**
     * 检查是否有自驱能力
     * 
     * <p>必须同时满足：</p>
     * <ol>
     *   <li>mainFirst = true</li>
     *   <li>mainFirstConfig 存在且非空</li>
     *   <li>driverConditions 非空</li>
     * </ol>
     *
     * @param metadata 元数据
     * @return 是否有自驱能力
     */
    public static boolean hasSelfDriveCapability(Map<String, Object> metadata) {
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
        
        Object conditions = metadata.get("driverConditions");
        if (conditions instanceof List) {
            return !((List<?>) conditions).isEmpty();
        }
        
        return conditions != null;
    }
    
    /**
     * 获取驱动条件
     * 
     * @param metadata 元数据
     * @return 驱动条件列表
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getDriverConditions(Map<String, Object> metadata) {
        if (metadata == null) {
            return Collections.emptyList();
        }
        
        Object conditions = metadata.get("driverConditions");
        if (conditions instanceof List) {
            return (List<Map<String, Object>>) conditions;
        }
        
        return Collections.emptyList();
    }
    
    /**
     * 获取参与者
     * 
     * @param metadata 元数据
     * @return 参与者定义
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getParticipants(Map<String, Object> metadata) {
        if (metadata == null) {
            return Collections.emptyMap();
        }
        
        Object participants = metadata.get("participants");
        if (participants instanceof Map) {
            return (Map<String, Object>) participants;
        }
        
        return Collections.emptyMap();
    }
    
    /**
     * 检查是否公开可见
     * 
     * @param metadata 元数据
     * @return 是否公开
     */
    public static boolean isPublicVisibility(Map<String, Object> metadata) {
        if (metadata == null) {
            return false;
        }
        
        Object visibility = metadata.get("visibility");
        return visibility != null && "public".equals(visibility.toString());
    }
    
    /**
     * 获取可见性
     * 
     * @param metadata 元数据
     * @param defaultValue 默认值
     * @return 可见性
     */
    public static String getVisibility(Map<String, Object> metadata, String defaultValue) {
        if (metadata == null) {
            return defaultValue;
        }
        
        Object visibility = metadata.get("visibility");
        return visibility != null ? visibility.toString() : defaultValue;
    }
    
    /**
     * 计算业务语义评分
     * 
     * <p>评分标准（满分10分）：</p>
     * <ul>
     *   <li>driverConditions 非空：3分</li>
     *   <li>participants 非空：3分</li>
     *   <li>visibility = public：2分</li>
     *   <li>有协作能力：1分</li>
     *   <li>有业务标签：1分</li>
     * </ul>
     *
     * @param metadata 元数据
     * @return 业务语义评分（0-10）
     */
    public static int calculateBusinessSemanticsScore(Map<String, Object> metadata) {
        int score = 0;
        
        if (metadata == null) {
            return score;
        }
        
        if (!getDriverConditions(metadata).isEmpty()) {
            score += 3;
        }
        
        if (!getParticipants(metadata).isEmpty()) {
            score += 3;
        }
        
        if (isPublicVisibility(metadata)) {
            score += 2;
        }
        
        Object collaboration = metadata.get("collaboration");
        if (collaboration != null) {
            score += 1;
        }
        
        if (hasBusinessTags(metadata)) {
            score += 1;
        }
        
        return score;
    }
}

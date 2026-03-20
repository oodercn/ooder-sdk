package net.ooder.scene.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 系统配置服务
 * 
 * <p>提供系统 Skill 配置的管理能力。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface SystemConfigService {
    
    /**
     * 获取所有系统 Skill 配置
     */
    CompletableFuture<List<SystemSkillConfig>> listSystemSkills();
    
    /**
     * 获取指定分类的系统 Skill 配置
     */
    CompletableFuture<List<SystemSkillConfig>> listSystemSkillsByCategory(String category);
    
    /**
     * 获取指定 Skill 配置
     */
    CompletableFuture<SystemSkillConfig> getSkillConfig(String skillId);
    
    /**
     * 更新 Skill 配置
     */
    CompletableFuture<Void> updateSkillConfig(String skillId, Map<String, Object> config, boolean restart);
    
    /**
     * 启动 Skill
     */
    CompletableFuture<Void> startSkill(String skillId);
    
    /**
     * 停止 Skill
     */
    CompletableFuture<Void> stopSkill(String skillId);
    
    /**
     * 获取配置历史
     */
    CompletableFuture<List<ConfigHistory>> getConfigHistory(String skillId, int limit);
    
    /**
     * 获取 Skill 运行时状态
     */
    CompletableFuture<SkillRuntimeStatus> getSkillRuntimeStatus(String skillId);
    
    /**
     * 获取所有分类
     */
    CompletableFuture<List<SkillCategory>> getCategories();
    
    /**
     * 重置 Skill 配置为默认值
     */
    CompletableFuture<Void> resetSkillConfig(String skillId);
}

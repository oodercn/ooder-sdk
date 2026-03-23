package net.ooder.scene.skill.prompt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 精简提示语降级服务
 *
 * <p>当 RAG 检索失败或返回低质量结果时，使用精简的静态提示词作为降级方案</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class FallbackPromptService {

    private static final Logger log = LoggerFactory.getLogger(FallbackPromptService.class);

    private static final Map<String, String> DEFAULT_PROMPTS = new HashMap<>();

    static {
        DEFAULT_PROMPTS.put("default", 
            "你是Ooder场景技能平台的智能助手。" +
            "请根据用户的问题提供准确、有帮助的回答。");
        
        DEFAULT_PROMPTS.put("discovery", 
            "你是能力发现助手。" +
            "帮助用户发现和安装所需的能力技能。");
        
        DEFAULT_PROMPTS.put("knowledge", 
            "你是知识管理助手。" +
            "帮助用户管理和检索知识库中的信息。");
        
        DEFAULT_PROMPTS.put("scene", 
            "你是场景管理助手。" +
            "帮助用户创建和管理自动化场景。");
    }

    private String customDefaultPrompt;

    public String getFallbackPrompt(String skillId) {
        if (customDefaultPrompt != null && !customDefaultPrompt.isEmpty()) {
            return customDefaultPrompt;
        }

        if (skillId == null || skillId.isEmpty()) {
            return DEFAULT_PROMPTS.get("default");
        }

        String lowerSkillId = skillId.toLowerCase();
        
        if (lowerSkillId.contains("discovery") || lowerSkillId.contains("discover")) {
            return DEFAULT_PROMPTS.get("discovery");
        }
        if (lowerSkillId.contains("knowledge") || lowerSkillId.contains("kb")) {
            return DEFAULT_PROMPTS.get("knowledge");
        }
        if (lowerSkillId.contains("scene") || lowerSkillId.contains("skill")) {
            return DEFAULT_PROMPTS.get("scene");
        }

        return DEFAULT_PROMPTS.get("default");
    }

    public String getFallbackPrompt(String skillId, String roleId) {
        String basePrompt = getFallbackPrompt(skillId);
        
        if (roleId == null || roleId.isEmpty()) {
            return basePrompt;
        }

        if (roleId.contains("hr") || roleId.contains("recruit")) {
            return basePrompt + " 你专注于招聘相关事务。";
        }
        if (roleId.contains("admin")) {
            return basePrompt + " 你具有系统管理权限。";
        }
        if (roleId.contains("assistant")) {
            return basePrompt + " 你是一个通用助手。";
        }

        return basePrompt;
    }

    public void setCustomDefaultPrompt(String prompt) {
        this.customDefaultPrompt = prompt;
        log.info("Custom default prompt set");
    }

    public void registerFallbackPrompt(String skillId, String prompt) {
        if (skillId != null && prompt != null) {
            DEFAULT_PROMPTS.put(skillId.toLowerCase(), prompt);
            log.info("Registered fallback prompt for skill: {}", skillId);
        }
    }

    public void removeFallbackPrompt(String skillId) {
        if (skillId != null) {
            DEFAULT_PROMPTS.remove(skillId.toLowerCase());
            log.info("Removed fallback prompt for skill: {}", skillId);
        }
    }
}

package net.ooder.skill.hotplug;

import net.ooder.skill.hotplug.model.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Skill 分类解析器
 * 用于从 Skill ID 推断业务分类
 *
 * 分类规则：
 * - llm: 大模型相关
 * - knowledge: 知识库相关
 * - vfs: 文件存储相关
 * - org: 组织权限相关
 * - msg: 消息通知相关
 * - biz: 业务场景相关
 * - ui: 界面组件相关
 * - sys: 系统基础相关
 */
@Component
public class CategoryResolver {

    private static final Logger logger = LoggerFactory.getLogger(CategoryResolver.class);

    // 分类规则映射：分类 -> 关键字列表
    private static final Map<String, List<String>> CATEGORY_RULES = new LinkedHashMap<>();

    static {
        // LLM 大模型
        CATEGORY_RULES.put("llm", Arrays.asList(
            "-llm", "-baidu", "-openai", "-qianwen", "-deepseek",
            "-kimi", "-chatglm", "-ollama", "-claude", "-gpt",
            "-ai", "-model", "-embedding"
        ));

        // Knowledge 知识库
        CATEGORY_RULES.put("knowledge", Arrays.asList(
            "-knowledge", "-kb", "-rag", "-search", "-index",
            "-vector", "-document", "-doc", "-wiki"
        ));

        // VFS 文件存储
        CATEGORY_RULES.put("vfs", Arrays.asList(
            "-vfs", "-storage", "-minio", "-s3", "-oss",
            "-file", "-ftp", "-sftp", "-nas", "-fs"
        ));

        // Org 组织权限
        CATEGORY_RULES.put("org", Arrays.asList(
            "-org", "-auth", "-user", "-ldap", "-dingding",
            "-wechat", "-sso", "-oauth", "-permission", "-role",
            "-dept", "-department", "-team"
        ));

        // Msg 消息通知
        CATEGORY_RULES.put("msg", Arrays.asList(
            "-msg", "-notification", "-notify", "-im", "-mqtt",
            "-sms", "-email", "-push", "-webhook", "-alert",
            "-queue", "-kafka", "-rabbitmq"
        ));

        // Biz 业务场景
        CATEGORY_RULES.put("biz", Arrays.asList(
            "-scene", "-recruitment", "-approval", "-real-estate",
            "-recording", "-onboarding", "-meeting", "-project",
            "-business", "-workflow", "-process", "-crm",
            "-erp", "-hr", "-finance", "-marketing"
        ));

        // UI 界面组件
        CATEGORY_RULES.put("ui", Arrays.asList(
            "-ui", "-nexus", "-a2ui", "-dashboard", "-portal",
            "-chart", "-form", "-table", "-editor", "-viewer"
        ));

        // Sys 系统基础
        CATEGORY_RULES.put("sys", Arrays.asList(
            "-scheduler", "-task", "-cmd", "-res", "-monitor",
            "-log", "-cache", "-config", "-dict", "-agent",
            "-system", "-core", "-base", "-common"
        ));
    }

    /**
     * 解析 Skill 分类
     *
     * @param metadata Skill 元数据
     * @return 分类代码
     */
    public String resolve(SkillMetadata metadata) {
        if (metadata == null) {
            return "sys";
        }

        // 1. 优先使用明确声明的 category
        String declaredCategory = metadata.getCategory();
        if (declaredCategory != null && !declaredCategory.trim().isEmpty()) {
            logger.debug("Category resolved from declaration: {} -> {}",
                metadata.getId(), declaredCategory);
            return declaredCategory.toLowerCase();
        }

        // 2. 从 Skill ID 推断
        String skillId = metadata.getId();
        return resolveFromSkillId(skillId);
    }

    /**
     * 从 Skill ID 推断分类
     *
     * @param skillId Skill ID
     * @return 分类代码
     */
    public String resolveFromSkillId(String skillId) {
        if (skillId == null || skillId.trim().isEmpty()) {
            return "sys";
        }

        String id = skillId.toLowerCase();

        // 遍历规则，按优先级匹配
        for (Map.Entry<String, List<String>> entry : CATEGORY_RULES.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (id.contains(keyword)) {
                    logger.debug("Category resolved from skillId: {} -> {}", skillId, category);
                    return category;
                }
            }
        }

        // 默认返回 sys
        logger.debug("Category defaulted to sys: {}", skillId);
        return "sys";
    }

    /**
     * 添加自定义分类规则
     *
     * @param category 分类代码
     * @param keywords 关键字列表
     */
    public void addCategoryRule(String category, String... keywords) {
        CATEGORY_RULES.computeIfAbsent(category, k -> new ArrayList<>())
                      .addAll(Arrays.asList(keywords));
        logger.info("Added category rule: {} -> {}", category, Arrays.toString(keywords));
    }

    /**
     * 获取所有分类规则
     *
     * @return 分类规则映射
     */
    public Map<String, List<String>> getCategoryRules() {
        return Collections.unmodifiableMap(CATEGORY_RULES);
    }

    /**
     * 验证分类是否有效
     *
     * @param category 分类代码
     * @return 是否有效
     */
    public boolean isValidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return false;
        }
        return CATEGORY_RULES.containsKey(category.toLowerCase());
    }
}

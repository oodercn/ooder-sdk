package net.ooder.skill.hotplug;

import net.ooder.skill.hotplug.model.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Skill 形态解析器
 * 用于识别 Skill 的类型：SCENE（场景应用）、DRIVER（驱动适配）、PROVIDER（能力提供）
 *
 * 识别优先级：
 * 1. skill.yaml 中明确声明的 form 字段
 * 2. 包分类（pkgCategory）
 * 3. Skill ID 命名约定
 * 4. 可配置的分类规则
 */
@Component
public class SkillFormResolver {

    private static final Logger logger = LoggerFactory.getLogger(SkillFormResolver.class);

    // 场景应用关键字（可配置化）
    private static final Set<String> SCENE_KEYWORDS = new HashSet<>(Arrays.asList(
        "scene", "recruitment", "approval", "real-estate", "recording",
        "onboarding", "meeting", "project", "business", "workflow",
        "process", "application", "app", "portal", "dashboard",
        "biz", "form", "management", "system"  // 添加业务场景相关关键字
    ));

    // 驱动适配关键字
    private static final Set<String> DRIVER_KEYWORDS = new HashSet<>(Arrays.asList(
        "driver", "adapter", "connector", "gateway", "proxy",
        "bridge", "integration", "sync", "fetcher", "publisher"
    ));

    // 能力提供关键字
    private static final Set<String> PROVIDER_KEYWORDS = new HashSet<>(Arrays.asList(
        "service", "api", "provider", "tool", "utility",
        "helper", "common", "base", "core", "engine"
    ));

    /**
     * 解析 Skill 形态
     *
     * @param metadata Skill 元数据
     * @return SkillForm 枚举值
     */
    public SkillForm resolve(SkillMetadata metadata) {
        if (metadata == null) {
            logger.warn("SkillMetadata is null, defaulting to PROVIDER");
            return SkillForm.PROVIDER;
        }

        String skillId = metadata.getId();
        logger.debug("Resolving SkillForm for skill: {}", skillId);

        // 1. 优先使用 skill.yaml 中明确声明的 form 字段
        String declaredForm = metadata.getForm();
        logger.debug("Skill {} has declared form: {}", skillId, declaredForm);

        if (declaredForm != null && !declaredForm.trim().isEmpty()) {
            SkillForm form = parseForm(declaredForm);
            if (form != null) {
                logger.info("Skill {} form resolved from declaration: {}", skillId, form);
                return form;
            } else {
                logger.warn("Skill {} has invalid declared form: {}, will use fallback", skillId, declaredForm);
            }
        } else {
            logger.debug("Skill {} has no declared form (metadata.form is null/empty), checking spec.skillForm via metadata.getForm()", skillId);
        }

        // 2. 从元数据中提取信息
        String pkgCategory = metadata.getCategory();
        String description = metadata.getDescription();
        logger.debug("Skill {} - category: {}, description: {}", skillId, pkgCategory, description);

        SkillForm fallbackResult = resolve(skillId, pkgCategory, description);
        logger.info("Skill {} form resolved via fallback: {} (note: spec.skillForm may not be loaded correctly)", skillId, fallbackResult);

        return fallbackResult;
    }

    /**
     * 解析 Skill 形态（基于基本信息）
     *
     * @param skillId Skill ID
     * @param pkgCategory 包分类
     * @param description 描述
     * @return SkillForm 枚举值
     */
    public SkillForm resolve(String skillId, String pkgCategory, String description) {
        // 3. 基于包分类判断
        if (pkgCategory != null && !pkgCategory.trim().isEmpty()) {
            SkillForm form = resolveByCategory(pkgCategory);
            if (form != null) {
                logger.debug("Skill form resolved by category: {} -> {}", skillId, form);
                return form;
            }
        }

        // 4. 基于 Skill ID 命名约定判断
        if (skillId != null && !skillId.trim().isEmpty()) {
            SkillForm form = resolveByNamingConvention(skillId);
            if (form != null) {
                logger.debug("Skill form resolved by naming convention: {} -> {}", skillId, form);
                return form;
            }
        }

        // 5. 基于描述判断
        if (description != null && !description.trim().isEmpty()) {
            SkillForm form = resolveByDescription(description);
            if (form != null) {
                logger.debug("Skill form resolved by description: {} -> {}", skillId, form);
                return form;
            }
        }

        // 默认返回 PROVIDER
        logger.debug("Skill form defaulted to PROVIDER: {}", skillId);
        return SkillForm.PROVIDER;
    }

    /**
     * 解析声明的 form 值
     */
    private SkillForm parseForm(String declaredForm) {
        try {
            return SkillForm.valueOf(declaredForm.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Unknown skill form declaration: {}", declaredForm);
            return null;
        }
    }

    /**
     * 基于包分类判断
     */
    private SkillForm resolveByCategory(String category) {
        String cat = category.toLowerCase();

        if (cat.equals("scene") || cat.equals("scenes") ||
            cat.equals("application") || cat.equals("app")) {
            return SkillForm.SCENE;
        }

        if (cat.equals("driver") || cat.equals("drivers") ||
            cat.equals("adapter") || cat.equals("adapters")) {
            return SkillForm.DRIVER;
        }

        if (cat.equals("provider") || cat.equals("providers") ||
            cat.equals("service") || cat.equals("services")) {
            return SkillForm.PROVIDER;
        }

        return null;
    }

    /**
     * 基于命名约定判断
     */
    private SkillForm resolveByNamingConvention(String skillId) {
        String id = skillId.toLowerCase();

        // 检查场景应用关键字
        for (String keyword : SCENE_KEYWORDS) {
            if (id.contains(keyword)) {
                return SkillForm.SCENE;
            }
        }

        // 检查驱动适配关键字
        for (String keyword : DRIVER_KEYWORDS) {
            if (id.contains(keyword)) {
                return SkillForm.DRIVER;
            }
        }

        // 检查能力提供关键字
        for (String keyword : PROVIDER_KEYWORDS) {
            if (id.contains(keyword)) {
                return SkillForm.PROVIDER;
            }
        }

        return null;
    }

    /**
     * 基于描述判断
     */
    private SkillForm resolveByDescription(String description) {
        String desc = description.toLowerCase();

        // 场景应用特征
        if (containsAny(desc, "场景", "应用", "业务", "流程", "工作流",
                       "scene", "application", "business", "workflow")) {
            return SkillForm.SCENE;
        }

        // 驱动适配特征
        if (containsAny(desc, "驱动", "适配", "连接", "同步",
                       "driver", "adapter", "connector", "sync")) {
            return SkillForm.DRIVER;
        }

        // 能力提供特征
        if (containsAny(desc, "服务", "接口", "工具", "基础",
                       "service", "api", "tool", "base")) {
            return SkillForm.PROVIDER;
        }

        return null;
    }

    /**
     * 检查字符串是否包含任意关键字
     */
    private boolean containsAny(String str, String... keywords) {
        for (String keyword : keywords) {
            if (str.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 添加自定义关键字（支持动态扩展）
     *
     * @param form Skill 形态
     * @param keywords 关键字列表
     */
    public void addKeywords(SkillForm form, String... keywords) {
        switch (form) {
            case SCENE:
                SCENE_KEYWORDS.addAll(Arrays.asList(keywords));
                break;
            case DRIVER:
                DRIVER_KEYWORDS.addAll(Arrays.asList(keywords));
                break;
            case PROVIDER:
                PROVIDER_KEYWORDS.addAll(Arrays.asList(keywords));
                break;
        }
    }
}

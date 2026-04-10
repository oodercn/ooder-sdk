package net.ooder.skill.hotplug.converter;

import net.ooder.skill.hotplug.model.SkillPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认转换器集合
 * 提供常用的 SkillPackage 转换器
 */
public class DefaultConverters {

    /**
     * 转换为标准 CapabilityDTO（Map 形式）
     * OS 层可以直接使用或参考此实现
     */
    public static SkillPackageConverter<Map<String, Object>> toCapabilityDTO() {
        return pkg -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", pkg.getSkillId());
            dto.put("skillId", pkg.getSkillId());
            dto.put("name", pkg.getName());
            dto.put("version", pkg.getVersion());
            dto.put("description", pkg.getDescription());
            dto.put("author", pkg.getAuthor());
            dto.put("category", pkg.getCategory());        // SDK 已推断
            dto.put("skillForm", pkg.getSkillForm());      // SDK 已推断
            dto.put("sceneCapability", pkg.isSceneCapability());
            return dto;
        };
    }

    /**
     * 转换为简化版 CapabilityDTO
     * 只包含核心字段
     */
    public static SkillPackageConverter<Map<String, Object>> toSimpleCapabilityDTO() {
        return pkg -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("skillId", pkg.getSkillId());
            dto.put("name", pkg.getName());
            dto.put("category", pkg.getCategory());
            dto.put("skillForm", pkg.getSkillForm());
            return dto;
        };
    }

    /**
     * 转换为安装技能对象（Map 形式）
     */
    public static SkillPackageConverter<Map<String, Object>> toInstalledSkill() {
        return pkg -> {
            Map<String, Object> skill = new HashMap<>();
            skill.put("skillId", pkg.getSkillId());
            skill.put("name", pkg.getName());
            skill.put("version", pkg.getVersion());
            skill.put("description", pkg.getDescription());
            skill.put("category", pkg.getCategory());
            skill.put("skillForm", pkg.getSkillForm());
            skill.put("status", "INSTALLED");
            return skill;
        };
    }

    /**
     * 转换为发现结果对象（Map 形式）
     */
    public static SkillPackageConverter<Map<String, Object>> toDiscoveryResult() {
        return pkg -> {
            Map<String, Object> result = new HashMap<>();
            result.put("skillId", pkg.getSkillId());
            result.put("name", pkg.getName());
            result.put("version", pkg.getVersion());
            result.put("description", pkg.getDescription());
            result.put("author", pkg.getAuthor());
            result.put("category", pkg.getCategory());
            result.put("skillForm", pkg.getSkillForm());
            result.put("isSceneCapability", pkg.isSceneCapability());
            result.put("fileName", pkg.getFile() != null ? pkg.getFile().getName() : null);
            return result;
        };
    }

    /**
     * 创建自定义转换器
     *
     * @param mapper 自定义映射逻辑
     * @return 转换器
     */
    public static <T> SkillPackageConverter<T> custom(java.util.function.Function<SkillPackage, T> mapper) {
        return mapper::apply;
    }
}

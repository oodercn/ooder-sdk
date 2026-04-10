package net.ooder.skill.hotplug.converter;

import net.ooder.skill.hotplug.model.SkillPackage;

/**
 * SkillPackage 转换器接口
 * 用于将 SkillPackage 转换为各种 DTO 或实体对象
 *
 * @param <T> 转换目标类型
 */
@FunctionalInterface
public interface SkillPackageConverter<T> {

    /**
     * 将 SkillPackage 转换为目标类型
     *
     * @param pkg SkillPackage
     * @return 转换后的对象
     */
    T convert(SkillPackage pkg);
}

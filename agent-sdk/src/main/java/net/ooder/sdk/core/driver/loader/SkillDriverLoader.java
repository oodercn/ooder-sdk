package net.ooder.sdk.core.driver.loader;

import net.ooder.sdk.core.driver.Driver;

import java.util.List;

/**
 * Skill Driver 加载器接口
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public interface SkillDriverLoader {

    /**
     * 加载 Skill Driver
     *
     * @param skillId Skill ID
     * @return Driver 实例
     */
    Driver loadSkillDriver(String skillId);

    /**
     * 注册 Skill Driver
     *
     * @param skillId Skill ID
     * @param driver  Driver 实例
     */
    void registerSkillDriver(String skillId, Driver driver);

    /**
     * 获取所有 Skill Drivers
     *
     * @return Driver 列表
     */
    List<Driver> getAllSkillDrivers();

    /**
     * 检查 Skill Driver 是否存在
     *
     * @param skillId Skill ID
     * @return 是否存在
     */
    boolean hasSkillDriver(String skillId);
}

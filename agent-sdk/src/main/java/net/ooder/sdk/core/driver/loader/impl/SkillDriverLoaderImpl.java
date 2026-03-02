package net.ooder.sdk.core.driver.loader.impl;

import net.ooder.sdk.core.driver.Driver;
import net.ooder.sdk.core.driver.loader.SkillDriverLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill Driver 加载器实现
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public class SkillDriverLoaderImpl implements SkillDriverLoader {

    private final Map<String, Driver> skillDrivers = new ConcurrentHashMap<>();

    @Override
    public Driver loadSkillDriver(String skillId) {
        return skillDrivers.get(skillId);
    }

    @Override
    public void registerSkillDriver(String skillId, Driver driver) {
        skillDrivers.put(skillId, driver);
    }

    @Override
    public List<Driver> getAllSkillDrivers() {
        return new ArrayList<>(skillDrivers.values());
    }

    @Override
    public boolean hasSkillDriver(String skillId) {
        return skillDrivers.containsKey(skillId);
    }
}

package net.ooder.skills.container.core;

import net.ooder.skills.container.api.CapabilityRegistry;
import net.ooder.skills.container.api.SkillHandle;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill 句柄实现
 */
public class SkillHandleImpl implements SkillHandle {

    private final String skillId;
    private final String version;
    private String name;
    private String description;
    private String jarPath;
    private SkillState state;
    private final List<CapabilityRegistry.Capability> capabilities;
    private ClassLoader classLoader;
    private Class<?> skillClass;
    private Object instance;

    public SkillHandleImpl(String skillId, String version) {
        this.skillId = skillId;
        this.version = version;
        this.state = SkillState.CREATED;
        this.capabilities = new ArrayList<>();
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    @Override
    public SkillState getState() {
        return state;
    }

    public void setState(SkillState state) {
        this.state = state;
    }

    @Override
    public List<CapabilityRegistry.Capability> getCapabilities() {
        return new ArrayList<>(capabilities);
    }

    public void addCapability(CapabilityRegistry.Capability capability) {
        this.capabilities.add(capability);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> getSkillClass() {
        return skillClass;
    }

    public void setSkillClass(Class<?> skillClass) {
        this.skillClass = skillClass;
    }

    @Override
    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}

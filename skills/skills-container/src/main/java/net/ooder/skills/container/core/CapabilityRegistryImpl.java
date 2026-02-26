package net.ooder.skills.container.core;

import net.ooder.skills.container.api.CapabilityRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 能力注册中心实现
 */
public class CapabilityRegistryImpl implements CapabilityRegistry {

    private static final Logger logger = LoggerFactory.getLogger(CapabilityRegistryImpl.class);

    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();
    private final Map<String, String> capabilityToSkill = new ConcurrentHashMap<>();

    @Override
    public void registerCapability(String skillId, Capability capability) {
        String capId = capability.getId();
        logger.info("Registering capability: {} from skill: {}", capId, skillId);

        capabilities.put(capId, capability);
        capabilityToSkill.put(capId, skillId);
    }

    @Override
    public void unregisterCapability(String skillId, String capabilityId) {
        logger.info("Unregistering capability: {} from skill: {}", capabilityId, skillId);

        String registeredSkill = capabilityToSkill.get(capabilityId);
        if (registeredSkill != null && registeredSkill.equals(skillId)) {
            capabilities.remove(capabilityId);
            capabilityToSkill.remove(capabilityId);
        }
    }

    @Override
    public List<Capability> discoverCapabilities(String category) {
        return capabilities.values().stream()
            .filter(cap -> category == null || category.equals(cap.getCategory()))
            .collect(Collectors.toList());
    }

    @Override
    public String getProviderSkill(String capabilityId) {
        return capabilityToSkill.get(capabilityId);
    }

    @Override
    public Object invokeCapability(String capabilityId, Map<String, Object> params) {
        Capability cap = capabilities.get(capabilityId);
        if (cap == null) {
            throw new IllegalArgumentException("Capability not found: " + capabilityId);
        }

        String skillId = capabilityToSkill.get(capabilityId);
        logger.debug("Invoking capability: {} from skill: {}", capabilityId, skillId);

        // TODO: 通过反射调用 Skill 的方法
        throw new UnsupportedOperationException("Capability invocation not implemented yet");
    }

    @Override
    public List<Capability> getAllCapabilities() {
        return new ArrayList<>(capabilities.values());
    }
}

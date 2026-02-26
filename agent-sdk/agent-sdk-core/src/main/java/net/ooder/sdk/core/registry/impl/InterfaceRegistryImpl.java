package net.ooder.sdk.core.registry.impl;

import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.InterfaceDependency;
import net.ooder.sdk.core.registry.InterfaceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class InterfaceRegistryImpl implements InterfaceRegistry {

    private static final Logger log = LoggerFactory.getLogger(InterfaceRegistryImpl.class);

    private final Map<String, InterfaceDefinition> interfaces = new ConcurrentHashMap<>();
    private final Map<String, List<String>> implementations = new ConcurrentHashMap<>();
    private final Map<String, String> preferredImplementations = new ConcurrentHashMap<>();

    @Override
    public void register(InterfaceDefinition definition) {
        if (definition == null || definition.getInterfaceId() == null) {
            throw new IllegalArgumentException("Interface definition or ID cannot be null");
        }

        String interfaceId = definition.getInterfaceId();
        interfaces.put(interfaceId, definition);

        if (!implementations.containsKey(interfaceId)) {
            implementations.put(interfaceId, new CopyOnWriteArrayList<>());
        }

        log.info("Interface registered: {} v{}", interfaceId, definition.getVersion());
    }

    @Override
    public void unregister(String interfaceId) {
        if (interfaceId == null) {
            return;
        }

        InterfaceDefinition removed = interfaces.remove(interfaceId);
        implementations.remove(interfaceId);
        preferredImplementations.remove(interfaceId);

        if (removed != null) {
            log.info("Interface unregistered: {}", interfaceId);
        }
    }

    @Override
    public Optional<InterfaceDefinition> getInterface(String interfaceId) {
        if (interfaceId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(interfaces.get(interfaceId));
    }

    @Override
    public List<InterfaceDefinition> getAllInterfaces() {
        return new ArrayList<>(interfaces.values());
    }

    @Override
    public List<InterfaceDefinition> getInterfacesByVersion(String version) {
        return interfaces.values().stream()
            .filter(def -> version != null && version.equals(def.getVersion()))
            .collect(Collectors.toList());
    }

    @Override
    public boolean hasInterface(String interfaceId) {
        return interfaceId != null && interfaces.containsKey(interfaceId);
    }

    @Override
    public boolean hasInterface(String interfaceId, String version) {
        InterfaceDefinition def = interfaces.get(interfaceId);
        return def != null && version != null && version.equals(def.getVersion());
    }

    @Override
    public void registerImplementation(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            throw new IllegalArgumentException("Interface ID and Skill ID cannot be null");
        }

        if (!hasInterface(interfaceId)) {
            log.warn("Attempting to register implementation for unknown interface: {}", interfaceId);
        }

        List<String> impls = implementations.computeIfAbsent(interfaceId, k -> new CopyOnWriteArrayList<>());
        if (!impls.contains(skillId)) {
            impls.add(skillId);
            log.info("Implementation registered: {} -> {}", interfaceId, skillId);
        }
    }

    @Override
    public void unregisterImplementation(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            return;
        }

        List<String> impls = implementations.get(interfaceId);
        if (impls != null && impls.remove(skillId)) {
            log.info("Implementation unregistered: {} -> {}", interfaceId, skillId);

            if (skillId.equals(preferredImplementations.get(interfaceId))) {
                preferredImplementations.remove(interfaceId);
            }
        }
    }

    @Override
    public List<String> getImplementations(String interfaceId) {
        if (interfaceId == null) {
            return Collections.emptyList();
        }
        List<String> impls = implementations.get(interfaceId);
        return impls != null ? new ArrayList<>(impls) : Collections.emptyList();
    }

    @Override
    public String getPreferredImplementation(String interfaceId) {
        if (interfaceId == null) {
            return null;
        }

        String preferred = preferredImplementations.get(interfaceId);
        if (preferred != null) {
            return preferred;
        }

        List<String> impls = implementations.get(interfaceId);
        if (impls != null && !impls.isEmpty()) {
            return impls.get(0);
        }

        return null;
    }

    @Override
    public void setPreferredImplementation(String interfaceId, String skillId) {
        if (interfaceId == null || skillId == null) {
            throw new IllegalArgumentException("Interface ID and Skill ID cannot be null");
        }

        preferredImplementations.put(interfaceId, skillId);
        log.info("Preferred implementation set: {} -> {}", interfaceId, skillId);
    }

    @Override
    public List<InterfaceDefinition> resolveDependencies(List<InterfaceDependency> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return Collections.emptyList();
        }

        List<InterfaceDefinition> resolved = new ArrayList<>();

        for (InterfaceDependency dep : dependencies) {
            InterfaceDefinition def = interfaces.get(dep.getInterfaceId());

            if (def == null) {
                if (dep.isRequired()) {
                    throw new IllegalStateException("Required interface not found: " + dep.getInterfaceId());
                }
                log.warn("Optional interface not found: {}", dep.getInterfaceId());
                continue;
            }

            if (dep.hasVersionConstraint() && !dep.versionSatisfies(def.getVersion())) {
                if (dep.isRequired()) {
                    throw new IllegalStateException("Interface version mismatch for " +
                        dep.getInterfaceId() + ": required " + dep.getVersion() +
                        ", actual " + def.getVersion());
                }
                log.warn("Interface version mismatch for {}: required {}, actual {}",
                    dep.getInterfaceId(), dep.getVersion(), def.getVersion());
                continue;
            }

            resolved.add(def);
        }

        return resolved;
    }

    @Override
    public boolean validateDependencies(List<InterfaceDependency> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }

        for (InterfaceDependency dep : dependencies) {
            InterfaceDefinition def = interfaces.get(dep.getInterfaceId());

            if (def == null) {
                if (dep.isRequired()) {
                    return false;
                }
                continue;
            }

            if (dep.hasVersionConstraint() && !dep.versionSatisfies(def.getVersion())) {
                if (dep.isRequired()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int getInterfaceCount() {
        return interfaces.size();
    }

    @Override
    public void clear() {
        interfaces.clear();
        implementations.clear();
        preferredImplementations.clear();
        log.info("Interface registry cleared");
    }

    @Override
    public InterfaceRegistryStats getStats() {
        InterfaceRegistryStats stats = new InterfaceRegistryStats();
        stats.setTotalInterfaces(interfaces.size());
        stats.setLastUpdateTime(System.currentTimeMillis());

        int totalImpls = 0;
        int withImpls = 0;
        int withoutImpls = 0;

        for (Map.Entry<String, List<String>> entry : implementations.entrySet()) {
            int count = entry.getValue().size();
            totalImpls += count;
            if (count > 0) {
                withImpls++;
            } else {
                withoutImpls++;
            }
        }

        for (String interfaceId : interfaces.keySet()) {
            if (!implementations.containsKey(interfaceId)) {
                withoutImpls++;
            }
        }

        stats.setTotalImplementations(totalImpls);
        stats.setInterfacesWithImplementations(withImpls);
        stats.setInterfacesWithoutImplementations(withoutImpls);

        return stats;
    }
}

package net.ooder.sdk.core.capability.impl;

import net.ooder.sdk.api.capability.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 内存实现的 CAP 注册表
 *
 * @author Ooder Team
 * @version 2.3
 */
public class InMemoryCapRegistry implements CapRegistry {

    private final Map<String, Capability> capabilities = new ConcurrentHashMap<>();

    private final Map<String, String> addressIndex = new ConcurrentHashMap<>();

    private final Map<String, Set<Integer>> domainAddresses = new ConcurrentHashMap<>();

    private final List<CapRegistryListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void register(Capability capability) throws CapRegistryException {
        if (capability == null) {
            throw new CapRegistryException("Capability cannot be null");
        }
        if (capability.getCapabilityId() == null || capability.getCapabilityId().isEmpty()) {
            throw new CapRegistryException("Capability ID cannot be empty");
        }

        if (capability.getAddress() != null) {
            String existingCapId = addressIndex.get(capability.getAddress().toFullString());
            if (existingCapId != null && !existingCapId.equals(capability.getCapId())) {
                throw new CapRegistryException(
                    "Address " + capability.getAddress() + " is already occupied by " + existingCapId,
                    CapRegistryException.ErrorCode.ADDRESS_OCCUPIED
                );
            }
        }

        if (capability.getAddress() == null) {
            String domainId = capability.getSkillId() != null ?
                extractDomainFromSkillId(capability.getSkillId()) : "default";
            capability.setAddress(allocateAddress(domainId));
        }

        capabilities.put(capability.getCapabilityId(), capability);
        addressIndex.put(capability.getAddress().toFullString(), capability.getCapabilityId());

        domainAddresses
            .computeIfAbsent(capability.getAddress().getDomainId(), k -> ConcurrentHashMap.newKeySet())
            .add(capability.getAddress().getAddress());

        notifyRegistered(capability);
    }

    @Override
    public void unregister(String capId) throws CapRegistryException {
        Capability capability = capabilities.remove(capId);
        if (capability == null) {
            throw new CapRegistryException(
                "Capability not found: " + capId,
                CapRegistryException.ErrorCode.CAPABILITY_NOT_FOUND
            );
        }

        addressIndex.remove(capability.getAddress().toFullString());

        Set<Integer> addresses = domainAddresses.get(capability.getAddress().getDomainId());
        if (addresses != null) {
            addresses.remove(capability.getAddress().getAddress());
        }

        notifyUnregistered(capId);
    }

    @Override
    public Capability findById(String capId) {
        return capabilities.get(capId);
    }

    @Override
    public Capability findByAddress(CapAddress address) {
        if (address == null) return null;
        String capId = addressIndex.get(address.toFullString());
        return capId != null ? capabilities.get(capId) : null;
    }

    @Override
    public List<Capability> findByDomain(String domainId) {
        return capabilities.values().stream()
            .filter(cap -> cap.getAddress() != null && domainId.equals(cap.getAddress().getDomainId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Capability> findByType(CapabilityType type) {
        return capabilities.values().stream()
            .filter(cap -> type.equals(cap.getType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Capability> findBySkill(String skillId) {
        return capabilities.values().stream()
            .filter(cap -> skillId != null && skillId.equals(cap.getSkillId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Capability> findByTag(String tag) {
        return capabilities.values().stream()
            .filter(cap -> cap.getTags() != null && cap.getTags().contains(tag))
            .collect(Collectors.toList());
    }

    @Override
    public List<Capability> findAll() {
        return new ArrayList<>(capabilities.values());
    }

    @Override
    public boolean hasCapability(String capId) {
        return capabilities.containsKey(capId);
    }

    @Override
    public boolean isAddressOccupied(CapAddress address) {
        return address != null && addressIndex.containsKey(address.toFullString());
    }

    @Override
    public void updateStatus(String capId, CapabilityStatus status) throws CapRegistryException {
        Capability capability = capabilities.get(capId);
        if (capability == null) {
            throw new CapRegistryException(
                "Capability not found: " + capId,
                CapRegistryException.ErrorCode.CAPABILITY_NOT_FOUND
            );
        }

        CapabilityStatus oldStatus = capability.getStatus();
        capability.setStatus(status);

        notifyStatusChanged(capId, oldStatus, status);
    }

    @Override
    public CapabilityStatus getStatus(String capId) {
        Capability capability = capabilities.get(capId);
        return capability != null ? capability.getStatus() : null;
    }

    @Override
    public CapAddress allocateAddress(String domainId) throws CapRegistryException {
        Set<Integer> usedAddresses = domainAddresses.getOrDefault(domainId, Collections.emptySet());

        for (int i = 0; i <= 255; i++) {
            if (!usedAddresses.contains(i)) {
                CapAddress address = CapAddress.of(i, domainId);
                notifyAddressAllocated(address);
                return address;
            }
        }

        throw new CapRegistryException(
            "No available address in domain: " + domainId,
            CapRegistryException.ErrorCode.REGISTRY_FULL
        );
    }

    @Override
    public void releaseAddress(CapAddress address) {
        if (address == null) return;

        Set<Integer> addresses = domainAddresses.get(address.getDomainId());
        if (addresses != null) {
            addresses.remove(address.getAddress());
        }

        notifyAddressReleased(address);
    }

    @Override
    public DomainStats getDomainStats(String domainId) {
        List<Capability> domainCaps = findByDomain(domainId);

        DomainStats stats = new DomainStats();
        stats.setDomainId(domainId);
        stats.setTotalCapabilities(domainCaps.size());
        stats.setEnabledCapabilities((int) domainCaps.stream()
            .filter(cap -> cap.getStatus() == CapabilityStatus.ENABLED).count());
        stats.setHealthyCapabilities((int) domainCaps.stream()
            .filter(cap -> cap.getStatus() == CapabilityStatus.HEALTHY).count());

        Map<CapabilityType, Integer> typeDist = new HashMap<>();
        for (Capability cap : domainCaps) {
            if (cap.getType() != null) {
                typeDist.merge(cap.getType(), 1, Integer::sum);
            }
        }
        stats.setTypeDistribution(typeDist);

        return stats;
    }

    @Override
    public List<String> getAllDomains() {
        return new ArrayList<>(domainAddresses.keySet());
    }

    @Override
    public void addListener(CapRegistryListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(CapRegistryListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void clear() {
        capabilities.clear();
        addressIndex.clear();
        domainAddresses.clear();
    }

    @Override
    public int size() {
        return capabilities.size();
    }

    private String extractDomainFromSkillId(String skillId) {
        if (skillId.contains(".")) {
            return skillId.substring(0, skillId.lastIndexOf('.'));
        }
        return "default";
    }

    private void notifyRegistered(Capability capability) {
        for (CapRegistryListener listener : listeners) {
            try {
                listener.onCapabilityRegistered(capability);
            } catch (Exception e) {
            }
        }
    }

    private void notifyUnregistered(String capId) {
        for (CapRegistryListener listener : listeners) {
            try {
                listener.onCapabilityUnregistered(capId);
            } catch (Exception e) {
            }
        }
    }

    private void notifyStatusChanged(String capId, CapabilityStatus oldStatus, CapabilityStatus newStatus) {
        for (CapRegistryListener listener : listeners) {
            try {
                listener.onCapabilityStatusChanged(capId, oldStatus, newStatus);
            } catch (Exception e) {
            }
        }
    }

    private void notifyAddressAllocated(CapAddress address) {
        for (CapRegistryListener listener : listeners) {
            try {
                listener.onAddressAllocated(address);
            } catch (Exception e) {
            }
        }
    }

    private void notifyAddressReleased(CapAddress address) {
        for (CapRegistryListener listener : listeners) {
            try {
                listener.onAddressReleased(address);
            } catch (Exception e) {
            }
        }
    }
}

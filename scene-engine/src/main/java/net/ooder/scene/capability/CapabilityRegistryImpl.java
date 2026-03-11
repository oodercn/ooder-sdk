package net.ooder.scene.capability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityRegistryImpl implements CapabilityRegistry {

    private static final Logger log = LoggerFactory.getLogger(CapabilityRegistryImpl.class);
    private static final String REGISTRY_CONFIG = "capability-registry.yaml";

    private final Map<Integer, CapabilityInfo> addressMap = new ConcurrentHashMap<>();
    private final Map<String, CapabilityInfo> codeMap = new ConcurrentHashMap<>();
    private final ObjectMapper yamlMapper;

    public CapabilityRegistryImpl() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
        loadFromConfig();
    }

    private void loadFromConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(REGISTRY_CONFIG)) {
            if (is != null) {
                RegistryConfig config = yamlMapper.readValue(is, RegistryConfig.class);
                for (CapabilityInfo capability : config.getSegments()) {
                    register(capability);
                }
                log.info("Loaded {} capabilities from {}", config.getSegments().size(), REGISTRY_CONFIG);
            }
        } catch (IOException e) {
            log.warn("Failed to load capability registry: {}", e.getMessage());
        }

        for (CapabilitySegment segment : CapabilitySegment.values()) {
            if (!codeMap.containsKey(segment.getCode())) {
                CapabilityInfo info = fromEnum(segment);
                register(info);
            }
        }
    }

    private CapabilityInfo fromEnum(CapabilitySegment segment) {
        CapabilityInfo info = new CapabilityInfo();
        info.setBaseAddress(segment.getBaseAddress());
        info.setCode(segment.getCode());
        info.setName(segment.getName());
        info.setSelectionMode(segment.getSelectionMode());
        info.setSwitchScope(segment.getSwitchScope());
        info.setFallbackProvider(segment.getFallbackProvider());

        SlotInfo primarySlot = new SlotInfo();
        primarySlot.setOffset(0);
        primarySlot.setName("PRIMARY");
        if (segment.getFallbackProvider() != null) {
            ProviderInfo provider = new ProviderInfo(
                segment.getFallbackProvider(),
                segment.getName(),
                "micro"
            );
            primarySlot.getProviders().add(provider);
            primarySlot.setFallback(segment.getFallbackProvider());
        }
        info.getSlots().add(primarySlot);

        return info;
    }

    @Override
    public CapabilityInfo getCapability(int address) {
        int baseAddress = (address / 5) * 5;
        return addressMap.get(baseAddress);
    }

    @Override
    public CapabilityInfo getCapability(String code) {
        return codeMap.get(code);
    }

    @Override
    public List<ProviderInfo> getProviders(int address) {
        CapabilityInfo capability = getCapability(address);
        if (capability == null) {
            return new ArrayList<>();
        }

        int offset = address % 5;
        SegmentSlot slot = SegmentSlot.fromOffset(offset);
        if (slot == null) {
            return new ArrayList<>();
        }

        SlotInfo slotInfo = capability.getSlot(slot);
        return slotInfo != null ? slotInfo.getProviders() : new ArrayList<>();
    }

    @Override
    public List<ProviderInfo> getProviders(String code) {
        CapabilityInfo capability = getCapability(code);
        if (capability == null || capability.getPrimarySlot() == null) {
            return new ArrayList<>();
        }
        return capability.getPrimarySlot().getProviders();
    }

    @Override
    public String getDefaultProvider(int address) {
        CapabilityInfo capability = getCapability(address);
        if (capability == null) {
            return null;
        }
        return capability.getFallbackProvider();
    }

    @Override
    public String getFallbackProvider(int address) {
        return getDefaultProvider(address);
    }

    @Override
    public List<CapabilityInfo> getAllCapabilities() {
        return new ArrayList<>(codeMap.values());
    }

    @Override
    public void register(CapabilityInfo capability) {
        addressMap.put(capability.getBaseAddress(), capability);
        codeMap.put(capability.getCode(), capability);
        log.debug("Registered capability: {} ({})", capability.getCode(), capability.getBaseAddress());
    }

    @Override
    public void unregister(String code) {
        CapabilityInfo capability = codeMap.remove(code);
        if (capability != null) {
            addressMap.remove(capability.getBaseAddress());
        }
    }

    @Override
    public boolean hasCapability(String code) {
        return codeMap.containsKey(code);
    }

    public static class RegistryConfig {
        private List<CapabilityInfo> segments = new ArrayList<>();

        public List<CapabilityInfo> getSegments() { return segments; }
        public void setSegments(List<CapabilityInfo> segments) { this.segments = segments; }
    }
}

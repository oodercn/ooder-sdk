package net.ooder.config.scene.registry;

import net.ooder.config.scene.CapabilityConfig;
import net.ooder.config.scene.enums.CapabilityType;
import net.ooder.config.scene.enums.ServiceType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityRegistry {
    
    private static final CapabilityRegistry INSTANCE = new CapabilityRegistry();
    
    private final Map<String, CapabilityConfig> capabilities = new ConcurrentHashMap<String, CapabilityConfig>();
    private final Map<String, List<String>> serviceCapabilities = new ConcurrentHashMap<String, List<String>>();
    
    private CapabilityRegistry() {
        initDefaultCapabilities();
    }
    
    public static CapabilityRegistry getInstance() {
        return INSTANCE;
    }
    
    private void initDefaultCapabilities() {
        for (CapabilityType type : CapabilityType.values()) {
            CapabilityConfig config = createDefaultConfig(type);
            register(type.getCode(), config);
        }
    }
    
    private CapabilityConfig createDefaultConfig(CapabilityType type) {
        CapabilityConfig config = new CapabilityConfig();
        config.setCapabilityName(type.getDisplayName());
        config.setInterfaceId(type.getDefaultInterfaceId());
        config.setEndpoint(type.getDefaultEndpoint());
        config.setTimeout(type.getDefaultTimeout());
        config.setRetryCount(type.getDefaultRetryCount());
        config.setEnabled(true);
        return config;
    }
    
    public void register(String code, CapabilityConfig config) {
        if (code == null || config == null) {
            throw new IllegalArgumentException("Code and config cannot be null");
        }
        
        capabilities.put(code, config);
        
        CapabilityType type = CapabilityType.fromCode(code);
        if (type != null && type.getServiceType() != null) {
            String serviceCode = type.getServiceType().getCode();
            List<String> caps = serviceCapabilities.computeIfAbsent(serviceCode, k -> new ArrayList<String>());
            if (!caps.contains(code)) {
                caps.add(code);
            }
        }
    }
    
    public void register(ServiceType serviceType, String customCode, CapabilityConfig config) {
        if (serviceType == null || customCode == null || config == null) {
            throw new IllegalArgumentException("ServiceType, code and config cannot be null");
        }
        
        String fullCode = serviceType.getCode() + ":" + customCode;
        capabilities.put(fullCode, config);
        capabilities.put(customCode, config);
        
        List<String> caps = serviceCapabilities.computeIfAbsent(serviceType.getCode(), k -> new ArrayList<String>());
        if (!caps.contains(customCode)) {
            caps.add(customCode);
        }
    }
    
    public void registerCustom(String serviceCode, String capabilityCode, CapabilityConfig config) {
        if (serviceCode == null || capabilityCode == null || config == null) {
            throw new IllegalArgumentException("ServiceCode, capabilityCode and config cannot be null");
        }
        
        String fullCode = serviceCode + ":" + capabilityCode;
        capabilities.put(fullCode, config);
        capabilities.put(capabilityCode, config);
        
        List<String> caps = serviceCapabilities.computeIfAbsent(serviceCode, k -> new ArrayList<String>());
        if (!caps.contains(capabilityCode)) {
            caps.add(capabilityCode);
        }
    }
    
    public CapabilityConfig getCapability(String code) {
        return capabilities.get(code);
    }
    
    public CapabilityConfig getCapability(ServiceType serviceType, String capabilityCode) {
        if (serviceType == null) {
            return capabilities.get(capabilityCode);
        }
        String fullCode = serviceType.getCode() + ":" + capabilityCode;
        CapabilityConfig config = capabilities.get(fullCode);
        if (config == null) {
            config = capabilities.get(capabilityCode);
        }
        return config;
    }
    
    public CapabilityConfig getCapability(String serviceCode, String capabilityCode) {
        String fullCode = serviceCode + ":" + capabilityCode;
        CapabilityConfig config = capabilities.get(fullCode);
        if (config == null) {
            config = capabilities.get(capabilityCode);
        }
        return config;
    }
    
    public List<String> getCapabilitiesByService(String serviceCode) {
        List<String> caps = serviceCapabilities.get(serviceCode);
        return caps != null ? new ArrayList<String>(caps) : new ArrayList<String>();
    }
    
    public List<String> getCapabilitiesByService(ServiceType serviceType) {
        if (serviceType == null) {
            return new ArrayList<String>();
        }
        return getCapabilitiesByService(serviceType.getCode());
    }
    
    public List<CapabilityConfig> getCapabilityConfigsByService(String serviceCode) {
        List<String> codes = getCapabilitiesByService(serviceCode);
        List<CapabilityConfig> configs = new ArrayList<CapabilityConfig>();
        for (String code : codes) {
            CapabilityConfig config = capabilities.get(code);
            if (config != null) {
                configs.add(config);
            }
        }
        return configs;
    }
    
    public boolean hasCapability(String code) {
        return capabilities.containsKey(code);
    }
    
    public boolean hasCapability(ServiceType serviceType, String capabilityCode) {
        if (serviceType == null) {
            return capabilities.containsKey(capabilityCode);
        }
        String fullCode = serviceType.getCode() + ":" + capabilityCode;
        return capabilities.containsKey(fullCode) || capabilities.containsKey(capabilityCode);
    }
    
    public void unregister(String code) {
        capabilities.remove(code);
    }
    
    public void unregister(ServiceType serviceType, String capabilityCode) {
        if (serviceType != null) {
            String fullCode = serviceType.getCode() + ":" + capabilityCode;
            capabilities.remove(fullCode);
        }
        capabilities.remove(capabilityCode);
        
        if (serviceType != null) {
            List<String> caps = serviceCapabilities.get(serviceType.getCode());
            if (caps != null) {
                caps.remove(capabilityCode);
            }
        }
    }
    
    public void clear() {
        capabilities.clear();
        serviceCapabilities.clear();
        initDefaultCapabilities();
    }
    
    public Set<String> getAllCapabilityCodes() {
        return new HashSet<String>(capabilities.keySet());
    }
    
    public int size() {
        return capabilities.size();
    }
    
    public Map<String, Object> exportDocumentation() {
        Map<String, Object> docs = new LinkedHashMap<String, Object>();
        
        for (ServiceType service : ServiceType.values()) {
            Map<String, Object> serviceDoc = new LinkedHashMap<String, Object>();
            serviceDoc.put("displayName", service.getDisplayName());
            serviceDoc.put("description", service.getDescription());
            serviceDoc.put("clientClass", service.getClientClass());
            
            List<Map<String, Object>> caps = new ArrayList<Map<String, Object>>();
            for (String capCode : getCapabilitiesByService(service)) {
                CapabilityType type = CapabilityType.fromCode(capCode);
                if (type != null) {
                    Map<String, Object> capDoc = new LinkedHashMap<String, Object>();
                    capDoc.put("code", type.getCode());
                    capDoc.put("displayName", type.getDisplayName());
                    capDoc.put("description", type.getDescription());
                    capDoc.put("interfaceId", type.getDefaultInterfaceId());
                    capDoc.put("defaultEndpoint", type.getDefaultEndpoint());
                    capDoc.put("defaultTimeout", type.getDefaultTimeout());
                    caps.add(capDoc);
                }
            }
            serviceDoc.put("capabilities", caps);
            docs.put(service.getCode(), serviceDoc);
        }
        
        return docs;
    }
}

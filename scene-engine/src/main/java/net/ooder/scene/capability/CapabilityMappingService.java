package net.ooder.scene.capability;

import net.ooder.skills.capability.CapabilityAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityMappingService {

    private static final Logger log = LoggerFactory.getLogger(CapabilityMappingService.class);

    private final Map<String, CapabilityMapping> capabilityMappings = new ConcurrentHashMap<>();

    public CapabilityMappingService() {
        initDefaultMappings();
    }

    private void initDefaultMappings() {
        registerMapping("user.create", CapabilityAddress.ORG_LOCAL, "create");
        registerMapping("user.read", CapabilityAddress.ORG_LOCAL, "read");
        registerMapping("user.update", CapabilityAddress.ORG_LOCAL, "update");
        registerMapping("user.delete", CapabilityAddress.ORG_LOCAL, "delete");
        registerMapping("user.list", CapabilityAddress.ORG_LOCAL, "list");
        
        registerMapping("file.upload", CapabilityAddress.VFS_LOCAL, "upload");
        registerMapping("file.download", CapabilityAddress.VFS_LOCAL, "download");
        registerMapping("file.delete", CapabilityAddress.VFS_LOCAL, "delete");
        registerMapping("file.list", CapabilityAddress.VFS_LOCAL, "list");
        
        registerMapping("llm.chat", CapabilityAddress.LLM_OLLAMA, "chat");
        registerMapping("llm.complete", CapabilityAddress.LLM_OLLAMA, "complete");
        registerMapping("llm.embed", CapabilityAddress.LLM_OLLAMA, "embed");
        
        registerMapping("knowledge.search", CapabilityAddress.KNOW_VECTOR, "search");
        registerMapping("knowledge.add", CapabilityAddress.KNOW_VECTOR, "add");
        
        log.info("Initialized {} default capability mappings", capabilityMappings.size());
    }

    public void registerMapping(String capabilityId, CapabilityAddress address, String operation) {
        CapabilityMapping mapping = new CapabilityMapping(capabilityId, address, operation);
        capabilityMappings.put(capabilityId, mapping);
        log.debug("Registered mapping: {} -> {} ({})", capabilityId, address, operation);
    }

    public void unregisterMapping(String capabilityId) {
        capabilityMappings.remove(capabilityId);
        log.debug("Unregistered mapping: {}", capabilityId);
    }

    public CapabilityMapping getMapping(String capabilityId) {
        return capabilityMappings.get(capabilityId);
    }

    public CapabilityAddress getAddress(String capabilityId) {
        CapabilityMapping mapping = capabilityMappings.get(capabilityId);
        return mapping != null ? mapping.getAddress() : null;
    }

    public String getOperation(String capabilityId) {
        CapabilityMapping mapping = capabilityMappings.get(capabilityId);
        return mapping != null ? mapping.getOperation() : null;
    }

    public boolean hasMapping(String capabilityId) {
        return capabilityMappings.containsKey(capabilityId);
    }

    public Map<String, CapabilityMapping> getAllMappings() {
        return new ConcurrentHashMap<>(capabilityMappings);
    }

    public void clear() {
        capabilityMappings.clear();
        log.info("Cleared all capability mappings");
    }

    public static class CapabilityMapping {
        private final String capabilityId;
        private final CapabilityAddress address;
        private final String operation;

        public CapabilityMapping(String capabilityId, CapabilityAddress address, String operation) {
            this.capabilityId = capabilityId;
            this.address = address;
            this.operation = operation;
        }

        public String getCapabilityId() { return capabilityId; }
        public CapabilityAddress getAddress() { return address; }
        public String getOperation() { return operation; }
    }
}

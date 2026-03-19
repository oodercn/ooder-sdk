package net.ooder.scene.bridge;

import net.ooder.scene.capability.CapabilityBinding;
import net.ooder.scene.group.SceneGroup;
import net.ooder.scene.group.SceneGroupManager;
import net.ooder.scene.group.archive.SceneGroupArchiver;
import net.ooder.scene.group.persistence.SceneGroupPersistence;
import net.ooder.scene.skill.knowledge.KnowledgeBinding;
import net.ooder.scene.participant.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSceneGroup implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(UserSceneGroup.class);
    
    private final String sceneGroupId;
    private final SceneGroupManager sceneGroupManager;
    private final SceneGroup sceneGroup;
    private final SceneGroupBridge bridge;
    private final SceneGroupPersistence persistence;
    private final SceneGroupArchiver archiver;
    private final Map<String, Object> runtimeContext = new ConcurrentHashMap<>();
    
    private UserSceneGroup(String sceneGroupId,
                          SceneGroupManager sceneGroupManager,
                          SceneGroup sceneGroup,
                          SceneGroupBridge bridge,
                          SceneGroupPersistence persistence,
                          SceneGroupArchiver archiver) {
        this.sceneGroupId = sceneGroupId;
        this.sceneGroupManager = sceneGroupManager;
        this.sceneGroup = sceneGroup;
        this.bridge = bridge;
        this.persistence = persistence;
        this.archiver = archiver;
    }
    
    public static UserSceneGroup getOrCreate(String sceneGroupId,
                                              SceneGroupManager sceneGroupManager,
                                              SceneGroupBridge bridge,
                                              SceneGroupPersistence persistence,
                                              SceneGroupArchiver archiver) {
        SceneGroup group = sceneGroupManager.getSceneGroup(sceneGroupId);
        
        if (group == null) {
            try {
                group = persistence.load(sceneGroupId).orElse(null);
            } catch (Exception e) {
                logger.warn("Failed to load SceneGroup from persistence: {}", sceneGroupId, e);
            }
        }
        
        if (group == null) {
            throw new IllegalArgumentException("SceneGroup not found: " + sceneGroupId);
        }
        
        return new UserSceneGroup(sceneGroupId, sceneGroupManager, group, bridge, persistence, archiver);
    }
    
    public String getSceneGroupId() {
        return sceneGroupId;
    }
    
    public String getName() {
        return sceneGroup.getName();
    }
    
    public void setName(String name) {
        sceneGroup.setName(name);
        save();
    }
    
    public String getDescription() {
        return sceneGroup.getDescription();
    }
    
    public void setDescription(String description) {
        sceneGroup.setDescription(description);
        save();
    }
    
    public SceneGroup.Status getStatus() {
        return sceneGroup.getStatus();
    }
    
    public String getTemplateId() {
        return sceneGroup.getTemplateId();
    }
    
    public String getCreatorId() {
        return sceneGroup.getCreatorId();
    }
    
    public List<Participant> getParticipants() {
        return sceneGroup.getAllParticipants();
    }
    
    public Participant addParticipant(String userId, String name, Participant.Type type, Participant.Role role) {
        String participantId = sceneGroupId + "-p-" + userId;
        
        Participant participant = new Participant(participantId, userId, name, type);
        participant.setRole(role);
        
        sceneGroup.addParticipant(participant);
        
        if (bridge != null) {
            bridge.syncFromSeToSdk(sceneGroupId);
        }
        
        save();
        
        logger.info("Added participant {} to SceneGroup {}", userId, sceneGroupId);
        
        return participant;
    }
    
    public boolean removeParticipant(String userId) {
        Participant participant = sceneGroup.getParticipant(userId);
        if (participant == null) {
            return false;
        }
        
        sceneGroup.removeParticipant(userId);
        
        if (bridge != null) {
            bridge.syncFromSeToSdk(sceneGroupId);
        }
        
        save();
        
        logger.info("Removed participant {} from SceneGroup {}", userId, sceneGroupId);
        
        return true;
    }
    
    public boolean updateParticipantRole(String userId, Participant.Role newRole) {
        Participant participant = sceneGroup.getParticipant(userId);
        if (participant == null) {
            return false;
        }
        
        Participant.Role oldRole = participant.getRole();
        participant.setRole(newRole);
        
        if (bridge != null) {
            bridge.syncFromSeToSdk(sceneGroupId);
        }
        
        save();
        
        logger.info("Updated participant {} role: {} -> {} in SceneGroup {}", 
            userId, oldRole, newRole, sceneGroupId);
        
        return true;
    }
    
    public List<CapabilityBinding> getCapabilityBindings() {
        return sceneGroup.getAllCapabilityBindings();
    }
    
    public CapabilityBinding addCapabilityBinding(String capabilityId, String name, String providerType) {
        String bindingId = sceneGroupId + "-b-" + System.currentTimeMillis();
        
        CapabilityBinding binding = new CapabilityBinding(bindingId, sceneGroupId, capabilityId);
        binding.setCapName(name);
        
        sceneGroup.addCapabilityBinding(binding);
        
        save();
        
        logger.info("Added capability binding {} to SceneGroup {}", capabilityId, sceneGroupId);
        
        return binding;
    }
    
    public boolean removeCapabilityBinding(String bindingId) {
        boolean removed = sceneGroup.removeCapabilityBinding(bindingId);
        
        if (removed) {
            save();
            logger.info("Removed capability binding {} from SceneGroup {}", bindingId, sceneGroupId);
        }
        
        return removed;
    }
    
    public List<KnowledgeBinding> getKnowledgeBindings() {
        return sceneGroup.getAllKnowledgeBindings();
    }
    
    public KnowledgeBinding addKnowledgeBinding(String knowledgeBaseId, String name, String layer) {
        KnowledgeBinding binding = new KnowledgeBinding();
        binding.setSceneGroupId(sceneGroupId);
        binding.setKbId(knowledgeBaseId);
        binding.setKbName(name);
        binding.setLayer(layer);
        binding.setBindTime(System.currentTimeMillis());
        
        sceneGroup.addKnowledgeBinding(binding);
        
        save();
        
        logger.info("Added knowledge binding {} to SceneGroup {}", knowledgeBaseId, sceneGroupId);
        
        return binding;
    }
    
    public boolean removeKnowledgeBinding(String kbId) {
        boolean removed = sceneGroup.removeKnowledgeBinding(kbId);
        
        if (removed) {
            save();
            logger.info("Removed knowledge binding {} from SceneGroup {}", kbId, sceneGroupId);
        }
        
        return removed;
    }
    
    public Map<String, Object> getConfig() {
        return sceneGroup.getAllConfig();
    }
    
    public Object getConfig(String key) {
        return sceneGroup.getConfig(key);
    }
    
    public void setConfig(String key, Object value) {
        sceneGroup.setConfig(key, value);
        save();
    }
    
    public void setConfigs(Map<String, Object> configs) {
        for (Map.Entry<String, Object> entry : configs.entrySet()) {
            sceneGroup.setConfig(entry.getKey(), entry.getValue());
        }
        save();
    }
    
    public boolean activate() {
        boolean activated = sceneGroup.activate();
        
        if (activated) {
            save();
            logger.info("Activated SceneGroup: {}", sceneGroupId);
        }
        
        return activated;
    }
    
    public boolean suspend() {
        boolean suspended = sceneGroup.suspend();
        
        if (suspended) {
            save();
            logger.info("Suspended SceneGroup: {}", sceneGroupId);
        }
        
        return suspended;
    }
    
    public SceneGroupArchiver.ArchiveResult archive(String description) {
        if (archiver == null) {
            throw new UnsupportedOperationException("Archiver not configured");
        }
        
        return archiver.archive(sceneGroupId, description);
    }
    
    public SceneGroupArchiver.ArchiveResult restore(String archiveId) {
        if (archiver == null) {
            throw new UnsupportedOperationException("Archiver not configured");
        }
        
        return archiver.restore(sceneGroupId, archiveId);
    }
    
    public void syncFromSdk() {
        if (bridge != null) {
            bridge.syncFromSdkToSe(sceneGroupId);
            reload();
        }
    }
    
    public void syncToSdk() {
        if (bridge != null) {
            bridge.syncFromSeToSdk(sceneGroupId);
        }
    }
    
    public Object getRuntimeContext(String key) {
        return runtimeContext.get(key);
    }
    
    public void setRuntimeContext(String key, Object value) {
        runtimeContext.put(key, value);
    }
    
    public void removeRuntimeContext(String key) {
        runtimeContext.remove(key);
    }
    
    private void save() {
        if (persistence != null) {
            try {
                persistence.save(sceneGroup);
            } catch (Exception e) {
                logger.error("Failed to save SceneGroup: {}", sceneGroupId, e);
            }
        }
    }
    
    private void reload() {
        if (persistence != null) {
            try {
                persistence.load(sceneGroupId).ifPresent(g -> {
                    sceneGroup.setName(g.getName());
                    sceneGroup.setDescription(g.getDescription());
                });
            } catch (Exception e) {
                logger.error("Failed to reload SceneGroup: {}", sceneGroupId, e);
            }
        }
    }
    
    @Override
    public void close() {
        save();
        runtimeContext.clear();
    }
}

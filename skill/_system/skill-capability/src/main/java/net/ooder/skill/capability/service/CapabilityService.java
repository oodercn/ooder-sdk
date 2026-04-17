package net.ooder.skill.capability.service;

import java.util.List;
import net.ooder.skill.capability.model.*;

public interface CapabilityService {
    
    Capability register(Capability capability);
    
    Capability update(Capability capability);
    
    void unregister(String capabilityId);
    
    Capability findById(String capabilityId);
    
    List<Capability> findAll();
    
    List<Capability> findByType(CapabilityType type);
    
    List<Capability> findBySceneType(String sceneType);
    
    List<Capability> search(String query);
    
    void updateStatus(String capabilityId, String status);
    
    List<Capability> findByOwnership(CapabilityOwnership ownership);
    
    List<Capability> findByOwnershipAndSceneType(CapabilityOwnership ownership, String sceneType);
    
    Capability addSceneType(String capabilityId, String sceneType, String approvedBy);
    
    Capability removeSceneType(String capabilityId, String sceneType, String approvedBy);
    
    List<Capability> findBySkillForm(SkillForm form);
    
    List<Capability> findBySceneTypeNew(SceneType sceneType);
    
    List<Capability> findByCapabilityCategory(CapabilityCategory category);
    
    List<Capability> findByFilters(SkillForm skillForm, SceneType sceneType, CapabilityCategory category, 
                                    CapabilityOwnership ownership, String keyword);
    
    void updateInstallStatus(String capabilityId, boolean installed);
    
    boolean isInstalled(String capabilityId);
    
    CapabilityStatus getCapabilityStatus(String capabilityId);
}

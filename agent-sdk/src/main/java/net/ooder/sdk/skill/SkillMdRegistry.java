package net.ooder.sdk.skill;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SkillMdRegistry {
    
    void register(SkillMdDocument document);
    
    void registerFromPath(String skillMdPath);
    
    void unregister(String skillId);
    
    Optional<SkillMdDocument> getSkill(String skillId);
    
    List<SkillMdDocument> getAllSkills();
    
    List<SkillMdDocument> getSkillsByDomain(String domain);
    
    List<SkillMdDocument> searchSkills(String keyword);
    
    List<SkillMdDocument> getSkillsByTag(String tag);
    
    boolean hasSkill(String skillId);
    
    int getSkillCount();
    
    void clear();
    
    void addRegistryListener(SkillRegistryListener listener);
    
    void removeRegistryListener(SkillRegistryListener listener);
    
    Map<String, Object> getSkillMetadata(String skillId);
    
    List<String> getSkillInputNames(String skillId);
    
    List<String> getSkillOutputNames(String skillId);
    
    boolean validateSkill(String skillId);
    
    List<String> getValidationErrors(String skillId);
    
    interface SkillRegistryListener {
        
        void onSkillRegistered(SkillMdDocument document);
        
        void onSkillUnregistered(String skillId);
        
        void onSkillUpdated(SkillMdDocument document);
    }
}

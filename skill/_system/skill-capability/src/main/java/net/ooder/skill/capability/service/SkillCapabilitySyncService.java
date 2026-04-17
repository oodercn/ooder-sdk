package net.ooder.skill.capability.service;

import java.util.Map;

public interface SkillCapabilitySyncService {
    
    Map<String, Object> syncAllSkills();
    
    int getSyncedCount();
    
    int getSkippedCount();
    
    int getErrorCount();
}

package net.ooder.skill.capability.service;

import net.ooder.skill.capability.model.*;

public interface SceneSkillCategoryDetector {
    
    SkillForm detectSkillForm(Capability capability);
    
    SceneType detectSceneType(Capability capability);
    
    CapabilityCategory detectCategory(Capability capability);
}

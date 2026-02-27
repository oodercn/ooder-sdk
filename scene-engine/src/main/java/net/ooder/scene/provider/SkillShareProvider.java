package net.ooder.scene.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.share.SharedSkill;
import net.ooder.scene.provider.model.share.ReceivedSkill;

import java.util.List;
import java.util.Map;

public interface SkillShareProvider extends BaseProvider {
    
    Result<SharedSkill> shareSkill(Map<String, Object> skillData);
    
    Result<List<SharedSkill>> getSharedSkills();
    
    Result<List<ReceivedSkill>> getReceivedSkills();
    
    Result<Boolean> cancelShare(String shareId);
    
    Result<Boolean> acceptShare(String shareId);
    
    Result<Boolean> rejectShare(String shareId);
}

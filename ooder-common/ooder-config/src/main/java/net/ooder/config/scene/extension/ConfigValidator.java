package net.ooder.config.scene.extension;

import net.ooder.config.scene.*;

public interface ConfigValidator {
    
    ValidationResult validate(SceneConfig config);
    
    ValidationResult validate(OrgSceneConfig config);
    
    ValidationResult validate(VfsSceneConfig config);
    
    ValidationResult validate(MsgSceneConfig config);
    
    ValidationResult validate(JdsSceneConfig config);
}

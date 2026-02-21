package net.ooder.sdk.validator;

import java.util.List;

public interface SceneValidator {
    
    ValidationResult validate(ScenePackage scene);
    
    ValidationResult validateLevel(ScenePackage scene, int level);
    
    List<ValidationCheck> getChecks(int level);
}
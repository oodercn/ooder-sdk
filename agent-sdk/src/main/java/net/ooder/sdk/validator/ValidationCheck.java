package net.ooder.sdk.validator;

public interface ValidationCheck {
    
    String getId();
    
    String getName();
    
    String getDescription();
    
    CheckResult execute(ScenePackage scene);
    
    int getLevel();
    
    Severity getSeverity();
}
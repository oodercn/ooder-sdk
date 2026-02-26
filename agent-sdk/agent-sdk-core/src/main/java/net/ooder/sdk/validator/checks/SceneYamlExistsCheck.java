package net.ooder.sdk.validator.checks;

import net.ooder.sdk.validator.*;

public class SceneYamlExistsCheck implements ValidationCheck {
    
    @Override
    public String getId() {
        return "SCENE-001";
    }
    
    @Override
    public String getName() {
        return "scene-yaml-exists";
    }
    
    @Override
    public String getDescription() {
        return "检查scene.yaml文件是否存在";
    }
    
    @Override
    public CheckResult execute(ScenePackage scene) {
        CheckResult result = new CheckResult();
        result.setCheckId(getId());
        result.setCheckName(getName());
        result.setLevel(getLevel());
        result.setSeverity(getSeverity());
        
        long start = System.currentTimeMillis();
        boolean exists = scene.hasFile("META-INF/scene/scene.yaml");
        result.setDuration(System.currentTimeMillis() - start);
        
        if (exists) {
            result.setStatus(Status.PASS);
            result.setMessage("scene.yaml 文件存在");
        } else {
            result.setStatus(Status.FAIL);
            result.setMessage("scene.yaml 文件不存在");
        }
        
        return result;
    }
    
    @Override
    public int getLevel() {
        return 1;
    }
    
    @Override
    public Severity getSeverity() {
        return Severity.ERROR;
    }
}
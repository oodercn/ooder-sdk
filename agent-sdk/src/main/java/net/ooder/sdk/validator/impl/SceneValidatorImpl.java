package net.ooder.sdk.validator.impl;

import net.ooder.sdk.validator.*;
import net.ooder.sdk.validator.checks.SceneYamlExistsCheck;

import java.util.ArrayList;
import java.util.List;

public class SceneValidatorImpl implements SceneValidator {
    private final List<ValidationCheck> checks;
    
    public SceneValidatorImpl() {
        this.checks = new ArrayList<>();
        setupDefaultChecks();
    }
    
    private void setupDefaultChecks() {
        // 添加默认的验证检查
        checks.add(new SceneYamlExistsCheck());
        // 可以添加更多默认检查
    }
    
    @Override
    public ValidationResult validate(ScenePackage scene) {
        return validateLevel(scene, 4); // 默认验证所有4个级别
    }
    
    @Override
    public ValidationResult validateLevel(ScenePackage scene, int level) {
        ValidationResult result = new ValidationResult();
        result.setTimestamp(System.currentTimeMillis());
        result.setValid(true);
        
        List<CheckResult> details = new ArrayList<>();
        int passed = 0, warnings = 0, errors = 0;
        
        for (ValidationCheck check : getChecks(level)) {
            CheckResult checkResult = check.execute(scene);
            details.add(checkResult);
            
            switch (checkResult.getStatus()) {
                case PASS:
                    passed++;
                    break;
                case WARN:
                    warnings++;
                    break;
                case FAIL:
                    errors++;
                    result.setValid(false);
                    break;
                case SKIP:
                    break;
            }
        }
        
        result.setTotalChecks(details.size());
        result.setPassed(passed);
        result.setWarnings(warnings);
        result.setErrors(errors);
        result.setDetails(details);
        
        // 计算分数
        int score = (passed * 100) / (details.size() > 0 ? details.size() : 1);
        result.setScore(score);
        
        return result;
    }
    
    @Override
    public List<ValidationCheck> getChecks(int level) {
        List<ValidationCheck> levelChecks = new ArrayList<>();
        for (ValidationCheck check : checks) {
            if (check.getLevel() <= level) {
                levelChecks.add(check);
            }
        }
        return levelChecks;
    }
}
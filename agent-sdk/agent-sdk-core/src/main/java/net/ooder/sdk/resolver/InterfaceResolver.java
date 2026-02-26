package net.ooder.sdk.resolver;

import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.InterfaceDependency;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterfaceResolver {
    
    ResolvedInterface resolve(String interfaceId);
    
    ResolvedInterface resolve(String interfaceId, String preferredSkillId);
    
    ResolvedInterface resolveWithFallback(String interfaceId);
    
    List<ResolvedInterface> resolveAll(List<InterfaceDependency> dependencies);
    
    boolean canResolve(String interfaceId);
    
    boolean canResolveWithFallback(String interfaceId);
    
    ValidationResult validate(String interfaceId);
    
    ValidationResult validateAll(List<InterfaceDependency> dependencies);
    
    void setFallbackStrategy(FallbackStrategy strategy);
    
    FallbackStrategy getFallbackStrategy();
    
    enum FallbackStrategy {
        NONE,
        LOCAL_FIRST,
        REMOTE_FIRST,
        AUTO
    }
    
    class ResolvedInterface {
        private final String interfaceId;
        private final InterfaceDefinition definition;
        private final String skillId;
        private final Object driver;
        private final boolean isFallback;
        private final List<String> alternativeSkills;
        
        public ResolvedInterface(String interfaceId, InterfaceDefinition definition, 
                                  String skillId, Object driver, boolean isFallback) {
            this.interfaceId = interfaceId;
            this.definition = definition;
            this.skillId = skillId;
            this.driver = driver;
            this.isFallback = isFallback;
            this.alternativeSkills = new java.util.ArrayList<>();
        }
        
        public String getInterfaceId() { return interfaceId; }
        public InterfaceDefinition getDefinition() { return definition; }
        public String getSkillId() { return skillId; }
        public Object getDriver() { return driver; }
        public boolean isFallback() { return isFallback; }
        public List<String> getAlternativeSkills() { return alternativeSkills; }
        
        public void addAlternativeSkill(String skillId) {
            if (skillId != null && !skillId.equals(this.skillId)) {
                alternativeSkills.add(skillId);
            }
        }
        
        public boolean isResolved() {
            return definition != null && driver != null;
        }
        
        public <T> Optional<T> getDriverAs(Class<T> type) {
            if (driver == null) {
                return Optional.empty();
            }
            try {
                return Optional.of(type.cast(driver));
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
    }
    
    class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        private final Map<String, String> details;
        
        public ValidationResult(boolean valid) {
            this.valid = valid;
            this.errors = new java.util.ArrayList<>();
            this.warnings = new java.util.ArrayList<>();
            this.details = new java.util.HashMap<>();
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public Map<String, String> getDetails() { return details; }
        
        public void addError(String error) { errors.add(error); }
        public void addWarning(String warning) { warnings.add(warning); }
        public void addDetail(String key, String value) { details.put(key, value); }
        
        public static ValidationResult success() {
            return new ValidationResult(true);
        }
        
        public static ValidationResult failure(String error) {
            ValidationResult result = new ValidationResult(false);
            result.addError(error);
            return result;
        }
    }
}

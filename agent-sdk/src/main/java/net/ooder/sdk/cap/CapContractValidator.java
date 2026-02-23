package net.ooder.sdk.cap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CapContractValidator {
    
    private static final Logger log = LoggerFactory.getLogger(CapContractValidator.class);
    
    public ValidationResult validate(CapDefinition definition) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (definition == null) {
            errors.add("CAP definition cannot be null");
            return new ValidationResult(false, errors, warnings);
        }
        
        validateMetadata(definition, errors, warnings);
        validateInterface(definition, errors, warnings);
        validateOffline(definition, errors, warnings);
        validateDependencies(definition, errors, warnings);
        
        boolean valid = errors.isEmpty();
        return new ValidationResult(valid, errors, warnings);
    }
    
    public ValidationResult validateRequest(CapDefinition definition, Map<String, Object> request) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (definition == null || definition.getSpec() == null) {
            errors.add("CAP definition or spec is null");
            return new ValidationResult(false, errors, warnings);
        }
        
        CapDefinition.CapInterface interface_ = definition.getSpec().getInterface_();
        if (interface_ == null || interface_.getRequest() == null) {
            return new ValidationResult(true, errors, warnings);
        }
        
        CapDefinition.CapSchema requestSchema = interface_.getRequest();
        validateSchema(request, requestSchema, "request", errors, warnings);
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    public ValidationResult validateResponse(CapDefinition definition, Map<String, Object> response) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        if (definition == null || definition.getSpec() == null) {
            errors.add("CAP definition or spec is null");
            return new ValidationResult(false, errors, warnings);
        }
        
        CapDefinition.CapInterface interface_ = definition.getSpec().getInterface_();
        if (interface_ == null || interface_.getResponse() == null) {
            return new ValidationResult(true, errors, warnings);
        }
        
        CapDefinition.CapSchema responseSchema = interface_.getResponse();
        validateSchema(response, responseSchema, "response", errors, warnings);
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }
    
    private void validateMetadata(CapDefinition definition, List<String> errors, List<String> warnings) {
        CapDefinition.CapMetadata metadata = definition.getMetadata();
        if (metadata == null) {
            errors.add("CAP metadata is required");
            return;
        }
        
        if (metadata.getCapId() == null || metadata.getCapId().isEmpty()) {
            errors.add("CAP ID is required");
        } else if (!CapAddress.isValidAddress(metadata.getCapId())) {
            errors.add("Invalid CAP address format: " + metadata.getCapId());
        }
        
        if (metadata.getName() == null || metadata.getName().isEmpty()) {
            errors.add("CAP name is required");
        } else if (!metadata.getName().matches("^[A-Z][A-Z0-9_]*$")) {
            warnings.add("CAP name should be uppercase with underscores: " + metadata.getName());
        }
        
        if (metadata.getVersion() == null || metadata.getVersion().isEmpty()) {
            errors.add("CAP version is required");
        } else if (!metadata.getVersion().matches("^\\d+\\.\\d+\\.\\d+$")) {
            errors.add("CAP version must follow semantic versioning (x.y.z): " + metadata.getVersion());
        }
        
        if (metadata.getStatus() == null || metadata.getStatus().isEmpty()) {
            warnings.add("CAP status is recommended");
        } else {
            String status = metadata.getStatus().toLowerCase();
            if (!status.equals("draft") && !status.equals("stable") && !status.equals("deprecated")) {
                warnings.add("CAP status should be draft, stable, or deprecated: " + metadata.getStatus());
            }
        }
    }
    
    private void validateInterface(CapDefinition definition, List<String> errors, List<String> warnings) {
        if (definition.getSpec() == null) {
            errors.add("CAP spec is required");
            return;
        }
        
        CapDefinition.CapInterface interface_ = definition.getSpec().getInterface_();
        if (interface_ == null) {
            errors.add("CAP interface is required");
            return;
        }
        
        if (interface_.getProtocol() == null || interface_.getProtocol().isEmpty()) {
            errors.add("Interface protocol is required");
        } else {
            String protocol = interface_.getProtocol().toLowerCase();
            if (!isValidProtocol(protocol)) {
                errors.add("Invalid interface protocol: " + interface_.getProtocol());
            }
        }
        
        if (interface_.getPath() == null || interface_.getPath().isEmpty()) {
            warnings.add("Interface path is recommended");
        }
        
        if (interface_.getTimeout() <= 0) {
            warnings.add("Interface timeout should be positive: " + interface_.getTimeout());
        }
    }
    
    private void validateOffline(CapDefinition definition, List<String> errors, List<String> warnings) {
        if (definition.getSpec() == null) {
            return;
        }
        
        CapDefinition.CapOffline offline = definition.getSpec().getOffline();
        if (offline == null) {
            warnings.add("Offline configuration is recommended for CAP: " + definition.getCapId());
            return;
        }
        
        if (offline.isRequired()) {
            if (offline.getStrategy() == null || offline.getStrategy().isEmpty()) {
                errors.add("Offline strategy is required when offline is mandatory");
            } else {
                String strategy = offline.getStrategy().toLowerCase();
                if (!isValidOfflineStrategy(strategy)) {
                    errors.add("Invalid offline strategy: " + offline.getStrategy());
                }
            }
            
            if (offline.getFallback() == null) {
                errors.add("Fallback handler is required when offline is mandatory");
            }
        }
    }
    
    private void validateDependencies(CapDefinition definition, List<String> errors, List<String> warnings) {
        if (definition.getSpec() == null || definition.getSpec().getDependencies() == null) {
            return;
        }
        
        for (CapDefinition.CapDependency dep : definition.getSpec().getDependencies()) {
            if (dep.getCapId() == null || dep.getCapId().isEmpty()) {
                errors.add("Dependency CAP ID is required");
            } else if (!CapAddress.isValidAddress(dep.getCapId())) {
                errors.add("Invalid dependency CAP address: " + dep.getCapId());
            }
        }
    }
    
    private void validateSchema(Object data, CapDefinition.CapSchema schema, String context, 
                                List<String> errors, List<String> warnings) {
        if (schema == null) {
            return;
        }
        
        if (schema.getRequired() != null && !schema.getRequired().isEmpty()) {
            if (!(data instanceof Map)) {
                errors.add(context + " must be an object for required field validation");
                return;
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> dataMap = (Map<String, Object>) data;
            
            for (String requiredField : schema.getRequired()) {
                if (!dataMap.containsKey(requiredField)) {
                    errors.add(context + " missing required field: " + requiredField);
                }
            }
        }
    }
    
    private boolean isValidProtocol(String protocol) {
        return protocol.equals("http") || protocol.equals("grpc") || 
               protocol.equals("websocket") || protocol.equals("udp") || 
               protocol.equals("local-jar");
    }
    
    private boolean isValidOfflineStrategy(String strategy) {
        return strategy.equals("queue") || strategy.equals("cache") || 
               strategy.equals("reject") || strategy.equals("custom");
    }
    
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
            this.warnings = warnings != null ? warnings : new ArrayList<>();
        }
        
        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{valid=").append(valid);
            if (!errors.isEmpty()) {
                sb.append(", errors=").append(errors);
            }
            if (!warnings.isEmpty()) {
                sb.append(", warnings=").append(warnings);
            }
            sb.append("}");
            return sb.toString();
        }
    }
}

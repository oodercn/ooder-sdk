package net.ooder.scene.procedure;

import net.ooder.sdk.api.completeness.CompletenessDetail;
import net.ooder.sdk.api.procedure.*;
import net.ooder.scene.procedure.completeness.DefaultCompletenessEvaluator;
import net.ooder.scene.procedure.persistence.YamlProcedurePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 企业规范流程服务实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class EnterpriseProcedureServiceImpl implements EnterpriseProcedureService {

    private static final Logger log = LoggerFactory.getLogger(EnterpriseProcedureServiceImpl.class);

    private final Map<String, EnterpriseProcedure> procedureStore = new ConcurrentHashMap<>();
    private final YamlProcedurePersistence persistence;
    private final DefaultCompletenessEvaluator completenessEvaluator;

    public EnterpriseProcedureServiceImpl() {
        this.persistence = new YamlProcedurePersistence();
        this.completenessEvaluator = new DefaultCompletenessEvaluator();
        loadFromPersistence();
    }

    public EnterpriseProcedureServiceImpl(YamlProcedurePersistence persistence) {
        this.persistence = persistence;
        this.completenessEvaluator = new DefaultCompletenessEvaluator();
        loadFromPersistence();
    }

    private void loadFromPersistence() {
        try {
            List<EnterpriseProcedure> procedures = persistence.loadAll();
            for (EnterpriseProcedure procedure : procedures) {
                procedureStore.put(procedure.getProcedureId(), procedure);
            }
            log.info("Loaded {} procedures from persistence", procedures.size());
        } catch (Exception e) {
            log.warn("Failed to load procedures from persistence: {}", e.getMessage());
        }
    }

    @Override
    public EnterpriseProcedure create(EnterpriseProcedureCreateRequest request) {
        validateCreateRequest(request);

        EnterpriseProcedureEntity procedure = new EnterpriseProcedureEntity();
        procedure.setProcedureId(generateId());
        procedure.setName(request.getName());
        procedure.setCategory(request.getCategory());
        procedure.setDescription(request.getDescription());
        procedure.setTags(request.getTags());
        procedure.setSource(request.getSource());
        procedure.setOrganizationId(request.getOrganizationId());
        procedure.setDepartmentIds(request.getDepartmentIds());
        procedure.setRoles(request.getRoles());
        procedure.setSteps(request.getSteps());
        procedure.setRules(request.getRules());
        procedure.setRequiredCapabilities(request.getRequiredCapabilities());
        procedure.setKnowledgeBaseIds(request.getKnowledgeBaseIds());
        procedure.setAuthor(request.getAuthor());
        procedure.setExtensions(request.getExtensions());
        procedure.setStatus(ProcedureStatus.DRAFT);

        CompletenessDetail detail = completenessEvaluator.evaluate(procedure);
        procedure.setCompleteness(detail.getOverallScore());

        procedureStore.put(procedure.getProcedureId(), procedure);

        try {
            persistence.save(procedure);
            log.info("Created procedure: {} ({})", procedure.getName(), procedure.getProcedureId());
        } catch (Exception e) {
            log.error("Failed to persist procedure: {}", procedure.getProcedureId(), e);
        }

        return procedure;
    }

    @Override
    public EnterpriseProcedure get(String procedureId) {
        if (procedureId == null) {
            return null;
        }
        return procedureStore.get(procedureId);
    }

    @Override
    public EnterpriseProcedure update(String procedureId, EnterpriseProcedureUpdateRequest request) {
        EnterpriseProcedure existing = procedureStore.get(procedureId);
        if (existing == null) {
            throw new IllegalArgumentException("Procedure not found: " + procedureId);
        }

        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getCategory() != null) {
            existing.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            existing.setTags(request.getTags());
        }
        if (request.getDepartmentIds() != null) {
            existing.setDepartmentIds(request.getDepartmentIds());
        }
        if (request.getRoles() != null) {
            existing.setRoles(request.getRoles());
        }
        if (request.getSteps() != null) {
            existing.setSteps(request.getSteps());
        }
        if (request.getRules() != null) {
            existing.setRules(request.getRules());
        }
        if (request.getRequiredCapabilities() != null) {
            existing.setRequiredCapabilities(request.getRequiredCapabilities());
        }
        if (request.getOptionalCapabilities() != null) {
            existing.setOptionalCapabilities(request.getOptionalCapabilities());
        }
        if (request.getKnowledgeBaseIds() != null) {
            existing.setKnowledgeBaseIds(request.getKnowledgeBaseIds());
        }
        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getExtensions() != null) {
            existing.setExtensions(request.getExtensions());
        }

        existing.setUpdateTime(System.currentTimeMillis());

        CompletenessDetail detail = completenessEvaluator.evaluate(existing);
        existing.setCompleteness(detail.getOverallScore());

        try {
            persistence.save(existing);
            log.info("Updated procedure: {}", procedureId);
        } catch (Exception e) {
            log.error("Failed to persist procedure update: {}", procedureId, e);
        }

        return existing;
    }

    @Override
    public void delete(String procedureId) {
        EnterpriseProcedure removed = procedureStore.remove(procedureId);
        if (removed != null) {
            try {
                persistence.delete(procedureId);
                log.info("Deleted procedure: {}", procedureId);
            } catch (Exception e) {
                log.error("Failed to delete procedure from persistence: {}", procedureId, e);
            }
        }
    }

    @Override
    public List<EnterpriseProcedure> list(EnterpriseProcedureQueryRequest request) {
        return procedureStore.values().stream()
                .filter(p -> filterByRequest(p, request))
                .sorted((a, b) -> Long.compare(b.getUpdateTime(), a.getUpdateTime()))
                .skip((long) (request.getPage() - 1) * request.getPageSize())
                .limit(request.getPageSize())
                .collect(Collectors.toList());
    }

    private boolean filterByRequest(EnterpriseProcedure procedure, EnterpriseProcedureQueryRequest request) {
        if (request.getOrganizationId() != null && 
            !request.getOrganizationId().equals(procedure.getOrganizationId())) {
            return false;
        }
        if (request.getCategory() != null && 
            !request.getCategory().equals(procedure.getCategory())) {
            return false;
        }
        if (request.getStatus() != null && 
            request.getStatus() != procedure.getStatus()) {
            return false;
        }
        if (request.getSource() != null && 
            request.getSource() != procedure.getSource()) {
            return false;
        }
        if (request.getMinCompleteness() > 0 && 
            procedure.getCompleteness() < request.getMinCompleteness()) {
            return false;
        }
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String keyword = request.getKeyword().toLowerCase();
            String name = procedure.getName() != null ? procedure.getName().toLowerCase() : "";
            String desc = procedure.getDescription() != null ? procedure.getDescription().toLowerCase() : "";
            if (!name.contains(keyword) && !desc.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EnterpriseProcedure llmAssistCreate(List<Document> documents) {
        throw new UnsupportedOperationException("LLM assisted creation requires LLM service integration");
    }

    @Override
    public EnterpriseProcedurePreview llmPreview(List<Document> documents) {
        throw new UnsupportedOperationException("LLM preview requires LLM service integration");
    }

    @Override
    public CompletenessDetail evaluateCompleteness(String procedureId) {
        EnterpriseProcedure procedure = get(procedureId);
        if (procedure == null) {
            throw new IllegalArgumentException("Procedure not found: " + procedureId);
        }
        return completenessEvaluator.evaluate(procedure);
    }

    @Override
    public List<CompletenessSuggestion> getCompletenessSuggestions(String procedureId) {
        EnterpriseProcedure procedure = get(procedureId);
        if (procedure == null) {
            return Collections.emptyList();
        }
        return completenessEvaluator.getSuggestions(procedure);
    }

    @Override
    public ValidationResult validate(String procedureId) {
        EnterpriseProcedure procedure = get(procedureId);
        if (procedure == null) {
            return createValidationResult(false, Collections.singletonList("Procedure not found: " + procedureId));
        }
        return validateProcedure(procedure);
    }

    private void validateCreateRequest(EnterpriseProcedureCreateRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            errors.add("Name is required");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            errors.add("Category is required");
        }
        if (request.getOrganizationId() == null || request.getOrganizationId().trim().isEmpty()) {
            errors.add("OrganizationId is required");
        }
        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            errors.add("Author is required");
        }
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            errors.add("At least one role is required");
        }
        if (request.getSteps() == null || request.getSteps().isEmpty()) {
            errors.add("At least one step is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join("; ", errors));
        }
    }

    private ValidationResult validateProcedure(EnterpriseProcedure procedure) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (procedure.getName() == null || procedure.getName().trim().isEmpty()) {
            errors.add("Name is required");
        }
        if (procedure.getRoles() == null || procedure.getRoles().isEmpty()) {
            errors.add("At least one role is required");
        }
        if (procedure.getSteps() == null || procedure.getSteps().isEmpty()) {
            errors.add("At least one step is required");
        }
        if (procedure.getRequiredCapabilities() == null || procedure.getRequiredCapabilities().isEmpty()) {
            warnings.add("No required capabilities defined");
        }

        return createValidationResult(errors.isEmpty(), errors, warnings);
    }

    private ValidationResult createValidationResult(boolean valid, List<String> errors) {
        return createValidationResult(valid, errors, Collections.emptyList());
    }

    private ValidationResult createValidationResult(boolean valid, List<String> errors, List<String> warnings) {
        ValidationResultEntity result = new ValidationResultEntity();
        result.setValid(valid);
        result.setErrors(errors);
        result.setWarnings(warnings);
        return result;
    }

    private String generateId() {
        return "proc-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 验证结果实体
     */
    private static class ValidationResultEntity implements ValidationResult {
        private boolean valid;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        @Override
        public boolean isValid() {
            return valid;
        }

        @Override
        public void setValid(boolean valid) {
            this.valid = valid;
        }

        @Override
        public List<String> getErrors() {
            return errors;
        }

        @Override
        public void setErrors(List<String> errors) {
            this.errors = errors != null ? errors : new ArrayList<>();
        }

        @Override
        public List<String> getWarnings() {
            return warnings;
        }

        @Override
        public void setWarnings(List<String> warnings) {
            this.warnings = warnings != null ? warnings : new ArrayList<>();
        }
    }
}

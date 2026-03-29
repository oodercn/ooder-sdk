package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.*;
import net.ooder.scene.fusion.matcher.DefaultProcedureMatcher;
import net.ooder.scene.fusion.resolver.DefaultConflictResolver;
import net.ooder.scene.fusion.persistence.YamlFusedTemplatePersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 融合模板服务实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class FusionTemplateServiceImpl implements FusionTemplateService {

    private static final Logger log = LoggerFactory.getLogger(FusionTemplateServiceImpl.class);

    private final Map<String, FusedWorkflowTemplate> templateStore = new ConcurrentHashMap<>();
    private final DefaultProcedureMatcher procedureMatcher;
    private final DefaultConflictResolver conflictResolver;
    private final YamlFusedTemplatePersistence persistence;

    public FusionTemplateServiceImpl() {
        this.procedureMatcher = new DefaultProcedureMatcher();
        this.conflictResolver = new DefaultConflictResolver();
        this.persistence = new YamlFusedTemplatePersistence();
        loadFromPersistence();
    }

    private void loadFromPersistence() {
        try {
            List<FusedWorkflowTemplate> templates = persistence.loadAll();
            for (FusedWorkflowTemplate template : templates) {
                templateStore.put(template.getTemplateId(), template);
            }
            log.info("Loaded {} fused templates from persistence", templates.size());
        } catch (Exception e) {
            log.warn("Failed to load fused templates from persistence: {}", e.getMessage());
        }
    }

    @Override
    public List<ProcedureMatchResult> matchProcedures(String skillId) {
        return procedureMatcher.match(skillId);
    }

    @Override
    public int calculateMatchScore(String procedureId, String skillId) {
        return procedureMatcher.calculateScore(procedureId, skillId);
    }

    @Override
    public FusedWorkflowTemplate fuse(FusionRequest request) {
        validateFusionRequest(request);

        int matchScore = calculateMatchScore(
                request.getEnterpriseProcedureId(), 
                request.getSkillId()
        );

        FusionPreview preview = preview(request);

        FusedWorkflowTemplateEntity template = new FusedWorkflowTemplateEntity();
        template.setTemplateId(generateId());
        template.setName(request.getName() != null ? request.getName() : 
                "Fused-" + request.getEnterpriseProcedureId());
        template.setDescription(request.getDescription());
        template.setEnterpriseProcedureId(request.getEnterpriseProcedureId());
        template.setSkillId(request.getSkillId());
        template.setSkillTemplateId(request.getSkillTemplateId());
        template.setMatchScore(matchScore);
        template.setFusionStrategy(request.getFusionStrategy());
        template.setFusionConflicts(preview.getConflicts());
        template.setFusionTime(System.currentTimeMillis());
        template.setFusedBy(request.getFusedBy());
        
        FusedWorkflowTemplate previewTemplate = preview.getTemplate();
        if (previewTemplate != null) {
            template.setRoles(previewTemplate.getRoles());
            template.setActivationSteps(previewTemplate.getActivationSteps());
            template.setMenus(previewTemplate.getMenus());
            template.setRules(previewTemplate.getRules());
            template.setCapabilities(previewTemplate.getCapabilities());
        }
        template.setStatus(TemplateStatus.DRAFT);

        List<FusionConflict> unresolvedConflicts = preview.getConflicts().stream()
                .filter(c -> c.getResolution() == null)
                .collect(Collectors.toList());

        if (!unresolvedConflicts.isEmpty() && 
                !Boolean.TRUE.equals(request.getFusionStrategy().isAutoResolveConflict())) {
            throw new FusionConflictException("存在未解决的融合冲突", unresolvedConflicts);
        }

        if (!unresolvedConflicts.isEmpty()) {
            for (FusionConflict conflict : unresolvedConflicts) {
                conflictResolver.resolve(conflict, request.getFusionStrategy());
            }
        }

        templateStore.put(template.getTemplateId(), template);

        try {
            persistence.save(template);
            log.info("Created fused template: {} (matchScore: {})", 
                    template.getTemplateId(), matchScore);
        } catch (Exception e) {
            log.error("Failed to persist fused template: {}", template.getTemplateId(), e);
        }

        return template;
    }

    @Override
    public FusionPreview preview(FusionRequest request) {
        return procedureMatcher.preview(request);
    }

    @Override
    public FusedWorkflowTemplate resolveConflict(String templateId, ConflictResolutionRequest request) {
        FusedWorkflowTemplate template = get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        List<FusionConflict> conflicts = template.getFusionConflicts();
        if (conflicts == null || conflicts.isEmpty()) {
            return template;
        }

        for (ConflictResolutionItem item : request.getResolutions()) {
            for (FusionConflict conflict : conflicts) {
                if (conflict.getConflictId().equals(item.getConflictId())) {
                    conflict.setResolution(item.getResolution());
                    conflict.setResolvedValue(item.getResolvedValue());
                    conflict.setResolvedBy(request.getResolvedBy());
                    conflict.setResolvedAt(System.currentTimeMillis());
                    conflict.setComment(item.getComment());
                }
            }
        }

        template.setFusionConflicts(conflicts);
        template.setUpdateTime(System.currentTimeMillis());

        try {
            persistence.save(template);
            log.info("Resolved conflicts for template: {}", templateId);
        } catch (Exception e) {
            log.error("Failed to persist conflict resolution: {}", templateId, e);
        }

        return template;
    }

    @Override
    public FusedWorkflowTemplate get(String templateId) {
        if (templateId == null) {
            return null;
        }
        return templateStore.get(templateId);
    }

    @Override
    public List<FusedWorkflowTemplate> list(FusionTemplateQueryRequest request) {
        return templateStore.values().stream()
                .filter(t -> filterByRequest(t, request))
                .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
                .skip((long) (request.getPage() - 1) * request.getPageSize())
                .limit(request.getPageSize())
                .collect(Collectors.toList());
    }

    private boolean filterByRequest(FusedWorkflowTemplate template, FusionTemplateQueryRequest request) {
        if (request.getSkillId() != null && 
                !request.getSkillId().equals(template.getSkillId())) {
            return false;
        }
        if (request.getProcedureId() != null && 
                !request.getProcedureId().equals(template.getEnterpriseProcedureId())) {
            return false;
        }
        if (request.getStatus() != null && 
                request.getStatus() != template.getStatus()) {
            return false;
        }
        return true;
    }

    @Override
    public void delete(String templateId) {
        FusedWorkflowTemplate removed = templateStore.remove(templateId);
        if (removed != null) {
            try {
                persistence.delete(templateId);
                log.info("Deleted fused template: {}", templateId);
            } catch (Exception e) {
                log.error("Failed to delete fused template from persistence: {}", templateId, e);
            }
        }
    }

    @Override
    public List<TemplateVersion> getVersionHistory(String templateId) {
        try {
            return persistence.loadVersions(templateId);
        } catch (Exception e) {
            log.error("Failed to load version history for template: {}", templateId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public FusedWorkflowTemplate rollback(String templateId, int version) {
        FusedWorkflowTemplate template = get(templateId);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        Optional<FusedWorkflowTemplate> previousVersion;
        try {
            previousVersion = persistence.loadVersion(templateId, version);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load version: " + version, e);
        }
        if (previousVersion.isEmpty()) {
            throw new IllegalArgumentException("Version not found: " + version);
        }

        FusedWorkflowTemplate rolledBack = previousVersion.get();
        templateStore.put(templateId, rolledBack);

        try {
            persistence.save(rolledBack);
            log.info("Rolled back template {} to version {}", templateId, version);
        } catch (Exception e) {
            log.error("Failed to persist rollback: {}", templateId, e);
        }

        return rolledBack;
    }

    private void validateFusionRequest(FusionRequest request) {
        List<String> errors = new ArrayList<>();

        if (request.getEnterpriseProcedureId() == null || 
                request.getEnterpriseProcedureId().isEmpty()) {
            errors.add("EnterpriseProcedureId is required");
        }
        if (request.getSkillId() == null || request.getSkillId().isEmpty()) {
            errors.add("SkillId is required");
        }
        if (request.getFusionStrategy() == null) {
            errors.add("FusionStrategy is required");
        }
        if (request.getFusedBy() == null || request.getFusedBy().isEmpty()) {
            errors.add("FusedBy is required");
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join("; ", errors));
        }
    }

    private String generateId() {
        return "fused-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * 融合冲突异常
     */
    public static class FusionConflictException extends RuntimeException {
        private final List<FusionConflict> conflicts;

        public FusionConflictException(String message, List<FusionConflict> conflicts) {
            super(message);
            this.conflicts = conflicts;
        }

        public List<FusionConflict> getConflicts() {
            return conflicts;
        }
    }
}

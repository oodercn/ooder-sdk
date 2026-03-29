package net.ooder.scene.fusion.matcher;

import net.ooder.sdk.api.fusion.*;
import net.ooder.sdk.api.procedure.EnterpriseProcedure;
import net.ooder.sdk.api.procedure.EnterpriseProcedureService;
import net.ooder.scene.fusion.FusionConflictEntity;
import net.ooder.scene.fusion.FusionPreviewEntity;
import net.ooder.scene.fusion.FusedWorkflowTemplateEntity;
import net.ooder.scene.fusion.ProcedureMatchResultEntity;
import net.ooder.scene.procedure.EnterpriseProcedureServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 默认企业规范匹配器
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class DefaultProcedureMatcher {

    private static final Logger log = LoggerFactory.getLogger(DefaultProcedureMatcher.class);

    private final EnterpriseProcedureService procedureService;

    public DefaultProcedureMatcher() {
        this.procedureService = new EnterpriseProcedureServiceImpl();
    }

    public DefaultProcedureMatcher(EnterpriseProcedureService procedureService) {
        this.procedureService = procedureService != null ? procedureService : 
                new EnterpriseProcedureServiceImpl();
    }

    public List<ProcedureMatchResult> match(String skillId) {
        List<EnterpriseProcedure> allProcedures = procedureService.list(
                new net.ooder.scene.procedure.EnterpriseProcedureQueryRequestEntity()
        );

        List<ProcedureMatchResult> results = new ArrayList<>();

        for (EnterpriseProcedure procedure : allProcedures) {
            int score = calculateScore(procedure.getProcedureId(), skillId);
            if (score > 0) {
                ProcedureMatchResultEntity result = new ProcedureMatchResultEntity();
                result.setProcedureId(procedure.getProcedureId());
                result.setProcedureName(procedure.getName());
                result.setMatchScore(score);
                result.setRoleMatchScore(calculateRoleMatchScore(procedure, skillId));
                result.setCapabilityMatchScore(calculateCapabilityMatchScore(procedure, skillId));
                result.setStepMatchScore(calculateStepMatchScore(procedure, skillId));
                result.setCategoryMatchScore(calculateCategoryMatchScore(procedure, skillId));
                results.add(result);
            }
        }

        results.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));

        return results;
    }

    public int calculateScore(String procedureId, String skillId) {
        EnterpriseProcedure procedure = procedureService.get(procedureId);
        if (procedure == null) {
            return 0;
        }

        double roleScore = calculateRoleMatchScore(procedure, skillId);
        double capabilityScore = calculateCapabilityMatchScore(procedure, skillId);
        double stepScore = calculateStepMatchScore(procedure, skillId);
        double categoryScore = calculateCategoryMatchScore(procedure, skillId);

        int totalScore = (int) (
                roleScore * 0.3 +
                capabilityScore * 0.3 +
                stepScore * 0.2 +
                categoryScore * 0.2
        );

        log.debug("Match score for procedure {} with skill {}: {}", procedureId, skillId, totalScore);

        return totalScore;
    }

    public FusionPreview preview(FusionRequest request) {
        EnterpriseProcedure procedure = procedureService.get(request.getEnterpriseProcedureId());
        if (procedure == null) {
            throw new IllegalArgumentException("Procedure not found: " + request.getEnterpriseProcedureId());
        }

        FusedWorkflowTemplateEntity template = new FusedWorkflowTemplateEntity();
        template.setTemplateId("preview-" + UUID.randomUUID().toString().substring(0, 8));
        template.setEnterpriseProcedureId(request.getEnterpriseProcedureId());
        template.setSkillId(request.getSkillId());
        template.setMatchScore(calculateScore(request.getEnterpriseProcedureId(), request.getSkillId()));
        template.setName(procedure.getName());
        template.setDescription(procedure.getDescription());

        List<FusionConflict> conflicts = detectConflicts(procedure, request);

        FusionPreviewEntity preview = new FusionPreviewEntity();
        preview.setTemplate(template);
        preview.setConflicts(conflicts);

        return preview;
    }

    private double calculateRoleMatchScore(EnterpriseProcedure procedure, String skillId) {
        if (procedure.getRoles() == null || procedure.getRoles().isEmpty()) {
            return 0;
        }
        return Math.min(100, procedure.getRoles().size() * 25);
    }

    private double calculateCapabilityMatchScore(EnterpriseProcedure procedure, String skillId) {
        if (procedure.getRequiredCapabilities() == null || procedure.getRequiredCapabilities().isEmpty()) {
            return 0;
        }
        return Math.min(100, procedure.getRequiredCapabilities().size() * 20);
    }

    private double calculateStepMatchScore(EnterpriseProcedure procedure, String skillId) {
        if (procedure.getSteps() == null || procedure.getSteps().isEmpty()) {
            return 0;
        }
        return Math.min(100, procedure.getSteps().size() * 20);
    }

    private double calculateCategoryMatchScore(EnterpriseProcedure procedure, String skillId) {
        if (procedure.getCategory() == null) {
            return 0;
        }
        return 50;
    }

    private List<FusionConflict> detectConflicts(EnterpriseProcedure procedure, FusionRequest request) {
        List<FusionConflict> conflicts = new ArrayList<>();

        if (procedure.getRoles() != null && procedure.getRoles().size() > 5) {
            FusionConflictEntity conflict = new FusionConflictEntity();
            conflict.setConflictId(UUID.randomUUID().toString());
            conflict.setField("roles");
            conflict.setType(ConflictType.STRUCTURE_MISMATCH);
            conflict.setComment("角色数量较多，可能需要简化");
            conflicts.add(conflict);
        }

        if (procedure.getSteps() != null && procedure.getSteps().size() > 10) {
            FusionConflictEntity conflict = new FusionConflictEntity();
            conflict.setConflictId(UUID.randomUUID().toString());
            conflict.setField("steps");
            conflict.setType(ConflictType.STRUCTURE_MISMATCH);
            conflict.setComment("步骤数量较多，可能需要简化");
            conflicts.add(conflict);
        }

        return conflicts;
    }
}

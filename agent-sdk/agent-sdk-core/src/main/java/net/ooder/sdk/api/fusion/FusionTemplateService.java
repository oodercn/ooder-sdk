package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.util.List;

/**
 * Fusion Template Service Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionTemplateService {

    List<ProcedureMatchResult> matchProcedures(String skillId);

    int calculateMatchScore(String procedureId, String skillId);

    FusedWorkflowTemplate fuse(FusionRequest request);

    FusionPreview preview(FusionRequest request);

    FusedWorkflowTemplate resolveConflict(String templateId, ConflictResolutionRequest request);

    FusedWorkflowTemplate get(String templateId);

    List<FusedWorkflowTemplate> list(FusionTemplateQueryRequest request);

    void delete(String templateId);

    List<TemplateVersion> getVersionHistory(String templateId);

    FusedWorkflowTemplate rollback(String templateId, int version);
}

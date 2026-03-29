package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;
import net.ooder.sdk.api.completeness.CompletenessDetail;

import java.util.List;

/**
 * Enterprise Procedure Service Interface
 * 
 * <p>Provides complete lifecycle management for enterprise procedures,
 * including CRUD, LLM-assisted creation, completeness evaluation, etc.</p>
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface EnterpriseProcedureService {

    EnterpriseProcedure create(EnterpriseProcedureCreateRequest request);

    EnterpriseProcedure get(String procedureId);

    EnterpriseProcedure update(String procedureId, EnterpriseProcedureUpdateRequest request);

    void delete(String procedureId);

    List<EnterpriseProcedure> list(EnterpriseProcedureQueryRequest request);

    EnterpriseProcedure llmAssistCreate(List<Document> documents);

    EnterpriseProcedurePreview llmPreview(List<Document> documents);

    CompletenessDetail evaluateCompleteness(String procedureId);

    List<CompletenessSuggestion> getCompletenessSuggestions(String procedureId);

    ValidationResult validate(String procedureId);
}

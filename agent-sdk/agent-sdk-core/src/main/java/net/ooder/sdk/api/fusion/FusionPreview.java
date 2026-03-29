package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Fusion Preview Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface FusionPreview extends Serializable {

    FusedWorkflowTemplate getTemplate();
    void setTemplate(FusedWorkflowTemplate template);

    List<FusionConflict> getConflicts();
    void setConflicts(List<FusionConflict> conflicts);

    boolean hasConflicts();
    void setHasConflicts(boolean hasConflicts);

    List<String> getWarnings();
    void setWarnings(List<String> warnings);
}

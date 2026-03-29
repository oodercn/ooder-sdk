package net.ooder.scene.fusion;

import net.ooder.sdk.api.fusion.FusionConflict;
import net.ooder.sdk.api.fusion.FusionPreview;
import net.ooder.sdk.api.fusion.FusedWorkflowTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 融合预览实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class FusionPreviewEntity implements FusionPreview {

    private static final long serialVersionUID = 1L;

    private FusedWorkflowTemplate template;
    private List<FusionConflict> conflicts = new ArrayList<>();
    private boolean hasConflicts;
    private List<String> warnings = new ArrayList<>();

    @Override
    public FusedWorkflowTemplate getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(FusedWorkflowTemplate template) {
        this.template = template;
    }

    @Override
    public List<FusionConflict> getConflicts() {
        return conflicts;
    }

    @Override
    public void setConflicts(List<FusionConflict> conflicts) {
        this.conflicts = conflicts != null ? conflicts : new ArrayList<>();
        this.hasConflicts = !this.conflicts.isEmpty();
    }

    @Override
    public boolean hasConflicts() {
        return hasConflicts;
    }

    @Override
    public void setHasConflicts(boolean hasConflicts) {
        this.hasConflicts = hasConflicts;
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

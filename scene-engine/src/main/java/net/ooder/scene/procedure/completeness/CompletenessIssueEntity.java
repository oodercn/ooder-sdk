package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.CompletenessIssue;
import net.ooder.sdk.api.completeness.IssueSeverity;

/**
 * 完善度问题实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CompletenessIssueEntity implements CompletenessIssue {

    private String dimension;
    private String description;
    private IssueSeverity severity = IssueSeverity.WARNING;
    private String suggestion;
    private String actionUrl;

    @Override
    public String getDimension() {
        return dimension;
    }

    @Override
    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public IssueSeverity getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(IssueSeverity severity) {
        this.severity = severity != null ? severity : IssueSeverity.WARNING;
    }

    @Override
    public String getSuggestion() {
        return suggestion;
    }

    @Override
    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    @Override
    public String getActionUrl() {
        return actionUrl;
    }

    @Override
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}

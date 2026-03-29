package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Completeness Issue Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessIssue extends Serializable {

    String getDimension();
    void setDimension(String dimension);

    String getDescription();
    void setDescription(String description);

    IssueSeverity getSeverity();
    void setSeverity(IssueSeverity severity);

    String getSuggestion();
    void setSuggestion(String suggestion);

    String getActionUrl();
    void setActionUrl(String actionUrl);
}

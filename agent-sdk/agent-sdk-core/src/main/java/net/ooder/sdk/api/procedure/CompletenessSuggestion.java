package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Completeness Suggestion Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessSuggestion extends Serializable {

    String getDimension();
    void setDimension(String dimension);

    String getDescription();
    void setDescription(String description);

    String getSuggestion();
    void setSuggestion(String suggestion);

    String getActionUrl();
    void setActionUrl(String actionUrl);
}

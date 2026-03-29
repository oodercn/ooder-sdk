package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.procedure.CompletenessSuggestion;

/**
 * 完善度改进建议实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class CompletenessSuggestionEntity implements CompletenessSuggestion {

    private static final long serialVersionUID = 1L;

    private String dimension;
    private String description;
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

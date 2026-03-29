package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;

/**
 * Completeness Check Item Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessCheckItem extends Serializable {

    String getItemId();
    void setItemId(String itemId);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getCheckExpression();
    void setCheckExpression(String checkExpression);

    int getScore();
    void setScore(int score);

    IssueSeverity getSeverity();
    void setSeverity(IssueSeverity severity);
}

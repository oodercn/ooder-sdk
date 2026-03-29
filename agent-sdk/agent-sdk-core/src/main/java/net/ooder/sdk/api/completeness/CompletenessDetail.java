package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Completeness Detail Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessDetail extends Serializable {

    int getOverallScore();
    void setOverallScore(int overallScore);

    List<CompletenessDimension> getDimensions();
    void setDimensions(List<CompletenessDimension> dimensions);

    List<CompletenessIssue> getIssues();
    void setIssues(List<CompletenessIssue> issues);

    List<String> getSuggestions();
    void setSuggestions(List<String> suggestions);
}

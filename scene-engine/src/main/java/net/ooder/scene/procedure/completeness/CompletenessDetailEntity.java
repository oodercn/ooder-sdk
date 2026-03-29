package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.CompletenessDetail;
import net.ooder.sdk.api.completeness.CompletenessDimension;
import net.ooder.sdk.api.completeness.CompletenessIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * 完善度详情实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CompletenessDetailEntity implements CompletenessDetail {

    private int overallScore;
    private List<CompletenessDimension> dimensions = new ArrayList<>();
    private List<CompletenessIssue> issues = new ArrayList<>();
    private List<String> suggestions = new ArrayList<>();

    @Override
    public int getOverallScore() {
        return overallScore;
    }

    @Override
    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    @Override
    public List<CompletenessDimension> getDimensions() {
        return dimensions;
    }

    @Override
    public void setDimensions(List<CompletenessDimension> dimensions) {
        this.dimensions = dimensions != null ? dimensions : new ArrayList<>();
    }

    @Override
    public List<CompletenessIssue> getIssues() {
        return issues;
    }

    @Override
    public void setIssues(List<CompletenessIssue> issues) {
        this.issues = issues != null ? issues : new ArrayList<>();
    }

    @Override
    public List<String> getSuggestions() {
        return suggestions;
    }

    @Override
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions != null ? suggestions : new ArrayList<>();
    }
}

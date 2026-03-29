package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.CompletenessCheckItem;
import net.ooder.sdk.api.completeness.IssueSeverity;

/**
 * 完善度检查项实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class CompletenessCheckItemEntity implements CompletenessCheckItem {

    private static final long serialVersionUID = 1L;

    private String itemId;
    private String name;
    private String description;
    private String checkExpression;
    private int score;
    private IssueSeverity severity;

    public CompletenessCheckItemEntity() {
    }

    @Override
    public String getItemId() {
        return itemId;
    }

    @Override
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
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
    public String getCheckExpression() {
        return checkExpression;
    }

    @Override
    public void setCheckExpression(String checkExpression) {
        this.checkExpression = checkExpression;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public IssueSeverity getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(IssueSeverity severity) {
        this.severity = severity;
    }
}

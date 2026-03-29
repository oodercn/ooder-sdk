package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.CompletenessDimension;

import java.util.ArrayList;
import java.util.List;

/**
 * 完善度维度实体实现
 *
 * @author SE SDK Team
 * @version 3.1.0
 * @since 3.1.0
 */
public class CompletenessDimensionEntity implements CompletenessDimension {

    private String name;
    private int weight;
    private int score;
    private String status;
    private List<String> checkedItems = new ArrayList<>();
    private List<String> missingItems = new ArrayList<>();

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
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
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public List<String> getCheckedItems() {
        return checkedItems;
    }

    @Override
    public void setCheckedItems(List<String> checkedItems) {
        this.checkedItems = checkedItems != null ? checkedItems : new ArrayList<>();
    }

    @Override
    public List<String> getMissingItems() {
        return missingItems;
    }

    @Override
    public void setMissingItems(List<String> missingItems) {
        this.missingItems = missingItems != null ? missingItems : new ArrayList<>();
    }
}

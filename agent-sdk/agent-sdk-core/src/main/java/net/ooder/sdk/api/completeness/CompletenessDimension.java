package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Completeness Dimension Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessDimension extends Serializable {

    String getName();
    void setName(String name);

    int getWeight();
    void setWeight(int weight);

    int getScore();
    void setScore(int score);

    String getStatus();
    void setStatus(String status);

    List<String> getCheckedItems();
    void setCheckedItems(List<String> checkedItems);

    List<String> getMissingItems();
    void setMissingItems(List<String> missingItems);
}

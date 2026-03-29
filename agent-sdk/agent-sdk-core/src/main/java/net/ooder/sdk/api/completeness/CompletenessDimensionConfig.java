package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

import java.io.Serializable;
import java.util.List;

/**
 * Completeness Dimension Config Interface
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public interface CompletenessDimensionConfig extends Serializable {

    String getDimensionId();
    void setDimensionId(String dimensionId);

    String getName();
    void setName(String name);

    int getWeight();
    void setWeight(int weight);

    List<CompletenessCheckItem> getCheckItems();
    void setCheckItems(List<CompletenessCheckItem> checkItems);

    boolean isEnabled();
    void setEnabled(boolean enabled);
}

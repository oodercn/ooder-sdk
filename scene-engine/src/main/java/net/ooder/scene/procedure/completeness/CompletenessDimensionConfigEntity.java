package net.ooder.scene.procedure.completeness;

import net.ooder.sdk.api.completeness.CompletenessDimensionConfig;
import net.ooder.sdk.api.completeness.CompletenessCheckItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 完善度维度配置实体实现
 *
 * @author SE SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
public class CompletenessDimensionConfigEntity implements CompletenessDimensionConfig {

    private static final long serialVersionUID = 1L;

    private String dimensionId;
    private String name;
    private int weight;
    private List<CompletenessCheckItem> checkItems = new ArrayList<>();
    private boolean enabled = true;

    public CompletenessDimensionConfigEntity() {
    }

    @Override
    public String getDimensionId() {
        return dimensionId;
    }

    @Override
    public void setDimensionId(String dimensionId) {
        this.dimensionId = dimensionId;
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
    public int getWeight() {
        return weight;
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public List<CompletenessCheckItem> getCheckItems() {
        return checkItems;
    }

    @Override
    public void setCheckItems(List<CompletenessCheckItem> checkItems) {
        this.checkItems = checkItems != null ? checkItems : new ArrayList<>();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

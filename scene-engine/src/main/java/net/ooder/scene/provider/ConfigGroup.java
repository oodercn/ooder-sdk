package net.ooder.scene.provider;

import java.util.List;

/**
 * 配置分组
 */
public class ConfigGroup {
    private String groupName;
    private String description;
    private int configCount;
    private long lastUpdated;
    private List<ConfigItem> items;

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getConfigCount() { return configCount; }
    public void setConfigCount(int configCount) { this.configCount = configCount; }
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    public List<ConfigItem> getItems() { return items; }
    public void setItems(List<ConfigItem> items) { this.items = items; }
}

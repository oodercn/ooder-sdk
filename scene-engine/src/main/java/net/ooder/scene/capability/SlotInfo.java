package net.ooder.scene.capability;

import java.util.ArrayList;
import java.util.List;

public class SlotInfo {

    private int offset;
    private String name;
    private String description;
    private List<ProviderInfo> providers = new ArrayList<>();
    private String fallback;

    public SlotInfo() {}

    public int getOffset() { return offset; }
    public void setOffset(int offset) { this.offset = offset; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ProviderInfo> getProviders() { return providers; }
    public void setProviders(List<ProviderInfo> providers) { this.providers = providers; }
    public String getFallback() { return fallback; }
    public void setFallback(String fallback) { this.fallback = fallback; }

    public ProviderInfo getProvider(String skillId) {
        return providers.stream()
            .filter(p -> p.getSkillId().equals(skillId))
            .findFirst()
            .orElse(null);
    }

    public ProviderInfo getFallbackProvider() {
        if (fallback != null) {
            return getProvider(fallback);
        }
        return providers.isEmpty() ? null : providers.get(0);
    }

    public ProviderInfo getConfigManager() {
        return providers.stream()
            .filter(ProviderInfo::isConfigManager)
            .findFirst()
            .orElse(getFallbackProvider());
    }
}

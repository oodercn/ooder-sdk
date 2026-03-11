package net.ooder.scene.capability;

import java.util.ArrayList;
import java.util.List;

public class CapabilityInfo {

    private int baseAddress;
    private String code;
    private String name;
    private SelectionMode selectionMode;
    private SwitchScope switchScope;
    private String fallbackProvider;
    private List<SlotInfo> slots = new ArrayList<>();

    public CapabilityInfo() {}

    public int getBaseAddress() { return baseAddress; }
    public void setBaseAddress(int baseAddress) { this.baseAddress = baseAddress; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SelectionMode getSelectionMode() { return selectionMode; }
    public void setSelectionMode(SelectionMode selectionMode) { this.selectionMode = selectionMode; }
    public SwitchScope getSwitchScope() { return switchScope; }
    public void setSwitchScope(SwitchScope switchScope) { this.switchScope = switchScope; }
    public String getFallbackProvider() { return fallbackProvider; }
    public void setFallbackProvider(String fallbackProvider) { this.fallbackProvider = fallbackProvider; }
    public List<SlotInfo> getSlots() { return slots; }
    public void setSlots(List<SlotInfo> slots) { this.slots = slots; }

    public SlotInfo getSlot(SegmentSlot slot) {
        return slots.stream()
            .filter(s -> s.getOffset() == slot.getOffset())
            .findFirst()
            .orElse(null);
    }

    public SlotInfo getPrimarySlot() {
        return getSlot(SegmentSlot.PRIMARY);
    }

    public int getAddress(SegmentSlot slot) {
        return baseAddress + slot.getOffset();
    }
}

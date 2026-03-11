package net.ooder.scene.capability;

public enum SegmentSlot {
    PRIMARY(0),
    STANDBY(1),
    CACHE(2),
    READONLY(3),
    ARCHIVE(4);

    private final int offset;

    SegmentSlot(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public static SegmentSlot fromOffset(int offset) {
        for (SegmentSlot slot : values()) {
            if (slot.offset == offset) {
                return slot;
            }
        }
        return null;
    }
}

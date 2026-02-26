package net.ooder.sdk.llm.common.enums;

public enum SyncMode {
    REALTIME("realtime", "实时同步"),
    SCHEDULED("scheduled", "定时同步"),
    ON_DEMAND("on_demand", "按需同步");

    private final String code;
    private final String description;

    SyncMode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SyncMode fromCode(String code) {
        for (SyncMode mode : values()) {
            if (mode.code.equals(code)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown sync mode: " + code);
    }
}

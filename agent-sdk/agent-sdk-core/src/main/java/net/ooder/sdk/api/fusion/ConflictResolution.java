package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

/**
 * Conflict Resolution Enum
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ConflictResolution {

    USE_ENTERPRISE("use_enterprise", "Use enterprise procedure value"),

    USE_SKILL("use_skill", "Use skill definition value"),

    MERGE("merge", "Merge both values"),

    CUSTOM("custom", "Use custom value"),

    SKIP("skip", "Skip this field");

    private final String code;
    private final String description;

    ConflictResolution(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ConflictResolution fromCode(String code) {
        if (code == null) {
            return MERGE;
        }
        for (ConflictResolution resolution : values()) {
            if (resolution.code.equals(code)) {
                return resolution;
            }
        }
        return MERGE;
    }
}

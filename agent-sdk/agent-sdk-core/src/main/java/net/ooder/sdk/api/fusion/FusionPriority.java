package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

/**
 * Fusion Priority Enum
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum FusionPriority {

    ENTERPRISE_FIRST("enterprise_first", "Enterprise procedure first"),

    SKILL_FIRST("skill_first", "Skill definition first"),

    MERGE("merge", "Merge both"),

    USER_DECIDE("user_decide", "User decides");

    private final String code;
    private final String description;

    FusionPriority(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FusionPriority fromCode(String code) {
        if (code == null) {
            return MERGE;
        }
        for (FusionPriority priority : values()) {
            if (priority.code.equals(code)) {
                return priority;
            }
        }
        return MERGE;
    }
}

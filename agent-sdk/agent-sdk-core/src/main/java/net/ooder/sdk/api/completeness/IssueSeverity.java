package net.ooder.sdk.api.completeness;

import net.ooder.sdk.api.PublicAPI;

/**
 * Issue Severity Enum
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum IssueSeverity {

    CRITICAL("critical", "Critical - Must fix"),

    WARNING("warning", "Warning - Should fix"),

    INFO("info", "Info - Optional fix");

    private final String code;
    private final String description;

    IssueSeverity(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static IssueSeverity fromCode(String code) {
        if (code == null) {
            return INFO;
        }
        for (IssueSeverity severity : values()) {
            if (severity.code.equals(code)) {
                return severity;
            }
        }
        return INFO;
    }
}

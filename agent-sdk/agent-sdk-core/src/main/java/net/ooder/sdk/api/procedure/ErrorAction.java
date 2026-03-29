package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

/**
 * Error Action Type
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ErrorAction {

    WARN("warn", "Warning"),

    BLOCK("block", "Block"),

    CORRECT("correct", "Auto correct"),

    ESCALATE("escalate", "Escalate");

    private final String code;
    private final String description;

    ErrorAction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ErrorAction fromCode(String code) {
        if (code == null) {
            return WARN;
        }
        for (ErrorAction action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        return WARN;
    }
}

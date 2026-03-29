package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

/**
 * Enterprise Procedure Status
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ProcedureStatus {

    DRAFT("draft", "Draft"),

    ACTIVE("active", "Active"),

    INACTIVE("inactive", "Inactive"),

    ARCHIVED("archived", "Archived"),

    DEPRECATED("deprecated", "Deprecated");

    private final String code;
    private final String description;

    ProcedureStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProcedureStatus fromCode(String code) {
        if (code == null) {
            return DRAFT;
        }
        for (ProcedureStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return DRAFT;
    }
}

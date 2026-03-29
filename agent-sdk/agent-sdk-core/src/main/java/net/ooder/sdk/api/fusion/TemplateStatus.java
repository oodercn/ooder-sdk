package net.ooder.sdk.api.fusion;

import net.ooder.sdk.api.PublicAPI;

/**
 * Template Status Enum
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum TemplateStatus {

    DRAFT("draft", "Draft"),

    ACTIVE("active", "Active"),

    INACTIVE("inactive", "Inactive"),

    ARCHIVED("archived", "Archived");

    private final String code;
    private final String description;

    TemplateStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TemplateStatus fromCode(String code) {
        if (code == null) {
            return DRAFT;
        }
        for (TemplateStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return DRAFT;
    }
}

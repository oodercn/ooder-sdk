package net.ooder.sdk.api.procedure;

import net.ooder.sdk.api.PublicAPI;

/**
 * Enterprise Procedure Source Type
 *
 * @author Agent SDK Team
 * @version 3.0.1
 * @since 3.0.1
 */
@PublicAPI
public enum ProcedureSource {

    MANUAL("manual", "Manual creation"),

    LLM_GENERATED("llm_generated", "LLM generated"),

    KNOWLEDGE_BASE("knowledge_base", "Extracted from knowledge base"),

    IMPORTED("imported", "External import"),

    TEMPLATE("template", "Created from template");

    private final String code;
    private final String description;

    ProcedureSource(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ProcedureSource fromCode(String code) {
        if (code == null) {
            return MANUAL;
        }
        for (ProcedureSource source : values()) {
            if (source.code.equals(code)) {
                return source;
            }
        }
        return MANUAL;
    }
}

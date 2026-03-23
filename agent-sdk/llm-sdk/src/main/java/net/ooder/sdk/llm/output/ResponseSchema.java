package net.ooder.sdk.llm.output;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 响应 Schema 定义
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSchema {

    private String type;
    private List<SchemaProperty> properties;
    private List<String> required;
    private String description;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemaProperty {
        private String name;
        private String type;
        private String description;
        private Object defaultValue;
        private List<String> enumValues;
        private List<SchemaProperty> nestedProperties;
    }

    public static ResponseSchema object(List<SchemaProperty> properties, List<String> required) {
        return ResponseSchema.builder()
                .type("object")
                .properties(properties)
                .required(required)
                .build();
    }

    public static ResponseSchema array(SchemaProperty items) {
        List<SchemaProperty> props = new ArrayList<>();
        props.add(items);
        return ResponseSchema.builder()
                .type("array")
                .properties(props)
                .build();
    }

    public static ResponseSchema string(String description) {
        return ResponseSchema.builder()
                .type("string")
                .description(description)
                .build();
    }

    public static ResponseSchema integer(String description) {
        return ResponseSchema.builder()
                .type("integer")
                .description(description)
                .build();
    }

    public static ResponseSchema bool(String description) {
        return ResponseSchema.builder()
                .type("boolean")
                .description(description)
                .build();
    }
}

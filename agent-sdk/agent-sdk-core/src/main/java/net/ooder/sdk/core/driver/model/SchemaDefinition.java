package net.ooder.sdk.core.driver.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Schema 定义
 */
public class SchemaDefinition {
    private String type;
    private String description;
    private Map<String, SchemaDefinition> properties = new HashMap<>();
    private Object defaultValue;
    private boolean required;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, SchemaDefinition> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SchemaDefinition> properties) {
        this.properties = properties != null ? properties : new HashMap<>();
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}

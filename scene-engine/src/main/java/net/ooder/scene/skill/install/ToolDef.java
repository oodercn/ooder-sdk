package net.ooder.scene.skill.install;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具定义
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class ToolDef {

    private String name;
    private String description;
    private String category;
    private Map<String, Object> parameters;
    private String handler;
    private String permission;
    private int timeout;
    private Map<String, Object> metadata;

    public ToolDef() {
        this.parameters = new HashMap<>();
        this.metadata = new HashMap<>();
        this.timeout = 30000;
    }

    public ToolDef(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters != null ? parameters : new HashMap<>();
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
}

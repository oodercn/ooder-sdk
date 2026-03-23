package net.ooder.scene.llm.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局上下文配置
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class GlobalContextConfig {

    private List<MenuItem> menuItems;
    private List<ToolDefinition> globalTools;
    private String systemBasePrompt;
    private Map<String, Object> globalVariables;

    public GlobalContextConfig() {
        this.menuItems = new ArrayList<>();
        this.globalTools = new ArrayList<>();
        this.globalVariables = new HashMap<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
    }

    public List<ToolDefinition> getGlobalTools() {
        return globalTools;
    }

    public void setGlobalTools(List<ToolDefinition> globalTools) {
        this.globalTools = globalTools != null ? globalTools : new ArrayList<>();
    }

    public String getSystemBasePrompt() {
        return systemBasePrompt;
    }

    public void setSystemBasePrompt(String systemBasePrompt) {
        this.systemBasePrompt = systemBasePrompt;
    }

    public Map<String, Object> getGlobalVariables() {
        return globalVariables;
    }

    public void setGlobalVariables(Map<String, Object> globalVariables) {
        this.globalVariables = globalVariables != null ? globalVariables : new HashMap<>();
    }

    /**
     * 菜单项
     */
    public static class MenuItem {
        private String id;
        private String name;
        private String path;
        private String icon;
        private String category;
        private List<String> keywords;
        private Map<String, Object> functionCall;

        public MenuItem() {
            this.keywords = new ArrayList<>();
            this.functionCall = new HashMap<>();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords != null ? keywords : new ArrayList<>();
        }

        public Map<String, Object> getFunctionCall() {
            return functionCall;
        }

        public void setFunctionCall(Map<String, Object> functionCall) {
            this.functionCall = functionCall != null ? functionCall : new HashMap<>();
        }
    }

    /**
     * 工具定义
     */
    public static class ToolDefinition {
        private String name;
        private String description;
        private Map<String, Object> parameters;

        public ToolDefinition() {
            this.parameters = new HashMap<>();
        }

        public ToolDefinition(String name, String description) {
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

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public void setParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : new HashMap<>();
        }
    }

    /**
     * Builder 模式
     */
    public static class Builder {
        private final GlobalContextConfig config;

        public Builder() {
            config = new GlobalContextConfig();
        }

        public Builder systemBasePrompt(String prompt) {
            config.setSystemBasePrompt(prompt);
            return this;
        }

        public Builder menuItem(MenuItem item) {
            config.getMenuItems().add(item);
            return this;
        }

        public Builder globalTool(ToolDefinition tool) {
            config.getGlobalTools().add(tool);
            return this;
        }

        public Builder globalVariable(String key, Object value) {
            config.getGlobalVariables().put(key, value);
            return this;
        }

        public GlobalContextConfig build() {
            return config;
        }
    }
}

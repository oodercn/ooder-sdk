package net.ooder.scene.ui;

import java.util.List;
import java.util.Map;

/**
 * Nexus UI 配置
 * 定义 UI Skill 的配置信息
 *
 * @author ooder
 * @since 2.3
 */
public class NexusUiConfig {
    
    /** Skill ID */
    private String skillId;
    
    /** UI 名称 */
    private String name;
    
    /** UI 描述 */
    private String description;
    
    /** UI 类型：menu, widget, page */
    private String type;
    
    /** 图标 */
    private String icon;
    
    /** 入口路径 */
    private String entryPath;
    
    /** 菜单配置 */
    private MenuConfig menu;
    
    /** 路由配置 */
    private List<RouteConfig> routes;
    
    /** 依赖的 JS/CSS */
    private List<String> scripts;
    private List<String> styles;
    
    /** 扩展属性 */
    private Map<String, Object> metadata;
    
    public NexusUiConfig() {}
    
    // Getters and Setters
    public String getSkillId() { return skillId; }
    public void setSkillId(String skillId) { this.skillId = skillId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getEntryPath() { return entryPath; }
    public void setEntryPath(String entryPath) { this.entryPath = entryPath; }
    
    public MenuConfig getMenu() { return menu; }
    public void setMenu(MenuConfig menu) { this.menu = menu; }
    
    public List<RouteConfig> getRoutes() { return routes; }
    public void setRoutes(List<RouteConfig> routes) { this.routes = routes; }
    
    public List<String> getScripts() { return scripts; }
    public void setScripts(List<String> scripts) { this.scripts = scripts; }
    
    public List<String> getStyles() { return styles; }
    public void setStyles(List<String> styles) { this.styles = styles; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}

package net.ooder.skills.api;

import java.util.List;
import java.util.Map;

/**
 * 菜单配置
 * 定义场景中的菜单项
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class MenuConfig {
    
    private String menuId;
    private String name;
    private String description;
    private String icon;
    private int order;
    private boolean visible;
    private List<MenuItemConfig> items;
    private Map<String, String> metadata;
    
    public String getMenuId() {
        return menuId;
    }
    
    public void setMenuId(String menuId) {
        this.menuId = menuId;
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
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public List<MenuItemConfig> getItems() {
        return items;
    }
    
    public void setItems(List<MenuItemConfig> items) {
        this.items = items;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    public static class MenuItemConfig {
        private String itemId;
        private String label;
        private String action;
        private String icon;
        private int order;
        private boolean enabled;
        private Map<String, String> metadata;
        
        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public int getOrder() { return order; }
        public void setOrder(int order) { this.order = order; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    }
}

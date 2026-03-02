package net.ooder.scene.ui;

import java.util.List;

/**
 * 菜单配置
 *
 * @author ooder
 * @since 2.3
 */
public class MenuConfig {
    
    /** 菜单ID */
    private String menuId;
    
    /** 菜单标题 */
    private String title;
    
    /** 父菜单ID */
    private String parentId;
    
    /** 菜单图标 */
    private String icon;
    
    /** 菜单路径 */
    private String path;
    
    /** 排序 */
    private int order;
    
    /** 是否显示 */
    private boolean visible;
    
    /** 权限 */
    private List<String> permissions;
    
    /** 子菜单 */
    private List<MenuConfig> children;
    
    public MenuConfig() {
        this.visible = true;
        this.order = 0;
    }
    
    // Getters and Setters
    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { this.permissions = permissions; }
    
    public List<MenuConfig> getChildren() { return children; }
    public void setChildren(List<MenuConfig> children) { this.children = children; }
}

package net.ooder.scene.ui;

/**
 * 路由配置
 *
 * @author ooder
 * @since 2.3
 */
public class RouteConfig {
    
    /** 路由路径 */
    private String path;
    
    /** 组件名称 */
    private String component;
    
    /** 路由标题 */
    private String title;
    
    /** 是否精确匹配 */
    private boolean exact;
    
    /** 是否需要认证 */
    private boolean requireAuth;
    
    public RouteConfig() {
        this.exact = false;
        this.requireAuth = true;
    }
    
    public RouteConfig(String path, String component) {
        this();
        this.path = path;
        this.component = component;
    }
    
    // Getters and Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public boolean isExact() { return exact; }
    public void setExact(boolean exact) { this.exact = exact; }
    
    public boolean isRequireAuth() { return requireAuth; }
    public void setRequireAuth(boolean requireAuth) { this.requireAuth = requireAuth; }
}

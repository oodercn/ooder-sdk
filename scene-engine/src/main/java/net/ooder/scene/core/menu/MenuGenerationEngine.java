package net.ooder.scene.core.menu;

import java.util.List;
import java.util.Map;

/**
 * 菜单生成引擎接口
 *
 * <p>根据场景配置和用户角色动态生成菜单。</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface MenuGenerationEngine {

    /**
     * 生成场景菜单
     *
     * @param sceneId 场景ID
     * @param userId 用户ID
     * @param role 用户角色
     * @return 菜单列表
     */
    List<MenuItem> generateSceneMenu(String sceneId, String userId, String role);

    /**
     * 生成用户菜单
     *
     * @param userId 用户ID
     * @return 用户菜单
     */
    UserMenu generateUserMenu(String userId);

    /**
     * 刷新菜单缓存
     *
     * @param sceneId 场景ID
     */
    void refreshMenuCache(String sceneId);

    /**
     * 清除菜单缓存
     *
     * @param sceneId 场景ID
     */
    void clearMenuCache(String sceneId);

    /**
     * 注册菜单提供者
     *
     * @param provider 菜单提供者
     */
    void registerMenuProvider(MenuProvider provider);

    /**
     * 注销菜单提供者
     *
     * @param providerId 提供者ID
     */
    void unregisterMenuProvider(String providerId);

    /**
     * 获取菜单配置
     *
     * @param sceneId 场景ID
     * @return 菜单配置
     */
    MenuConfig getMenuConfig(String sceneId);

    /**
     * 更新菜单配置
     *
     * @param sceneId 场景ID
     * @param config 菜单配置
     */
    void updateMenuConfig(String sceneId, MenuConfig config);

    /**
     * 菜单项
     */
    class MenuItem {
        private String id;
        private String name;
        private String icon;
        private String path;
        private String component;
        private String permission;
        private int sort;
        private boolean visible;
        private boolean enabled;
        private List<MenuItem> children;
        private Map<String, Object> extra;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getComponent() { return component; }
        public void setComponent(String component) { this.component = component; }
        public String getPermission() { return permission; }
        public void setPermission(String permission) { this.permission = permission; }
        public int getSort() { return sort; }
        public void setSort(int sort) { this.sort = sort; }
        public boolean isVisible() { return visible; }
        public void setVisible(boolean visible) { this.visible = visible; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        public List<MenuItem> getChildren() { return children; }
        public void setChildren(List<MenuItem> children) { this.children = children; }
        public Map<String, Object> getExtra() { return extra; }
        public void setExtra(Map<String, Object> extra) { this.extra = extra; }
    }

    /**
     * 用户菜单
     */
    class UserMenu {
        private String userId;
        private List<MenuItem> menus;
        private List<String> permissions;
        private long generatedTime;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public List<MenuItem> getMenus() { return menus; }
        public void setMenus(List<MenuItem> menus) { this.menus = menus; }
        public List<String> getPermissions() { return permissions; }
        public void setPermissions(List<String> permissions) { this.permissions = permissions; }
        public long getGeneratedTime() { return generatedTime; }
        public void setGeneratedTime(long generatedTime) { this.generatedTime = generatedTime; }
    }

    /**
     * 菜单提供者
     */
    interface MenuProvider {
        String getProviderId();
        int getPriority();
        List<MenuItem> provide(String sceneId, String userId, String role);
    }

    /**
     * 菜单配置
     */
    class MenuConfig {
        private String sceneId;
        private boolean dynamic;
        private boolean cacheEnabled;
        private long cacheTtl;
        private List<MenuRule> rules;

        public String getSceneId() { return sceneId; }
        public void setSceneId(String sceneId) { this.sceneId = sceneId; }
        public boolean isDynamic() { return dynamic; }
        public void setDynamic(boolean dynamic) { this.dynamic = dynamic; }
        public boolean isCacheEnabled() { return cacheEnabled; }
        public void setCacheEnabled(boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
        public long getCacheTtl() { return cacheTtl; }
        public void setCacheTtl(long cacheTtl) { this.cacheTtl = cacheTtl; }
        public List<MenuRule> getRules() { return rules; }
        public void setRules(List<MenuRule> rules) { this.rules = rules; }
    }

    /**
     * 菜单规则
     */
    class MenuRule {
        private String ruleId;
        private String name;
        private String condition;
        private List<String> includeMenus;
        private List<String> excludeMenus;
        private int priority;

        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public List<String> getIncludeMenus() { return includeMenus; }
        public void setIncludeMenus(List<String> includeMenus) { this.includeMenus = includeMenus; }
        public List<String> getExcludeMenus() { return excludeMenus; }
        public void setExcludeMenus(List<String> excludeMenus) { this.excludeMenus = excludeMenus; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
    }
}

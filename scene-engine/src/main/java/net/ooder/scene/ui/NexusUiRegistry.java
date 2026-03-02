package net.ooder.scene.ui;

import java.util.List;
import java.util.Optional;

/**
 * Nexus UI 注册表接口
 * 管理 UI Skill 的注册和查询
 *
 * @author ooder
 * @since 2.3
 */
public interface NexusUiRegistry {
    
    /**
     * 注册 UI
     * @param config UI 配置
     */
    void register(NexusUiConfig config);
    
    /**
     * 注销 UI
     * @param skillId Skill ID
     */
    void unregister(String skillId);
    
    /**
     * 获取 UI 配置
     * @param skillId Skill ID
     * @return UI 配置
     */
    Optional<NexusUiConfig> get(String skillId);
    
    /**
     * 列出所有已注册的 UI
     * @return UI 配置列表
     */
    List<NexusUiConfig> listAll();
    
    /**
     * 按类型列出 UI
     * @param type UI 类型
     * @return UI 配置列表
     */
    List<NexusUiConfig> listByType(String type);
    
    /**
     * 检查是否已注册
     * @param skillId Skill ID
     * @return 是否已注册
     */
    boolean isRegistered(String skillId);
    
    /**
     * 获取所有菜单配置
     * @return 菜单配置列表
     */
    List<MenuConfig> getAllMenus();
    
    /**
     * 获取所有路由配置
     * @return 路由配置列表
     */
    List<RouteConfig> getAllRoutes();
}

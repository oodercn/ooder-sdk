package net.ooder.scene.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Nexus UI 注册表实现
 *
 * @author ooder
 * @since 2.3
 */
@Component
public class NexusUiRegistryImpl implements NexusUiRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(NexusUiRegistryImpl.class);
    
    /** UI 配置存储 */
    private final Map<String, NexusUiConfig> uiRegistry = new ConcurrentHashMap<>();
    
    /** 菜单注册表 */
    private final MenuRegistry menuRegistry;
    
    public NexusUiRegistryImpl() {
        this.menuRegistry = new MenuRegistry();
    }
    
    @PostConstruct
    public void init() {
        log.info("NexusUiRegistry initialized");
    }
    
    @Override
    public void register(NexusUiConfig config) {
        if (config == null || config.getSkillId() == null) {
            log.warn("Cannot register null UI config");
            return;
        }
        
        String skillId = config.getSkillId();
        
        // 注册 UI
        uiRegistry.put(skillId, config);
        log.info("Registered UI: {} ({})", skillId, config.getName());
        
        // 注册菜单
        if (config.getMenu() != null) {
            menuRegistry.register(config.getMenu());
            log.info("Registered menu for UI: {}", skillId);
        }
    }
    
    @Override
    public void unregister(String skillId) {
        NexusUiConfig config = uiRegistry.remove(skillId);
        if (config != null) {
            log.info("Unregistered UI: {}", skillId);
            
            // 注销菜单
            if (config.getMenu() != null) {
                menuRegistry.unregister(config.getMenu().getMenuId());
            }
        }
    }
    
    @Override
    public Optional<NexusUiConfig> get(String skillId) {
        return Optional.ofNullable(uiRegistry.get(skillId));
    }
    
    @Override
    public List<NexusUiConfig> listAll() {
        return new ArrayList<>(uiRegistry.values());
    }
    
    @Override
    public List<NexusUiConfig> listByType(String type) {
        return uiRegistry.values().stream()
                .filter(ui -> type.equals(ui.getType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isRegistered(String skillId) {
        return uiRegistry.containsKey(skillId);
    }
    
    @Override
    public List<MenuConfig> getAllMenus() {
        return menuRegistry.getAllMenus();
    }
    
    @Override
    public List<RouteConfig> getAllRoutes() {
        return uiRegistry.values().stream()
                .flatMap(ui -> ui.getRoutes() != null ? ui.getRoutes().stream() : null)
                .collect(Collectors.toList());
    }
    
    /**
     * 菜单注册表内部类
     */
    private static class MenuRegistry {
        private final Map<String, MenuConfig> menus = new ConcurrentHashMap<>();
        
        void register(MenuConfig menu) {
            if (menu != null && menu.getMenuId() != null) {
                menus.put(menu.getMenuId(), menu);
            }
        }
        
        void unregister(String menuId) {
            menus.remove(menuId);
        }
        
        List<MenuConfig> getAllMenus() {
            return new ArrayList<>(menus.values());
        }
    }
}

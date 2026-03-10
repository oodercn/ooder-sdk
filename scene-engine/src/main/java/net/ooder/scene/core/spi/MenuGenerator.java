package net.ooder.scene.core.spi;

import net.ooder.scene.core.template.SceneTemplate;
import net.ooder.scene.ui.MenuConfig;

import java.util.List;

/**
 * 菜单生成器接口
 * 
 * <p>扩展点：用于根据模板和角色生成菜单</p>
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface MenuGenerator {
    
    /**
     * 生成菜单
     * 
     * @param template 场景模板
     * @param roleId 角色ID
     * @return 菜单配置列表
     */
    List<MenuConfig> generate(SceneTemplate template, String roleId);
    
    /**
     * 检查是否支持该模板
     * 
     * @param template 场景模板
     * @return true 如果支持
     */
    boolean supports(SceneTemplate template);
    
    /**
     * 获取生成器名称
     */
    String getName();
}

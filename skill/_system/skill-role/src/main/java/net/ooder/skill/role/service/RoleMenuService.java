package net.ooder.skill.role.service;

import java.util.List;
import net.ooder.skill.role.dto.MenuItemDTO;

public interface RoleMenuService {
    
    List<MenuItemDTO> getMenusByRole(String roleId);
    
    void setMenusForRole(String roleId, List<MenuItemDTO> menus);
    
    MenuItemDTO addMenuToRole(String roleId, MenuItemDTO menu);
    
    boolean removeMenuFromRole(String roleId, String menuId);
}

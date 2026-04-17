package net.ooder.skill.role.service.impl;

import net.ooder.skill.role.dto.MenuItemDTO;
import net.ooder.skill.role.service.RoleMenuService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    private final Map<String, List<MenuItemDTO>> roleMenus = new HashMap<>();

    @Override
    public List<MenuItemDTO> getMenusByRole(String roleId) {
        return roleMenus.getOrDefault(roleId, new ArrayList<>());
    }

    @Override
    public void setMenusForRole(String roleId, List<MenuItemDTO> menus) {
        roleMenus.put(roleId, menus);
    }

    @Override
    public MenuItemDTO addMenuToRole(String roleId, MenuItemDTO menu) {
        List<MenuItemDTO> menus = roleMenus.computeIfAbsent(roleId, k -> new ArrayList<>());
        menus.add(menu);
        return menu;
    }

    @Override
    public boolean removeMenuFromRole(String roleId, String menuId) {
        List<MenuItemDTO> menus = roleMenus.get(roleId);
        if (menus != null) {
            return menus.removeIf(m -> menuId.equals(m.getId()));
        }
        return false;
    }
}

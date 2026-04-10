package net.ooder.skill.common.controller;

import net.ooder.skill.common.model.UserSession;
import net.ooder.skill.common.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/pages")
public class PageController {

    @Autowired
    private AuthService authService;

    @GetMapping("/{page}")
    public String renderPage(@PathVariable String page, Model model, HttpServletRequest request) {
        UserSession user = authService.getCurrentUser(request);
        
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("userId", user.getUserId());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("name", user.getName());
            model.addAttribute("role", user.getRole());
            model.addAttribute("roleType", user.getRoleType());
            model.addAttribute("permissions", user.getPermissions());
        } else {
            model.addAttribute("user", null);
            model.addAttribute("userId", "");
            model.addAttribute("username", "");
            model.addAttribute("name", "游客");
            model.addAttribute("role", "");
            model.addAttribute("roleType", "");
            model.addAttribute("permissions", java.util.Collections.emptyList());
        }

        model.addAttribute("theme", "light");
        model.addAttribute("version", "2.3.1");
        model.addAttribute("timestamp", System.currentTimeMillis());
        
        return "pages/" + page;
    }

    @GetMapping("/role/{role}")
    public String renderRolePage(@PathVariable String role, Model model, HttpServletRequest request) {
        UserSession user = authService.getCurrentUser(request);
        
        if (user == null) {
            return "redirect:/login";
        }

        if (!role.equals(user.getRoleType())) {
            return "redirect:/pages/dashboard";
        }

        model.addAttribute("user", user);
        model.addAttribute("theme", "light");
        model.addAttribute("version", "2.3.1");
        
        String roleName = getRoleName(role);
        model.addAttribute("roleName", roleName);
        
        return "pages/role-" + role;
    }

    private String getRoleName(String roleType) {
        Map<String, String> roleNames = new HashMap<>();
        roleNames.put("installer", "系统安装者");
        roleNames.put("admin", "系统管理员");
        roleNames.put("leader", "主导者");
        roleNames.put("collaborator", "协作者");
        
        return roleNames.getOrDefault(roleType, "用户");
    }
}

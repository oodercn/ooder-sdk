package net.ooder.skill.common.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import net.ooder.skill.common.model.ResultModel;
import net.ooder.skill.common.model.UserSession;
import net.ooder.skill.common.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    @Autowired(required = false)
    private AuthService authService;

    private JSONObject menuConfig;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("config/menu-config.json");
            if (resource.exists()) {
                String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
                menuConfig = JSON.parseObject(content);
                log.info("Menu config loaded successfully");
            }
        } catch (IOException e) {
            log.warn("Failed to load menu config: {}", e.getMessage());
            menuConfig = new JSONObject();
        }
    }

    @GetMapping
    public ResultModel<List<Map<String, Object>>> getCurrentUserMenu(
            @RequestParam(required = false) String role,
            jakarta.servlet.http.HttpServletRequest request) {
        
        String roleType = role;
        
        if (authService != null) {
            UserSession user = authService.getCurrentUser(request);
            if (roleType == null && user != null) {
                roleType = user.getRoleType();
            }
        }
        
        if (roleType == null) {
            roleType = "collaborator";
        }

        List<Map<String, Object>> menus = buildMenuForRole(roleType);
        return ResultModel.success(menus);
    }
    
    @GetMapping("/skills")
    public ResultModel<List<Map<String, Object>>> getInstalledSkillMenus() {
        List<Map<String, Object>> skillMenus = loadInstalledSkillMenus();
        return ResultModel.success(skillMenus);
    }

    @GetMapping("/config")
    public ResultModel<Object> getMenuConfig() {
        return ResultModel.success(menuConfig);
    }

    @GetMapping("/roles")
    public ResultModel<List<Map<String, Object>>> getAvailableRoles() {
        List<Map<String, Object>> roles = Arrays.asList(
            createRole("installer", "系统安装者", "安装技能包，初始化系统", 
                Arrays.asList("skill:install", "skill:view", "system:init")),
            createRole("admin", "系统管理员", "发现场景技能，配置分发", 
                Arrays.asList("capability:discover", "capability:install", "capability:distribute", 
                    "scene:create", "scene:manage", "user:assign")),
            createRole("leader", "主导者", "激活场景，获取KEY，执行入网", 
                Arrays.asList("scene:activate", "scene:manage", "key:generate", 
                    "participant:manage", "task:assign")),
            createRole("collaborator", "协作者", "参与业务流转，执行任务", 
                Arrays.asList("task:view", "task:execute", "task:submit", "scene:view"))
        );
        return ResultModel.success(roles);
    }

    private List<Map<String, Object>> buildMenuForRole(String roleType) {
        List<Map<String, Object>> menus = new ArrayList<>();

        List<Map<String, Object>> skillMenus = loadInstalledSkillMenus();
        menus.addAll(skillMenus);
        
        if (skillMenus.isEmpty()) {
            if ("installer".equals(roleType)) {
                menus.add(createMenuItem("install", "安装向导", "ri-install-line", "/setup/", null));
            }
        }

        return menus;
    }
    
    private List<Map<String, Object>> loadInstalledSkillMenus() {
        List<Map<String, Object>> menus = new ArrayList<>();
        
        File registryFile = new File("data/installed-skills/registry.properties");
        if (!registryFile.exists()) {
            return menus;
        }
        
        try {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(registryFile)) {
                props.load(fis);
            }
            
            Set<String> skillIds = new HashSet<>();
            for (String key : props.stringPropertyNames()) {
                int dotIndex = key.indexOf('.');
                if (dotIndex > 0) {
                    skillIds.add(key.substring(0, dotIndex));
                }
            }
            
            for (String skillId : skillIds) {
                if (skillId == null) continue;
                String path = props.getProperty(skillId + ".path");
                if (path != null) {
                    Map<String, Object> skillMenu = loadSkillMenu(skillId, path);
                    if (skillMenu != null) {
                        menus.add(skillMenu);
                    }
                }
            }
            
        } catch (Exception e) {
            log.warn("Failed to load installed skill menus: {}", e.getMessage());
        }
        
        return menus;
    }
    
    private Map<String, Object> loadSkillMenu(String skillId, String skillPath) {
        File skillDir = new File(skillPath).getAbsoluteFile();
        File skillYaml = new File(skillDir, "skill.yaml");
        if (!skillYaml.exists()) {
            skillYaml = new File(skillDir, "src/main/resources/skill.yaml");
        }
        
        String skillName = skillId;
        String skillIcon = "ri-puzzle-line";
        
        if (skillYaml.exists()) {
            try {
                String content = StreamUtils.copyToString(new FileInputStream(skillYaml), StandardCharsets.UTF_8);
                
                java.util.regex.Matcher nameMatcher = java.util.regex.Pattern.compile("name:\\s*(.+)")
                    .matcher(content);
                if (nameMatcher.find()) {
                    skillName = nameMatcher.group(1).trim();
                }
            } catch (Exception e) {
                log.debug("Failed to parse skill.yaml: {}", e.getMessage());
            }
        }
        
        List<Map<String, Object>> menuItems = loadSkillSubMenuConfig(skillDir.getAbsolutePath(), skillId);
        log.info("Loaded {} menu items for skill: {}", menuItems.size(), skillId);
        
        if (!menuItems.isEmpty()) {
            Map<String, Object> firstItem = menuItems.get(0);
            
            if (firstItem.containsKey("children") && firstItem.get("children") instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) firstItem.get("children");
                
                Map<String, Object> menu = new LinkedHashMap<>();
                menu.put("id", firstItem.get("id"));
                menu.put("name", firstItem.get("name"));
                menu.put("icon", firstItem.get("icon"));
                menu.put("url", children.isEmpty() ? firstItem.get("url") : children.get(0).get("url"));
                menu.put("children", children);
                
                log.info("Created skill menu '{}' with {} children, first url: {}", 
                    firstItem.get("name"), children.size(), menu.get("url"));
                return menu;
            } else {
                Map<String, Object> menu = new LinkedHashMap<>();
                menu.put("id", firstItem.get("id"));
                menu.put("name", firstItem.get("name"));
                menu.put("icon", firstItem.get("icon"));
                menu.put("url", firstItem.get("url"));
                if (menuItems.size() > 1) {
                    menu.put("children", menuItems);
                }
                log.info("Created skill menu with url: {}", firstItem.get("url"));
                return menu;
            }
        }
        
        log.warn("No submenus found for skill: {}, skipping menu entry", skillId);
        return null;
    }
    
    private List<Map<String, Object>> loadSkillSubMenuConfig(String skillPath, String skillId) {
        List<Map<String, Object>> subMenus = new ArrayList<>();
        
        log.info("Loading menu config for skill: {} from path: {}", skillId, skillPath);
        
        File menuConfigFile = new File(skillPath, "src/main/resources/static/console/menu-config.json");
        log.info("Checking menu config file: {} exists: {}", menuConfigFile.getAbsolutePath(), menuConfigFile.exists());
        
        if (menuConfigFile.exists()) {
            try {
                String content = StreamUtils.copyToString(new FileInputStream(menuConfigFile), StandardCharsets.UTF_8);
                log.info("Loaded menu config from: {}", menuConfigFile.getAbsolutePath());
                JSONObject config = JSON.parseObject(content);
                List<Map> items = config != null && config.containsKey("menu") 
                    ? config.getList("menu", Map.class) 
                    : JSON.parseArray(content, Map.class);
                
                if (items != null) {
                    log.info("Found {} menu items", items.size());
                    for (Map item : items) {
                        Map<String, Object> subMenu = convertMenuItem(item, skillId);
                        subMenus.add(subMenu);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to load skill menu config: {}", e.getMessage(), e);
            }
        } else {
            log.warn("Menu config not found at: {}", menuConfigFile.getAbsolutePath());
        }
        
        return subMenus;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> convertMenuItem(Map item, String skillId) {
        Map<String, Object> menu = new LinkedHashMap<>();
        menu.put("id", item.get("id"));
        menu.put("name", item.get("name"));
        menu.put("icon", item.get("icon"));
        
        String url = (String) item.get("url");
        if (url != null && !url.startsWith("/console/skills/")) {
            url = "/console/skills/" + skillId + url.substring("/console".length());
        }
        menu.put("url", url);
        
        if (item.containsKey("children")) {
            List<Map> children = (List<Map>) item.get("children");
            if (children != null && !children.isEmpty()) {
                List<Map<String, Object>> convertedChildren = new ArrayList<>();
                for (Map child : children) {
                    convertedChildren.add(convertMenuItem(child, skillId));
                }
                menu.put("children", convertedChildren);
            }
        }
        
        return menu;
    }

    private Map<String, Object> createMenuItem(String id, String name, String icon, String url, List<Map<String, Object>> children) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("name", name);
        item.put("icon", icon);
        item.put("url", url);
        if (children != null && !children.isEmpty()) {
            item.put("children", children);
        }
        return item;
    }

    private Map<String, Object> createRole(String id, String name, String description, List<String> permissions) {
        Map<String, Object> role = new LinkedHashMap<>();
        role.put("id", id);
        role.put("name", name);
        role.put("description", description);
        role.put("permissions", permissions);
        return role;
    }
}

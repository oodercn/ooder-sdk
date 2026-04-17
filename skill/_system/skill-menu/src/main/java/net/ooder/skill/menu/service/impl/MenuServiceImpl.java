package net.ooder.skill.menu.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import net.ooder.skill.menu.dto.MenuItemDTO;
import net.ooder.skill.menu.service.MenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger log = LoggerFactory.getLogger(MenuServiceImpl.class);
    
    private static final String CONFIG_DIR = "data/config";
    private static final String MENUS_FILE = "menus.json";
    
    private final Map<String, MenuItemDTO> menuRegistry = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        loadMenus();
        log.info("[MenuServiceImpl] Initialized with {} menus", menuRegistry.size());
    }
    
    private void loadMenus() {
        Path menuPath = Paths.get(CONFIG_DIR, MENUS_FILE);
        
        if (Files.exists(menuPath)) {
            try {
                String content = new String(Files.readAllBytes(menuPath), StandardCharsets.UTF_8);
                List<MenuItemDTO> menus = JSON.parseArray(content, MenuItemDTO.class);
                if (menus != null) {
                    for (MenuItemDTO menu : menus) {
                        if (menu.getId() != null) {
                            menuRegistry.put(menu.getId(), menu);
                        }
                    }
                }
                log.info("Loaded {} menus from {}", menus != null ? menus.size() : 0, menuPath);
            } catch (Exception e) {
                log.error("Failed to load menus: {}", e.getMessage());
                initDefaultMenus();
            }
        } else {
            initDefaultMenus();
            saveMenus();
        }
    }
    
    private void initDefaultMenus() {
        int sort = 0;

        MenuItemDTO dashboard = createMenuItem(++sort, "menu-dashboard", null,
                "工作台", "ri-home-line", "/console/pages/workbench.html", 0);

        // 能力管理菜单
        MenuItemDTO capabilityRoot = createMenuItem(++sort, "menu-capability", null,
                "能力管理", "ri-apps-line", null, 0);
        MenuItemDTO myCapabilities = createMenuItem(++sort, "menu-my-capabilities", "menu-capability",
                "我的能力", "ri-user-star-line", "/console/pages/my-capabilities.html", 1);
        MenuItemDTO capabilityDiscovery = createMenuItem(++sort, "menu-capability-discovery", "menu-capability",
                "能力发现", "ri-store-2-line", "/console/pages/capability-discovery.html", 1);
        MenuItemDTO installScene = createMenuItem(++sort, "menu-install-scene", "menu-capability",
                "场景安装", "ri-install-line", "/console/pages/install-scene.html", 1);

        MenuItemDTO agentRoot = createMenuItem(++sort, "menu-agent", null,
                "智能体管理", "ri-robot-line", null, 0);
        MenuItemDTO agentChat = createMenuItem(++sort, "menu-agent-chat", "menu-agent",
                "对话管理", "ri-chat-3-line", "/console/pages/agent/chat.html", 1);
        MenuItemDTO agentTodo = createMenuItem(++sort, "menu-agent-todo", "menu-agent",
                "任务管理", "ri-checkbox-circle-line", "/console/pages/agent/todos.html", 1);
        MenuItemDTO agentContext = createMenuItem(++sort, "menu-agent-context", "menu-agent",
                "会话上下文", "ri-file-list-3-line", "/console/pages/agent/context.html", 1);

        MenuItemDTO ragRoot = createMenuItem(++sort, "menu-rag", null,
                "知识库管理", "ri-book-open-line", null, 0);
        MenuItemDTO ragKnowledge = createMenuItem(++sort, "menu-rag-knowledge", "menu-rag",
                "知识库列表", "ri-database-2-line", "/console/pages/rag/knowledge.html", 1);
        MenuItemDTO knowledgeBase = createMenuItem(++sort, "menu-knowledge-base", "menu-rag",
                "知识库管理", "ri-book-open-line", "/console/pages/knowledge-base.html", 1);
        MenuItemDTO ragClassify = createMenuItem(++sort, "menu-rag-classify", "menu-rag",
                "分类管理", "ri-tags-line", "/console/pages/rag/classify.html", 1);
        MenuItemDTO ragConfig = createMenuItem(++sort, "menu-rag-config", "menu-rag",
                "RAG配置", "ri-settings-4-line", "/console/pages/rag/config.html", 1);

        MenuItemDTO tenantRoot = createMenuItem(++sort, "menu-tenant", null,
                "租户管理", "ri-building-line", null, 0);
        MenuItemDTO tenantList = createMenuItem(++sort, "menu-tenant-list", "menu-tenant",
                "租户列表", "ri-team-line", "/console/pages/tenant/list.html", 1);
        MenuItemDTO tenantMember = createMenuItem(++sort, "menu-tenant-member", "menu-tenant",
                "成员管理", "ri-user-settings-line", "/console/pages/tenant/members.html", 1);
        MenuItemDTO tenantQuota = createMenuItem(++sort, "menu-tenant-quota", "menu-tenant",
                "配额管理", "ri-bar-chart-box-line", "/console/pages/tenant/quota.html", 1);

        MenuItemDTO imRoot = createMenuItem(++sort, "menu-im", null,
                "消息中心", "ri-message-3-line", null, 0);
        MenuItemDTO imMqtt = createMenuItem(++sort, "menu-im-mqtt", "menu-im",
                "MQTT通道", "ri-wifi-line", "/console/pages/im/mqtt.html", 1);
        MenuItemDTO imWebhook = createMenuItem(++sort, "menu-im-webhook", "menu-im",
                "Webhook配置", "ri-link", "/console/pages/im/webhook.html", 1);
        MenuItemDTO imMessage = createMenuItem(++sort, "menu-im-message", "menu-im",
                "消息记录", "ri-mail-send-line", "/console/pages/im/messages.html", 1);

        MenuItemDTO auditRoot = createMenuItem(++sort, "menu-audit", null,
                "审计日志", "ri-shield-check-line", null, 0);
        MenuItemDTO auditLog = createMenuItem(++sort, "menu-audit-log", "menu-audit",
                "操作日志", "ri-file-text-line", "/console/pages/audit/logs.html", 1);
        MenuItemDTO auditAlert = createMenuItem(++sort, "menu-audit-alert", "menu-audit",
                "异常告警", "ri-alarm-warning-line", "/console/pages/audit/alerts.html", 1);

        MenuItemDTO sceneRoot = createMenuItem(++sort, "menu-scene", null,
                "场景引擎", "ri-layout-grid-line", null, 0);
        MenuItemDTO sceneManage = createMenuItem(++sort, "menu-scene-manage", "menu-scene",
                "场景管理", "ri-apps-line", "/console/pages/scene/manage.html", 1);
        MenuItemDTO sceneTodo = createMenuItem(++sort, "menu-scene-todo", "menu-scene",
                "Todo任务", "ri-task-line", "/console/pages/scene/todos.html", 1);

        MenuItemDTO workflowRoot = createMenuItem(++sort, "menu-workflow", null,
                "工作流程", "ri-flow-chart", null, 0);
        MenuItemDTO wfDefList = createMenuItem(++sort, "menu-wf-def-list", "menu-workflow",
                "流程定义", "ri-file-list-3-line", "/console/pages/workflow/definitions.html", 1);
        MenuItemDTO wfWaited = createMenuItem(++sort, "menu-wf-waited", "menu-workflow",
                "我的待办", "ri-time-line", "/console/pages/workflow/waited.html", 1);
        MenuItemDTO wfMyWork = createMenuItem(++sort, "menu-wf-mywork", "menu-workflow",
                "在办任务", "ri-checkbox-circle-line", "/console/pages/workflow/mywork.html", 1);
        MenuItemDTO wfCompleted = createMenuItem(++sort, "menu-wf-completed", "menu-workflow",
                "已办事项", "ri-check-double-line", "/console/pages/workflow/completed.html", 1);
        MenuItemDTO wfDraft = createMenuItem(++sort, "menu-wf-draft", "menu-workflow",
                "草稿箱", "ri-draft-line", "/console/pages/workflow/draft.html", 1);
        MenuItemDTO wfMonitor = createMenuItem(++sort, "menu-wf-monitor", "menu-workflow",
                "流程监控", "ri-eye-line", "/console/pages/workflow/monitor.html", 1);
        MenuItemDTO wfArchive = createMenuItem(++sort, "menu-wf-archive", "menu-workflow",
                "归档文件", "ri-archive-line", "/console/pages/workflow/archive.html", 1);

        MenuItemDTO settingsRoot = createMenuItem(++sort, "menu-settings", null,
                "系统设置", "ri-settings-3-line", null, 0);
        MenuItemDTO settingsMenu = createMenuItem(++sort, "menu-settings-menu", "menu-settings",
                "菜单管理", "ri-menu-line", "/console/pages/settings/menus.html", 1);
        MenuItemDTO settingsRole = createMenuItem(++sort, "menu-settings-role", "menu-settings",
                "角色权限", "ri-shield-user-line", "/console/pages/settings/roles.html", 1);
        MenuItemDTO settingsBasic = createMenuItem(++sort, "menu-settings-basic", "menu-settings",
                "基础配置", "ri-tools-line", "/console/pages/settings/basic.html", 1);
    }

    private MenuItemDTO createMenuItem(int sort, String id, String parentId,
                                       String name, String icon, String url, int level) {
        MenuItemDTO item = new MenuItemDTO();
        item.setId(id);
        item.setParentId(parentId);
        item.setName(name);
        item.setIcon(icon);
        item.setUrl(url);
        item.setSort(sort);
        item.setVisible(true);
        item.setActive(true);
        item.setLevel(level);
        menuRegistry.put(item.getId(), item);
        return item;
    }
    
    private void saveMenus() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            
            Path menuPath = Paths.get(CONFIG_DIR, MENUS_FILE);
            List<MenuItemDTO> menus = new ArrayList<>(menuRegistry.values());
            String content = JSON.toJSONString(menus, JSONWriter.Feature.PrettyFormat);
            Files.write(menuPath, content.getBytes(StandardCharsets.UTF_8));
            
            log.info("Saved {} menus to {}", menus.size(), menuPath);
        } catch (Exception e) {
            log.error("Failed to save menus: {}", e.getMessage());
        }
    }
    
    @Override
    public MenuItemDTO create(MenuItemDTO menu) {
        if (menu == null || menu.getId() == null) {
            return null;
        }
        
        if (menu.getSort() == 0) {
            menu.setSort(menuRegistry.size() + 1);
        }
        if (menu.getParentId() == null || menu.getParentId().isEmpty()) {
            menu.setLevel(0);
        }
        
        menuRegistry.put(menu.getId(), menu);
        saveMenus();
        
        log.info("[create] Created menu: {} - {}", menu.getId(), menu.getName());
        return menu;
    }
    
    @Override
    public MenuItemDTO update(MenuItemDTO menu) {
        if (menu == null || menu.getId() == null) {
            return null;
        }
        
        MenuItemDTO existing = menuRegistry.get(menu.getId());
        if (existing == null) {
            return null;
        }
        
        if (menu.getName() != null) existing.setName(menu.getName());
        if (menu.getIcon() != null) existing.setIcon(menu.getIcon());
        if (menu.getUrl() != null) existing.setUrl(menu.getUrl());
        if (menu.getSort() > 0) existing.setSort(menu.getSort());
        existing.setVisible(menu.isVisible());
        existing.setActive(menu.isActive());
        
        saveMenus();
        
        log.info("[update] Updated menu: {}", menu.getId());
        return existing;
    }
    
    @Override
    public void delete(String menuId) {
        if (menuId == null) {
            return;
        }
        
        menuRegistry.remove(menuId);
        
        for (MenuItemDTO menu : menuRegistry.values()) {
            if (menuId.equals(menu.getParentId())) {
                menu.setParentId(null);
            }
        }
        
        saveMenus();
        log.info("[delete] Deleted menu: {}", menuId);
    }
    
    @Override
    public MenuItemDTO findById(String menuId) {
        return menuRegistry.get(menuId);
    }
    
    @Override
    public List<MenuItemDTO> findAll() {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public List<MenuItemDTO> getMenuTree() {
        List<MenuItemDTO> allMenus = menuRegistry.values().stream()
            .sorted(Comparator.comparingInt(MenuItemDTO::getSort))
            .collect(Collectors.toList());
        
        Map<String, MenuItemDTO> menuMap = new LinkedHashMap<>();
        for (MenuItemDTO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
            menu.setChildren(new ArrayList<>());
        }
        
        List<MenuItemDTO> rootMenus = new ArrayList<>();
        for (MenuItemDTO menu : allMenus) {
            String parentId = menu.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootMenus.add(menu);
            } else {
                MenuItemDTO parent = menuMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(menu);
                } else {
                    rootMenus.add(menu);
                }
            }
        }
        
        return rootMenus;
    }
    
    @Override
    public void move(String menuId, String newParentId, int newSort) {
        MenuItemDTO menu = menuRegistry.get(menuId);
        if (menu == null) {
            return;
        }
        
        menu.setParentId(newParentId);
        menu.setSort(newSort);
        
        if (newParentId != null && !newParentId.isEmpty()) {
            MenuItemDTO parent = menuRegistry.get(newParentId);
            if (parent != null) {
                menu.setLevel(parent.getLevel() + 1);
            }
        } else {
            menu.setLevel(0);
        }
        
        saveMenus();
        log.info("[move] Moved menu {} to parentId: {}, sort: {}", menuId, newParentId, newSort);
    }
    
    @Override
    public List<MenuItemDTO> findByRoleId(String roleId) {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public List<MenuItemDTO> findByUserId(String userId) {
        return new ArrayList<>(menuRegistry.values());
    }
    
    @Override
    public void setRoleMenus(String roleId, List<String> menuIds) {
        log.info("[setRoleMenus] Set {} menus for role {}", menuIds != null ? menuIds.size() : 0, roleId);
    }
}

package net.ooder.scene.skill.tool.builtin;

import net.ooder.scene.llm.context.MultiLevelContextManager;
import net.ooder.scene.skill.tool.Tool;
import net.ooder.scene.skill.tool.ToolContext;
import net.ooder.scene.skill.tool.ToolResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 页面导航工具
 *
 * <p>用于 LLM Function Calling，支持通过自然语言导航到指定页面</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public class NavigateToPageTool implements Tool {

    private static final Logger log = LoggerFactory.getLogger(NavigateToPageTool.class);

    private static final String ID = "navigate_to_page";
    private static final String NAME = "navigate_to_page";
    private static final String DESCRIPTION = "导航到指定页面。当用户请求打开某个功能模块时使用，如：打开知识资料库、打开能力发现等。";

    private final MultiLevelContextManager contextManager;
    private final Map<String, String> pageNames;

    public NavigateToPageTool(MultiLevelContextManager contextManager) {
        this.contextManager = contextManager;
        this.pageNames = new HashMap<>();
        initPageNames();
    }

    private void initPageNames() {
        pageNames.put("knowledge-center", "知识资料库");
        pageNames.put("knowledge-base", "知识库管理");
        pageNames.put("capability-discovery", "能力发现");
        pageNames.put("capability-install", "能力安装");
        pageNames.put("capability-management", "能力管理");
        pageNames.put("scene-group-detail", "场景组详情");
        pageNames.put("scene-management", "场景管理");
        pageNames.put("llm-config", "LLM 配置");
        pageNames.put("llm-monitor", "LLM 监控");
        pageNames.put("llm-knowledge-config", "LLM 知识配置");
        pageNames.put("user-management", "用户管理");
        pageNames.put("role-management", "角色管理");
        pageNames.put("org-management", "组织管理");
        pageNames.put("my-profile", "个人中心");
        pageNames.put("my-scenes", "我的场景");
        pageNames.put("my-capabilities", "我的能力");
        pageNames.put("my-history", "历史记录");
        pageNames.put("my-todos", "待办事项");
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getCategory() {
        return "navigation";
    }

    @Override
    public List<String> getTags() {
        return Arrays.asList("navigation", "page", "ui");
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");

        Map<String, Object> properties = new LinkedHashMap<>();

        Map<String, Object> pageProp = new LinkedHashMap<>();
        pageProp.put("type", "string");
        pageProp.put("description", "目标页面ID或名称。可以是页面ID（如 knowledge-center）或页面名称（如 知识资料库）");
        
        List<String> enumValues = new ArrayList<>(pageNames.keySet());
        enumValues.addAll(pageNames.values());
        pageProp.put("enum", enumValues);
        
        properties.put("page", pageProp);

        Map<String, Object> reasonProp = new LinkedHashMap<>();
        reasonProp.put("type", "string");
        reasonProp.put("description", "导航原因（可选）");
        properties.put("reason", reasonProp);

        schema.put("properties", properties);
        schema.put("required", Collections.singletonList("page"));

        return schema;
    }

    @Override
    public ToolResult execute(Map<String, Object> parameters, ToolContext context) {
        String pageInput = (String) parameters.get("page");
        String reason = (String) parameters.get("reason");

        if (pageInput == null || pageInput.isEmpty()) {
            return ToolResult.error("Missing required parameter: page");
        }

        String pageId = resolvePageId(pageInput);
        if (pageId == null) {
            return ToolResult.error("Unknown page: " + pageInput + 
                ". Available pages: " + String.join(", ", pageNames.keySet()));
        }

        try {
            contextManager.reloadContextForPage(pageId);
            contextManager.setCurrentPageId(pageId);

            String pageName = pageNames.getOrDefault(pageId, pageId);
            String message = "已导航到页面: " + pageName;
            if (reason != null && !reason.isEmpty()) {
                message += " (原因: " + reason + ")";
            }

            log.info("Navigated to page: {} ({})", pageId, pageName);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("pageId", pageId);
            result.put("pageName", pageName);
            result.put("message", message);
            result.put("timestamp", System.currentTimeMillis());

            return ToolResult.success(result);

        } catch (Exception e) {
            log.error("Failed to navigate to page: {}", pageId, e);
            return ToolResult.error("Failed to navigate to page: " + e.getMessage());
        }
    }

    private String resolvePageId(String input) {
        if (pageNames.containsKey(input)) {
            return input;
        }
        
        String lowerInput = input.toLowerCase().trim();
        
        for (Map.Entry<String, String> entry : pageNames.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(input) ||
                entry.getValue().toLowerCase().contains(lowerInput)) {
                return entry.getKey();
            }
        }
        
        for (Map.Entry<String, String> entry : pageNames.entrySet()) {
            if (entry.getKey().contains(lowerInput) || 
                entry.getValue().toLowerCase().contains(lowerInput)) {
                return entry.getKey();
            }
        }

        return null;
    }

    public void addPageMapping(String pageId, String pageName) {
        pageNames.put(pageId, pageName);
    }

    public void removePageMapping(String pageId) {
        pageNames.remove(pageId);
    }
}

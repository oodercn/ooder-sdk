package net.ooder.scene.skill.tool.impl;

import net.ooder.scene.skill.tool.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 工具注册表实现
 *
 * <p>管理所有可被 LLM Function Calling 调用的工具。</p>
 *
 * <p>架构层次：应用层 - 智能增强</p>
 *
 * <h3>Skills Context 动态注入</h3>
 * <p>支持从 SkillActivationContext 动态注入工具，当工具过多时自动清理不常用工具。</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ToolRegistryImpl implements ToolRegistry {
    
    private static final Logger log = LoggerFactory.getLogger(ToolRegistryImpl.class);
    
    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    private final Map<String, List<Tool>> categoryIndex = new ConcurrentHashMap<>();
    private final Map<String, List<Tool>> tagIndex = new ConcurrentHashMap<>();
    private final Map<String, String> toolSkillMapping = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> toolUsageStats = new ConcurrentHashMap<>();
    
    private int maxTools = DEFAULT_MAX_TOOLS;
    
    @Override
    public void register(Tool tool) {
        if (tool == null || tool.getName() == null) {
            throw new IllegalArgumentException("Tool and tool name cannot be null");
        }
        
        String name = tool.getName();
        if (tools.containsKey(name)) {
            log.warn("Tool already registered, overwriting: {}", name);
        }
        
        tools.put(name, tool);
        toolUsageStats.computeIfAbsent(name, k -> new AtomicInteger(0));
        
        String category = tool.getCategory();
        categoryIndex.computeIfAbsent(category, k -> new ArrayList<>()).add(tool);
        
        for (String tag : tool.getTags()) {
            tagIndex.computeIfAbsent(tag, k -> new ArrayList<>()).add(tool);
        }
        
        log.info("Tool registered: {} [category={}, tags={}]", 
                name, category, tool.getTags());
    }
    
    @Override
    public void registerAll(List<Tool> toolList) {
        for (Tool tool : toolList) {
            register(tool);
        }
        log.info("Registered {} tools", toolList.size());
    }
    
    @Override
    public void unregister(String name) {
        Tool removed = tools.remove(name);
        if (removed != null) {
            String category = removed.getCategory();
            List<Tool> categoryTools = categoryIndex.get(category);
            if (categoryTools != null) {
                categoryTools.removeIf(t -> t.getName().equals(name));
            }
            
            for (String tag : removed.getTags()) {
                List<Tool> tagTools = tagIndex.get(tag);
                if (tagTools != null) {
                    tagTools.removeIf(t -> t.getName().equals(name));
                }
            }
            
            log.info("Tool unregistered: {}", name);
        }
    }
    
    @Override
    public Optional<Tool> getTool(String name) {
        return Optional.ofNullable(tools.get(name));
    }
    
    @Override
    public boolean hasTool(String name) {
        return tools.containsKey(name);
    }
    
    @Override
    public List<Tool> listAll() {
        return new ArrayList<>(tools.values());
    }
    
    @Override
    public List<Tool> listByCategory(String category) {
        return new ArrayList<>(categoryIndex.getOrDefault(category, Collections.emptyList()));
    }
    
    @Override
    public List<Tool> listByTag(String tag) {
        return new ArrayList<>(tagIndex.getOrDefault(tag, Collections.emptyList()));
    }
    
    @Override
    public List<Map<String, Object>> getToolDefinitions() {
        return tools.values().stream()
                .map(this::toToolDefinition)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getToolDefinitions(List<String> toolNames) {
        List<Map<String, Object>> definitions = new ArrayList<>();
        for (String name : toolNames) {
            Tool tool = tools.get(name);
            if (tool != null) {
                definitions.add(toToolDefinition(tool));
            }
        }
        return definitions;
    }
    
    @Override
    public void clear() {
        tools.clear();
        categoryIndex.clear();
        tagIndex.clear();
        toolSkillMapping.clear();
        toolUsageStats.clear();
        log.info("All tools cleared");
    }
    
    private Map<String, Object> toToolDefinition(Tool tool) {
        Map<String, Object> definition = new LinkedHashMap<>();
        definition.put("type", "function");
        
        Map<String, Object> function = new LinkedHashMap<>();
        function.put("name", tool.getName());
        function.put("description", tool.getDescription());
        function.put("parameters", tool.getParametersSchema());
        
        definition.put("function", function);
        
        return definition;
    }

    @Override
    public void injectFromSkill(String skillId, List<Tool> skillTools) {
        if (skillId == null || skillTools == null || skillTools.isEmpty()) {
            return;
        }

        int currentCount = tools.size();
        int newCount = skillTools.size();
        
        if (currentCount + newCount > maxTools) {
            int removed = cleanupUnusedTools(maxTools - newCount);
            log.info("Auto cleanup: removed {} unused tools before injecting from skill {}", removed, skillId);
        }

        for (Tool tool : skillTools) {
            String toolName = tool.getName();
            tools.put(toolName, tool);
            toolSkillMapping.put(toolName, skillId);
            toolUsageStats.computeIfAbsent(toolName, k -> new AtomicInteger(0));
            
            String category = tool.getCategory();
            categoryIndex.computeIfAbsent(category, k -> new ArrayList<>()).add(tool);
            
            for (String tag : tool.getTags()) {
                tagIndex.computeIfAbsent(tag, k -> new ArrayList<>()).add(tool);
            }
        }

        log.info("Injected {} tools from skill {} (total: {})", skillTools.size(), skillId, tools.size());
    }

    @Override
    public void clearSkillTools(String skillId) {
        if (skillId == null) {
            return;
        }

        List<String> toRemove = new ArrayList<>();
        for (Map.Entry<String, String> entry : toolSkillMapping.entrySet()) {
            if (skillId.equals(entry.getValue())) {
                toRemove.add(entry.getKey());
            }
        }

        for (String toolName : toRemove) {
            unregister(toolName);
            toolSkillMapping.remove(toolName);
            toolUsageStats.remove(toolName);
        }

        log.info("Cleared {} tools from skill {}", toRemove.size(), skillId);
    }

    @Override
    public void recordToolUsage(String toolName) {
        if (toolName == null) {
            return;
        }
        toolUsageStats.computeIfAbsent(toolName, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public int getToolUsageCount(String toolName) {
        if (toolName == null) {
            return 0;
        }
        AtomicInteger count = toolUsageStats.get(toolName);
        return count != null ? count.get() : 0;
    }

    @Override
    public int cleanupUnusedTools(int targetMax) {
        if (targetMax <= 0 || tools.size() <= targetMax) {
            return 0;
        }

        List<Map.Entry<String, AtomicInteger>> sortedTools = toolUsageStats.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> e.getValue().get()))
                .collect(Collectors.toList());

        int toRemove = tools.size() - targetMax;
        int removed = 0;

        for (Map.Entry<String, AtomicInteger> entry : sortedTools) {
            if (removed >= toRemove) {
                break;
            }

            String toolName = entry.getKey();
            int usageCount = entry.getValue().get();
            
            if (usageCount == 0 && tools.containsKey(toolName)) {
                unregister(toolName);
                toolSkillMapping.remove(toolName);
                toolUsageStats.remove(toolName);
                removed++;
                log.debug("Removed unused tool: {}", toolName);
            }
        }

        if (removed < toRemove) {
            for (Map.Entry<String, AtomicInteger> entry : sortedTools) {
                if (removed >= toRemove) {
                    break;
                }

                String toolName = entry.getKey();
                if (tools.containsKey(toolName)) {
                    unregister(toolName);
                    toolSkillMapping.remove(toolName);
                    toolUsageStats.remove(toolName);
                    removed++;
                    log.debug("Removed low-usage tool: {} (usage: {})", toolName, entry.getValue().get());
                }
            }
        }

        log.info("Cleanup completed: removed {} tools (current: {}, target: {})", removed, tools.size(), targetMax);
        return removed;
    }

    @Override
    public int getToolCount() {
        return tools.size();
    }

    @Override
    public void setMaxTools(int maxTools) {
        this.maxTools = maxTools > 0 ? maxTools : DEFAULT_MAX_TOOLS;
    }

    @Override
    public int getMaxTools() {
        return maxTools;
    }
}

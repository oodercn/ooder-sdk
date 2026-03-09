package net.ooder.sdk.llm.scene;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import net.ooder.sdk.llm.tool.ToolDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolContext {

    /**
     * 可用工具列表
     */
    @Builder.Default
    private List<ToolDefinition> availableTools = new ArrayList<>();

    /**
     * 工具执行历史
     */
    @Builder.Default
    private List<ToolExecutionRecord> executionHistory = new ArrayList<>();

    /**
     * 工具配置
     */
    @Builder.Default
    private Map<String, Object> toolConfig = new HashMap<>();

    /**
     * 工具执行记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ToolExecutionRecord {
        private String toolId;
        private String invocationId;
        private long timestamp;
        private Object input;
        private Object output;
        private boolean success;
        private String errorMessage;
    }

    /**
     * 注册工具
     */
    public void registerTool(ToolDefinition tool) {
        availableTools.add(tool);
    }

    /**
     * 获取工具
     */
    public ToolDefinition getTool(String toolId) {
        return availableTools.stream()
                .filter(t -> t.getToolId().equals(toolId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 记录执行
     */
    public void recordExecution(ToolExecutionRecord record) {
        executionHistory.add(record);
    }
}

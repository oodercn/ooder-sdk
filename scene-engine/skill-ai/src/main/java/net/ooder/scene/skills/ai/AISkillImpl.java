package net.ooder.scene.skills.ai;

import net.ooder.scene.core.*;
import net.ooder.scene.skill.LlmProvider;
import net.ooder.scene.skill.SkillService;
import net.ooder.scene.skill.SkillRuntimeStatus;

import java.util.*;
import java.util.concurrent.*;

/**
 * AI Skill 实现
 */
public class AISkillImpl implements AISkill, SkillService {

    private final String skillId = "ai";
    private final String skillName = "AI Capability Skill";
    private final String skillVersion = "0.8.0";

    private LlmProvider llmProvider;
    private final Map<String, MCPClientHolder> mcpClients = new ConcurrentHashMap<>();
    private final Map<String, WorkflowDefinition> workflows = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void setLlmProvider(LlmProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

    @Override
    public String getSkillId() {
        return skillId;
    }

    @Override
    public String getSkillName() {
        return skillName;
    }

    @Override
    public String getSkillVersion() {
        return skillVersion;
    }

    @Override
    public List<String> getCapabilities() {
        return Arrays.asList(
            "aigc.text-generation",
            "aigc.chat",
            "mcp.client-management",
            "mcp.tool-call",
            "workflow.execution",
            "workflow.management"
        );
    }

    @Override
    public CompletableFuture<AIGCResult> generateText(String modelId, String prompt, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            if (llmProvider == null) {
                return AIGCResult.failure("LLM provider not configured");
            }

            try {
                long startTime = System.currentTimeMillis();
                // 使用 complete 方法进行文本补全
                String result = llmProvider.complete(modelId, prompt, params);
                long executionTime = System.currentTimeMillis() - startTime;

                AIGCResult aigcResult = AIGCResult.success(
                    UUID.randomUUID().toString(),
                    result,
                    modelId
                );
                aigcResult.setExecutionTime(executionTime);
                return aigcResult;
            } catch (Exception e) {
                return AIGCResult.failure("Generation failed: " + e.getMessage());
            }
        }, executor);
    }

    @Override
    public CompletableFuture<AIGCResult> chat(String modelId, List<Message> messages, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            if (llmProvider == null) {
                return AIGCResult.failure("LLM provider not configured");
            }

            try {
                long startTime = System.currentTimeMillis();
                // 转换消息格式并调用 chat 方法
                Map<String, Object> result = llmProvider.chat(modelId, convertMessages(messages), params);
                long executionTime = System.currentTimeMillis() - startTime;

                String content = (String) result.get("content");
                AIGCResult aigcResult = AIGCResult.success(
                    UUID.randomUUID().toString(),
                    content,
                    modelId
                );
                aigcResult.setExecutionTime(executionTime);
                return aigcResult;
            } catch (Exception e) {
                return AIGCResult.failure("Chat failed: " + e.getMessage());
            }
        }, executor);
    }

    @Override
    public CompletableFuture<List<ModelInfo>> getAvailableModels() {
        return CompletableFuture.supplyAsync(() -> {
            if (llmProvider == null) {
                return Collections.emptyList();
            }

            List<ModelInfo> models = new ArrayList<>();
            // 从 LlmProvider 获取支持的模型列表
            List<String> modelIds = llmProvider.getSupportedModels();
            for (String modelId : modelIds) {
                ModelInfo info = new ModelInfo();
                info.setModelId(modelId);
                info.setName(modelId);
                info.setAvailable(true);
                models.add(info);
            }
            return models;
        }, executor);
    }

    @Override
    public CompletableFuture<MCPResult> registerMCPClient(String clientId, MCPConfig config) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MCPClientHolder holder = new MCPClientHolder(clientId, config);
                mcpClients.put(clientId, holder);
                return MCPResult.success(UUID.randomUUID().toString(), "Client registered successfully");
            } catch (Exception e) {
                return MCPResult.failure("Registration failed: " + e.getMessage());
            }
        }, executor);
    }

    @Override
    public CompletableFuture<MCPResult> callMCPTool(String clientId, String toolName, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            MCPClientHolder client = mcpClients.get(clientId);
            if (client == null) {
                return MCPResult.failure("Client not found: " + clientId);
            }

            try {
                // 调用 MCP 工具
                Map<String, Object> result = client.callTool(toolName, params);
                return MCPResult.success(UUID.randomUUID().toString(), result);
            } catch (Exception e) {
                return MCPResult.failure("Tool call failed: " + e.getMessage());
            }
        }, executor);
    }

    @Override
    public List<MCPClientInfo> getMCPClients() {
        List<MCPClientInfo> infos = new ArrayList<>();
        for (Map.Entry<String, MCPClientHolder> entry : mcpClients.entrySet()) {
            MCPClientInfo info = new MCPClientInfo();
            info.setClientId(entry.getKey());
            info.setName(entry.getValue().getConfig().getName());
            info.setTransportType(entry.getValue().getConfig().getTransportType());
            info.setConnected(entry.getValue().isConnected());
            infos.add(info);
        }
        return infos;
    }

    @Override
    public CompletableFuture<WorkflowResult> executeWorkflow(String workflowId, Map<String, Object> params) {
        return CompletableFuture.supplyAsync(() -> {
            WorkflowDefinition workflow = workflows.get(workflowId);
            if (workflow == null) {
                return WorkflowResult.failure(
                    UUID.randomUUID().toString(),
                    workflowId,
                    "Workflow not found: " + workflowId
                );
            }

            String executionId = UUID.randomUUID().toString();
            long startTime = System.currentTimeMillis();

            try {
                WorkflowEngine engine = new WorkflowEngine(this, workflow);
                Map<String, Object> output = engine.execute(params);
                long executionTime = System.currentTimeMillis() - startTime;

                WorkflowResult result = WorkflowResult.success(executionId, workflowId, output);
                result.setExecutionTime(executionTime);
                result.setCompletedSteps(engine.getCompletedSteps());
                result.setTotalSteps(engine.getTotalSteps());
                return result;
            } catch (Exception e) {
                return WorkflowResult.failure(executionId, workflowId, e.getMessage());
            }
        }, executor);
    }

    @Override
    public CompletableFuture<WorkflowResult> registerWorkflow(WorkflowDefinition workflow) {
        return CompletableFuture.supplyAsync(() -> {
            if (workflow == null || workflow.getWorkflowId() == null) {
                return WorkflowResult.failure(
                    UUID.randomUUID().toString(),
                    null,
                    "Workflow or workflowId cannot be null"
                );
            }

            workflows.put(workflow.getWorkflowId(), workflow);
            return WorkflowResult.success(
                UUID.randomUUID().toString(),
                workflow.getWorkflowId(),
                Collections.singletonMap("status", "registered")
            );
        }, executor);
    }

    @Override
    public List<WorkflowDefinition> getWorkflows() {
        return new ArrayList<>(workflows.values());
    }

    @Override
    public Object invoke(String capability, Map<String, Object> params) {
        switch (capability) {
            case "aigc.text-generation":
                String modelId = (String) params.get("modelId");
                String prompt = (String) params.get("prompt");
                return generateText(modelId, prompt, params);
            case "aigc.chat":
                // 处理 chat 调用
                return CompletableFuture.completedFuture(AIGCResult.failure("Not implemented"));
            case "mcp.tool-call":
                String clientId = (String) params.get("clientId");
                String toolName = (String) params.get("toolName");
                return callMCPTool(clientId, toolName, params);
            case "workflow.execution":
                String workflowId = (String) params.get("workflowId");
                return executeWorkflow(workflowId, params);
            default:
                return CompletableFuture.completedFuture(
                    AIGCResult.failure("Unknown capability: " + capability)
                );
        }
    }

    // ==================== SkillService 接口实现 ====================

    @Override
    public SkillInfo findSkill(String skillId) {
        if (this.skillId.equals(skillId)) {
            SkillInfo info = new SkillInfo();
            info.setSkillId(this.skillId);
            info.setName(this.skillName);
            info.setVersion(this.skillVersion);
            return info;
        }
        return null;
    }

    @Override
    public List<SkillInfo> searchSkills(SkillQuery query) {
        return Collections.singletonList(findSkill(skillId));
    }

    @Override
    public List<SkillInfo> discoverSkills(SkillQuery query) {
        return searchSkills(query);
    }

    @Override
    public SkillInstallResult installSkill(String userId, String skillId) {
        return installSkill(userId, skillId, Collections.emptyMap());
    }

    @Override
    public SkillInstallResult installSkill(String userId, String skillId, Map<String, Object> config) {
        SkillInstallResult result = new SkillInstallResult();
        result.setSuccess(true);
        result.setSkillId(skillId);
        return result;
    }

    @Override
    public SkillInstallProgress getInstallProgress(String installId) {
        SkillInstallProgress progress = new SkillInstallProgress();
        progress.setInstallId(installId);
        progress.setSkillId(skillId);
        progress.setStage("COMPLETED");
        progress.setProgress(100);
        progress.setMessage("Installation completed");
        return progress;
    }

    @Override
    public SkillUninstallResult uninstallSkill(String userId, String skillId) {
        SkillUninstallResult result = new SkillUninstallResult();
        result.setSuccess(true);
        return result;
    }

    @Override
    public List<InstalledSkillInfo> listInstalledSkills(String userId) {
        InstalledSkillInfo info = new InstalledSkillInfo();
        info.setSkillId(skillId);
        return Collections.singletonList(info);
    }

    @Override
    public List<CapabilityInfo> listCapabilities(String skillId) {
        if (this.skillId.equals(skillId)) {
            List<CapabilityInfo> capabilities = new ArrayList<>();
            for (String cap : getCapabilities()) {
                CapabilityInfo info = new CapabilityInfo(
                    cap,           // capId
                    cap,           // name
                    "1.0.0",       // version
                    "AI",          // category
                    "AI capability", // description
                    skillId,       // skillId
                    "invoke"       // method
                );
                capabilities.add(info);
            }
            return capabilities;
        }
        return Collections.emptyList();
    }

    @Override
    public Object invokeCapability(String userId, String skillId, String capability, Map<String, Object> params) {
        return invoke(capability, params);
    }

    @Override
    public SkillRuntimeStatus getRuntimeStatus(String skillId) {
        SkillRuntimeStatus status = new SkillRuntimeStatus();
        status.setSkillId(skillId);
        status.setStatus("RUNNING");
        status.setStartTime(System.currentTimeMillis());
        return status;
    }

    @Override
    public void startSkill(String userId, String skillId) {
        // 启动逻辑
    }

    @Override
    public void stopSkill(String userId, String skillId) {
        // 停止逻辑
    }

    @Override
    public void restartSkill(String userId, String skillId) {
        stopSkill(userId, skillId);
        startSkill(userId, skillId);
    }

    // ==================== 私有方法 ====================

    private List<Map<String, Object>> convertMessages(List<Message> messages) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Message msg : messages) {
            Map<String, Object> map = new HashMap<>();
            map.put("role", msg.getRole().name().toLowerCase());
            map.put("content", msg.getContent());
            if (msg.getToolCallId() != null) {
                map.put("tool_call_id", msg.getToolCallId());
            }
            result.add(map);
        }
        return result;
    }

    /**
     * MCP 客户端持有者
     */
    private static class MCPClientHolder {
        private final String clientId;
        private final MCPConfig config;
        private boolean connected;

        MCPClientHolder(String clientId, MCPConfig config) {
            this.clientId = clientId;
            this.config = config;
            this.connected = true;
        }

        MCPConfig getConfig() {
            return config;
        }

        boolean isConnected() {
            return connected;
        }

        Map<String, Object> callTool(String toolName, Map<String, Object> params) {
            // 实际实现需要调用 MCP 协议
            Map<String, Object> result = new HashMap<>();
            result.put("tool", toolName);
            result.put("status", "success");
            return result;
        }
    }

    /**
     * 工作流执行引擎
     */
    private static class WorkflowEngine {
        private final AISkillImpl skill;
        private final WorkflowDefinition workflow;
        private int completedSteps = 0;
        private int totalSteps = 0;

        WorkflowEngine(AISkillImpl skill, WorkflowDefinition workflow) {
            this.skill = skill;
            this.workflow = workflow;
            this.totalSteps = workflow.getSteps() != null ? workflow.getSteps().size() : 0;
        }

        Map<String, Object> execute(Map<String, Object> params) {
            Map<String, Object> context = new HashMap<>(params);
            if (workflow.getSteps() == null) {
                return context;
            }

            for (WorkflowStep step : workflow.getSteps()) {
                executeStep(step, context);
                completedSteps++;
            }

            return context;
        }

        private void executeStep(WorkflowStep step, Map<String, Object> context) {
            // 根据步骤类型执行不同逻辑
            switch (step.getType()) {
                case AI_GENERATION:
                    // 执行 AI 生成
                    break;
                case MCP_CALL:
                    // 调用 MCP 工具
                    break;
                case CONDITION:
                    // 条件判断
                    break;
                case PARALLEL:
                    // 并行执行
                    break;
                case WAIT:
                    // 等待
                    break;
                case CUSTOM:
                    // 自定义逻辑
                    break;
            }
        }

        int getCompletedSteps() {
            return completedSteps;
        }

        int getTotalSteps() {
            return totalSteps;
        }
    }
}

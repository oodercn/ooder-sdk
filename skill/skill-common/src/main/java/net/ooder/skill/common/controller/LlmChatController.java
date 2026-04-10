package net.ooder.skill.common.controller;

import net.ooder.skill.common.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmChatController {

    private static final Logger log = LoggerFactory.getLogger(LlmChatController.class);
    
    @Autowired
    private ChatSessionController chatSessionController;

    @PostMapping("/chat")
    public ResultModel<Map<String, Object>> chat(@RequestBody Map<String, Object> request) {
        String message = (String) request.getOrDefault("message", "");
        String provider = (String) request.getOrDefault("provider", "mock");
        String model = (String) request.getOrDefault("model", "default");
        String sessionId = (String) request.get("sessionId");
        String skillContext = (String) request.get("skillContext");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> history = (List<Map<String, String>>) request.get("history");
        
        log.info("Chat API called with provider: {}, model: {}, sessionId: {}", provider, model, sessionId);
        
        String responseContent = generateResponse(message, history, skillContext);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", responseContent);
        response.put("model", model);
        response.put("provider", provider);
        response.put("sessionId", sessionId);
        
        if (responseContent.contains("```script")) {
            response.put("hasScript", true);
            response.put("script", extractScript(responseContent));
        }
        
        return ResultModel.success(response);
    }

    @PostMapping("/chat/stream")
    public ResultModel<Map<String, Object>> chatStream(@RequestBody Map<String, Object> request) {
        return chat(request);
    }
    
    @PostMapping("/function-call")
    public ResultModel<Map<String, Object>> functionCall(@RequestBody Map<String, Object> request) {
        String functionName = (String) request.getOrDefault("functionName", "");
        @SuppressWarnings("unchecked")
        Map<String, Object> params = (Map<String, Object>) request.getOrDefault("params", new HashMap<>());
        
        log.info("Function call: {} with params: {}", functionName, params);
        
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("functionName", functionName);
        result.put("success", true);
        result.put("result", executeFunction(functionName, params));
        
        return ResultModel.success(result);
    }

    private String generateResponse(String message, List<Map<String, String>> history, String skillContext) {
        if (message == null) message = "";
        
        StringBuilder contextBuilder = new StringBuilder();
        
        if (skillContext != null && !skillContext.isEmpty()) {
            contextBuilder.append("[当前页面上下文]\n").append(skillContext).append("\n\n");
        }
        
        if (history != null && !history.isEmpty()) {
            contextBuilder.append("[对话历史]\n");
            int start = Math.max(0, history.size() - 6);
            for (int i = start; i < history.size(); i++) {
                Map<String, String> msg = history.get(i);
                contextBuilder.append(msg.get("role")).append(": ")
                    .append(msg.get("content")).append("\n");
            }
            contextBuilder.append("\n");
        }
        
        String context = contextBuilder.toString();
        
        if (message.contains("配置") || message.contains("设置")) {
            return buildConfigResponse(message, skillContext);
        } else if (message.contains("分析")) {
            return buildAnalysisResponse(message, skillContext);
        } else if (message.contains("代码") || message.contains("生成")) {
            return buildCodeResponse(message, skillContext);
        } else if (message.contains("执行") || message.contains("运行")) {
            return buildScriptResponse(message);
        } else if (message.contains("帮助") || message.contains("使用")) {
            return buildHelpResponse();
        } else {
            return buildDefaultResponse(message, skillContext);
        }
    }
    
    private String buildConfigResponse(String message, String skillContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("我可以帮助您进行配置。\n\n");
        
        if (skillContext != null && !skillContext.isEmpty()) {
            sb.append("根据当前页面上下文，以下是可以配置的内容：\n\n");
            sb.append("```script\n");
            sb.append("// 获取当前页面配置\n");
            sb.append("const config = window.__OODER_PAGE_CONFIG__ || {};\n");
            sb.append("console.log('当前配置:', config);\n");
            sb.append("```\n\n");
        } else {
            sb.append("请告诉我您想要配置的具体内容，例如：\n");
            sb.append("- 场景配置\n");
            sb.append("- 能力绑定\n");
            sb.append("- 参与者管理\n");
        }
        
        return sb.toString();
    }
    
    private String buildAnalysisResponse(String message, String skillContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("我可以帮助您分析数据。\n\n");
        
        if (skillContext != null) {
            sb.append("根据当前页面上下文分析结果：\n\n");
            sb.append("```script\n");
            sb.append("// 分析页面数据\n");
            sb.append("const data = window.__OODER_PAGE_DATA__ || {};\n");
            sb.append("console.log('页面数据:', data);\n");
            sb.append("return { analyzed: true, dataPoints: Object.keys(data).length };\n");
            sb.append("```\n");
        }
        
        return sb.toString();
    }
    
    private String buildCodeResponse(String message, String skillContext) {
        return "我可以帮助您生成代码。\n\n" +
            "```java\n" +
            "// 示例代码\n" +
            "public class Example {\n" +
            "    public void execute() {\n" +
            "        System.out.println(\"Hello, Ooder!\");\n" +
            "    }\n" +
            "}\n" +
            "```\n\n" +
            "如果您需要针对当前页面的代码，请告诉我具体需求。";
    }
    
    private String buildScriptResponse(String message) {
        return "我可以生成可执行的脚本。\n\n" +
            "```script\n" +
            "// 示例：获取页面信息\n" +
            "const title = document.title;\n" +
            "const url = window.location.href;\n" +
            "console.log('页面标题:', title);\n" +
            "console.log('页面URL:', url);\n" +
            "\n" +
            "// 返回结果\n" +
            "return { title, url };\n" +
            "```\n\n" +
            "脚本将在您的浏览器中安全执行。";
    }
    
    private String buildHelpResponse() {
        return "## Ooder 智能助手使用指南\n\n" +
            "### 功能列表\n" +
            "1. **自动配置** - 根据当前页面内容提供配置建议\n" +
            "2. **数据分析** - 分析当前页面的数据\n" +
            "3. **代码生成** - 生成相关代码片段\n" +
            "4. **脚本执行** - 生成并执行 JavaScript 脚本\n\n" +
            "### 快捷操作\n" +
            "- 点击快捷按钮快速开始对话\n" +
            "- 支持上下文感知，自动获取当前页面信息\n" +
            "- 支持多轮对话，记住上下文\n\n" +
            "### 脚本执行\n" +
            "当回复包含 ` ```script ` 代码块时，您可以点击执行按钮运行脚本。";
    }
    
    private String buildDefaultResponse(String message, String skillContext) {
        StringBuilder sb = new StringBuilder();
        sb.append("您好！我是 Ooder 智能助手。\n\n");
        sb.append("我可以帮助您：\n");
        sb.append("- 📝 自动配置场景和能力\n");
        sb.append("- 📊 分析页面数据\n");
        sb.append("- 💻 生成代码片段\n");
        sb.append("- 🔧 执行脚本操作\n");
        sb.append("- ❓ 解答使用问题\n\n");
        
        if (skillContext != null && !skillContext.isEmpty()) {
            sb.append("我已获取到当前页面的上下文信息，可以直接为您提供建议。\n\n");
            sb.append("请问有什么可以帮您的？");
        } else {
            sb.append("请问有什么可以帮您的？");
        }
        
        return sb.toString();
    }
    
    private String extractScript(String content) {
        int start = content.indexOf("```script");
        if (start == -1) return null;
        
        start = content.indexOf("\n", start) + 1;
        int end = content.indexOf("```", start);
        if (end == -1) return null;
        
        return content.substring(start, end).trim();
    }
    
    private Object executeFunction(String functionName, Map<String, Object> params) {
        switch (functionName) {
            case "getPageConfig":
                return Collections.singletonMap("config", "mock-config");
            case "analyzeData":
                return Collections.singletonMap("analysis", "mock-analysis");
            case "executeScript":
                String script = (String) params.get("script");
                return Collections.singletonMap("result", "script-executed");
            default:
                return Collections.singletonMap("result", "unknown-function");
        }
    }
}

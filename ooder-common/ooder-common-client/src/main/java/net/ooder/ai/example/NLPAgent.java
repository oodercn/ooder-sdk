/**
 * $RCSfile: NLPAgent.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.ai.example;

import net.ooder.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 自然语言处理AI Agent示例
 * 完整展示 ooder 1.0注解驱动架构在AI服务模块的应用 */
@Agent(
    id = "nlp-agent-001",
    name = "通用自然语言处理Agent",
    description = "提供文本分类、实体识别和情感分析能力的AI服务组件",
    version = "1.0.0",
    domain = AgentDomain.NLP
)
@AgentCapabilities({
    @AgentCapability(name = "text-classification", version = "2.3.1", provider = "HuggingFace", isCore = false),
    @AgentCapability(name = "entity-recognition", version = "1.8.0", provider = "StanfordNLP", isCore = true)
})
public class NLPAgent {
    
    /**
     * 文本分类方法
     */
    @AgentAction(
        name = "classifyText",
        description = "对输入文本进行分类",
        executionMode = ExecutionMode.SYNC,
        timeout = 3000, value = ""
    )
    public String classifyText(
        @AggregationType.AgentParam(name = "text", description = "待分类文本", required = true)
        String text,
        
        @AggregationType.AgentParam(name = "categories", description = "分类类别列表", required = true, validationRule = "notEmpty")
        List<String> categories
    ) {
        // 实际项目中这里会调用AI模型进行文本分类
        return "tech"; // 模拟返回分类结果
    }
    
    /**
     * 实体识别方法
     */
    @AgentAction(
        name = "recognizeEntities",
        description = "识别文本中的命名实体",
        executionMode = ExecutionMode.ASYNC,
        timeout = 5000, value = ""
    )
    public List<String> recognizeEntities(
        @AggregationType.AgentParam(name = "text", description = "待处理文本", required = true, validationRule = "notEmpty")
        String text
    ) {
        // 实际项目中这里会调用实体识别模型
        return Arrays.asList("Apple", "California"); // 模拟返回实体列表
    }
    
    /**
     * 初始化回调方法
    @AgentLifecycle(phase = LifecyclePhase.INIT, order = 1, value = Stage.INIT)

    public void initialize() {
        System.out.println("NLPAgent初始化: 加载模型配置...");
        // 初始化逻辑：加载配置、建立连接等
    }
    
    /**
     * 启动回调方法
     */
    @AgentLifecycle(phase = LifecyclePhase.START, order = 2)
    public void start() {
        System.out.println("NLPAgent启动: 加载AI模型...");
        // 启动逻辑：加载模型、预热服务等
    }
    
    /**
     * 销毁回调方法
     */
    @AgentLifecycle(phase = LifecyclePhase.DESTROY, order = 1, value = Stage.DESTROY)
    public void destroy() {
        System.out.println("NLPAgent销毁: 释放模型资源...");
        // 销毁逻辑：释放资源、关闭连接等
    }
}
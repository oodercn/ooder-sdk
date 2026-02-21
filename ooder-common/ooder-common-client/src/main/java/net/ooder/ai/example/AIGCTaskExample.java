/**
 * $RCSfile: AIGCTaskExample.java,v $
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

import net.ooder.annotation.AIGCModel;
import net.ooder.annotation.AIGCTask;
import net.ooder.annotation.PriorityLevel;
import net.ooder.annotation.TaskType;

/**
 * AIGC任务处理示例，展示AIGCTask注解的使用
 */
@AIGCModel(modelId = "gpt-4o", version = "1.0")
public class AIGCTaskExample {

    /**
     * 文本摘要任务示例
     */
    @AIGCTask(
        taskId = "SUMMARY-2024-001",
        type = TaskType.SUMMARY,
        priority = PriorityLevel.HIGH,
        timeout = 60000,
        retryCount = 3,
        description = "生成文档摘要，提取关键信息", modelId = "", name = ""
    )
    public String generateSummary(String documentContent) {
        // 实际业务逻辑实现
        return "摘要内容...";
    }

    /**
     * 图像生成任务示例
     */
    @AIGCTask(
        taskId = "IMAGE-2024-002",
        type = TaskType.IMAGE_GENERATION,
        priority = PriorityLevel.MEDIUM,
        timeout = 120000,
        retryCount = 2,
        description = "根据文本描述生成图像", modelId = "", name = ""
    )
    public byte[] generateImage(String prompt) {
        // 实际业务逻辑实现
        return new byte[0];
    }
}
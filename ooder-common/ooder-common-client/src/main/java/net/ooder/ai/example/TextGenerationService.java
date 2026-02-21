/**
 * $RCSfile: TextGenerationService.java,v $
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

@AIGCModel(
    modelId = "gpt-3.5-turbo",
    name = "GPT-3.5 Turbo",
    version = "0.1",
    type = ModelType.TEXT_GENERATION,
    provider = "OpenAI",
    maxTokens = 4096
)
@AIGCSecurity(
    contentAudit = true,
    auditPolicy = AuditPolicy.NORMAL,
    allowedRoles = {"AI_USER", "ADMIN"}
)
public class TextGenerationService {

    @AIGCTask(
        taskId = "text-generation",
        name = "文本生成任务",
        modelId = "gpt-3.5-turbo",
        priority = PriorityLevel.NORMAL,
        type = TaskType.TEXT_GENERATION
    )
    public String generateText(
            @AIGCPrompt(
                template = "请根据以下主题生成一篇{length}字的文章：{topic}",
                templateType = TemplateType.TEXT
            ) String prompt,
            @AIGCModel.AIGCData(type = DataType.JSON) String parameters) {
        // 实现逻辑
        return "generated text";
    }
}
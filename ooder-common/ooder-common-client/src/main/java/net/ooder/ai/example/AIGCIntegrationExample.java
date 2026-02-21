/**
 * $RCSfile: AIGCIntegrationExample.java,v $
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

/**
 * AIGC综合应用示例，展示多个注解的组合使用
 * 包含安全策略、资源控制和多模态任务示例
 */
@AIGCModel(
    modelId = "gpt-4o", 
    version = "1.0", 
    name = "gpt-4o", 
    type = ModelType.MULTIMODAL
)
@AIGCSecurity(
    sensitiveLevel = SensitiveLevel.HIGH,
    dataMasking = true, 
    auditLog = true, 
    // 新增安全属性
    dataRetentionDays = 15,  // 医疗数据仅保存15天
    complianceLevel = ComplianceLevel.MEDICAL,  // 符合医疗级合规要求
    dynamicMaskingRules = {"patient_id:mask", "phone:partial"}  // 动态脱敏规则
)
public class AIGCIntegrationExample {

    /**
     * 智能客服响应生成（基础文本任务）
     */
    @AIGCTask(
        taskId = "CUSTOMER_SERVICE-2024-001",
        type = TaskType.TEXT_GENERATION,
        priority = PriorityLevel.HIGH,
        timeout = 45000,
        modelId = "gpt-4o",
        name = "智能客服响应生成",
        // 新增资源控制属性
        cpuQuota = 2,
        memQuota = 4,
        // 依赖用户身份验证任务
        dependencies = {"USER_AUTH-2024-001"}
    )
    @AIGCPrompt(
        template = "作为智能客服，请根据以下对话历史回答用户问题:\n{chatHistory}\n用户问题:{question}",
        variables = {"chatHistory", "question"},
        maxTokens = 1024,
        temperature = 0.7f
    )
    public String generateCustomerServiceResponse(String chatHistory, String question) {
        // 实际调用AIGC模型的业务逻辑
        return "智能客服回答内容...";
    }

    /**
     * 医学报告语音合成（新增音频任务类型）
     */
    @AIGCTask(
        taskId = "MEDICAL_REPORT-2024-002",
        type = TaskType.AUDIO_GENERATION,  // 新增任务类型
        priority = PriorityLevel.HIGHEST,
        timeout = 90000,
        modelId = "tts-medical-v1",
        name = "医学报告语音合成",
        // 高资源需求配置
        cpuQuota = 4,
        memQuota = 8,
        gpuQuota = 1,  // GPU加速
        dependencies = {"REPORT_GENERATE-2024-001"}
    )
    @AIGCSecurity(
        sensitiveLevel = SensitiveLevel.HIGHLY_CONFIDENTIAL,
        complianceLevel = ComplianceLevel.MEDICAL,
        dataRetentionDays = 7
    )
    public byte[] generateMedicalReportAudio(String reportText) {
        // 调用语音合成模型的业务逻辑
        return new byte[0];
    }

    /**
     * 多模态医学影像分析（新增数据分析任务）
     */
    @AIGCTask(
        taskId = "MEDICAL_ANALYSIS-2024-003",
        type = TaskType.DATA_ANALYSIS,  // 新增任务类型
        priority = PriorityLevel.HIGH,
        timeout = 180000,
        modelId = "med-imaging-v2",
        name = "医学影像分析",
        // 极高资源配置
        cpuQuota = 8,
        memQuota = 32,
        gpuQuota = 2,
        retryCount = 1
    )
    public String analyzeMedicalImages(byte[] imageData, String patientHistory) {
        // 调用医学影像分析模型的业务逻辑
        return "影像分析报告...";
    }
}
/**
 * $RCSfile: AIGCModelExample.java,v $
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

import net.ooder.ai.bean.AIGCModelBean;
import net.ooder.annotation.AIGCModel;

/**
 * AIGC模型服务示例，展示AIGCModel注解的使用
 */
@AIGCModel(
    modelId = "gpt-4o",
    version = "1.0",
    provider = "OpenAI",
    capabilities = {"text-generation", "image-generation", "multi-modal"},
    isDefault = true,
    timeout = 30000,
    maxTokens = 8192
)
public class AIGCModelExample {
    public static void main(String[] args) {
        // 创建AIGCModelBean实例
        AIGCModelBean modelBean = new AIGCModelBean();
        modelBean.setModelId("gpt-4o");
        modelBean.setVersion("1.0");
        modelBean.setProvider("OpenAI");
        modelBean.setCapabilities(new String[] {"text-generation", "image-generation", "multi-modal"});
        modelBean.setDefault(true);
        modelBean.setTimeout(30000);
        modelBean.setMaxTokens(8192);

        // 转换为注解字符串
        String annotationStr = modelBean.toAnnotationStr();
        System.out.println("AIGCModel注解字符串: " + annotationStr);
    }
}
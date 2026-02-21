/**
 * $RCSfile: AIGCModelBean.java,v $
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
package net.ooder.ai.bean;


import net.ooder.annotation.*;

/**
 * AIGC模型注解对应的Bean类
 */
@AnnotationType(clazz = AIGCModel.class)
public class AIGCModelBean implements CustomBean {
    private String modelId;
    private String name;
    private ModelType type;
    private String version;
    private ModelStatus status;
    private String[] capabilities;
    private int maxTokens;
    private String provider;
    private boolean isDefault;
    private int timeout;


    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ModelStatus getStatus() {
        return status;
    }

    public void setStatus(ModelStatus status) {
        this.status = status;
    }

    @Override
    public String toAnnotationStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("@AIGCModel(");
        sb.append("modelId=\"").append(modelId).append("\", ");
        sb.append("name=\"").append(name).append("\", ");
        sb.append("type=ModelType.").append(type).append(", ");
        sb.append("version=\"").append(version).append("\", ");
        sb.append("status=ModelStatus.").append(status).append(", ");
        sb.append("capabilities={");
        if (capabilities != null && capabilities.length > 0) {
            for (int i = 0; i < capabilities.length; i++) {
                sb.append("\"").append(capabilities[i]).append("\"");
                if (i < capabilities.length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}, ");
        sb.append("maxTokens=").append(maxTokens).append(", ");
        sb.append("provider=\"").append(provider).append("\", ");
        sb.append("isDefault=").append(isDefault).append(", ");
        sb.append("timeout=").append(timeout);
        sb.append(")");
        return sb.toString();
    }

    // Getters and setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }
    public String[] getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String[] capabilities) {
        this.capabilities = capabilities;
    }
}
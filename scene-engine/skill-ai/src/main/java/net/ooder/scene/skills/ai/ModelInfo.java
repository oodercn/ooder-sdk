package net.ooder.scene.skills.ai;

import java.util.List;
import java.util.Map;

/**
 * 模型信息
 */
public class ModelInfo {

    private String modelId;
    private String name;
    private String provider;
    private String version;
    private List<String> capabilities;
    private Map<String, Object> parameters;
    private boolean available;

    // Getters and Setters
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }

    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}

package net.ooder.scene.core;

/**
 * 能力信息
 */
public class CapabilityInfo {
    private String name;
    private String description;
    private String inputSchema;
    private String outputSchema;
    private boolean async;

    public CapabilityInfo() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getInputSchema() { return inputSchema; }
    public void setInputSchema(String inputSchema) { this.inputSchema = inputSchema; }
    public String getOutputSchema() { return outputSchema; }
    public void setOutputSchema(String outputSchema) { this.outputSchema = outputSchema; }
    public boolean isAsync() { return async; }
    public void setAsync(boolean async) { this.async = async; }
}

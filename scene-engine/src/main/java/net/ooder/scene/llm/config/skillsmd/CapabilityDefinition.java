package net.ooder.scene.llm.config.skillsmd;

import java.util.ArrayList;
import java.util.List;

/**
 * Capability 定义
 *
 * @author ooder
 * @since 2.4
 */
public class CapabilityDefinition {

    private String id;
    private String name;
    private String description;
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private String output;
    private String outputType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParameterDefinition> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterDefinition> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(ParameterDefinition parameter) {
        this.parameters.add(parameter);
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public boolean hasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    public String getFunctionName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        return id != null ? id.replaceAll("-", "_") : null;
    }
}

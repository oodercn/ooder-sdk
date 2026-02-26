package net.ooder.sdk.a2a.capability;

import java.util.List;
import java.util.Map;

/**
 * 能力声明接口
 *
 * <p>定义Skill的能力声明规范</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public interface CapabilityDeclaration {

    /**
     * 获取能力ID
     *
     * @return 能力唯一标识
     */
    String getId();

    /**
     * 获取能力名称
     *
     * @return 能力名称
     */
    String getName();

    /**
     * 获取能力描述
     *
     * @return 能力描述
     */
    String getDescription();

    /**
     * 获取输入参数定义
     *
     * @return 参数定义列表
     */
    List<ParameterDefinition> getInputParameters();

    /**
     * 获取输出定义
     *
     * @return 输出定义
     */
    OutputDefinition getOutput();

    /**
     * 获取能力元数据
     *
     * @return 元数据映射
     */
    Map<String, Object> getMetadata();

    /**
     * 参数定义
     */
    class ParameterDefinition {
        private String name;
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;

        public ParameterDefinition() {}

        public ParameterDefinition(String name, String type, String description, boolean required) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.required = required;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public Object getDefaultValue() { return defaultValue; }
        public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    }

    /**
     * 输出定义
     */
    class OutputDefinition {
        private String type;
        private String description;

        public OutputDefinition() {}

        public OutputDefinition(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}

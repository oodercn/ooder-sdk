package net.ooder.scene.skill.capability;

import java.util.Map;

/**
 * 能力单元接口
 *
 * <p>定义技能对外暴露的能力单元</p>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public interface Capability {

    /**
     * 能力ID
     */
    String getId();

    /**
     * 能力名称
     */
    String getName();

    /**
     * 能力类型
     */
    default CapabilityType getType() {
        return CapabilityType.INTERNAL;
    }

    /**
     * 能力描述
     */
    default String getDescription() {
        return "";
    }

    /**
     * 输入参数定义
     */
    default Map<String, ParameterDefinition> getInputParameters() {
        return null;
    }

    /**
     * 输出参数定义
     */
    default Map<String, ParameterDefinition> getOutputParameters() {
        return null;
    }

    /**
     * 是否为私有能力（不对外暴露）
     */
    default boolean isPrivate() {
        return false;
    }

    /**
     * 能力类型枚举
     */
    enum CapabilityType {
        INTERNAL,   // 内部能力
        EXPOSED,    // 对外暴露
        ENTRY       // 入口能力
    }

    /**
     * 参数定义
     */
    interface ParameterDefinition {
        String getName();
        String getType();
        boolean isRequired();
        String getDescription();
        Object getDefaultValue();
    }
}

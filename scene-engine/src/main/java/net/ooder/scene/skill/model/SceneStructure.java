package net.ooder.scene.skill.model;

import net.ooder.scene.skill.capability.Capability;

import java.util.List;
import java.util.Map;

/**
 * 场景结构
 *
 * <p>定义场景技能的内部组织结构，仅当 SkillForm = SCENE 时有效</p>
 *
 * <h3>类比文件系统：</h3>
 * <ul>
 *   <li>SceneStructure = 文件夹的内部结构</li>
 *   <li>internalCapabilities = 文件夹内的隐藏文件</li>
 *   <li>childSkills = 子文件夹和文件</li>
 *   <li>orchestration = 文件夹的组织规则</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public interface SceneStructure {

    /**
     * 内部能力列表
     *
     * <p>场景技能私有的能力，不对外暴露</p>
     *
     * <p>类比：文件夹内的隐藏文件</p>
     *
     * @return 内部能力列表
     */
    List<InternalCapability> getInternalCapabilities();

    /**
     * 子技能列表
     *
     * <p>场景包含的子技能（场景或独立）</p>
     *
     * <p>类比：文件夹内的子文件夹和文件</p>
     *
     * @return 子技能列表
     */
    List<Skill> getChildSkills();

    /**
     * 获取指定子技能
     */
    default Skill getChildSkill(String skillId) {
        return getChildSkills().stream()
                .filter(s -> s.getSkillId().equals(skillId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 是否具有指定子技能
     */
    default boolean hasChildSkill(String skillId) {
        return getChildSkill(skillId) != null;
    }

    /**
     * 编排逻辑
     *
     * <p>定义内部能力的调用顺序和规则</p>
     *
     * @return 编排逻辑
     */
    Orchestration getOrchestration();

    /**
     * 协作配置
     *
     * <p>定义与外部技能的协作方式</p>
     *
     * @return 协作配置
     */
    CollaborationConfig getCollaborationConfig();

    /**
     * 场景入口
     *
     * <p>用户访问场景的入口点</p>
     *
     * @return 入口能力ID
     */
    String getEntryCapability();

    /**
     * 场景状态
     *
     * @return 当前状态
     */
    SceneState getState();

    /**
     * 场景元数据
     *
     * @return 元数据键值对
     */
    Map<String, Object> getMetadata();

    /**
     * 内部能力接口
     */
    interface InternalCapability extends Capability {
        /**
         * 是否为私有（不对外暴露）
         */
        default boolean isPrivate() {
            return true;
        }
    }

    /**
     * 编排逻辑接口
     */
    interface Orchestration {
        /**
         * 编排类型
         */
        OrchestrationType getType();

        /**
         * 执行步骤
         */
        List<Step> getSteps();

        /**
         * 编排类型枚举
         */
        enum OrchestrationType {
            SEQUENTIAL,    // 顺序执行
            PARALLEL,      // 并行执行
            CONDITIONAL,   // 条件执行
            LOOP,          // 循环执行
            STATE_MACHINE  // 状态机
        }

        /**
         * 执行步骤
         */
        interface Step {
            String getId();
            String getCapabilityId();
            Map<String, String> inputMapping();
            Map<String, String> outputMapping();
            String getCondition();  // 条件表达式（可选）
        }
    }

    /**
     * 协作配置接口
     */
    interface CollaborationConfig {
        /**
         * 是否允许外部访问
         */
        boolean isExternallyAccessible();

        /**
         * 暴露给外部的能力
         */
        List<String> getExposedCapabilities();

        /**
         * 依赖的外部技能
         */
        List<ExternalDependency> getExternalDependencies();

        /**
         * A2A协作配置
         */
        A2AConfig getA2AConfig();
    }

    /**
     * 外部依赖
     */
    interface ExternalDependency {
        String getSkillId();
        String getCapabilityId();
        boolean isRequired();
        String getFallbackStrategy();
    }

    /**
     * A2A配置
     */
    interface A2AConfig {
        boolean enabled();
        String getEndpoint();
        Map<String, String> getHeaders();
    }

    /**
     * 场景状态
     */
    enum SceneState {
        CREATED,      // 已创建
        INITIALIZING, // 初始化中
        ACTIVE,       // 运行中
        PAUSED,       // 已暂停
        DESTROYED     // 已销毁
    }
}

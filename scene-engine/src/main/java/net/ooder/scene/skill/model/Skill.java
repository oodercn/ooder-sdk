package net.ooder.scene.skill.model;

import net.ooder.scene.skill.capability.Capability;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 技能 - 核心实体
 *
 * <p>技能是系统的唯一核心实体，场景是技能的形态之一</p>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>技能是唯一核心，场景是技能的属性</li>
 *   <li>形态（form）决定技能的结构（场景/独立）</li>
 *   <li>分类（category）决定技能的能力类型</li>
 *   <li>目的（purposes）决定技能的使用场景</li>
 *   <li>所有属性在开发时声明，运行时只读</li>
 * </ul>
 *
 * <h3>类比文件系统：</h3>
 * <ul>
 *   <li>Skill = File/Folder（文件/文件夹）</li>
 *   <li>SkillForm = 是文件还是文件夹</li>
 *   <li>SceneType = 文件夹类型（源码包/资源文件夹/普通文件夹）</li>
 *   <li>SkillCategory = 文件扩展名（.doc/.exe/.flow）</li>
 *   <li>ServicePurpose = 文件用途（个人/团队/即时/定时）</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public interface Skill {

    // ========== 基础信息 ==========

    /**
     * 技能ID - 全局唯一标识
     *
     * <p>类比：文件路径</p>
     */
    String getSkillId();

    /**
     * 技能名称
     *
     * <p>类比：文件名</p>
     */
    String getName();

    /**
     * 技能版本
     */
    String getVersion();

    /**
     * 技能描述
     */
    String getDescription();

    // ========== 形态维度 ==========

    /**
     * 技能形态
     *
     * <p>决定技能是场景（容器）还是独立（原子）</p>
     *
     * <p>类比：是文件还是文件夹</p>
     *
     * @return SCENE 或 STANDALONE
     */
    SkillForm getForm();

    /**
     * 场景类型
     *
     * <p>仅当 form = SCENE 时有效</p>
     *
     * <p>类比：文件夹类型（源码包/资源文件夹/普通文件夹）</p>
     *
     * @return 场景类型，非场景技能返回 empty
     */
    default Optional<SceneType> getSceneType() {
        return Optional.empty();
    }

    /**
     * 是否为场景技能
     */
    default boolean isScene() {
        return getForm() == SkillForm.SCENE;
    }

    /**
     * 是否为独立技能
     */
    default boolean isStandalone() {
        return getForm() == SkillForm.STANDALONE;
    }

    // ========== 分类维度 ==========

    /**
     * 技能分类
     *
     * <p>决定技能的能力类型和技术实现</p>
     *
     * <p>类比：文件扩展名（.doc/.exe/.flow）</p>
     *
     * @return 技能分类
     */
    SkillCategory getCategory();

    // ========== 目的维度 ==========

    /**
     * 服务目的
     *
     * <p>决定技能的使用场景和服务对象</p>
     *
     * <p>类比：文件用途（个人/团队/即时/定时）</p>
     *
     * <p>可多选组合，如：PERSONAL + INSTANT + REACTIVE</p>
     *
     * @return 服务目的集合
     */
    Set<ServicePurpose> getPurposes();

    /**
     * 获取服务范围
     */
    default Optional<ServicePurpose.Scope> getServiceScope() {
        return getPurposes().stream()
                .filter(ServicePurpose::isScope)
                .map(ServicePurpose::getScope)
                .findFirst();
    }

    /**
     * 获取服务时效
     */
    default Optional<ServicePurpose.Duration> getServiceDuration() {
        return getPurposes().stream()
                .filter(ServicePurpose::isDuration)
                .map(ServicePurpose::getDuration)
                .findFirst();
    }

    /**
     * 获取服务主动性
     */
    default Optional<ServicePurpose.Initiative> getServiceInitiative() {
        return getPurposes().stream()
                .filter(ServicePurpose::isInitiative)
                .map(ServicePurpose::getInitiative)
                .findFirst();
    }

    // ========== 能力维度 ==========

    /**
     * 能力单元列表
     *
     * <p>技能对外暴露的能力</p>
     *
     * @return 能力单元列表
     */
    List<Capability> getCapabilities();

    /**
     * 获取指定能力
     */
    default Optional<Capability> getCapability(String capabilityId) {
        return getCapabilities().stream()
                .filter(c -> c.getId().equals(capabilityId))
                .findFirst();
    }

    /**
     * 是否具有指定能力
     */
    default boolean hasCapability(String capabilityId) {
        return getCapability(capabilityId).isPresent();
    }

    // ========== 场景结构（仅场景技能）==========

    /**
     * 场景结构
     *
     * <p>仅当 form = SCENE 时有效</p>
     *
     * <p>包含内部能力、子技能、编排逻辑等</p>
     *
     * @return 场景结构，非场景技能返回 empty
     */
    default Optional<SceneStructure> getSceneStructure() {
        return Optional.empty();
    }

    // ========== 便捷方法 ==========

    /**
     * 是否可自驱动
     *
     * <p>场景技能：根据 sceneType 判断</p>
     * <p>独立技能：根据 serviceInitiative 判断</p>
     */
    default boolean canSelfDrive() {
        if (isScene()) {
            return getSceneType()
                    .map(SceneType::canSelfDrive)
                    .orElse(false);
        }
        return getServiceInitiative()
                .map(i -> i == ServicePurpose.Initiative.PROACTIVE)
                .orElse(false);
    }

    /**
     * 是否可被触发
     */
    default boolean canBeTriggered() {
        if (isScene()) {
            return getSceneType()
                    .map(SceneType::canBeTriggered)
                    .orElse(false);
        }
        return getServiceInitiative()
                .map(i -> i == ServicePurpose.Initiative.REACTIVE)
                .orElse(true); // 独立技能默认可被触发
    }

    /**
     * 是否需要模型支持
     */
    default boolean requiresModel() {
        return getCategory().requiresModel();
    }

    /**
     * 是否可执行
     */
    default boolean isExecutable() {
        return getCategory().isExecutable();
    }

    /**
     * 获取技能路径
     *
     * <p>类比：文件路径</p>
     */
    SkillPath getPath();

    /**
     * 获取父技能ID
     *
     * <p>如果是子技能，返回父场景技能ID</p>
     *
     * @return 父技能ID，无父技能返回 empty
     */
    default Optional<String> getParentId() {
        return Optional.empty();
    }

    /**
     * 是否为根技能
     */
    default boolean isRoot() {
        return !getParentId().isPresent();
    }
}

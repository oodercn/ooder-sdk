package net.ooder.skills.api;

/**
 * 技能形态
 *
 * <p>定义技能的基本结构形态，类比文件系统中的文件和文件夹</p>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>技能是唯一核心实体，场景是技能的形态之一</li>
 *   <li>形态在开发时声明，运行时只读</li>
 *   <li>形态决定技能的组织方式和生命周期</li>
 * </ul>
 *
 * @author Agent-SDK Team
 * @version 3.1
 * @since 3.0
 */
public enum SkillForm {

    /**
     * 场景技能 - 容器型
     *
     * <p>类比：文件系统中的文件夹</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>可包含内部能力和子技能</li>
     *   <li>有组织结构，可嵌套</li>
     *   <li>通过场景类型（AUTO/TRIGGER/HYBRID）决定运行模式</li>
     *   <li>用户通过场景入口访问内部能力</li>
     * </ul>
     */
    SCENE("SCENE", "场景技能", "folder", true),

    /**
     * 独立技能 - 原子型
     *
     * <p>类比：文件系统中的文件</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>单一能力单元，不可再分</li>
     *   <li>直接对外提供服务</li>
     *   <li>可被场景技能引用和组合</li>
     *   <li>通过Agent组网参与协作</li>
     * </ul>
     */
    STANDALONE("STANDALONE", "独立技能", "file", false),

    /**
     * 能力提供者 - 服务型
     *
     * <p>类比：文件系统中的系统文件</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>提供基础能力服务</li>
     *   <li>不直接面向最终用户</li>
     *   <li>被其他技能依赖和调用</li>
     *   <li>通常作为底层服务存在</li>
     * </ul>
     */
    PROVIDER("PROVIDER", "能力提供者", "system-file", false),

    /**
     * 驱动技能 - 驱动型
     *
     * <p>类比：文件系统中的驱动程序</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>驱动场景运行</li>
     *   <li>处理特定类型的输入</li>
     *   <li>通常与外部系统交互</li>
     *   <li>作为场景的能力支撑</li>
     * </ul>
     */
    DRIVER("DRIVER", "驱动技能", "driver", false),

    /**
     * 内部能力 - 内部型
     *
     * <p>类比：文件系统中的隐藏文件</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>系统内部使用</li>
     *   <li>不对外暴露</li>
     *   <li>作为基础设施存在</li>
     *   <li>用户无感知</li>
     * </ul>
     */
    INTERNAL("INTERNAL", "内部能力", "hidden-file", false);

    private final String code;
    private final String name;
    private final String fileSystemAnalog;
    private final boolean isContainer;

    SkillForm(String code, String name, String fileSystemAnalog, boolean isContainer) {
        this.code = code;
        this.name = name;
        this.fileSystemAnalog = fileSystemAnalog;
        this.isContainer = isContainer;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFileSystemAnalog() {
        return fileSystemAnalog;
    }

    /**
     * 是否为容器型（可包含其他技能）
     */
    public boolean isContainer() {
        return isContainer;
    }

    /**
     * 是否为场景技能
     */
    public boolean isScene() {
        return this == SCENE;
    }

    /**
     * 是否为独立技能
     */
    public boolean isStandalone() {
        return this == STANDALONE;
    }

    /**
     * 是否为能力提供者
     */
    public boolean isProvider() {
        return this == PROVIDER;
    }

    /**
     * 是否为驱动技能
     */
    public boolean isDriver() {
        return this == DRIVER;
    }

    /**
     * 是否为内部能力
     */
    public boolean isInternal() {
        return this == INTERNAL;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举值，找不到返回 null
     */
    public static SkillForm fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (SkillForm form : values()) {
            if (form.code.equalsIgnoreCase(code)) {
                return form;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举，带默认值
     *
     * @param code 代码
     * @param defaultValue 默认值
     * @return 枚举值
     */
    public static SkillForm fromCode(String code, SkillForm defaultValue) {
        SkillForm result = fromCode(code);
        return result != null ? result : defaultValue;
    }
}

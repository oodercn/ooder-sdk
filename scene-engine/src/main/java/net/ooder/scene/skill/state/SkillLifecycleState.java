package net.ooder.scene.skill.state;

/**
 * Skill生命周期状态枚举
 *
 * <p>定义Skill从创建到销毁的完整生命周期状态：</p>
 * <pre>
 *     CREATED ──start()──► STARTING ──init success──► RUNNING
 *        ▲                                              │
 *        │                                              │ pause()
 *        │                                              ▼
 *        │                                          PAUSED
 *        │                                              │ resume()
 *        │                                              ▼
 *   destroy()                                        STOPPING
 *        │                                              │
 *        ▼                                              ▼
 *    DESTROYED ◄──stop success──────────────────── STOPPED
 *        ▲
 *        │
 *        └── ERROR (任何状态出错可转入，可重试恢复)
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
public enum SkillLifecycleState {

    /**
     * 已创建 - Skill已安装但未启动
     */
    CREATED("已创建", "Skill已安装，等待启动"),

    /**
     * 启动中 - 正在初始化资源
     */
    STARTING("启动中", "正在初始化Skill资源"),

    /**
     * 运行中 - Skill正常运行，可接受调用
     */
    RUNNING("运行中", "Skill正常运行"),

    /**
     * 已暂停 - 临时停止，可恢复运行
     */
    PAUSED("已暂停", "Skill已暂停，可恢复"),

    /**
     * 停止中 - 正在清理资源
     */
    STOPPING("停止中", "正在停止Skill"),

    /**
     * 已停止 - Skill完全停止
     */
    STOPPED("已停止", "Skill已停止"),

    /**
     * 错误状态 - 运行出错，需要修复或重启
     */
    ERROR("错误", "Skill运行出错"),

    /**
     * 已销毁 - Skill完全清理，不可恢复
     */
    DESTROYED("已销毁", "Skill已完全销毁");

    private final String displayName;
    private final String description;

    SkillLifecycleState(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查是否可以从当前状态转换到目标状态
     */
    public boolean canTransitionTo(SkillLifecycleState targetState) {
        switch (this) {
            case CREATED:
                return targetState == STARTING || targetState == DESTROYED;
            case STARTING:
                return targetState == RUNNING || targetState == ERROR;
            case RUNNING:
                return targetState == PAUSED || targetState == STOPPING || targetState == ERROR;
            case PAUSED:
                return targetState == RUNNING || targetState == STOPPING || targetState == ERROR;
            case STOPPING:
                return targetState == STOPPED || targetState == ERROR;
            case STOPPED:
                return targetState == STARTING || targetState == DESTROYED;
            case ERROR:
                return targetState == STARTING || targetState == STOPPED || targetState == DESTROYED;
            case DESTROYED:
                return false; // 销毁后不可转换
            default:
                return false;
        }
    }

    /**
     * 检查是否处于活动状态（可接受调用）
     */
    public boolean isActive() {
        return this == RUNNING || this == PAUSED;
    }

    /**
     * 检查是否处于终止状态
     */
    public boolean isTerminal() {
        return this == STOPPED || this == DESTROYED;
    }

    /**
     * 检查是否处于错误状态
     */
    public boolean isError() {
        return this == ERROR;
    }
}

package net.ooder.scene.skill.classification;

/**
 * 能力子类型枚举
 * 
 * <p>区分场景能力的驱动类型：</p>
 * <ul>
 *   <li>DRIVER: 驱动型能力 - 主动驱动场景执行</li>
 *   <li>EXECUTOR: 执行型能力 - 被动执行具体任务</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 */
public enum CapabilitySubType {
    
    /**
     * 驱动型能力
     * 
     * <p>主动驱动场景执行的能力，通常包含：</p>
     * <ul>
     *   <li>自驱逻辑（mainFirst）</li>
     *   <li>定时触发</li>
     *   <li>事件监听</li>
     *   <li>条件检测</li>
     * </ul>
     */
    DRIVER("DRIVER", "驱动型能力", "Driver Capability", 
        "主动驱动场景执行的能力", 1),
    
    /**
     * 执行型能力
     * 
     * <p>被动执行具体任务的能力，通常包含：</p>
     * <ul>
     *   <li>任务执行</li>
     *   <li>数据处理</li>
     *   <li>API调用</li>
     *   <li>结果返回</li>
     * </ul>
     */
    EXECUTOR("EXECUTOR", "执行型能力", "Executor Capability", 
        "被动执行具体任务的能力", 2);

    private final String code;
    private final String name;
    private final String englishName;
    private final String description;
    private final int sort;

    CapabilitySubType(String code, String name, String englishName, String description, int sort) {
        this.code = code;
        this.name = name;
        this.englishName = englishName;
        this.description = description;
        this.sort = sort;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getDescription() {
        return description;
    }

    public int getSort() {
        return sort;
    }

    /**
     * 是否为驱动型
     */
    public boolean isDriver() {
        return this == DRIVER;
    }

    /**
     * 是否为执行型
     */
    public boolean isExecutor() {
        return this == EXECUTOR;
    }

    /**
     * 根据代码获取子类型
     */
    public static CapabilitySubType fromCode(String code) {
        for (CapabilitySubType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return EXECUTOR;  // 默认为执行型
    }
}

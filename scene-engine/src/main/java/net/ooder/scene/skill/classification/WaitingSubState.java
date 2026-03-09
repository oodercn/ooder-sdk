package net.ooder.scene.skill.classification;

/**
 * 等待子状态枚举
 * 
 * <p>定义场景技能在 WAITING 状态下的具体等待原因：</p>
 * <ul>
 *   <li>WAITING_APPROVAL: 等待审批</li>
 *   <li>WAITING_CONDITION: 等待条件满足</li>
 *   <li>WAITING_RESOURCE: 等待资源就绪</li>
 *   <li>WAITING_SCHEDULE: 等待调度</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 */
public enum WaitingSubState {
    
    /**
     * 等待审批
     * 
     * <p>场景技能需要人工或系统审批后才能继续执行</p>
     */
    WAITING_APPROVAL("WAITING_APPROVAL", "等待审批", "Waiting Approval", 
        "场景技能需要审批后才能继续执行", 1),
    
    /**
     * 等待条件满足
     * 
     * <p>场景技能等待前置条件满足后才能继续执行</p>
     */
    WAITING_CONDITION("WAITING_CONDITION", "等待条件", "Waiting Condition", 
        "场景技能等待前置条件满足后才能继续执行", 2),
    
    /**
     * 等待资源就绪
     * 
     * <p>场景技能等待所需资源（如API、数据、服务）就绪后才能继续执行</p>
     */
    WAITING_RESOURCE("WAITING_RESOURCE", "等待资源", "Waiting Resource", 
        "场景技能等待所需资源就绪后才能继续执行", 3),
    
    /**
     * 等待调度
     * 
     * <p>场景技能等待调度系统分配执行时间或资源</p>
     */
    WAITING_SCHEDULE("WAITING_SCHEDULE", "等待调度", "Waiting Schedule", 
        "场景技能等待调度系统分配执行时间或资源", 4);

    private final String code;
    private final String name;
    private final String englishName;
    private final String description;
    private final int sort;

    WaitingSubState(String code, String name, String englishName, String description, int sort) {
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
     * 是否为等待审批
     */
    public boolean isWaitingApproval() {
        return this == WAITING_APPROVAL;
    }

    /**
     * 是否为等待条件
     */
    public boolean isWaitingCondition() {
        return this == WAITING_CONDITION;
    }

    /**
     * 是否为等待资源
     */
    public boolean isWaitingResource() {
        return this == WAITING_RESOURCE;
    }

    /**
     * 是否为等待调度
     */
    public boolean isWaitingSchedule() {
        return this == WAITING_SCHEDULE;
    }

    /**
     * 是否需要人工干预
     */
    public boolean needsHumanIntervention() {
        return this == WAITING_APPROVAL;
    }

    /**
     * 是否可以自动恢复
     */
    public boolean canAutoResume() {
        return this == WAITING_CONDITION || this == WAITING_RESOURCE || this == WAITING_SCHEDULE;
    }

    /**
     * 根据代码获取等待子状态
     */
    public static WaitingSubState fromCode(String code) {
        for (WaitingSubState state : values()) {
            if (state.code.equals(code)) {
                return state;
            }
        }
        return WAITING_CONDITION;  // 默认为等待条件
    }
}

package net.ooder.scene.skill.model;

/**
 * 服务目的
 *
 * <p>定义技能的服务属性和使用场景，可多选组合</p>
 *
 * <h3>设计原则：</h3>
 * <ul>
 *   <li>目的描述技能的"使用场景"和"服务对象"</li>
 *   <li>与形态、分类是正交维度</li>
 *   <li>支持多选组合，如：PERSONAL + INSTANT + REACTIVE</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0
 * @since 3.0
 */
public enum ServicePurpose {

    // ========== 服务范围维度 ==========

    /**
     * 个人服务
     *
     * <p>为单个用户提供服务，数据隔离</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>单用户上下文</li>
     *   <li>数据私有</li>
     *   <li>个性化配置</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>个人文档助手</li>
     *   <li>个人日程管理</li>
     *   <li>个人笔记</li>
     * </ul>
     */
    PERSONAL("personal", "个人服务", Scope.PERSONAL, null, null),

    /**
     * 团队服务
     *
     * <p>为团队协作提供服务，共享上下文</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>多用户协作</li>
     *   <li>共享数据</li>
     *   <li>角色权限</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>项目管理</li>
     *   <li>会议协作</li>
     *   <li>团队文档</li>
     * </ul>
     */
    TEAM("team", "团队服务", Scope.TEAM, null, null),

    /**
     * 组织服务
     *
     * <p>为整个组织提供服务，标准化流程</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>跨团队</li>
     *   <li>标准化</li>
     *   <li>合规审计</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>审批流程</li>
     *   <li>人力资源</li>
     *   <li>财务管理</li>
     * </ul>
     */
    ORGANIZATION("organization", "组织服务", Scope.ORGANIZATION, null, null),

    /**
     * 公共服务
     *
     * <p>开放访问，通用能力</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>开放访问</li>
     *   <li>通用性强</li>
     *   <li>多租户</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>天气查询</li>
     *   <li>翻译服务</li>
     *   <li>地图服务</li>
     * </ul>
     */
    PUBLIC("public", "公共服务", Scope.PUBLIC, null, null),

    // ========== 服务时效维度 ==========

    /**
     * 即时服务
     *
     * <p>同步响应，立即返回</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>同步调用</li>
     *   <li>立即返回</li>
     *   <li>无状态</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>计算器</li>
     *   <li>查询</li>
     *   <li>格式转换</li>
     * </ul>
     */
    INSTANT("instant", "即时服务", null, Duration.INSTANT, null),

    /**
     * 持续服务
     *
     * <p>长期运行，状态保持</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>长期运行</li>
     *   <li>状态保持</li>
     *   <li>事件驱动</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>监控告警</li>
     *   <li>自动化流程</li>
     *   <li>实时同步</li>
     * </ul>
     */
    PERSISTENT("persistent", "持续服务", null, Duration.PERSISTENT, null),

    /**
     * 定时服务
     *
     * <p>按触发条件执行</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>定时触发</li>
     *   <li>条件执行</li>
     *   <li>可调度</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>定时报表</li>
     *   <li>定期备份</li>
     *   <li>定时提醒</li>
     * </ul>
     */
    SCHEDULED("scheduled", "定时服务", null, Duration.SCHEDULED, null),

    // ========== 服务主动性维度 ==========

    /**
     * 主动服务
     *
     * <p>自驱动，主动推送</p>
     *
     * <p>对应场景类型：AUTO</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>自驱动</li>
     *   <li>主动推送</li>
     *   <li>事件监听</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>智能推荐</li>
     *   <li>预警提醒</li>
     *   <li>自动同步</li>
     * </ul>
     */
    PROACTIVE("proactive", "主动服务", null, null, Initiative.PROACTIVE),

    /**
     * 被动服务
     *
     * <p>等待触发，被动响应</p>
     *
     * <p>对应场景类型：TRIGGER</p>
     *
     * <p>特点：</p>
     * <ul>
     *   <li>等待触发</li>
     *   <li>被动响应</li>
     *   <li>API调用</li>
     * </ul>
     *
     * <p>示例：</p>
     * <ul>
     *   <li>API服务</li>
     *   <li>查询接口</li>
     *   <li>指令响应</li>
     * </ul>
     */
    REACTIVE("reactive", "被动服务", null, null, Initiative.REACTIVE);

    private final String code;
    private final String name;
    private final Scope scope;
    private final Duration duration;
    private final Initiative initiative;

    ServicePurpose(String code, String name, Scope scope, Duration duration, Initiative initiative) {
        this.code = code;
        this.name = name;
        this.scope = scope;
        this.duration = duration;
        this.initiative = initiative;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    public Duration getDuration() {
        return duration;
    }

    public Initiative getInitiative() {
        return initiative;
    }

    /**
     * 是否为范围维度
     */
    public boolean isScope() {
        return scope != null;
    }

    /**
     * 是否为时效维度
     */
    public boolean isDuration() {
        return duration != null;
    }

    /**
     * 是否为主动性维度
     */
    public boolean isInitiative() {
        return initiative != null;
    }

    /**
     * 根据代码获取目的
     */
    public static ServicePurpose fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ServicePurpose purpose : values()) {
            if (purpose.code.equalsIgnoreCase(code)) {
                return purpose;
            }
        }
        return null;
    }

    // ========== 子枚举 ==========

    /**
     * 服务范围
     */
    public enum Scope {
        PERSONAL, TEAM, ORGANIZATION, PUBLIC
    }

    /**
     * 服务时效
     */
    public enum Duration {
        INSTANT, PERSISTENT, SCHEDULED
    }

    /**
     * 服务主动性
     */
    public enum Initiative {
        PROACTIVE, REACTIVE
    }
}

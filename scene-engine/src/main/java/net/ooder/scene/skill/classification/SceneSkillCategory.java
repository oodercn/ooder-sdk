package net.ooder.scene.skill.classification;

/**
 * 场景技能分类枚举
 * 
 * <p>v2.3.1 修订：根据自驱能力和业务语义评分进行分类</p>
 * 
 * <p>分类标准：</p>
 * <ul>
 *   <li>ABS: 自驱业务场景 - 有自驱能力 + 业务语义评分 ≥ 8分</li>
 *   <li>ASS: 自驱系统场景 - 有自驱能力 + 业务语义评分 < 8分</li>
 *   <li>TBS: 触发业务场景 - 无自驱能力 + 业务语义评分 ≥ 8分</li>
 *   <li>NOT_SCENE_SKILL: 普通技能 - 不满足场景技能基本标准 或 无自驱能力且业务语义评分 < 8分</li>
 * </ul>
 *
 * <p>自驱能力判定条件（必须同时满足）：</p>
 * <ol>
 *   <li>mainFirst = true</li>
 *   <li>mainFirstConfig 存在且非空</li>
 *   <li>driverConditions 非空</li>
 * </ol>
 *
 * <p>业务语义评分（满分10分）：</p>
 * <ul>
 *   <li>driverConditions 非空：3分</li>
 *   <li>participants 非空：3分</li>
 *   <li>visibility = public：2分</li>
 *   <li>有协作能力：1分</li>
 *   <li>有业务标签：1分</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 * @see <a href="https://github.com/ooderCN/ooder-skills/blob/main/docs/v2.3/GLOSSARY_V2.3.md">术语表 v2.3</a>
 */
public enum SceneSkillCategory {
    
    /**
     * 自驱业务场景
     * 
     * <p>自动驱动的业务场景，具备完整自驱能力和高业务语义</p>
     * <p>条件：hasSelfDrive=true 且 businessScore >= 8</p>
     */
    ABS("ABS", "自驱业务场景", "Auto Business Scene", 
        "自动驱动的业务场景，具备完整自驱能力和高业务语义", 1),
    
    /**
     * 自驱系统场景
     * 
     * <p>自动驱动的系统场景，具备自驱能力但业务语义不足</p>
     * <p>条件：hasSelfDrive=true 且 businessScore < 8</p>
     */
    ASS("ASS", "自驱系统场景", "Auto System Scene", 
        "自动驱动的系统场景，具备自驱能力但业务语义不足", 2),
    
    /**
     * 触发业务场景
     * 
     * <p>外部触发的业务场景，具备高业务语义但需要人工或API触发</p>
     * <p>条件：hasSelfDrive=false 且 businessScore >= 8</p>
     */
    TBS("TBS", "触发业务场景", "Trigger Business Scene", 
        "外部触发的业务场景，具备高业务语义但需要人工或API触发", 3),
    
    /**
     * 待定（保留用于人工审核场景）
     * 
     * <p>v2.3.1 说明：当前分类逻辑已简化，此分类保留用于特殊场景</p>
     * <p>原定义：业务语义评分在 3-7 分之间，需人工判定</p>
     */
    PENDING("PENDING", "待定", "Pending", 
        "待人工审核的场景技能", 4),
    
    /**
     * 无效分类（保留用于兼容）
     * 
     * <p>v2.3.1 说明：此分类已合并到 NOT_SCENE_SKILL，保留用于向后兼容</p>
     * <p>原定义：mainFirst=false 且业务语义评分 < 8分</p>
     * 
     * @deprecated 使用 NOT_SCENE_SKILL 替代
     */
    @Deprecated
    INVALID("INVALID", "无效分类", "Invalid", 
        "无效分类，请使用 NOT_SCENE_SKILL", 5),
    
    /**
     * 普通技能（非场景技能）
     * 
     * <p>不满足场景技能基本标准，或无自驱能力且业务语义评分不足</p>
     * <p>条件：不满足标准1或标准2，或 hasSelfDrive=false 且 businessScore < 8</p>
     */
    NOT_SCENE_SKILL("NOT_SCENE_SKILL", "普通技能", "Regular Skill", 
        "普通技能，非场景技能", 99);

    private final String code;
    private final String name;
    private final String englishName;
    private final String description;
    private final int sort;

    SceneSkillCategory(String code, String name, String englishName, String description, int sort) {
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
     * 是否为自驱业务场景
     */
    public boolean isABS() {
        return this == ABS;
    }

    /**
     * 是否为自驱系统场景
     */
    public boolean isASS() {
        return this == ASS;
    }

    /**
     * 是否为触发业务场景
     */
    public boolean isTBS() {
        return this == TBS;
    }

    /**
     * 是否为场景技能
     * 
     * <p>v2.3.1 修订：ABS/ASS/TBS 为场景技能，其他为普通技能</p>
     */
    public boolean isSceneSkill() {
        return this == ABS || this == ASS || this == TBS;
    }

    /**
     * 是否有自驱能力（ABS 或 ASS）
     */
    public boolean hasSelfDrive() {
        return this == ABS || this == ASS;
    }

    /**
     * 是否有业务语义（ABS 或 TBS）
     */
    public boolean hasBusinessSemantics() {
        return this == ABS || this == TBS;
    }

    /**
     * 是否需要外部触发（TBS）
     */
    public boolean needsExternalTrigger() {
        return this == TBS;
    }

    /**
     * 是否为有效分类
     * 
     * <p>v2.3.1 修订：ABS/ASS/TBS 为有效分类</p>
     */
    public boolean isValidCategory() {
        return this == ABS || this == ASS || this == TBS;
    }

    /**
     * 获取默认可见性
     */
    public String getDefaultVisibility() {
        switch (this) {
            case ABS:
            case TBS:
                return "public";
            case ASS:
                return "internal";
            default:
                return "internal";
        }
    }

    /**
     * 获取默认生命周期
     */
    public String getDefaultLifecycle() {
        switch (this) {
            case ABS:
                return "完整";
            case ASS:
                return "循环";
            case TBS:
                return "一次性";
            default:
                return "未知";
        }
    }

    /**
     * 根据代码获取分类
     */
    public static SceneSkillCategory fromCode(String code) {
        if (code == null) {
            return NOT_SCENE_SKILL;
        }
        for (SceneSkillCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return NOT_SCENE_SKILL;
    }
}

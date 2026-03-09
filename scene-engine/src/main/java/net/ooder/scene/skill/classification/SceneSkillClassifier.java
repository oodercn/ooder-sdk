package net.ooder.scene.skill.classification;

import net.ooder.skills.api.SkillPackage;

/**
 * 场景技能分类器接口
 * 
 * <p>v2.3.1 修订：根据自驱能力和业务语义评分确定分类</p>
 * 
 * <p>基本标准（必须同时满足才能成为场景技能）：</p>
 * <ul>
 *   <li>标准1: metadata.sceneSkill = true（兼容 metadata.type = scene-skill）</li>
 *   <li>标准2: sceneCapabilities 非空</li>
 * </ul>
 * 
 * <p>自驱能力判定（必须同时满足）：</p>
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
 *   <li>有业务标签：1分（优先使用 businessTags，兼容 tags）</li>
 * </ul>
 * 
 * <p>分类规则：</p>
 * <ul>
 *   <li>ABS: 有自驱能力 + 业务语义评分 >= 8</li>
 *   <li>ASS: 有自驱能力 + 业务语义评分 < 8</li>
 *   <li>TBS: 无自驱能力 + 业务语义评分 >= 8</li>
 *   <li>NOT_SCENE_SKILL: 不满足基本标准 或 无自驱能力且业务语义评分 < 8</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3
 */
public interface SceneSkillClassifier {
    
    /**
     * 检测技能包的分类
     *
     * @param skillPackage 技能包
     * @return 分类结果
     */
    SceneSkillClassificationResult detectCategory(SkillPackage skillPackage);
    
    /**
     * 计算业务语义评分
     * 
     * <p>评分标准（满分10分）：</p>
     * <ul>
     *   <li>driverConditions 非空：3分</li>
     *   <li>participants 非空：3分</li>
     *   <li>visibility = public：2分</li>
     *   <li>有协作能力：1分</li>
     *   <li>有业务标签：1分</li>
     * </ul>
     *
     * @param skillPackage 技能包
     * @return 业务语义评分（0-10）
     */
    int calculateBusinessSemanticsScore(SkillPackage skillPackage);
    
    /**
     * 检查是否满足标准1（是场景技能）
     * 
     * <p>v2.3.1 修订：优先检查 metadata.sceneSkill = true，兼容 metadata.type = scene-skill</p>
     *
     * @param skillPackage 技能包
     * @return 是否满足
     */
    boolean checkStandard1(SkillPackage skillPackage);
    
    /**
     * 检查是否满足标准2（sceneCapabilities 非空）
     *
     * @param skillPackage 技能包
     * @return 是否满足
     */
    boolean checkStandard2(SkillPackage skillPackage);
    
    /**
     * 检查是否满足标准3（有自驱能力）
     * 
     * <p>v2.3.1 修订：检查 mainFirst + mainFirstConfig + driverConditions</p>
     *
     * @param skillPackage 技能包
     * @return 是否满足
     */
    boolean checkStandard3(SkillPackage skillPackage);
    
    /**
     * 检查是否满足标准4（有业务语义）
     *
     * @param skillPackage 技能包
     * @return 是否满足
     */
    boolean checkStandard4(SkillPackage skillPackage);
    
    /**
     * 快速判断是否为场景技能
     *
     * @param skillPackage 技能包
     * @return 是否为场景技能
     */
    boolean isSceneSkill(SkillPackage skillPackage);
}

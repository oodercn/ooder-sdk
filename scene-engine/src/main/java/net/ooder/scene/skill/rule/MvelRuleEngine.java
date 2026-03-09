package net.ooder.scene.skill.rule;

import java.util.Map;

/**
 * MVEL 规则引擎接口
 * 
 * <p>基于 MVEL 表达式引擎的规则执行器</p>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface MvelRuleEngine {

    /**
     * 执行规则
     *
     * @param ruleId 规则ID
     * @param context 执行上下文
     * @return 执行结果
     */
    Map<String, Object> execute(String ruleId, Map<String, Object> context);

    /**
     * 执行脚本
     *
     * @param script MVEL脚本
     * @param context 执行上下文
     * @return 执行结果
     */
    Object executeScript(String script, Map<String, Object> context);

    /**
     * 注册规则
     *
     * @param rule 规则脚本
     */
    void registerRule(RuleScript rule);

    /**
     * 持久化规则
     *
     * @param rule 规则脚本
     */
    void persistRule(RuleScript rule);

    /**
     * 获取规则
     *
     * @param ruleId 规则ID
     * @return 规则脚本
     */
    RuleScript getRule(String ruleId);

    /**
     * 删除规则
     *
     * @param ruleId 规则ID
     */
    void removeRule(String ruleId);

    /**
     * 获取场景所有规则
     *
     * @param sceneId 场景ID
     * @return 规则列表
     */
    java.util.List<RuleScript> getSceneRules(String sceneId);

    /**
     * 检查规则是否存在
     *
     * @param ruleId 规则ID
     * @return 是否存在
     */
    boolean hasRule(String ruleId);

    /**
     * 获取规则数量
     *
     * @return 规则数量
     */
    int getRuleCount();
}

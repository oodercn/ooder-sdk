package net.ooder.sdk.llm.scene;

/**
 * NLP 上下文管理器
 * 支持组件化上下文管理
 */
public interface NlpContextManager {

    /**
     * 初始化 NLP 上下文
     *
     * @param componentType 组件类型
     * @param moduleViewType ModuleView类型
     * @param config 配置
     * @return NLP上下文
     */
    NlpContext initializeNlpContext(String componentType, NlpContext.ModuleViewType moduleViewType, Object config);

    /**
     * 注册组件上下文
     *
     * @param nlpContextId NLP上下文ID
     * @param componentContext 组件上下文
     */
    void registerComponentContext(String nlpContextId, NlpComponentContext componentContext);

    /**
     * 获取组件上下文
     *
     * @param nlpContextId NLP上下文ID
     * @param componentId 组件ID
     * @return 组件上下文
     */
    NlpComponentContext getComponentContext(String nlpContextId, String componentId);

    /**
     * 设置活跃组件
     *
     * @param nlpContextId NLP上下文ID
     * @param componentId 组件ID
     */
    void setActiveComponent(String nlpContextId, String componentId);

    /**
     * 获取活跃组件
     *
     * @param nlpContextId NLP上下文ID
     * @return 活跃组件上下文
     */
    NlpComponentContext getActiveComponent(String nlpContextId);

    /**
     * 设置表达式变量
     *
     * @param nlpContextId NLP上下文ID
     * @param name 变量名
     * @param value 变量值
     */
    void setExpressionVariable(String nlpContextId, String name, Object value);

    /**
     * 求值表达式
     *
     * @param nlpContextId NLP上下文ID
     * @param expression 表达式
     * @return 求值结果
     */
    Object evaluateExpression(String nlpContextId, String expression);

    /**
     * 获取NLP上下文
     *
     * @param nlpContextId NLP上下文ID
     * @return NLP上下文
     */
    NlpContext getNlpContext(String nlpContextId);

    /**
     * 更新NLP上下文
     *
     * @param nlpContext NLP上下文
     */
    void updateNlpContext(NlpContext nlpContext);

    /**
     * 销毁NLP上下文
     *
     * @param nlpContextId NLP上下文ID
     */
    void destroyNlpContext(String nlpContextId);
}

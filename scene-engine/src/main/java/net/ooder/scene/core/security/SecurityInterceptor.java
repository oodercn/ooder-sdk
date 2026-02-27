package net.ooder.scene.core.security;

import java.util.Map;

/**
 * 安全拦截器
 *
 * <p>拦截所有 Skill 操作，进行安全检查</p>
 */
public interface SecurityInterceptor {

    /**
     * 前置拦截
     *
     * @param context 操作上下文
     * @param request 技能请求
     * @return 是否允许执行
     */
    InterceptorResult beforeExecute(OperationContext context, SkillRequest request);

    /**
     * 后置拦截
     *
     * @param context 操作上下文
     * @param request 技能请求
     * @param response 技能响应
     */
    void afterExecute(OperationContext context, SkillRequest request, SkillResponse response);

    /**
     * 异常拦截
     *
     * @param context 操作上下文
     * @param request 技能请求
     * @param error 异常信息
     */
    void onError(OperationContext context, SkillRequest request, Throwable error);

    /**
     * 获取拦截器优先级
     */
    int getOrder();
}

package net.ooder.sdk.core.event;

/**
 * Core 层事件监听器接口
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li><b>只观察</b>：只能观察事件，不能修改事件状态</li>
 *   <li><b>无阻塞</b>：监听逻辑应该快速执行，不能阻塞主流程</li>
 *   <li><b>无异常</b>：监听方法不应抛出异常，异常会被捕获并记录</li>
 * </ul>
 *
 * @param <T> 监听的事件类型
 * @author Ooder Team
 * @version 2.3
 */
@FunctionalInterface
public interface CoreEventListener<T extends CoreEvent> {

    /**
     * 处理事件
     *
     * <p>注意：</p>
     * <ul>
     *   <li>此方法不能修改传入的事件对象</li>
     *   <li>此方法不能抛出异常，所有异常会被 EventBean 捕获</li>
     *   <li>此方法应该快速完成，避免阻塞事件发布线程</li>
     * </ul>
     *
     * @param event 事件对象（只读）
     */
    void onEvent(T event);

    /**
     * 获取监听的事件类型
     *
     * <p>用于 EventBean 进行事件路由</p>
     *
     * @return 事件类型 Class
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEventType() {
        // 通过反射获取泛型类型
        return (Class<T>) CoreEvent.class;
    }

    /**
     * 获取监听器优先级
     *
     * <p>优先级高的监听器先执行</p>
     *
     * @return 优先级数值，越大优先级越高
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 是否异步执行
     *
     * <p>如果返回 true，事件将在单独的线程池中异步处理</p>
     *
     * @return 是否异步
     */
    default boolean isAsync() {
        return false;
    }

    /**
     * 获取监听器名称（用于日志和监控）
     *
     * @return 监听器名称
     */
    default String getListenerName() {
        return this.getClass().getSimpleName();
    }
}

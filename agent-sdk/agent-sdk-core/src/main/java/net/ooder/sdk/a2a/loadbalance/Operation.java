package net.ooder.sdk.a2a.loadbalance;

/**
 * 操作接口
 *
 * @version 2.3.1
 * @since 2.3.1
 */
@FunctionalInterface
public interface Operation<T> {

    /**
     * 执行操作
     *
     * @return 操作结果
     * @throws Exception 执行异常
     */
    T execute() throws Exception;
}

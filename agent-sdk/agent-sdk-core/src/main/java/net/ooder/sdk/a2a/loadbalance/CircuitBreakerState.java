package net.ooder.sdk.a2a.loadbalance;

/**
 * 熔断器状态
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public enum CircuitBreakerState {

    CLOSED,
    OPEN,
    HALF_OPEN
}

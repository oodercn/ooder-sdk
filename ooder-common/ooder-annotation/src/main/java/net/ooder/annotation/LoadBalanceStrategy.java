package net.ooder.annotation;

/**
 * 负载均衡策略枚举
 * 定义服务调用的负载均衡方式
 */
public enum LoadBalanceStrategy {
    RANDOM, ROUND_ROBIN, LEAST_CONNECTED, WEIGHTED, CONSISTENT_HASH
}
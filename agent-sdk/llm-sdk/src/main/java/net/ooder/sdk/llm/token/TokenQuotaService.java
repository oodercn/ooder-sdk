package net.ooder.sdk.llm.token;

/**
 * Token 配额服务
 *
 * @version 2.3.1
 * @since 2.3.1
 */
public interface TokenQuotaService {

    /**
     * 检查配额
     *
     * @param quotaRequest 配额请求
     * @return 配额检查结果
     */
    QuotaCheckResult checkQuota(QuotaRequest quotaRequest);

    /**
     * 消耗配额
     *
     * @param consumption 消耗详情
     * @return 实际消耗量
     */
    int consumeQuota(TokenConsumption consumption);

    /**
     * 获取配额使用统计
     *
     * @param scope 统计范围
     * @return 使用统计
     */
    QuotaUsageStats getUsageStats(QuotaScope scope);

    /**
     * 设置配额
     *
     * @param scope 范围
     * @param quota 配额值
     */
    void setQuota(QuotaScope scope, int quota);

    /**
     * 重置配额
     *
     * @param scope 范围
     */
    void resetQuota(QuotaScope scope);

    /**
     * 预留配额
     *
     * @param quotaRequest 配额请求
     * @return 预留ID
     */
    String reserveQuota(QuotaRequest quotaRequest);

    /**
     * 释放预留配额
     *
     * @param reservationId 预留ID
     */
    void releaseReservation(String reservationId);

    /**
     * 确认预留配额
     *
     * @param reservationId 预留ID
     * @param actualConsumption 实际消耗
     */
    void confirmReservation(String reservationId, int actualConsumption);
}

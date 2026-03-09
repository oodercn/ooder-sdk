package net.ooder.scene.core.decision;

/**
 * 决策引擎接口
 * 
 * <p>支持离线/在线切换的智能决策引擎</p>
 * 
 * <p>三种工作模式：</p>
 * <ul>
 *   <li>ONLINE_ONLY - 仅在线决策，依赖LLM</li>
 *   <li>OFFLINE_ONLY - 仅离线决策，依赖规则引擎</li>
 *   <li>ONLINE_FIRST - 优先在线，降级离线（默认）</li>
 * </ul>
 *
 * @author ooder Team
 * @since 2.3.1
 */
public interface DecisionEngine {

    /**
     * 执行决策
     * 
     * <p>根据当前模式选择决策方式：</p>
     * <ul>
     *   <li>ONLINE_ONLY: 直接使用LLM决策</li>
     *   <li>OFFLINE_ONLY: 直接使用规则引擎决策</li>
     *   <li>ONLINE_FIRST: 优先LLM，失败降级到规则引擎</li>
     * </ul>
     *
     * @param context 决策上下文
     * @return 决策结果
     */
    DecisionResult decide(DecisionContext context);

    /**
     * 检查LLM是否可用
     *
     * @return LLM是否可用
     */
    boolean isLlmAvailable();

    /**
     * 获取当前决策模式
     *
     * @return 当前模式
     */
    DecisionMode getMode();

    /**
     * 设置决策模式
     *
     * @param mode 决策模式
     */
    void setMode(DecisionMode mode);

    /**
     * 获取引擎名称
     *
     * @return 引擎名称
     */
    String getName();

    /**
     * 获取引擎版本
     *
     * @return 引擎版本
     */
    String getVersion();

    /**
     * 重置引擎状态
     */
    void reset();

    /**
     * 获取决策统计信息
     *
     * @return 统计信息
     */
    DecisionStats getStats();

    /**
     * 决策统计信息
     */
    class DecisionStats {
        private long totalDecisions;
        private long successfulDecisions;
        private long failedDecisions;
        private long llmDecisions;
        private long ruleDecisions;
        private long cacheHits;
        private double averageLatencyMs;

        public long getTotalDecisions() {
            return totalDecisions;
        }

        public void setTotalDecisions(long totalDecisions) {
            this.totalDecisions = totalDecisions;
        }

        public long getSuccessfulDecisions() {
            return successfulDecisions;
        }

        public void setSuccessfulDecisions(long successfulDecisions) {
            this.successfulDecisions = successfulDecisions;
        }

        public long getFailedDecisions() {
            return failedDecisions;
        }

        public void setFailedDecisions(long failedDecisions) {
            this.failedDecisions = failedDecisions;
        }

        public long getLlmDecisions() {
            return llmDecisions;
        }

        public void setLlmDecisions(long llmDecisions) {
            this.llmDecisions = llmDecisions;
        }

        public long getRuleDecisions() {
            return ruleDecisions;
        }

        public void setRuleDecisions(long ruleDecisions) {
            this.ruleDecisions = ruleDecisions;
        }

        public long getCacheHits() {
            return cacheHits;
        }

        public void setCacheHits(long cacheHits) {
            this.cacheHits = cacheHits;
        }

        public double getAverageLatencyMs() {
            return averageLatencyMs;
        }

        public void setAverageLatencyMs(double averageLatencyMs) {
            this.averageLatencyMs = averageLatencyMs;
        }

        public double getSuccessRate() {
            return totalDecisions > 0 ? (double) successfulDecisions / totalDecisions : 0;
        }

        public double getLlmUsageRate() {
            return totalDecisions > 0 ? (double) llmDecisions / totalDecisions : 0;
        }

        public double getCacheHitRate() {
            return totalDecisions > 0 ? (double) cacheHits / totalDecisions : 0;
        }

        @Override
        public String toString() {
            return "DecisionStats{" +
                "total=" + totalDecisions +
                ", success=" + successfulDecisions +
                ", failed=" + failedDecisions +
                ", llm=" + llmDecisions +
                ", rule=" + ruleDecisions +
                ", cacheHits=" + cacheHits +
                ", avgLatency=" + String.format("%.2f", averageLatencyMs) + "ms" +
                '}';
        }
    }
}

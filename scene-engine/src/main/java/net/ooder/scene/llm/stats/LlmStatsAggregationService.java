package net.ooder.scene.llm.stats;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * LLM 统计聚合服务
 * 
 * <p>提供四级维度的统计聚合能力。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LlmStatsAggregationService {
    
    /**
     * 获取公司级统计
     */
    CompletableFuture<LlmCompanyStats> getCompanyStats(String companyId, StatsTimeRange timeRange);
    
    /**
     * 获取部门级统计
     */
    CompletableFuture<LlmDepartmentStats> getDepartmentStats(String departmentId, StatsTimeRange timeRange);
    
    /**
     * 获取用户级统计
     */
    CompletableFuture<LlmUserStats> getUserStats(String userId, StatsTimeRange timeRange);
    
    /**
     * 获取模块级统计
     */
    CompletableFuture<LlmModuleStats> getModuleStats(String moduleId, String userId, StatsTimeRange timeRange);
    
    /**
     * 获取公司下所有部门排名
     */
    CompletableFuture<List<LlmDepartmentStats>> getDepartmentRanking(String companyId, StatsTimeRange timeRange, int limit);
    
    /**
     * 获取部门下所有用户排名
     */
    CompletableFuture<List<LlmUserStats>> getUserRanking(String departmentId, StatsTimeRange timeRange, int limit);
    
    /**
     * 获取用户下所有模块排名
     */
    CompletableFuture<List<LlmModuleStats>> getModuleRanking(String userId, StatsTimeRange timeRange, int limit);
    
    /**
     * 刷新统计缓存
     */
    CompletableFuture<Void> refreshStats(String companyId);
}

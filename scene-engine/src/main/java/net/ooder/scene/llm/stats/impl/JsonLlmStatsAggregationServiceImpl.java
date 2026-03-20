package net.ooder.scene.llm.stats.impl;

import net.ooder.scene.llm.audit.LlmAuditService;
import net.ooder.scene.llm.audit.LlmCallLog;
import net.ooder.scene.llm.audit.LlmLogQuery;
import net.ooder.scene.llm.stats.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * LLM 统计聚合服务 - JSON 实现
 * 
 * <p>基于 LlmAuditService 实现统计聚合。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonLlmStatsAggregationServiceImpl implements LlmStatsAggregationService {
    
    private static final Logger log = LoggerFactory.getLogger(JsonLlmStatsAggregationServiceImpl.class);
    
    private final LlmAuditService auditService;
    
    public JsonLlmStatsAggregationServiceImpl(LlmAuditService auditService) {
        this.auditService = auditService;
    }
    
    @Override
    public CompletableFuture<LlmCompanyStats> getCompanyStats(String companyId, StatsTimeRange timeRange) {
        return auditService.getCompanyLlmStats(companyId, 
            timeRange.getStartTime(), timeRange.getEndTime());
    }
    
    @Override
    public CompletableFuture<LlmDepartmentStats> getDepartmentStats(String departmentId, StatsTimeRange timeRange) {
        return auditService.getDepartmentLlmStats(departmentId, 
            timeRange.getStartTime(), timeRange.getEndTime());
    }
    
    @Override
    public CompletableFuture<LlmUserStats> getUserStats(String userId, StatsTimeRange timeRange) {
        return auditService.getUserLlmStats(userId, 
            timeRange.getStartTime(), timeRange.getEndTime());
    }
    
    @Override
    public CompletableFuture<LlmModuleStats> getModuleStats(String moduleId, String userId, StatsTimeRange timeRange) {
        return auditService.getModuleLlmStats(moduleId, userId, 
            timeRange.getStartTime(), timeRange.getEndTime());
    }
    
    @Override
    public CompletableFuture<List<LlmDepartmentStats>> getDepartmentRanking(String companyId, StatsTimeRange timeRange, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            LlmLogQuery query = new LlmLogQuery();
            query.setCompanyId(companyId);
            query.setStartTime(timeRange.getStartTime());
            query.setEndTime(timeRange.getEndTime());
            
            List<LlmCallLog> logs = auditService.queryLlmLogs(query).join();
            
            Map<String, LlmDepartmentStats> statsMap = new HashMap<>();
            
            for (LlmCallLog logEntry : logs) {
                String deptId = logEntry.getDepartmentId();
                if (deptId == null) continue;
                
                LlmDepartmentStats stats = statsMap.computeIfAbsent(deptId, id -> {
                    LlmDepartmentStats s = new LlmDepartmentStats();
                    s.setDepartmentId(id);
                    s.setDepartmentName(logEntry.getDepartmentName());
                    s.setCompanyId(companyId);
                    return s;
                });
                
                stats.setTotalCalls(stats.getTotalCalls() + 1);
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    stats.setSuccessCalls(stats.getSuccessCalls() + 1);
                } else {
                    stats.setFailedCalls(stats.getFailedCalls() + 1);
                }
                stats.setTotalInputTokens(stats.getTotalInputTokens() + logEntry.getInputTokens());
                stats.setTotalOutputTokens(stats.getTotalOutputTokens() + logEntry.getOutputTokens());
                stats.setTotalTokens(stats.getTotalTokens() + logEntry.getTotalTokens());
                stats.setTotalCost(stats.getTotalCost() + logEntry.getCost());
            }
            
            return statsMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalCalls(), a.getTotalCalls()))
                .limit(limit)
                .collect(Collectors.toList());
        });
    }
    
    @Override
    public CompletableFuture<List<LlmUserStats>> getUserRanking(String departmentId, StatsTimeRange timeRange, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            LlmLogQuery query = new LlmLogQuery();
            query.setDepartmentId(departmentId);
            query.setStartTime(timeRange.getStartTime());
            query.setEndTime(timeRange.getEndTime());
            
            List<LlmCallLog> logs = auditService.queryLlmLogs(query).join();
            
            Map<String, LlmUserStats> statsMap = new HashMap<>();
            
            for (LlmCallLog logEntry : logs) {
                String userId = logEntry.getUserId();
                if (userId == null) continue;
                
                LlmUserStats stats = statsMap.computeIfAbsent(userId, id -> {
                    LlmUserStats s = new LlmUserStats();
                    s.setUserId(id);
                    s.setUserName(logEntry.getUserName());
                    s.setDepartmentId(departmentId);
                    s.setDepartmentName(logEntry.getDepartmentName());
                    return s;
                });
                
                stats.setTotalCalls(stats.getTotalCalls() + 1);
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    stats.setSuccessCalls(stats.getSuccessCalls() + 1);
                } else {
                    stats.setFailedCalls(stats.getFailedCalls() + 1);
                }
                stats.setTotalInputTokens(stats.getTotalInputTokens() + logEntry.getInputTokens());
                stats.setTotalOutputTokens(stats.getTotalOutputTokens() + logEntry.getOutputTokens());
                stats.setTotalTokens(stats.getTotalTokens() + logEntry.getTotalTokens());
                stats.setTotalCost(stats.getTotalCost() + logEntry.getCost());
            }
            
            return statsMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalCalls(), a.getTotalCalls()))
                .limit(limit)
                .collect(Collectors.toList());
        });
    }
    
    @Override
    public CompletableFuture<List<LlmModuleStats>> getModuleRanking(String userId, StatsTimeRange timeRange, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            LlmLogQuery query = new LlmLogQuery();
            query.setUserId(userId);
            query.setStartTime(timeRange.getStartTime());
            query.setEndTime(timeRange.getEndTime());
            
            List<LlmCallLog> logs = auditService.queryLlmLogs(query).join();
            
            Map<String, LlmModuleStats> statsMap = new HashMap<>();
            
            for (LlmCallLog logEntry : logs) {
                String moduleId = logEntry.getModuleId();
                if (moduleId == null) continue;
                
                LlmModuleStats stats = statsMap.computeIfAbsent(moduleId, id -> {
                    LlmModuleStats s = new LlmModuleStats();
                    s.setModuleId(id);
                    s.setModuleName(logEntry.getModuleName());
                    s.setUserId(userId);
                    return s;
                });
                
                stats.setTotalCalls(stats.getTotalCalls() + 1);
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    stats.setSuccessCalls(stats.getSuccessCalls() + 1);
                } else {
                    stats.setFailedCalls(stats.getFailedCalls() + 1);
                }
                stats.setTotalInputTokens(stats.getTotalInputTokens() + logEntry.getInputTokens());
                stats.setTotalOutputTokens(stats.getTotalOutputTokens() + logEntry.getOutputTokens());
                stats.setTotalTokens(stats.getTotalTokens() + logEntry.getTotalTokens());
                stats.setTotalCost(stats.getTotalCost() + logEntry.getCost());
            }
            
            return statsMap.values().stream()
                .sorted((a, b) -> Long.compare(b.getTotalCalls(), a.getTotalCalls()))
                .limit(limit)
                .collect(Collectors.toList());
        });
    }
    
    @Override
    public CompletableFuture<Void> refreshStats(String companyId) {
        return CompletableFuture.completedFuture(null);
    }
}

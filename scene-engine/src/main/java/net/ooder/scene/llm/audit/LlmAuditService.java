package net.ooder.scene.llm.audit;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.ooder.scene.llm.stats.LlmUserStats;
import net.ooder.scene.llm.stats.LlmDepartmentStats;
import net.ooder.scene.llm.stats.LlmCompanyStats;
import net.ooder.scene.llm.stats.LlmModuleStats;

/**
 * LLM 调用审计服务
 * 
 * <p>提供 LLM 调用的审计记录和统计能力。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public interface LlmAuditService {
    
    /**
     * 记录 LLM 调用
     */
    void logLlmCall(LlmCallContext context, LlmCallResult result);
    
    /**
     * 查询 LLM 调用日志
     */
    CompletableFuture<List<LlmCallLog>> queryLlmLogs(LlmLogQuery query);
    
    /**
     * 获取用户 LLM 统计
     */
    CompletableFuture<LlmUserStats> getUserLlmStats(String userId, long startTime, long endTime);
    
    /**
     * 获取部门 LLM 统计
     */
    CompletableFuture<LlmDepartmentStats> getDepartmentLlmStats(String departmentId, long startTime, long endTime);
    
    /**
     * 获取公司 LLM 统计
     */
    CompletableFuture<LlmCompanyStats> getCompanyLlmStats(String companyId, long startTime, long endTime);
    
    /**
     * 获取模块 LLM 统计
     */
    CompletableFuture<LlmModuleStats> getModuleLlmStats(String moduleId, String userId, long startTime, long endTime);
}

package net.ooder.scene.llm.audit.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.ooder.scene.llm.audit.*;
import net.ooder.scene.llm.stats.LlmCompanyStats;
import net.ooder.scene.llm.stats.LlmDepartmentStats;
import net.ooder.scene.llm.stats.LlmModuleStats;
import net.ooder.scene.llm.stats.LlmTrendStats;
import net.ooder.scene.llm.stats.LlmUserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * LLM 调用审计服务 - JSON 文件实现
 * 
 * <p>基于 JSON 文件存储，适用于轻量级部署场景。</p>
 * 
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class JsonLlmAuditServiceImpl implements LlmAuditService {
    
    private static final Logger log = LoggerFactory.getLogger(JsonLlmAuditServiceImpl.class);
    
    private final ObjectMapper objectMapper;
    private final File dataDir;
    private final List<LlmCallLog> callLogs = new CopyOnWriteArrayList<>();
    private final Map<String, LlmCallLog> logIndex = new ConcurrentHashMap<>();
    
    private static final int MAX_LOG_SIZE = 10000;
    
    public JsonLlmAuditServiceImpl(String dataPath) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.dataDir = new File(dataPath);
        
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        loadLogs();
    }
    
    private void loadLogs() {
        File file = new File(dataDir, "llm-call-logs.json");
        if (file.exists()) {
            try {
                LlmCallLog[] array = objectMapper.readValue(file, LlmCallLog[].class);
                for (LlmCallLog logEntry : array) {
                    callLogs.add(logEntry);
                    logIndex.put(logEntry.getLogId(), logEntry);
                }
                JsonLlmAuditServiceImpl.log.info("Loaded {} LLM call logs", callLogs.size());
            } catch (IOException e) {
                JsonLlmAuditServiceImpl.log.warn("Failed to load LLM call logs: {}", e.getMessage());
            }
        }
    }
    
    private void saveLogs() {
        try {
            File file = new File(dataDir, "llm-call-logs.json");
            objectMapper.writeValue(file, callLogs);
        } catch (IOException e) {
            log.error("Failed to save LLM call logs: {}", e.getMessage());
        }
    }
    
    @Override
    public void logLlmCall(LlmCallContext context, LlmCallResult result) {
        LlmCallLog logEntry = new LlmCallLog();
        logEntry.setLogId("log-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8));
        logEntry.setCompanyId(context.getCompanyId());
        logEntry.setCompanyName(context.getCompanyName());
        logEntry.setDepartmentId(context.getDepartmentId());
        logEntry.setDepartmentName(context.getDepartmentName());
        logEntry.setUserId(context.getUserId());
        logEntry.setUserName(context.getUserName());
        logEntry.setSceneId(context.getSceneId());
        logEntry.setSceneName(context.getSceneName());
        logEntry.setCapabilityId(context.getCapabilityId());
        logEntry.setCapabilityName(context.getCapabilityName());
        logEntry.setModuleId(context.getModuleId());
        logEntry.setModuleName(context.getModuleName());
        logEntry.setBusinessType(context.getBusinessType());
        logEntry.setClientIp(context.getClientIp());
        logEntry.setSessionId(context.getSessionId());
        logEntry.setRequestId(context.getRequestId());
        
        logEntry.setProviderId(result.getProviderId());
        logEntry.setProviderName(result.getProviderName());
        logEntry.setModel(result.getModel());
        logEntry.setRequestType(result.getRequestType());
        logEntry.setInputTokens(result.getInputTokens());
        logEntry.setOutputTokens(result.getOutputTokens());
        logEntry.setTotalTokens(result.getTotalTokens());
        logEntry.setCost(result.getCost());
        logEntry.setLatency(result.getLatency());
        logEntry.setStatus(result.getStatus());
        logEntry.setErrorMessage(result.getErrorMessage());
        logEntry.setTimestamp(System.currentTimeMillis());
        
        callLogs.add(0, logEntry);
        logIndex.put(logEntry.getLogId(), logEntry);
        
        while (callLogs.size() > MAX_LOG_SIZE) {
            LlmCallLog removed = callLogs.remove(callLogs.size() - 1);
            logIndex.remove(removed.getLogId());
        }
        
        saveLogs();
        log.debug("Logged LLM call: {} by user {}", logEntry.getLogId(), context.getUserId());
    }
    
    @Override
    public CompletableFuture<List<LlmCallLog>> queryLlmLogs(LlmLogQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<LlmCallLog> result = new ArrayList<>();
            for (LlmCallLog logEntry : callLogs) {
                if (query == null) {
                    result.add(logEntry);
                } else {
                    if (query.getCompanyId() != null && !query.getCompanyId().equals(logEntry.getCompanyId())) continue;
                    if (query.getDepartmentId() != null && !query.getDepartmentId().equals(logEntry.getDepartmentId())) continue;
                    if (query.getUserId() != null && !query.getUserId().equals(logEntry.getUserId())) continue;
                    if (query.getModuleId() != null && !query.getModuleId().equals(logEntry.getModuleId())) continue;
                    if (query.getStartTime() > 0 && logEntry.getTimestamp() < query.getStartTime()) continue;
                    if (query.getEndTime() > 0 && logEntry.getTimestamp() > query.getEndTime()) continue;
                    result.add(logEntry);
                }
            }
            return result;
        });
    }
    
    @Override
    public CompletableFuture<LlmUserStats> getUserLlmStats(String userId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            LlmUserStats stats = new LlmUserStats();
            stats.setUserId(userId);
            stats.setStatsTime(System.currentTimeMillis());
            
            long totalCalls = 0, successCalls = 0, failedCalls = 0;
            long totalInputTokens = 0, totalOutputTokens = 0;
            double totalCost = 0;
            long totalLatency = 0;
            
            for (LlmCallLog logEntry : callLogs) {
                if (!userId.equals(logEntry.getUserId())) continue;
                if (startTime > 0 && logEntry.getTimestamp() < startTime) continue;
                if (endTime > 0 && logEntry.getTimestamp() > endTime) continue;
                
                totalCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    successCalls++;
                } else {
                    failedCalls++;
                }
                totalInputTokens += logEntry.getInputTokens();
                totalOutputTokens += logEntry.getOutputTokens();
                totalCost += logEntry.getCost();
                totalLatency += logEntry.getLatency();
            }
            
            stats.setTotalCalls(totalCalls);
            stats.setSuccessCalls(successCalls);
            stats.setFailedCalls(failedCalls);
            stats.setSuccessRate(totalCalls > 0 ? (double) successCalls / totalCalls : 0);
            stats.setTotalInputTokens(totalInputTokens);
            stats.setTotalOutputTokens(totalOutputTokens);
            stats.setTotalTokens(totalInputTokens + totalOutputTokens);
            stats.setTotalCost(totalCost);
            stats.setAvgLatency(totalCalls > 0 ? (double) totalLatency / totalCalls : 0);
            
            return stats;
        });
    }
    
    @Override
    public CompletableFuture<LlmDepartmentStats> getDepartmentLlmStats(String departmentId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            LlmDepartmentStats stats = new LlmDepartmentStats();
            stats.setDepartmentId(departmentId);
            stats.setStatsTime(System.currentTimeMillis());
            
            long totalCalls = 0, successCalls = 0, failedCalls = 0;
            long totalInputTokens = 0, totalOutputTokens = 0;
            double totalCost = 0;
            long totalLatency = 0;
            
            for (LlmCallLog logEntry : callLogs) {
                if (!departmentId.equals(logEntry.getDepartmentId())) continue;
                if (startTime > 0 && logEntry.getTimestamp() < startTime) continue;
                if (endTime > 0 && logEntry.getTimestamp() > endTime) continue;
                
                totalCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    successCalls++;
                } else {
                    failedCalls++;
                }
                totalInputTokens += logEntry.getInputTokens();
                totalOutputTokens += logEntry.getOutputTokens();
                totalCost += logEntry.getCost();
                totalLatency += logEntry.getLatency();
            }
            
            stats.setTotalCalls(totalCalls);
            stats.setSuccessCalls(successCalls);
            stats.setFailedCalls(failedCalls);
            stats.setSuccessRate(totalCalls > 0 ? (double) successCalls / totalCalls : 0);
            stats.setTotalInputTokens(totalInputTokens);
            stats.setTotalOutputTokens(totalOutputTokens);
            stats.setTotalTokens(totalInputTokens + totalOutputTokens);
            stats.setTotalCost(totalCost);
            stats.setAvgLatency(totalCalls > 0 ? (double) totalLatency / totalCalls : 0);
            
            return stats;
        });
    }
    
    @Override
    public CompletableFuture<LlmCompanyStats> getCompanyLlmStats(String companyId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            LlmCompanyStats stats = new LlmCompanyStats();
            stats.setCompanyId(companyId);
            stats.setStatsTime(System.currentTimeMillis());
            stats.setStartTime(startTime);
            stats.setEndTime(endTime);
            
            long totalCalls = 0, successCalls = 0, failedCalls = 0;
            long totalInputTokens = 0, totalOutputTokens = 0;
            double totalCost = 0;
            long totalLatency = 0;
            long maxLatency = 0, minLatency = Long.MAX_VALUE;
            
            for (LlmCallLog logEntry : callLogs) {
                if (!companyId.equals(logEntry.getCompanyId())) continue;
                if (startTime > 0 && logEntry.getTimestamp() < startTime) continue;
                if (endTime > 0 && logEntry.getTimestamp() > endTime) continue;
                
                totalCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    successCalls++;
                } else {
                    failedCalls++;
                }
                totalInputTokens += logEntry.getInputTokens();
                totalOutputTokens += logEntry.getOutputTokens();
                totalCost += logEntry.getCost();
                totalLatency += logEntry.getLatency();
                maxLatency = Math.max(maxLatency, logEntry.getLatency());
                minLatency = Math.min(minLatency, logEntry.getLatency());
            }
            
            stats.setTotalCalls(totalCalls);
            stats.setSuccessCalls(successCalls);
            stats.setFailedCalls(failedCalls);
            stats.setSuccessRate(totalCalls > 0 ? (double) successCalls / totalCalls : 0);
            stats.setTotalInputTokens(totalInputTokens);
            stats.setTotalOutputTokens(totalOutputTokens);
            stats.setTotalTokens(totalInputTokens + totalOutputTokens);
            stats.setTotalCost(totalCost);
            stats.setAvgLatency(totalCalls > 0 ? (double) totalLatency / totalCalls : 0);
            stats.setMaxLatency(maxLatency == 0 ? 0 : maxLatency);
            stats.setMinLatency(minLatency == Long.MAX_VALUE ? 0 : minLatency);
            
            return stats;
        });
    }
    
    @Override
    public CompletableFuture<LlmModuleStats> getModuleLlmStats(String moduleId, String userId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            LlmModuleStats stats = new LlmModuleStats();
            stats.setModuleId(moduleId);
            stats.setUserId(userId);
            stats.setStatsTime(System.currentTimeMillis());
            
            long totalCalls = 0, successCalls = 0, failedCalls = 0;
            long totalInputTokens = 0, totalOutputTokens = 0;
            double totalCost = 0;
            long totalLatency = 0;
            Map<String, Long> providerDist = new HashMap<>();
            Map<String, Long> modelDist = new HashMap<>();
            
            for (LlmCallLog logEntry : callLogs) {
                if (!moduleId.equals(logEntry.getModuleId())) continue;
                if (userId != null && !userId.equals(logEntry.getUserId())) continue;
                if (startTime > 0 && logEntry.getTimestamp() < startTime) continue;
                if (endTime > 0 && logEntry.getTimestamp() > endTime) continue;
                
                totalCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    successCalls++;
                } else {
                    failedCalls++;
                }
                totalInputTokens += logEntry.getInputTokens();
                totalOutputTokens += logEntry.getOutputTokens();
                totalCost += logEntry.getCost();
                totalLatency += logEntry.getLatency();
                
                providerDist.merge(logEntry.getProviderName(), 1L, Long::sum);
                modelDist.merge(logEntry.getModel(), 1L, Long::sum);
            }
            
            stats.setTotalCalls(totalCalls);
            stats.setSuccessCalls(successCalls);
            stats.setFailedCalls(failedCalls);
            stats.setSuccessRate(totalCalls > 0 ? (double) successCalls / totalCalls : 0);
            stats.setTotalInputTokens(totalInputTokens);
            stats.setTotalOutputTokens(totalOutputTokens);
            stats.setTotalTokens(totalInputTokens + totalOutputTokens);
            stats.setTotalCost(totalCost);
            stats.setAvgLatency(totalCalls > 0 ? (double) totalLatency / totalCalls : 0);
            stats.setProviderDistribution(providerDist);
            stats.setModelDistribution(modelDist);
            
            return stats;
        });
    }
    
    @Override
    public CompletableFuture<LlmTrendStats> getTrendStats(String companyId, long startTime, long endTime) {
        return CompletableFuture.supplyAsync(() -> {
            LlmTrendStats stats = new LlmTrendStats();
            stats.setCompanyId(companyId);
            stats.setStartTime(startTime);
            stats.setEndTime(endTime);
            stats.setStatsTime(System.currentTimeMillis());
            
            long periodDuration = endTime - startTime;
            long previousStartTime = startTime - periodDuration;
            long previousEndTime = startTime;
            
            long currentCalls = 0, currentSuccessCalls = 0;
            long currentInputTokens = 0, currentOutputTokens = 0;
            double currentCost = 0;
            long currentLatency = 0;
            Map<String, Long> providerDist = new HashMap<>();
            Map<String, Long> modelDist = new HashMap<>();
            
            for (LlmCallLog logEntry : callLogs) {
                if (!companyId.equals(logEntry.getCompanyId())) continue;
                if (logEntry.getTimestamp() < startTime || logEntry.getTimestamp() > endTime) continue;
                
                currentCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    currentSuccessCalls++;
                }
                currentInputTokens += logEntry.getInputTokens();
                currentOutputTokens += logEntry.getOutputTokens();
                currentCost += logEntry.getCost();
                currentLatency += logEntry.getLatency();
                
                providerDist.merge(logEntry.getProviderName(), 1L, Long::sum);
                modelDist.merge(logEntry.getModel(), 1L, Long::sum);
            }
            
            long previousCalls = 0, previousSuccessCalls = 0;
            long previousInputTokens = 0, previousOutputTokens = 0;
            double previousCost = 0;
            long previousLatency = 0;
            
            for (LlmCallLog logEntry : callLogs) {
                if (!companyId.equals(logEntry.getCompanyId())) continue;
                if (logEntry.getTimestamp() < previousStartTime || logEntry.getTimestamp() >= previousEndTime) continue;
                
                previousCalls++;
                if ("success".equalsIgnoreCase(logEntry.getStatus())) {
                    previousSuccessCalls++;
                }
                previousInputTokens += logEntry.getInputTokens();
                previousOutputTokens += logEntry.getOutputTokens();
                previousCost += logEntry.getCost();
                previousLatency += logEntry.getLatency();
            }
            
            stats.setTotalCalls(currentCalls);
            stats.setPreviousCalls(previousCalls);
            stats.setCallsTrend(LlmTrendStats.calculateTrend(currentCalls, previousCalls));
            
            stats.setTotalTokens(currentInputTokens + currentOutputTokens);
            stats.setPreviousTokens(previousInputTokens + previousOutputTokens);
            stats.setTokensTrend(LlmTrendStats.calculateTrend(
                currentInputTokens + currentOutputTokens, 
                previousInputTokens + previousOutputTokens));
            
            stats.setTotalCost(currentCost);
            stats.setPreviousCost(previousCost);
            stats.setCostTrend(LlmTrendStats.calculateTrend(currentCost, previousCost));
            
            double currentAvgLatency = currentCalls > 0 ? (double) currentLatency / currentCalls : 0;
            double previousAvgLatency = previousCalls > 0 ? (double) previousLatency / previousCalls : 0;
            stats.setAvgLatency(currentAvgLatency);
            stats.setPreviousAvgLatency(previousAvgLatency);
            stats.setLatencyTrend(LlmTrendStats.calculateTrend(currentAvgLatency, previousAvgLatency));
            
            double currentSuccessRate = currentCalls > 0 ? (double) currentSuccessCalls / currentCalls : 0;
            double previousSuccessRate = previousCalls > 0 ? (double) previousSuccessCalls / previousCalls : 0;
            stats.setSuccessRate(currentSuccessRate);
            stats.setPreviousSuccessRate(previousSuccessRate);
            stats.setSuccessRateTrend(LlmTrendStats.calculateTrend(currentSuccessRate, previousSuccessRate));
            
            stats.setProviderDistribution(providerDist);
            stats.setModelDistribution(modelDist);
            
            return stats;
        });
    }
}

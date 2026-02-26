package net.ooder.sdk.skill.impl;

import net.ooder.sdk.skill.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeoutException;

public class SkillExecutionEngineImpl implements SkillExecutionEngine {

    private final SkillMdRegistry skillRegistry;
    private final Map<String, SkillExecutor> executors = new ConcurrentHashMap<>();
    private final List<SkillExecutionListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, SkillExecutionStats> statsMap = new ConcurrentHashMap<>();
    private final Queue<SkillExecutionRecord> recentExecutions = new ConcurrentLinkedQueue<>();

    private long defaultTimeout = 30000;
    private int maxRecentExecutions = 100;
    private final AtomicLong executionIdGenerator = new AtomicLong(0);

    public SkillExecutionEngineImpl() {
        this.skillRegistry = new SkillMdRegistryImpl();
    }

    public SkillExecutionEngineImpl(SkillMdRegistry skillRegistry) {
        this.skillRegistry = skillRegistry != null ? skillRegistry : new SkillMdRegistryImpl();
    }

    @Override
    public CompletableFuture<SkillExecutionResult> execute(String skillId, Map<String, Object> params) {
        if (skillId == null || skillId.trim().isEmpty()) {
            return CompletableFuture.completedFuture(
                SkillExecutionResult.failure(null, "Skill ID cannot be null or empty")
            );
        }

        Optional<SkillMdDocument> skillOpt = skillRegistry.getSkill(skillId);
        if (!skillOpt.isPresent()) {
            return CompletableFuture.completedFuture(
                SkillExecutionResult.failure(skillId, "Skill not found: " + skillId)
            );
        }

        return execute(skillOpt.get(), params);
    }

    @Override
    public CompletableFuture<SkillExecutionResult> execute(SkillMdDocument skill, Map<String, Object> params) {
        if (skill == null || skill.getSkillId() == null) {
            return CompletableFuture.completedFuture(
                SkillExecutionResult.failure(null, "Skill document cannot be null")
            );
        }

        String skillId = skill.getSkillId();
        String executionId = generateExecutionId();
        long startTime = System.currentTimeMillis();

        notifyExecutionStarted(skillId, params);

        SkillExecutor executor = executors.get(skillId);
        if (executor == null) {
            executor = createDefaultExecutor(skill);
        }

        if (!executor.isAvailable()) {
            SkillExecutionResult result = SkillExecutionResult.failure(skillId, "Executor is not available");
            recordExecution(executionId, skillId, params, result, startTime);
            notifyExecutionFailed(skillId, new IllegalStateException("Executor not available"));
            return CompletableFuture.completedFuture(result);
        }

        CompletableFuture<SkillExecutionResult> executionFuture = executor.execute(params != null ? params : new HashMap<>());

        // Java 8 compatible timeout implementation
        CompletableFuture<SkillExecutionResult> timeoutFuture = new CompletableFuture<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            timeoutFuture.completeExceptionally(new TimeoutException("Skill execution timed out after " + defaultTimeout + "ms"));
        }, defaultTimeout, TimeUnit.MILLISECONDS);

        CompletableFuture<SkillExecutionResult> future = executionFuture.applyToEither(timeoutFuture, result -> result)
            .whenComplete((result, throwable) -> {
                scheduler.shutdown();
                long executionTime = System.currentTimeMillis() - startTime;

                if (throwable != null) {
                    SkillExecutionResult failureResult = SkillExecutionResult.failure(skillId, throwable.getMessage());
                    failureResult.setExecutionId(executionId);
                    failureResult.setExecutionTime(executionTime);
                    recordExecution(executionId, skillId, params, failureResult, startTime);
                    updateStats(skillId, false, executionTime);
                    notifyExecutionFailed(skillId, throwable);
                } else {
                    result.setExecutionId(executionId);
                    result.setExecutionTime(executionTime);
                    recordExecution(executionId, skillId, params, result, startTime);
                    updateStats(skillId, result.isSuccess(), executionTime);
                    notifyExecutionCompleted(skillId, result);
                }
            });

        return future.exceptionally(throwable -> {
            long executionTime = System.currentTimeMillis() - startTime;
            SkillExecutionResult result = SkillExecutionResult.failure(skillId, "Execution error: " + throwable.getMessage());
            result.setExecutionId(executionId);
            result.setExecutionTime(executionTime);
            recordExecution(executionId, skillId, params, result, startTime);
            updateStats(skillId, false, executionTime);
            notifyExecutionFailed(skillId, throwable);
            return result;
        });
    }

    @Override
    public void registerExecutor(String skillId, SkillExecutor executor) {
        if (skillId == null || executor == null) {
            throw new IllegalArgumentException("Skill ID and executor cannot be null");
        }
        executors.put(skillId, executor);
    }

    @Override
    public void unregisterExecutor(String skillId) {
        if (skillId != null) {
            executors.remove(skillId);
        }
    }

    @Override
    public boolean hasExecutor(String skillId) {
        return skillId != null && executors.containsKey(skillId);
    }

    @Override
    public void setDefaultTimeout(long timeoutMillis) {
        if (timeoutMillis <= 0) {
            throw new IllegalArgumentException("Timeout must be positive");
        }
        this.defaultTimeout = timeoutMillis;
    }

    @Override
    public long getDefaultTimeout() {
        return defaultTimeout;
    }

    @Override
    public void addExecutionListener(SkillExecutionListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeExecutionListener(SkillExecutionListener listener) {
        listeners.remove(listener);
    }

    @Override
    public SkillExecutionStats getStats(String skillId) {
        return statsMap.getOrDefault(skillId, createEmptyStats(skillId));
    }

    @Override
    public List<SkillExecutionRecord> getRecentExecutions(int limit) {
        List<SkillExecutionRecord> result = new ArrayList<>();
        int count = 0;
        for (SkillExecutionRecord record : recentExecutions) {
            if (count >= limit) break;
            result.add(record);
            count++;
        }
        return result;
    }

    private String generateExecutionId() {
        return "exec-" + System.currentTimeMillis() + "-" + executionIdGenerator.incrementAndGet();
    }

    private SkillExecutor createDefaultExecutor(SkillMdDocument skill) {
        return new SkillExecutor() {
            @Override
            public CompletableFuture<SkillExecutionResult> execute(Map<String, Object> params) {
                return CompletableFuture.completedFuture(
                    SkillExecutionResult.success(skill.getSkillId(), 
                        "Default execution for skill: " + skill.getName())
                );
            }

            @Override
            public String getExecutorId() {
                return "default-" + skill.getSkillId();
            }

            @Override
            public boolean isAvailable() {
                return true;
            }
        };
    }

    private void recordExecution(String executionId, String skillId, Map<String, Object> params, 
                                  SkillExecutionResult result, long startTime) {
        SkillExecutionRecord record = new SkillExecutionRecord();
        record.setExecutionId(executionId);
        record.setSkillId(skillId);
        record.setSuccess(result.isSuccess());
        record.setExecutionTime(result.getExecutionTime());
        record.setTimestamp(startTime);
        record.setParams(params != null ? new HashMap<>(params) : new HashMap<>());
        record.setResult(result.getData());

        recentExecutions.offer(record);

        while (recentExecutions.size() > maxRecentExecutions) {
            recentExecutions.poll();
        }
    }

    private void updateStats(String skillId, boolean success, long executionTime) {
        statsMap.compute(skillId, (id, stats) -> {
            if (stats == null) {
                stats = createEmptyStats(skillId);
            }

            stats.setTotalExecutions(stats.getTotalExecutions() + 1);
            if (success) {
                stats.setSuccessCount(stats.getSuccessCount() + 1);
            } else {
                stats.setFailureCount(stats.getFailureCount() + 1);
            }

            long total = stats.getTotalExecutions();
            stats.setSuccessRate((double) stats.getSuccessCount() / total * 100);

            long currentAvg = stats.getAvgExecutionTime();
            long newAvg = (currentAvg * (total - 1) + executionTime) / total;
            stats.setAvgExecutionTime(newAvg);

            if (executionTime > stats.getMaxExecutionTime()) {
                stats.setMaxExecutionTime(executionTime);
            }

            if (stats.getMinExecutionTime() == 0 || executionTime < stats.getMinExecutionTime()) {
                stats.setMinExecutionTime(executionTime);
            }

            stats.setLastExecutionTime(System.currentTimeMillis());

            return stats;
        });
    }

    private SkillExecutionStats createEmptyStats(String skillId) {
        SkillExecutionStats stats = new SkillExecutionStats();
        stats.setSkillId(skillId);
        stats.setTotalExecutions(0);
        stats.setSuccessCount(0);
        stats.setFailureCount(0);
        stats.setSuccessRate(0.0);
        stats.setAvgExecutionTime(0);
        stats.setMaxExecutionTime(0);
        stats.setMinExecutionTime(0);
        stats.setLastExecutionTime(0);
        return stats;
    }

    private void notifyExecutionStarted(String skillId, Map<String, Object> params) {
        for (SkillExecutionListener listener : listeners) {
            try {
                listener.onExecutionStarted(skillId, params);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }

    private void notifyExecutionCompleted(String skillId, SkillExecutionResult result) {
        for (SkillExecutionListener listener : listeners) {
            try {
                listener.onExecutionCompleted(skillId, result);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }

    private void notifyExecutionFailed(String skillId, Throwable error) {
        for (SkillExecutionListener listener : listeners) {
            try {
                listener.onExecutionFailed(skillId, error);
            } catch (Exception e) {
                // Log error but don't stop notification
            }
        }
    }
}

package net.ooder.sdk.llm.broadcast;

import net.ooder.sdk.llm.model.AgentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 多智能体广播实现类
 */
public class MultiAgentBroadcastImpl implements MultiAgentBroadcast {

    private static final Logger log = LoggerFactory.getLogger(MultiAgentBroadcastImpl.class);

    private final ExecutorService executor;
    private final BroadcastStatistics statistics;
    private final AtomicInteger totalBroadcasts;

    public MultiAgentBroadcastImpl() {
        this.executor = Executors.newCachedThreadPool();
        this.statistics = new BroadcastStatistics();
        this.totalBroadcasts = new AtomicInteger(0);
    }

    @Override
    public CompletableFuture<List<AgentResponse>> broadcast(List<String> agentIds, String message) {
        return broadcastParallel(agentIds, message);
    }

    @Override
    public void broadcastStream(List<String> agentIds, String message, Consumer<AgentResponse> onResponse) {
        if (agentIds == null || agentIds.isEmpty() || onResponse == null) {
            return;
        }

        totalBroadcasts.incrementAndGet();
        log.debug("Starting stream broadcast to {} agents", agentIds.size());

        for (String agentId : agentIds) {
            executor.submit(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    AgentResponse response = sendToAgent(agentId, message, startTime);
                    onResponse.accept(response);
                } catch (Exception e) {
                    log.error("Error in stream broadcast to agent: {}", agentId, e);
                    AgentResponse errorResponse = createErrorResponse(agentId, e.getMessage(), startTime);
                    onResponse.accept(errorResponse);
                }
            });
        }
    }

    @Override
    public CompletableFuture<List<AgentResponse>> broadcastWithTimeout(
            List<String> agentIds, String message, long timeoutMs) {

        if (agentIds == null || agentIds.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        totalBroadcasts.incrementAndGet();
        log.debug("Starting timeout broadcast to {} agents with timeout {}ms", agentIds.size(), timeoutMs);

        List<CompletableFuture<AgentResponse>> futures = new ArrayList<>();

        for (String agentId : agentIds) {
            CompletableFuture<AgentResponse> future = CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    return sendToAgent(agentId, message, startTime);
                } catch (Exception e) {
                    log.error("Error broadcasting to agent: {}", agentId, e);
                    return createErrorResponse(agentId, e.getMessage(), startTime);
                }
            }, executor);

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<AgentResponse> responses = new ArrayList<>();
                    for (CompletableFuture<AgentResponse> future : futures) {
                        try {
                            responses.add(future.get());
                        } catch (Exception e) {
                            log.error("Error collecting response", e);
                        }
                    }
                    updateStatistics(responses);
                    return responses;
                });
    }

    @Override
    public CompletableFuture<List<AgentResponse>> broadcastParallel(List<String> agentIds, String message) {
        if (agentIds == null || agentIds.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        totalBroadcasts.incrementAndGet();
        log.debug("Starting parallel broadcast to {} agents", agentIds.size());

        List<CompletableFuture<AgentResponse>> futures = new ArrayList<>();

        for (String agentId : agentIds) {
            CompletableFuture<AgentResponse> future = CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                try {
                    return sendToAgent(agentId, message, startTime);
                } catch (Exception e) {
                    log.error("Error broadcasting to agent: {}", agentId, e);
                    return createErrorResponse(agentId, e.getMessage(), startTime);
                }
            }, executor);

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<AgentResponse> responses = new ArrayList<>();
                    for (CompletableFuture<AgentResponse> future : futures) {
                        try {
                            responses.add(future.get());
                        } catch (Exception e) {
                            log.error("Error collecting response", e);
                        }
                    }
                    updateStatistics(responses);
                    return responses;
                });
    }

    @Override
    public CompletableFuture<List<AgentResponse>> broadcastSequential(List<String> agentIds, String message) {
        if (agentIds == null || agentIds.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        totalBroadcasts.incrementAndGet();
        log.debug("Starting sequential broadcast to {} agents", agentIds.size());

        return CompletableFuture.supplyAsync(() -> {
            List<AgentResponse> responses = new ArrayList<>();

            for (String agentId : agentIds) {
                long startTime = System.currentTimeMillis();
                try {
                    AgentResponse response = sendToAgent(agentId, message, startTime);
                    responses.add(response);
                } catch (Exception e) {
                    log.error("Error broadcasting to agent: {}", agentId, e);
                    responses.add(createErrorResponse(agentId, e.getMessage(), startTime));
                }
            }

            updateStatistics(responses);
            return responses;
        }, executor);
    }

    @Override
    public BroadcastStatistics getStatistics() {
        BroadcastStatistics current = new BroadcastStatistics();
        current.setTotalBroadcasts(totalBroadcasts.get());
        current.setSuccessfulResponses(statistics.getSuccessfulResponses());
        current.setFailedResponses(statistics.getFailedResponses());
        current.setTotalResponseTime(statistics.getTotalResponseTime());

        int totalResponses = statistics.getSuccessfulResponses() + statistics.getFailedResponses();
        if (totalResponses > 0) {
            current.setAverageResponseTime((double) statistics.getTotalResponseTime() / totalResponses);
        } else {
            current.setAverageResponseTime(0.0);
        }

        return current;
    }

    /**
     * 发送消息到单个智能体（模拟实现）
     */
    private AgentResponse sendToAgent(String agentId, String message, long startTime) throws InterruptedException {
        // 模拟网络延迟和处理时间
        long delay = 100 + (long) (Math.random() * 500);
        Thread.sleep(delay);

        AgentResponse response = new AgentResponse();
        response.setAgentId(agentId);
        response.setAgentName("Agent-" + agentId);
        response.setContent("Response from " + agentId + " to: " + message.substring(0, Math.min(message.length(), 50)));
        response.setResponseTime(System.currentTimeMillis() - startTime);
        response.setStatus(AgentResponse.ResponseStatus.SUCCESS);

        log.debug("Received response from agent {} in {}ms", agentId, response.getResponseTime());

        return response;
    }

    /**
     * 创建错误响应
     */
    private AgentResponse createErrorResponse(String agentId, String errorMessage, long startTime) {
        AgentResponse response = new AgentResponse();
        response.setAgentId(agentId);
        response.setAgentName("Agent-" + agentId);
        response.setContent("Error: " + errorMessage);
        response.setResponseTime(System.currentTimeMillis() - startTime);
        response.setStatus(AgentResponse.ResponseStatus.ERROR);
        response.setErrorMessage(errorMessage);
        return response;
    }

    /**
     * 创建超时响应
     */
    private AgentResponse createTimeoutResponse(String agentId, long timeoutMs) {
        AgentResponse response = new AgentResponse();
        response.setAgentId(agentId);
        response.setAgentName("Agent-" + agentId);
        response.setContent("Request timed out after " + timeoutMs + "ms");
        response.setResponseTime(timeoutMs);
        response.setStatus(AgentResponse.ResponseStatus.TIMEOUT);
        response.setErrorMessage("Timeout");
        return response;
    }

    /**
     * 更新统计信息
     */
    private synchronized void updateStatistics(List<AgentResponse> responses) {
        for (AgentResponse response : responses) {
            if (response.getStatus() == AgentResponse.ResponseStatus.SUCCESS) {
                statistics.setSuccessfulResponses(statistics.getSuccessfulResponses() + 1);
            } else {
                statistics.setFailedResponses(statistics.getFailedResponses() + 1);
            }
            statistics.setTotalResponseTime(statistics.getTotalResponseTime() + response.getResponseTime());
        }
    }

    /**
     * 关闭广播服务
     */
    public void shutdown() {
        log.info("Shutting down MultiAgentBroadcast");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

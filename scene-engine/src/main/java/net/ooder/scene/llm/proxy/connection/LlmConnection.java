package net.ooder.scene.llm.proxy.connection;

import net.ooder.sdk.drivers.llm.LlmDriver;
import net.ooder.sdk.llm.model.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * LLM连接包装类
 * 封装LlmDriver，提供连接级别的管理
 */
public class LlmConnection {

    private final String connectionId;
    private final LlmDriver driver;
    private final LlmConnectionPool pool;
    private final long createdTime;
    private final AtomicBoolean inUse;
    private volatile long lastUsedTime;

    public LlmConnection(String connectionId, LlmDriver driver, LlmConnectionPool pool) {
        this.connectionId = connectionId;
        this.driver = driver;
        this.pool = pool;
        this.createdTime = System.currentTimeMillis();
        this.inUse = new AtomicBoolean(false);
        this.lastUsedTime = createdTime;
    }

    /**
     * 同步对话
     */
    public DriverChatResponse chat(DriverChatRequest request) {
        checkAvailable();
        try {
            updateLastUsedTime();
            CompletableFuture<DriverChatResponse> future = driver.chat(request);
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException("Chat failed: " + e.getMessage(), e);
        }
    }

    /**
     * 异步对话
     */
    public CompletableFuture<DriverChatResponse> chatAsync(DriverChatRequest request) {
        checkAvailable();
        updateLastUsedTime();
        return driver.chat(request);
    }

    /**
     * 流式对话
     */
    public CompletableFuture<DriverChatResponse> chatStream(DriverChatRequest request, LlmDriver.ChatStreamHandler handler) {
        checkAvailable();
        updateLastUsedTime();
        return driver.chatStream(request, handler);
    }

    /**
     * 检查连接是否可用
     */
    private void checkAvailable() {
        if (pool.isClosed()) {
            throw new IllegalStateException("Connection pool is closed");
        }
        if (!inUse.get()) {
            throw new IllegalStateException("Connection is not acquired");
        }
    }

    /**
     * 获取连接（标记为使用中）
     */
    public boolean acquire() {
        return inUse.compareAndSet(false, true);
    }

    /**
     * 释放连接
     */
    public void release() {
        inUse.set(false);
        pool.releaseConnection(this);
    }

    /**
     * 更新最后使用时间
     */
    private void updateLastUsedTime() {
        this.lastUsedTime = System.currentTimeMillis();
    }

    /**
     * 检查连接是否空闲超时
     */
    public boolean isIdleTimeout(long timeoutMs) {
        return System.currentTimeMillis() - lastUsedTime > timeoutMs;
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }

    public String getConnectionId() {
        return connectionId;
    }

    public LlmDriver getDriver() {
        return driver;
    }

    public LlmConnectionPool getPool() {
        return pool;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public boolean isInUse() {
        return inUse.get();
    }

    public long getLastUsedTime() {
        return lastUsedTime;
    }
}

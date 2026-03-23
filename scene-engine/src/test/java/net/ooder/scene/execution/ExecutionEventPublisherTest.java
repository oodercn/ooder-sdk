package net.ooder.scene.execution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionEventPublisherTest {

    private ExecutionEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new ExecutionEventPublisher();
    }

    @Test
    void testFireStarted() {
        AtomicInteger startedCount = new AtomicInteger(0);

        publisher.addGlobalListener(new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext context) {
                startedCount.incrementAndGet();
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
            }

            @Override
            public void onTimeout(ExecutionContext context) {
            }
        });

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");
        publisher.fireStarted(context);

        assertEquals(1, startedCount.get());
        assertEquals(ExecutionState.RUNNING, context.getState());
    }

    @Test
    void testFireProgress() {
        AtomicInteger progressValue = new AtomicInteger(-1);

        publisher.addGlobalListener(new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext context) {
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
                progressValue.set(progress);
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
            }

            @Override
            public void onTimeout(ExecutionContext context) {
            }
        });

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");
        publisher.fireProgress(context, 50, "Processing");

        assertEquals(50, progressValue.get());
        assertEquals(ExecutionState.PROGRESS, context.getState());
    }

    @Test
    void testFireCompleted() {
        AtomicInteger completedCount = new AtomicInteger(0);

        publisher.addGlobalListener(new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext context) {
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
                completedCount.incrementAndGet();
                assertTrue(result.isSuccess());
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
            }

            @Override
            public void onTimeout(ExecutionContext context) {
            }
        });

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");
        ExecutionResult result = ExecutionResult.success(context.getExecutionId(), "test-data");

        publisher.fireCompleted(context, result);

        assertEquals(1, completedCount.get());
        assertEquals(ExecutionState.COMPLETED, context.getState());
    }

    @Test
    void testFireFailed() {
        AtomicInteger failedCount = new AtomicInteger(0);

        publisher.addGlobalListener(new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext context) {
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
                failedCount.incrementAndGet();
                assertEquals("Test error", error.getMessage());
            }

            @Override
            public void onTimeout(ExecutionContext context) {
            }
        });

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");
        publisher.fireFailed(context, new RuntimeException("Test error"));

        assertEquals(1, failedCount.get());
        assertEquals(ExecutionState.FAILED, context.getState());
    }

    @Test
    void testFireTimeout() {
        AtomicInteger timeoutCount = new AtomicInteger(0);

        publisher.addGlobalListener(new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext context) {
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
            }

            @Override
            public void onTimeout(ExecutionContext context) {
                timeoutCount.incrementAndGet();
            }
        });

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");
        publisher.fireTimeout(context);

        assertEquals(1, timeoutCount.get());
        assertEquals(ExecutionState.TIMEOUT, context.getState());
    }

    @Test
    void testExecutionSpecificListener() {
        AtomicInteger count = new AtomicInteger(0);

        ExecutionContext context = new ExecutionContext("scene-001", "agent-001", "cap-001");

        publisher.addExecutionListener(context.getExecutionId(), new ExecutionListener() {
            @Override
            public void onStarted(ExecutionContext ctx) {
                count.incrementAndGet();
            }

            @Override
            public void onProgress(ExecutionContext context, int progress, String message) {
            }

            @Override
            public void onCompleted(ExecutionContext context, ExecutionResult result) {
            }

            @Override
            public void onFailed(ExecutionContext context, Throwable error) {
            }

            @Override
            public void onTimeout(ExecutionContext context) {
            }
        });

        publisher.fireStarted(context);

        assertEquals(1, count.get());
    }

    @Test
    void testExecutionResultFactory() {
        ExecutionResult success = ExecutionResult.success("exec-001", "data");
        assertTrue(success.isSuccess());
        assertEquals("data", success.getData());

        ExecutionResult failure = ExecutionResult.failure("exec-002", "ERR001", "Error message");
        assertFalse(failure.isSuccess());
        assertEquals("ERR001", failure.getErrorCode());
        assertEquals("Error message", failure.getMessage());
    }
}

package net.ooder.sdk.api.execution;

public interface ExecutionListener {

    void onStarted(TaskExecutionContext context);

    void onProgress(TaskExecutionContext context, int progress, String message);

    void onCompleted(TaskExecutionContext context, TaskExecutionResult result);

    void onFailed(TaskExecutionContext context, Throwable error);

    void onTimeout(TaskExecutionContext context);

    void onCancelled(TaskExecutionContext context);

    void onPaused(TaskExecutionContext context);

    void onResumed(TaskExecutionContext context);

    default String getListenerId() {
        return this.getClass().getName();
    }

    default int getOrder() {
        return 0;
    }
}

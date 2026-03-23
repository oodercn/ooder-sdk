package net.ooder.scene.execution;

public interface ExecutionListener {

    void onStarted(ExecutionContext context);

    void onProgress(ExecutionContext context, int progress, String message);

    void onCompleted(ExecutionContext context, ExecutionResult result);

    void onFailed(ExecutionContext context, Throwable error);

    void onTimeout(ExecutionContext context);

    default void onCancelled(ExecutionContext context) {
    }
}

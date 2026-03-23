package net.ooder.scene.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ExecutionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ExecutionEventPublisher.class);

    private final List<ExecutionListener> globalListeners = new CopyOnWriteArrayList<>();
    private final Map<String, List<ExecutionListener>> executionListeners = new ConcurrentHashMap<>();

    public void addGlobalListener(ExecutionListener listener) {
        if (listener != null) {
            globalListeners.add(listener);
            log.info("Global execution listener added: {}", listener.getClass().getSimpleName());
        }
    }

    public void removeGlobalListener(ExecutionListener listener) {
        globalListeners.remove(listener);
    }

    public void addExecutionListener(String executionId, ExecutionListener listener) {
        if (executionId != null && listener != null) {
            executionListeners.computeIfAbsent(executionId, k -> new CopyOnWriteArrayList<>())
                    .add(listener);
        }
    }

    public void removeExecutionListeners(String executionId) {
        if (executionId != null) {
            executionListeners.remove(executionId);
        }
    }

    public void fireStarted(ExecutionContext context) {
        if (context == null) return;

        context.setState(ExecutionState.RUNNING);
        log.info("Execution started: {}", context);

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onStarted(context);
            } catch (Exception e) {
                log.warn("Listener error onStarted: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onStarted(context);
                } catch (Exception e) {
                    log.warn("Listener error onStarted: {}", e.getMessage());
                }
            }
        }
    }

    public void fireProgress(ExecutionContext context, int progress, String message) {
        if (context == null) return;

        context.setState(ExecutionState.PROGRESS);
        log.debug("Execution progress: {} - {}% - {}", context.getExecutionId(), progress, message);

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onProgress(context, progress, message);
            } catch (Exception e) {
                log.warn("Listener error onProgress: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onProgress(context, progress, message);
                } catch (Exception e) {
                    log.warn("Listener error onProgress: {}", e.getMessage());
                }
            }
        }
    }

    public void fireCompleted(ExecutionContext context, ExecutionResult result) {
        if (context == null) return;

        context.setState(ExecutionState.COMPLETED);
        if (result != null) {
            result.setDuration(context.getElapsedTime());
        }
        log.info("Execution completed: {} - success={}", context, result != null && result.isSuccess());

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onCompleted(context, result);
            } catch (Exception e) {
                log.warn("Listener error onCompleted: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onCompleted(context, result);
                } catch (Exception e) {
                    log.warn("Listener error onCompleted: {}", e.getMessage());
                }
            }
        }

        executionListeners.remove(context.getExecutionId());
    }

    public void fireFailed(ExecutionContext context, Throwable error) {
        if (context == null) return;

        context.setState(ExecutionState.FAILED);
        log.error("Execution failed: {} - error={}", context, error != null ? error.getMessage() : "unknown");

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onFailed(context, error);
            } catch (Exception e) {
                log.warn("Listener error onFailed: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onFailed(context, error);
                } catch (Exception e) {
                    log.warn("Listener error onFailed: {}", e.getMessage());
                }
            }
        }

        executionListeners.remove(context.getExecutionId());
    }

    public void fireTimeout(ExecutionContext context) {
        if (context == null) return;

        context.setState(ExecutionState.TIMEOUT);
        log.warn("Execution timeout: {}", context);

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onTimeout(context);
            } catch (Exception e) {
                log.warn("Listener error onTimeout: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onTimeout(context);
                } catch (Exception e) {
                    log.warn("Listener error onTimeout: {}", e.getMessage());
                }
            }
        }

        executionListeners.remove(context.getExecutionId());
    }

    public void fireCancelled(ExecutionContext context) {
        if (context == null) return;

        context.setState(ExecutionState.CANCELLED);
        log.info("Execution cancelled: {}", context);

        for (ExecutionListener listener : globalListeners) {
            try {
                listener.onCancelled(context);
            } catch (Exception e) {
                log.warn("Listener error onCancelled: {}", e.getMessage());
            }
        }

        List<ExecutionListener> listeners = executionListeners.get(context.getExecutionId());
        if (listeners != null) {
            for (ExecutionListener listener : listeners) {
                try {
                    listener.onCancelled(context);
                } catch (Exception e) {
                    log.warn("Listener error onCancelled: {}", e.getMessage());
                }
            }
        }

        executionListeners.remove(context.getExecutionId());
    }

    public int getGlobalListenerCount() {
        return globalListeners.size();
    }

    public int getActiveExecutionCount() {
        return executionListeners.size();
    }
}

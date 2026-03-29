package net.ooder.scene.log.cascade;

import net.ooder.scene.log.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LogCascadeTrigger {

    void registerRule(LogCascadeRule rule);

    CompletableFuture<List<LogEntry>> process(LogEntry sourceLog);

    CompletableFuture<List<LogEntry>> getCascadeChain(String logId);

    void setEnabled(boolean enabled);

    boolean isEnabled();
}

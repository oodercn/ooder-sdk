package net.ooder.sdk.agent.installation.impl;

import net.ooder.sdk.agent.installation.InstallationSyncApi;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InstallationSyncApi 实现类（简化版）
 */
public class InstallationSyncApiImpl implements InstallationSyncApi {

    private final Map<String, InstallationStatus> installationStatuses = new ConcurrentHashMap<>();
    private final Map<String, InstallationStatusListener> listeners = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Boolean> broadcastInstallationStatus(String sceneId, InstallationStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            installationStatuses.put(sceneId, status);
            // 通知所有监听器
            for (InstallationStatusListener listener : listeners.values()) {
                listener.onStatusChanged(sceneId, status);
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<String> subscribeInstallationStatus(String sceneId, InstallationStatusListener listener) {
        return CompletableFuture.supplyAsync(() -> {
            String subscriptionId = "sub_" + System.currentTimeMillis();
            listeners.put(subscriptionId, listener);
            return subscriptionId;
        });
    }

    @Override
    public CompletableFuture<Boolean> unsubscribeInstallationStatus(String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            listeners.remove(subscriptionId);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> syncInstallationStatus(String sceneId, String targetAgentId) {
        return CompletableFuture.supplyAsync(() -> {
            InstallationStatus status = installationStatuses.get(sceneId);
            if (status != null) {
                // 这里应该通过A2A协议发送给目标Agent
                // a2aClient.send(targetAgentId, status);
            }
            return true;
        });
    }

    @Override
    public CompletableFuture<InstallationStatus> getInstallationStatus(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            return installationStatuses.getOrDefault(sceneId, createDefaultStatus(sceneId));
        });
    }

    private InstallationStatus createDefaultStatus(String sceneId) {
        InstallationStatus status = new InstallationStatus();
        status.setSceneId(sceneId);
        status.setState("UNKNOWN");
        status.setProgress(0);
        status.setTimestamp(System.currentTimeMillis());
        return status;
    }
}

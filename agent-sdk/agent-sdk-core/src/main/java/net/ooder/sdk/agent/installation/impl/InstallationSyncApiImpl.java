package net.ooder.sdk.agent.installation.impl;

import net.ooder.sdk.agent.installation.InstallationSyncApi;
import net.ooder.sdk.a2a.A2AClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InstallationSyncApi 实现类
 * 
 * @author Agent-SDK Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class InstallationSyncApiImpl implements InstallationSyncApi {

    private static final Logger logger = LoggerFactory.getLogger(InstallationSyncApiImpl.class);

    private final Map<String, InstallationStatus> installationStatuses = new ConcurrentHashMap<>();
    private final Map<String, InstallationStatusListener> listeners = new ConcurrentHashMap<>();
    private final A2AClient a2aClient;

    public InstallationSyncApiImpl() {
        this(null);
    }

    public InstallationSyncApiImpl(A2AClient a2aClient) {
        this.a2aClient = a2aClient;
        logger.info("InstallationSyncApiImpl initialized");
    }

    @Override
    public CompletableFuture<Boolean> broadcastInstallationStatus(String sceneId, InstallationStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Broadcasting installation status for scene: {}", sceneId);
                
                installationStatuses.put(sceneId, status);
                
                // 通知所有监听器
                for (InstallationStatusListener listener : listeners.values()) {
                    try {
                        listener.onStatusChanged(sceneId, status);
                    } catch (Exception e) {
                        logger.error("Error notifying listener for scene: {}", sceneId, e);
                    }
                }
                
                logger.info("Successfully broadcasted installation status for scene: {}", sceneId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to broadcast installation status for scene: {}", sceneId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<String> subscribeInstallationStatus(String sceneId, InstallationStatusListener listener) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Subscribing to installation status for scene: {}", sceneId);
                
                String subscriptionId = "sub_" + System.currentTimeMillis();
                listeners.put(subscriptionId, listener);
                
                logger.info("Successfully subscribed to installation status for scene: {}, subscriptionId: {}", sceneId, subscriptionId);
                return subscriptionId;
            } catch (Exception e) {
                logger.error("Failed to subscribe to installation status for scene: {}", sceneId, e);
                throw new RuntimeException("Subscription failed", e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> unsubscribeInstallationStatus(String subscriptionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Unsubscribing from installation status: {}", subscriptionId);
                
                listeners.remove(subscriptionId);
                
                logger.info("Successfully unsubscribed from installation status: {}", subscriptionId);
                return true;
            } catch (Exception e) {
                logger.error("Failed to unsubscribe from installation status: {}", subscriptionId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> syncInstallationStatus(String sceneId, String targetAgentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Syncing installation status for scene: {} to agent: {}", sceneId, targetAgentId);
                
                InstallationStatus status = installationStatuses.get(sceneId);
                if (status != null) {
                    // 使用A2A客户端发送状态
                    if (a2aClient != null) {
                        Map<String, Object> message = new HashMap<>();
                        message.put("type", "INSTALLATION_STATUS_SYNC");
                        message.put("sceneId", sceneId);
                        message.put("status", status);
                        
                        a2aClient.sendMessage(targetAgentId, message)
                            .thenAccept(success -> {
                                if (success) {
                                    logger.info("Successfully synced installation status for scene: {} to agent: {}", sceneId, targetAgentId);
                                } else {
                                    logger.warn("Failed to sync installation status for scene: {} to agent: {}", sceneId, targetAgentId);
                                }
                            })
                            .exceptionally(ex -> {
                                logger.error("Error syncing installation status for scene: {} to agent: {}", sceneId, targetAgentId, ex);
                                return null;
                            });
                    } else {
                        logger.warn("A2AClient not available, installation status not synced to: {}", targetAgentId);
                    }
                } else {
                    logger.warn("No installation status found for scene: {}", sceneId);
                }
                
                return true;
            } catch (Exception e) {
                logger.error("Failed to sync installation status for scene: {} to agent: {}", sceneId, targetAgentId, e);
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<InstallationStatus> getInstallationStatus(String sceneId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.debug("Getting installation status for scene: {}", sceneId);
                
                InstallationStatus status = installationStatuses.getOrDefault(sceneId, createDefaultStatus(sceneId));
                
                logger.debug("Retrieved installation status for scene: {}", sceneId);
                return status;
            } catch (Exception e) {
                logger.error("Failed to get installation status for scene: {}", sceneId, e);
                throw new RuntimeException("Failed to get installation status", e);
            }
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

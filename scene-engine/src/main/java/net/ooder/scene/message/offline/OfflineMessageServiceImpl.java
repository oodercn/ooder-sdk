package net.ooder.scene.message.offline;

import net.ooder.scene.core.PageResult;
import net.ooder.scene.message.queue.MessageEnvelope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 离线消息服务实现
 *
 * <p>提供离线消息的存储、检索和管理能力</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(OfflineMessageService.class)
public class OfflineMessageServiceImpl implements OfflineMessageService {

    private static final Logger log = LoggerFactory.getLogger(OfflineMessageServiceImpl.class);

    private static final int DEFAULT_MAX_MESSAGES_PER_RECIPIENT = 1000;
    private static final long DEFAULT_EXPIRE_HOURS = 168L;
    private static final int DEFAULT_MAX_DELIVERY_ATTEMPTS = 3;

    private final Map<String, OfflineMessage> messageStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> recipientMessages = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> typeIndex = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sceneGroupIndex = new ConcurrentHashMap<>();

    private final AtomicInteger totalMessagesStored = new AtomicInteger(0);
    private final AtomicInteger totalMessagesDelivered = new AtomicInteger(0);
    private final AtomicInteger totalMessagesAcknowledged = new AtomicInteger(0);

    private int maxMessagesPerRecipient = DEFAULT_MAX_MESSAGES_PER_RECIPIENT;
    private long expireHours = DEFAULT_EXPIRE_HOURS;
    private int maxDeliveryAttempts = DEFAULT_MAX_DELIVERY_ATTEMPTS;
    private OnlineCallback onlineCallback;

    public OfflineMessageServiceImpl() {
    }

    public void setMaxMessagesPerRecipient(int maxMessagesPerRecipient) {
        this.maxMessagesPerRecipient = maxMessagesPerRecipient;
    }

    public void setExpireHours(long expireHours) {
        this.expireHours = expireHours;
    }

    public void setMaxDeliveryAttempts(int maxDeliveryAttempts) {
        this.maxDeliveryAttempts = maxDeliveryAttempts;
    }

    @Override
    public String storeOfflineMessage(String recipientId, MessageEnvelope message) {
        if (recipientId == null || message == null) {
            return null;
        }

        cleanupOldMessagesIfNeeded(recipientId);

        String offlineMessageId = generateOfflineMessageId();
        long expireAt = System.currentTimeMillis() + (expireHours * 60 * 60 * 1000);

        OfflineMessage offlineMessage = OfflineMessage.builder()
                .offlineMessageId(offlineMessageId)
                .recipientId(recipientId)
                .envelope(message)
                .status(OfflineStatus.PENDING)
                .expireAt(expireAt)
                .build();

        messageStore.put(offlineMessageId, offlineMessage);
        recipientMessages.computeIfAbsent(recipientId, k -> ConcurrentHashMap.newKeySet()).add(offlineMessageId);

        if (message.getMessageType() != null) {
            typeIndex.computeIfAbsent(message.getMessageType(), k -> ConcurrentHashMap.newKeySet()).add(offlineMessageId);
        }

        if (message.getSceneGroupId() != null) {
            sceneGroupIndex.computeIfAbsent(message.getSceneGroupId(), k -> ConcurrentHashMap.newKeySet()).add(offlineMessageId);
        }

        totalMessagesStored.incrementAndGet();

        log.debug("Offline message stored: offlineMessageId={}, recipientId={}, messageId={}", 
                offlineMessageId, recipientId, message.getMessageId());

        return offlineMessageId;
    }

    @Override
    public List<String> storeOfflineMessages(String recipientId, List<MessageEnvelope> messages) {
        if (recipientId == null || messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> ids = new ArrayList<>();
        for (MessageEnvelope message : messages) {
            String id = storeOfflineMessage(recipientId, message);
            if (id != null) {
                ids.add(id);
            }
        }

        log.info("Batch stored {} offline messages for recipient: {}", ids.size(), recipientId);
        return ids;
    }

    @Override
    public List<OfflineMessage> getOfflineMessages(String recipientId) {
        if (recipientId == null) {
            return new ArrayList<>();
        }

        Set<String> messageIds = recipientMessages.get(recipientId);
        if (messageIds == null || messageIds.isEmpty()) {
            return new ArrayList<>();
        }

        return messageIds.stream()
                .map(messageStore::get)
                .filter(Objects::nonNull)
                .filter(m -> !m.isExpired())
                .sorted(Comparator.comparingLong(OfflineMessage::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<OfflineMessage> getOfflineMessages(String recipientId, int pageNum, int pageSize) {
        List<OfflineMessage> allMessages = getOfflineMessages(recipientId);
        
        int total = allMessages.size();
        int totalPages = (int) Math.ceil((double) total / pageSize);
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<OfflineMessage> pageData = fromIndex < total 
                ? allMessages.subList(fromIndex, toIndex) 
                : new ArrayList<>();

        PageResult<OfflineMessage> result = new PageResult<>();
        result.setItems(pageData);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotalPages(totalPages);

        return result;
    }

    @Override
    public int getOfflineMessageCount(String recipientId) {
        if (recipientId == null) {
            return 0;
        }

        Set<String> messageIds = recipientMessages.get(recipientId);
        if (messageIds == null) {
            return 0;
        }

        return (int) messageIds.stream()
                .map(messageStore::get)
                .filter(Objects::nonNull)
                .filter(m -> !m.isExpired())
                .count();
    }

    @Override
    public List<OfflineMessage> getOfflineMessagesByType(String recipientId, String messageType) {
        if (recipientId == null || messageType == null) {
            return new ArrayList<>();
        }

        Set<String> typeMessageIds = typeIndex.get(messageType);
        if (typeMessageIds == null) {
            return new ArrayList<>();
        }

        Set<String> recipientMessageIds = recipientMessages.get(recipientId);
        if (recipientMessageIds == null) {
            return new ArrayList<>();
        }

        return typeMessageIds.stream()
                .filter(recipientMessageIds::contains)
                .map(messageStore::get)
                .filter(Objects::nonNull)
                .filter(m -> !m.isExpired())
                .sorted(Comparator.comparingLong(OfflineMessage::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public List<OfflineMessage> getOfflineMessagesBySceneGroup(String recipientId, String sceneGroupId) {
        if (recipientId == null || sceneGroupId == null) {
            return new ArrayList<>();
        }

        Set<String> sceneMessageIds = sceneGroupIndex.get(sceneGroupId);
        if (sceneMessageIds == null) {
            return new ArrayList<>();
        }

        Set<String> recipientMessageIds = recipientMessages.get(recipientId);
        if (recipientMessageIds == null) {
            return new ArrayList<>();
        }

        return sceneMessageIds.stream()
                .filter(recipientMessageIds::contains)
                .map(messageStore::get)
                .filter(Objects::nonNull)
                .filter(m -> !m.isExpired())
                .sorted(Comparator.comparingLong(OfflineMessage::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public OfflineMessage getOfflineMessage(String offlineMessageId) {
        if (offlineMessageId == null) {
            return null;
        }
        return messageStore.get(offlineMessageId);
    }

    @Override
    public void acknowledgeMessage(String recipientId, String messageId) {
        if (recipientId == null || messageId == null) {
            return;
        }

        Set<String> messageIds = recipientMessages.get(recipientId);
        if (messageIds == null) {
            return;
        }

        for (String offlineMessageId : messageIds) {
            OfflineMessage offlineMessage = messageStore.get(offlineMessageId);
            if (offlineMessage != null && messageId.equals(offlineMessage.getMessageId())) {
                offlineMessage.markAcknowledged();
                totalMessagesAcknowledged.incrementAndGet();
                log.debug("Offline message acknowledged: offlineMessageId={}", offlineMessageId);
                break;
            }
        }
    }

    @Override
    public void acknowledgeMessages(String recipientId, List<String> messageIds) {
        if (recipientId == null || messageIds == null) {
            return;
        }

        for (String messageId : messageIds) {
            acknowledgeMessage(recipientId, messageId);
        }
    }

    @Override
    public void acknowledgeAllMessages(String recipientId) {
        if (recipientId == null) {
            return;
        }

        List<OfflineMessage> messages = getOfflineMessages(recipientId);
        for (OfflineMessage message : messages) {
            message.markAcknowledged();
            totalMessagesAcknowledged.incrementAndGet();
        }

        log.info("All offline messages acknowledged for recipient: {}, count={}", recipientId, messages.size());
    }

    @Override
    public int deleteExpiredMessages(String recipientId) {
        int count = 0;

        if (recipientId != null) {
            Set<String> messageIds = recipientMessages.get(recipientId);
            if (messageIds != null) {
                List<String> toDelete = messageIds.stream()
                        .map(messageStore::get)
                        .filter(Objects::nonNull)
                        .filter(OfflineMessage::isExpired)
                        .map(OfflineMessage::getOfflineMessageId)
                        .collect(Collectors.toList());

                for (String id : toDelete) {
                    deleteOfflineMessageInternal(id);
                    count++;
                }
            }
        } else {
            List<String> toDelete = messageStore.values().stream()
                    .filter(OfflineMessage::isExpired)
                    .map(OfflineMessage::getOfflineMessageId)
                    .collect(Collectors.toList());

            for (String id : toDelete) {
                deleteOfflineMessageInternal(id);
                count++;
            }
        }

        if (count > 0) {
            log.info("Deleted {} expired offline messages", count);
        }

        return count;
    }

    @Override
    public int cleanupMessagesBefore(long beforeTimestamp) {
        List<String> toDelete = messageStore.values().stream()
                .filter(m -> m.getCreatedAt() < beforeTimestamp)
                .map(OfflineMessage::getOfflineMessageId)
                .collect(Collectors.toList());

        for (String id : toDelete) {
            deleteOfflineMessageInternal(id);
        }

        if (!toDelete.isEmpty()) {
            log.info("Cleaned up {} messages before timestamp {}", toDelete.size(), beforeTimestamp);
        }

        return toDelete.size();
    }

    @Override
    public void deleteOfflineMessage(String offlineMessageId) {
        deleteOfflineMessageInternal(offlineMessageId);
    }

    @Override
    public void setOnlineCallback(OnlineCallback callback) {
        this.onlineCallback = callback;
    }

    @Override
    public void pushOfflineMessagesOnOnline(String userId) {
        pushOfflineMessagesOnOnline(userId, null);
    }

    @Override
    public void pushOfflineMessagesOnOnline(String userId, String sceneGroupId) {
        if (userId == null) {
            return;
        }

        List<OfflineMessage> messages;
        if (sceneGroupId != null) {
            messages = getOfflineMessagesBySceneGroup(userId, sceneGroupId);
        } else {
            messages = getOfflineMessages(userId);
        }

        if (messages.isEmpty()) {
            log.debug("No offline messages to push for user: {}", userId);
            return;
        }

        for (OfflineMessage message : messages) {
            message.markDelivered();
            message.incrementDeliveryAttempt();
            totalMessagesDelivered.incrementAndGet();
        }

        if (onlineCallback != null) {
            try {
                onlineCallback.onOnline(userId, messages);
                log.info("Pushed {} offline messages to user: {}", messages.size(), userId);
            } catch (Exception e) {
                log.error("Error pushing offline messages to user: {}", userId, e);
            }
        }
    }

    @Override
    public OfflineMessageStats getStats() {
        OfflineMessageStats stats = new OfflineMessageStats();

        int total = 0;
        int pending = 0;
        int delivered = 0;
        int acknowledged = 0;
        int expired = 0;

        for (OfflineMessage message : messageStore.values()) {
            total++;
            switch (message.getStatus()) {
                case PENDING: pending++; break;
                case DELIVERED: delivered++; break;
                case ACKNOWLEDGED: acknowledged++; break;
                case EXPIRED: expired++; break;
                default: break;
            }
        }

        stats.setTotalMessages(total);
        stats.setPendingMessages(pending);
        stats.setDeliveredMessages(delivered);
        stats.setAcknowledgedMessages(acknowledged);
        stats.setExpiredMessages(expired);
        stats.setTotalRecipients(recipientMessages.size());

        return stats;
    }

    private String generateOfflineMessageId() {
        return "offline_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private void cleanupOldMessagesIfNeeded(String recipientId) {
        Set<String> messageIds = recipientMessages.get(recipientId);
        if (messageIds == null) {
            return;
        }

        int currentCount = (int) messageIds.stream()
                .map(messageStore::get)
                .filter(Objects::nonNull)
                .filter(m -> !m.isExpired())
                .count();

        if (currentCount >= maxMessagesPerRecipient) {
            int toRemove = currentCount - maxMessagesPerRecipient + 1;

            List<String> sortedIds = messageIds.stream()
                    .map(messageStore::get)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingLong(OfflineMessage::getCreatedAt))
                    .map(OfflineMessage::getOfflineMessageId)
                    .collect(Collectors.toList());

            for (int i = 0; i < toRemove && i < sortedIds.size(); i++) {
                deleteOfflineMessageInternal(sortedIds.get(i));
            }

            log.debug("Cleaned up {} old messages for recipient: {}", toRemove, recipientId);
        }
    }

    private void deleteOfflineMessageInternal(String offlineMessageId) {
        if (offlineMessageId == null) {
            return;
        }

        OfflineMessage message = messageStore.remove(offlineMessageId);
        if (message == null) {
            return;
        }

        String recipientId = message.getRecipientId();
        if (recipientId != null) {
            Set<String> messages = recipientMessages.get(recipientId);
            if (messages != null) {
                messages.remove(offlineMessageId);
                if (messages.isEmpty()) {
                    recipientMessages.remove(recipientId);
                }
            }
        }

        if (message.getMessageType() != null) {
            Set<String> typeMessages = typeIndex.get(message.getMessageType());
            if (typeMessages != null) {
                typeMessages.remove(offlineMessageId);
                if (typeMessages.isEmpty()) {
                    typeIndex.remove(message.getMessageType());
                }
            }
        }

        if (message.getSceneGroupId() != null) {
            Set<String> sceneMessages = sceneGroupIndex.get(message.getSceneGroupId());
            if (sceneMessages != null) {
                sceneMessages.remove(offlineMessageId);
                if (sceneMessages.isEmpty()) {
                    sceneGroupIndex.remove(message.getSceneGroupId());
                }
            }
        }
    }
}

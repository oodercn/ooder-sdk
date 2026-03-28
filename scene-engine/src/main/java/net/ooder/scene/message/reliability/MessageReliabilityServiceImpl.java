package net.ooder.scene.message.reliability;

import net.ooder.scene.message.queue.DeliveryStatus;
import net.ooder.scene.message.queue.MessageEnvelope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 消息可靠性服务实现
 *
 * <p>提供消息投递的可靠性保证</p>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
@Component
@ConditionalOnMissingBean(MessageReliabilityService.class)
public class MessageReliabilityServiceImpl implements MessageReliabilityService {

    private static final Logger log = LoggerFactory.getLogger(MessageReliabilityServiceImpl.class);

    private static final long DEFAULT_TRACK_EXPIRE_HOURS = 24L;
    private static final RetryPolicy DEFAULT_RETRY_POLICY = RetryPolicy.exponentialBackoff(3, 1000L);

    private final Map<String, MessageTrack> trackStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> senderTracks = new ConcurrentHashMap<>();
    private final DelayQueue<RetryTask> retryQueue = new DelayQueue<>();
    
    private final AtomicLong totalMessages = new AtomicLong(0);
    private final AtomicLong deliveredMessages = new AtomicLong(0);
    private final AtomicLong readMessages = new AtomicLong(0);
    private final AtomicLong acknowledgedMessages = new AtomicLong(0);
    private final AtomicLong failedMessages = new AtomicLong(0);
    private final AtomicLong retriedMessages = new AtomicLong(0);
    private final AtomicLong expiredMessages = new AtomicLong(0);
    private final AtomicLong totalDeliveryTime = new AtomicLong(0);

    private long trackExpireHours = DEFAULT_TRACK_EXPIRE_HOURS;
    private DeliveryCallback deliveryCallback;
    private RetryCallback retryCallback;

    public MessageReliabilityServiceImpl() {
        startRetryProcessor();
    }

    public void setTrackExpireHours(long trackExpireHours) {
        this.trackExpireHours = trackExpireHours;
    }

    @Override
    public MessageTrack startTracking(String messageId, MessageEnvelope envelope) {
        if (messageId == null || envelope == null) {
            return null;
        }

        MessageTrack track = MessageTrack.builder()
                .messageId(messageId)
                .envelope(envelope)
                .retryPolicy(DEFAULT_RETRY_POLICY)
                .build();

        trackStore.put(messageId, track);

        String senderId = envelope.getFrom() != null ? envelope.getFrom().getId() : null;
        if (senderId != null) {
            senderTracks.computeIfAbsent(senderId, k -> ConcurrentHashMap.newKeySet()).add(messageId);
        }

        totalMessages.incrementAndGet();

        log.debug("Started tracking message: messageId={}", messageId);
        return track;
    }

    @Override
    public MessageTrack getTrackStatus(String messageId) {
        if (messageId == null) {
            return null;
        }
        return trackStore.get(messageId);
    }

    @Override
    public List<MessageTrack> getUnacknowledgedMessages(String senderId) {
        if (senderId == null) {
            return new ArrayList<>();
        }

        Set<String> messageIds = senderTracks.get(senderId);
        if (messageIds == null || messageIds.isEmpty()) {
            return new ArrayList<>();
        }

        return messageIds.stream()
                .map(trackStore::get)
                .filter(Objects::nonNull)
                .filter(track -> !track.getStatus().isTerminal())
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageTrack> getRetryQueue() {
        return trackStore.values().stream()
                .filter(track -> track.getStatus() == DeliveryStatus.RETRYING)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageTrack> getFailedMessages(String senderId) {
        return trackStore.values().stream()
                .filter(track -> track.getStatus() == DeliveryStatus.FAILED)
                .filter(track -> senderId == null || 
                        (track.getEnvelope() != null && 
                         track.getEnvelope().getFrom() != null && 
                         senderId.equals(track.getEnvelope().getFrom().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void confirmDelivered(String messageId, String recipientId) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null) {
            return;
        }

        track.markDelivered();
        deliveredMessages.incrementAndGet();
        
        long deliveryTime = track.getDeliveredAt() - track.getCreatedAt();
        totalDeliveryTime.addAndGet(deliveryTime);

        log.debug("Message delivered: messageId={}, recipientId={}, deliveryTime={}ms", 
                messageId, recipientId, deliveryTime);

        if (deliveryCallback != null) {
            try {
                deliveryCallback.onDelivered(track);
            } catch (Exception e) {
                log.error("Delivery callback error: messageId={}", messageId, e);
            }
        }
    }

    @Override
    public void confirmRead(String messageId, String recipientId) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null) {
            return;
        }

        track.markRead();
        readMessages.incrementAndGet();

        log.debug("Message read: messageId={}, recipientId={}", messageId, recipientId);
    }

    @Override
    public void confirmAcknowledged(String messageId, String recipientId) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null) {
            return;
        }

        track.markAcknowledged();
        acknowledgedMessages.incrementAndGet();

        log.debug("Message acknowledged: messageId={}, recipientId={}", messageId, recipientId);
    }

    @Override
    public void markFailed(String messageId, String errorMessage) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null) {
            return;
        }

        track.markFailed(errorMessage);
        failedMessages.incrementAndGet();

        log.warn("Message failed: messageId={}, error={}", messageId, errorMessage);

        if (deliveryCallback != null) {
            try {
                deliveryCallback.onFailed(track);
            } catch (Exception e) {
                log.error("Delivery callback error: messageId={}", messageId, e);
            }
        }
    }

    @Override
    public DeliveryAttempt recordAttempt(String messageId, boolean success, String errorMessage) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null) {
            return null;
        }

        DeliveryAttempt attempt = track.recordAttempt(success, errorMessage);

        if (!success && track.canRetry()) {
            scheduleRetry(track);
        }

        return attempt;
    }

    @Override
    public void setRetryPolicy(String messageId, RetryPolicy policy) {
        MessageTrack track = trackStore.get(messageId);
        if (track != null && policy != null) {
            track.setRetryPolicy(policy);
        }
    }

    @Override
    public boolean retryMessage(String messageId) {
        MessageTrack track = trackStore.get(messageId);
        if (track == null || !track.canRetry()) {
            return false;
        }

        return doRetry(track);
    }

    @Override
    public int retryMessages(List<String> messageIds) {
        if (messageIds == null || messageIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (String messageId : messageIds) {
            if (retryMessage(messageId)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void cancelRetry(String messageId) {
        MessageTrack track = trackStore.get(messageId);
        if (track != null) {
            track.setStatus(DeliveryStatus.FAILED);
            track.setErrorMessage("Retry cancelled");
            log.info("Retry cancelled: messageId={}", messageId);
        }
    }

    @Override
    public void setDeliveryCallback(DeliveryCallback callback) {
        this.deliveryCallback = callback;
    }

    @Override
    public void setRetryCallback(RetryCallback callback) {
        this.retryCallback = callback;
    }

    @Override
    public ReliabilityStats getStats() {
        ReliabilityStats stats = new ReliabilityStats();
        
        long total = totalMessages.get();
        long delivered = deliveredMessages.get();
        
        stats.setTotalMessages(total);
        stats.setDeliveredMessages(delivered);
        stats.setReadMessages(readMessages.get());
        stats.setAcknowledgedMessages(acknowledgedMessages.get());
        stats.setFailedMessages(failedMessages.get());
        stats.setRetriedMessages(retriedMessages.get());
        stats.setExpiredMessages(expiredMessages.get());
        
        if (total > 0) {
            stats.setDeliveryRate((double) delivered / total * 100);
            stats.setReadRate((double) readMessages.get() / total * 100);
        }
        
        if (delivered > 0) {
            stats.setAvgDeliveryTime((double) totalDeliveryTime.get() / delivered);
        }
        
        stats.setPendingCount((int) trackStore.values().stream()
                .filter(t -> !t.getStatus().isTerminal())
                .count());
        stats.setRetryQueueSize(retryQueue.size());
        
        return stats;
    }

    @Override
    public int cleanupExpiredTracks() {
        long expireTime = System.currentTimeMillis() - (trackExpireHours * 60 * 60 * 1000);
        int count = 0;

        List<String> toRemove = trackStore.values().stream()
                .filter(track -> track.getCreatedAt() < expireTime)
                .filter(track -> track.getStatus().isTerminal())
                .map(MessageTrack::getMessageId)
                .collect(Collectors.toList());

        for (String messageId : toRemove) {
            MessageTrack track = trackStore.remove(messageId);
            if (track != null) {
                String senderId = track.getEnvelope() != null && track.getEnvelope().getFrom() != null 
                        ? track.getEnvelope().getFrom().getId() : null;
                if (senderId != null) {
                    Set<String> tracks = senderTracks.get(senderId);
                    if (tracks != null) {
                        tracks.remove(messageId);
                    }
                }
                count++;
            }
        }

        if (count > 0) {
            log.info("Cleaned up {} expired track records", count);
        }

        return count;
    }

    private void scheduleRetry(MessageTrack track) {
        long delay = track.getNextRetryDelay();
        if (delay <= 0) {
            return;
        }

        RetryTask task = new RetryTask(track.getMessageId(), delay);
        retryQueue.offer(task);
        
        log.debug("Scheduled retry: messageId={}, delay={}ms", track.getMessageId(), delay);
    }

    private boolean doRetry(MessageTrack track) {
        if (retryCallback != null) {
            if (!retryCallback.beforeRetry(track)) {
                log.info("Retry cancelled by callback: messageId={}", track.getMessageId());
                return false;
            }
        }

        track.setStatus(DeliveryStatus.RETRYING);
        retriedMessages.incrementAndGet();

        log.info("Retrying message: messageId={}, attempt={}", 
                track.getMessageId(), track.getAttemptCount() + 1);

        if (retryCallback != null) {
            retryCallback.afterRetry(track, false);
        }

        return true;
    }

    private void startRetryProcessor() {
        Thread processor = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    RetryTask task = retryQueue.take();
                    MessageTrack track = trackStore.get(task.getMessageId());
                    if (track != null && track.canRetry()) {
                        doRetry(track);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error processing retry task", e);
                }
            }
        }, "message-retry-processor");
        processor.setDaemon(true);
        processor.start();
    }

    private static class RetryTask implements Delayed {
        private final String messageId;
        private final long executeTime;

        public RetryTask(String messageId, long delayMs) {
            this.messageId = messageId;
            this.executeTime = System.currentTimeMillis() + delayMs;
        }

        public String getMessageId() {
            return messageId;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(executeTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (other instanceof RetryTask) {
                return Long.compare(this.executeTime, ((RetryTask) other).executeTime);
            }
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}

package net.ooder.scene.service.push.impl;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.service.push.*;
import net.ooder.sdk.common.enums.MemberRole;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推送服务实现
 *
 * @author Ooder Team
 * @version 2.3.1
 * @since 2.3.1
 */
public class PushServiceImpl implements PushService {

    private final Map<String, PushDetail> pushStore = new ConcurrentHashMap<>();
    private final Map<String, PushFeedback> feedbackStore = new ConcurrentHashMap<>();
    private final SceneEventPublisher eventPublisher;

    public PushServiceImpl(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletableFuture<PushResult> pushToSubordinates(PushRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            String pushId = "push-" + UUID.randomUUID().toString().substring(0, 8);
            
            PushDetail detail = new PushDetail();
            detail.setPushId(pushId);
            detail.setSceneId(request.getSceneId());
            detail.setSceneName(request.getSceneName());
            detail.setLeaderId(request.getLeaderId());
            detail.setLeaderName(request.getLeaderName());
            detail.setMessage(request.getMessage());
            detail.setPushTime(System.currentTimeMillis());
            detail.setExpireTime(request.calculateExpireTime());
            detail.setTotalCount(request.getSubordinateIds().size());
            detail.setRoleAssignments(request.getRoleAssignments());
            
            List<String> successUserIds = new ArrayList<>();
            List<String> failedUserIds = new ArrayList<>();
            List<PushFeedback> feedbacks = new ArrayList<>();
            
            for (String userId : request.getSubordinateIds()) {
                try {
                    PushFeedback feedback = createFeedback(pushId, request, userId);
                    feedbackStore.put(feedback.getFeedbackId(), feedback);
                    feedbacks.add(feedback);
                    successUserIds.add(userId);
                } catch (Exception e) {
                    failedUserIds.add(userId);
                }
            }
            
            detail.setFeedbacks(feedbacks);
            detail.setPendingCount(successUserIds.size());
            pushStore.put(pushId, detail);
            
            publishPushEvent(pushId, request, successUserIds);
            
            if (failedUserIds.isEmpty()) {
                return PushResult.success(pushId, request.getSceneId(), successUserIds);
            } else {
                return PushResult.partial(pushId, request.getSceneId(), successUserIds, failedUserIds);
            }
        });
    }

    private PushFeedback createFeedback(String pushId, PushRequest request, String userId) {
        PushFeedback feedback = new PushFeedback();
        feedback.setFeedbackId("fb-" + UUID.randomUUID().toString().substring(0, 8));
        feedback.setPushId(pushId);
        feedback.setSceneId(request.getSceneId());
        feedback.setUserId(userId);
        feedback.setAssignedRole(request.getRoleAssignments().getOrDefault(userId, MemberRole.MEMBER));
        feedback.setStatus(PushFeedback.FeedbackStatus.PENDING);
        feedback.setPushTime(System.currentTimeMillis());
        return feedback;
    }

    @Override
    public PushDetail getPushDetail(String pushId) {
        return pushStore.get(pushId);
    }

    @Override
    public List<PushFeedback> getPushFeedbacks(String sceneId, String leaderId) {
        List<PushFeedback> result = new ArrayList<>();
        for (PushDetail detail : pushStore.values()) {
            if (detail.getSceneId().equals(sceneId) && detail.getLeaderId().equals(leaderId)) {
                result.addAll(detail.getFeedbacks());
            }
        }
        return result;
    }

    @Override
    public void confirmPush(String pushId, String userId) {
        PushDetail detail = pushStore.get(pushId);
        if (detail == null) return;
        
        for (PushFeedback feedback : detail.getFeedbacks()) {
            if (feedback.getUserId().equals(userId)) {
                feedback.setStatus(PushFeedback.FeedbackStatus.CONFIRMED);
                feedback.setFeedbackTime(System.currentTimeMillis());
                
                detail.setPendingCount(detail.getPendingCount() - 1);
                detail.setConfirmedCount(detail.getConfirmedCount() + 1);
                break;
            }
        }
    }

    @Override
    public void rejectPush(String pushId, String userId, String reason) {
        PushDetail detail = pushStore.get(pushId);
        if (detail == null) return;
        
        for (PushFeedback feedback : detail.getFeedbacks()) {
            if (feedback.getUserId().equals(userId)) {
                feedback.setStatus(PushFeedback.FeedbackStatus.REJECTED);
                feedback.setRejectReason(reason);
                feedback.setFeedbackTime(System.currentTimeMillis());
                
                detail.setPendingCount(detail.getPendingCount() - 1);
                detail.setRejectedCount(detail.getRejectedCount() + 1);
                break;
            }
        }
    }

    @Override
    public boolean cancelPush(String pushId) {
        PushDetail detail = pushStore.get(pushId);
        if (detail == null) return false;
        
        detail.setStatus(PushDetail.PushStatus.CANCELLED);
        for (PushFeedback feedback : detail.getFeedbacks()) {
            if (feedback.getStatus() == PushFeedback.FeedbackStatus.PENDING) {
                feedback.setStatus(PushFeedback.FeedbackStatus.CANCELLED);
            }
        }
        return true;
    }

    @Override
    public String retryPush(String pushId) {
        PushDetail oldDetail = pushStore.get(pushId);
        if (oldDetail == null) return null;
        
        List<String> failedUserIds = new ArrayList<>();
        Map<String, MemberRole> roleAssignments = new HashMap<>();
        
        for (PushFeedback feedback : oldDetail.getFeedbacks()) {
            if (feedback.getStatus() == PushFeedback.FeedbackStatus.PENDING || 
                feedback.getStatus() == PushFeedback.FeedbackStatus.REJECTED) {
                failedUserIds.add(feedback.getUserId());
                roleAssignments.put(feedback.getUserId(), feedback.getAssignedRole());
            }
        }
        
        if (failedUserIds.isEmpty()) return null;
        
        PushRequest request = new PushRequest(oldDetail.getSceneId(), oldDetail.getLeaderId());
        request.setSceneName(oldDetail.getSceneName());
        request.setLeaderName(oldDetail.getLeaderName());
        request.setMessage(oldDetail.getMessage());
        request.setSubordinateIds(failedUserIds);
        request.setRoleAssignments(roleAssignments);
        
        PushResult result = pushToSubordinates(request).join();
        return result.getPushId();
    }

    @Override
    public List<PushDetail> getReceivedPushes(String userId) {
        List<PushDetail> result = new ArrayList<>();
        for (PushDetail detail : pushStore.values()) {
            for (PushFeedback feedback : detail.getFeedbacks()) {
                if (feedback.getUserId().equals(userId)) {
                    result.add(detail);
                    break;
                }
            }
        }
        return result;
    }

    private void publishPushEvent(String pushId, PushRequest request, List<String> userIds) {
        if (eventPublisher != null) {
            // eventPublisher.publish(new PushEvent(pushId, request, userIds));
        }
    }
}

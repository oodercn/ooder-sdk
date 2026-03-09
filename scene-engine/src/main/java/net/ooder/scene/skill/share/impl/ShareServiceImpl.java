package net.ooder.scene.skill.share.impl;

import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.permission.Permission;
import net.ooder.scene.skill.permission.PermissionService;
import net.ooder.scene.skill.share.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 知识分享服务实现
 *
 * <p>提供知识库分享的完整能力实现。</p>
 *
 * <p>架构层次：应用层 - 知识分享实现</p>
 *
 * @author ooder
 * @since 2.3
 */
public class ShareServiceImpl implements ShareService {
    
    private static final Logger log = LoggerFactory.getLogger(ShareServiceImpl.class);
    
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final PermissionService permissionService;
    private final Map<String, ShareInfo> shareStore = new ConcurrentHashMap<>();
    private final Map<String, String> codeToShareMap = new ConcurrentHashMap<>();
    private final Map<String, ShareStats> statsStore = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> visitorStore = new ConcurrentHashMap<>();
    
    private final SecureRandom random = new SecureRandom();
    
    public ShareServiceImpl(KnowledgeBaseService knowledgeBaseService, 
                            PermissionService permissionService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.permissionService = permissionService;
    }
    
    @Override
    public ShareInfo createShare(ShareCreateRequest request) {
        log.info("Creating share for kb: {} by user: {}", request.getKbId(), request.getUserId());
        
        KnowledgeBase kb = knowledgeBaseService.get(request.getKbId());
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + request.getKbId());
        }
        
        if (!permissionService.hasPermission(request.getKbId(), request.getUserId(), Permission.ADMIN)) {
            throw new SecurityException("Only admin can create share");
        }
        
        String shareId = generateId();
        String shareCode = generateShareCode();
        
        ShareInfo share = new ShareInfo();
        share.setShareId(shareId);
        share.setShareCode(shareCode);
        share.setKbId(request.getKbId());
        share.setKbName(kb.getName());
        share.setCreatorId(request.getUserId());
        share.setPassword(request.getPassword());
        share.setMaxAccessCount(request.getMaxAccessCount());
        share.setCreatedAt(System.currentTimeMillis());
        share.setUpdatedAt(System.currentTimeMillis());
        share.setActive(true);
        
        if (request.getExpiresIn() > 0) {
            share.setExpiresAt(System.currentTimeMillis() + request.getExpiresIn());
        }
        
        shareStore.put(shareId, share);
        codeToShareMap.put(shareCode, shareId);
        
        ShareStats stats = new ShareStats(shareId);
        stats.setCreatedAt(System.currentTimeMillis());
        statsStore.put(shareId, stats);
        visitorStore.put(shareId, ConcurrentHashMap.newKeySet());
        
        log.info("Share created: shareId={}, code={}", shareId, shareCode);
        return share;
    }
    
    @Override
    public ShareInfo getShare(String shareId) {
        return shareStore.get(shareId);
    }
    
    @Override
    public ShareInfo getShareByCode(String shareCode) {
        String shareId = codeToShareMap.get(shareCode);
        return shareId != null ? shareStore.get(shareId) : null;
    }
    
    @Override
    public ShareValidationResult validateShare(String shareCode, String password) {
        ShareInfo share = getShareByCode(shareCode);
        
        if (share == null) {
            return ShareValidationResult.failure("分享不存在");
        }
        
        if (!share.isActive()) {
            return ShareValidationResult.failure("分享已取消");
        }
        
        if (share.isExpired()) {
            return ShareValidationResult.failure("分享已过期");
        }
        
        if (share.isAccessLimitReached()) {
            return ShareValidationResult.failure("分享访问次数已达上限");
        }
        
        if (share.isPasswordProtected()) {
            if (password == null || !password.equals(share.getPassword())) {
                return ShareValidationResult.failure("密码错误");
            }
        }
        
        return ShareValidationResult.success(share);
    }
    
    @Override
    public void cancelShare(String shareId, String userId) {
        log.info("Canceling share: {} by user: {}", shareId, userId);
        
        ShareInfo share = shareStore.get(shareId);
        if (share == null) {
            throw new IllegalArgumentException("Share not found: " + shareId);
        }
        
        if (!share.getCreatorId().equals(userId)) {
            throw new SecurityException("Only creator can cancel share");
        }
        
        share.setActive(false);
        share.setUpdatedAt(System.currentTimeMillis());
        
        codeToShareMap.remove(share.getShareCode());
        
        log.info("Share canceled: {}", shareId);
    }
    
    @Override
    public List<ShareInfo> listShares(String kbId, String userId) {
        if (!permissionService.hasPermission(kbId, userId, Permission.READ)) {
            throw new SecurityException("No permission to access kb: " + kbId);
        }
        
        return shareStore.values().stream()
                .filter(s -> s.getKbId().equals(kbId))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ShareInfo> listUserShares(String userId) {
        return shareStore.values().stream()
                .filter(s -> s.getCreatorId().equals(userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public ShareInfo updateShare(String shareId, ShareUpdateRequest request, String userId) {
        log.info("Updating share: {} by user: {}", shareId, userId);
        
        ShareInfo share = shareStore.get(shareId);
        if (share == null) {
            throw new IllegalArgumentException("Share not found: " + shareId);
        }
        
        if (!share.getCreatorId().equals(userId)) {
            throw new SecurityException("Only creator can update share");
        }
        
        if (request.getPassword() != null) {
            share.setPassword(request.getPassword());
        }
        if (request.getExpiresIn() > 0) {
            share.setExpiresAt(System.currentTimeMillis() + request.getExpiresIn());
        }
        if (request.getMaxAccessCount() > 0) {
            share.setMaxAccessCount(request.getMaxAccessCount());
        }
        if (request.getActive() != null) {
            share.setActive(request.getActive());
        }
        
        share.setUpdatedAt(System.currentTimeMillis());
        
        log.info("Share updated: {}", shareId);
        return share;
    }
    
    @Override
    public void recordAccess(String shareId, String visitorId) {
        ShareInfo share = shareStore.get(shareId);
        if (share != null) {
            share.setAccessCount(share.getAccessCount() + 1);
        }
        
        ShareStats stats = statsStore.get(shareId);
        if (stats != null) {
            stats.setTotalAccessCount(stats.getTotalAccessCount() + 1);
            stats.setLastAccessTime(System.currentTimeMillis());
        }
        
        Set<String> visitors = visitorStore.get(shareId);
        if (visitors != null && visitorId != null) {
            visitors.add(visitorId);
            if (stats != null) {
                stats.setUniqueVisitorCount(visitors.size());
            }
        }
    }
    
    @Override
    public ShareStats getStats(String shareId) {
        return statsStore.get(shareId);
    }
    
    private String generateId() {
        return "share_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private String generateShareCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        
        String code = sb.toString();
        if (codeToShareMap.containsKey(code)) {
            return generateShareCode();
        }
        return code;
    }
}

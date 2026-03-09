package net.ooder.scene.skill.permission.impl;

import net.ooder.scene.skill.knowledge.KnowledgeBase;
import net.ooder.scene.skill.knowledge.KnowledgeBaseService;
import net.ooder.scene.skill.permission.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现
 *
 * <p>提供知识库权限的完整管理能力实现。</p>
 *
 * <p>架构层次：应用层 - 权限管理实现</p>
 *
 * @author ooder
 * @since 2.3
 */
public class PermissionServiceImpl implements PermissionService {
    
    private static final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final Map<String, Map<String, KbPermission>> permissionStore = new ConcurrentHashMap<>();
    private final Map<String, List<KbPermission>> userPermissionIndex = new ConcurrentHashMap<>();
    
    public PermissionServiceImpl(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }
    
    @Override
    public boolean hasPermission(String kbId, String userId, Permission permission) {
        KbPermission kbPerm = getPermission(kbId, userId);
        
        if (kbPerm == null || !kbPerm.isValid()) {
            KnowledgeBase kb = knowledgeBaseService.get(kbId);
            if (kb != null && kb.isPublic() && permission == Permission.READ) {
                return true;
            }
            return false;
        }
        
        return kbPerm.getPermission().includes(permission);
    }
    
    @Override
    public boolean hasAnyPermission(String kbId, String userId, Permission... permissions) {
        for (Permission p : permissions) {
            if (hasPermission(kbId, userId, p)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public KbPermission getPermission(String kbId, String userId) {
        Map<String, KbPermission> kbPerms = permissionStore.get(kbId);
        if (kbPerms == null) {
            return null;
        }
        
        KbPermission perm = kbPerms.get(userId);
        if (perm != null && perm.isExpired()) {
            return null;
        }
        
        return perm;
    }
    
    @Override
    public KbPermission grantPermission(GrantPermissionRequest request) {
        log.info("Granting permission: kb={}, user={}, perm={}", 
                request.getKbId(), request.getUserId(), request.getPermission());
        
        KnowledgeBase kb = knowledgeBaseService.get(request.getKbId());
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + request.getKbId());
        }
        
        validateGrantPermission(request);
        
        KbPermission perm = new KbPermission(request.getKbId(), request.getUserId(), request.getPermission());
        perm.setId(generateId());
        perm.setKbName(kb.getName());
        perm.setGrantedBy(request.getGrantedBy());
        perm.setGrantedAt(System.currentTimeMillis());
        
        if (request.getExpiresIn() > 0) {
            perm.setExpiresAt(System.currentTimeMillis() + request.getExpiresIn());
        }
        
        Map<String, KbPermission> kbPerms = permissionStore.computeIfAbsent(
                request.getKbId(), k -> new ConcurrentHashMap<>());
        kbPerms.put(request.getUserId(), perm);
        
        List<KbPermission> userPerms = userPermissionIndex.computeIfAbsent(
                request.getUserId(), k -> new ArrayList<>());
        userPerms.removeIf(p -> p.getKbId().equals(request.getKbId()));
        userPerms.add(perm);
        
        log.info("Permission granted: id={}", perm.getId());
        return perm;
    }
    
    @Override
    public List<KbPermission> grantPermissions(List<GrantPermissionRequest> requests) {
        List<KbPermission> results = new ArrayList<>();
        for (GrantPermissionRequest request : requests) {
            try {
                results.add(grantPermission(request));
            } catch (Exception e) {
                log.error("Failed to grant permission for user {} on kb {}", 
                        request.getUserId(), request.getKbId(), e);
            }
        }
        return results;
    }
    
    @Override
    public void revokePermission(String kbId, String userId) {
        log.info("Revoking permission: kb={}, user={}", kbId, userId);
        
        Map<String, KbPermission> kbPerms = permissionStore.get(kbId);
        if (kbPerms != null) {
            kbPerms.remove(userId);
        }
        
        List<KbPermission> userPerms = userPermissionIndex.get(userId);
        if (userPerms != null) {
            userPerms.removeIf(p -> p.getKbId().equals(kbId));
        }
    }
    
    @Override
    public void revokePermissions(String kbId, List<String> userIds) {
        for (String userId : userIds) {
            revokePermission(kbId, userId);
        }
    }
    
    @Override
    public List<KbPermission> listPermissions(String kbId) {
        Map<String, KbPermission> kbPerms = permissionStore.get(kbId);
        if (kbPerms == null) {
            return java.util.Collections.emptyList();
        }
        
        return kbPerms.values().stream()
                .filter(KbPermission::isValid)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<KbPermission> listUserPermissions(String userId) {
        List<KbPermission> userPerms = userPermissionIndex.get(userId);
        if (userPerms == null) {
            return java.util.Collections.emptyList();
        }
        
        return userPerms.stream()
                .filter(KbPermission::isValid)
                .collect(Collectors.toList());
    }
    
    @Override
    public void transferOwnership(String kbId, String currentOwnerId, String newOwnerId) {
        log.info("Transferring ownership: kb={}, from={}, to={}", kbId, currentOwnerId, newOwnerId);
        
        KnowledgeBase kb = knowledgeBaseService.get(kbId);
        if (kb == null) {
            throw new IllegalArgumentException("Knowledge base not found: " + kbId);
        }
        
        if (!kb.getOwnerId().equals(currentOwnerId)) {
            throw new SecurityException("Only the owner can transfer ownership");
        }
        
        revokePermission(kbId, newOwnerId);
        
        KbPermission newOwnerPerm = new KbPermission(kbId, newOwnerId, Permission.OWNER);
        newOwnerPerm.setId(generateId());
        newOwnerPerm.setKbName(kb.getName());
        newOwnerPerm.setGrantedBy(currentOwnerId);
        newOwnerPerm.setGrantedAt(System.currentTimeMillis());
        
        Map<String, KbPermission> kbPerms = permissionStore.computeIfAbsent(
                kbId, k -> new ConcurrentHashMap<>());
        kbPerms.put(newOwnerId, newOwnerPerm);
        
        KbPermission oldOwnerPerm = new KbPermission(kbId, currentOwnerId, Permission.ADMIN);
        oldOwnerPerm.setId(generateId());
        oldOwnerPerm.setKbName(kb.getName());
        oldOwnerPerm.setGrantedBy(currentOwnerId);
        oldOwnerPerm.setGrantedAt(System.currentTimeMillis());
        kbPerms.put(currentOwnerId, oldOwnerPerm);
        
        log.info("Ownership transferred successfully");
    }
    
    private void validateGrantPermission(GrantPermissionRequest request) {
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getPermission() == null) {
            throw new IllegalArgumentException("Permission is required");
        }
    }
    
    private String generateId() {
        return "perm_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}

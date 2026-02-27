package net.ooder.scene.core.provider;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.user.UserEvent;
import net.ooder.scene.core.PageResult;
import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.UserProvider;
import net.ooder.scene.provider.model.user.Permission;
import net.ooder.scene.provider.model.user.SecurityLog;
import net.ooder.scene.provider.model.user.UserInfo;
import net.ooder.scene.provider.model.user.UserStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserProviderImpl implements UserProvider {

    private static final String PROVIDER_NAME = "user-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;
    private SceneEventPublisher eventPublisher;

    private final Map<String, UserInfo> userRegistry = new ConcurrentHashMap<>();
    private final Map<String, Permission> permissionRegistry = new ConcurrentHashMap<>();
    private final Map<String, List<String>> userPermissions = new ConcurrentHashMap<>();
    private final List<SecurityLog> securityLogs = new ArrayList<>();
    private final int maxLogsSize = 1000;
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initializeDefaultPermissions();
        initializeDefaultUsers();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private void initializeDefaultPermissions() {
        addPermission("user.read", "user", "read", "Read user information");
        addPermission("user.write", "user", "write", "Create and update users");
        addPermission("user.delete", "user", "delete", "Delete users");
        addPermission("config.read", "config", "read", "Read configuration");
        addPermission("config.write", "config", "write", "Modify configuration");
        addPermission("system.admin", "system", "admin", "Full system administration");
    }

    private void addPermission(String name, String resource, String action, String description) {
        Permission permission = new Permission();
        permission.setPermissionId(UUID.randomUUID().toString());
        permission.setName(name);
        permission.setResource(resource);
        permission.setAction(action);
        permission.setDescription(description);
        permission.setCreatedAt(System.currentTimeMillis());
        permissionRegistry.put(name, permission);
    }

    private void initializeDefaultUsers() {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("username", "admin");
        adminData.put("email", "admin@scene-engine.local");
        adminData.put("displayName", "Administrator");
        adminData.put("department", "System");
        adminData.put("roles", Arrays.asList("admin", "user"));
        
        Result<UserInfo> adminResult = createUser(adminData);
        if (adminResult.isSuccess()) {
            List<String> adminPermissions = new ArrayList<>();
            adminPermissions.add("system.admin");
            adminPermissions.add("user.read");
            adminPermissions.add("user.write");
            adminPermissions.add("user.delete");
            adminPermissions.add("config.read");
            adminPermissions.add("config.write");
            userPermissions.put(adminResult.getData().getUserId(), adminPermissions);
        }
    }

    @Override
    public Result<UserStatus> getStatus() {
        UserStatus status = new UserStatus();
        status.setTotalUsers(userRegistry.size());
        
        int activeUsers = 0;
        int disabledUsers = 0;
        for (UserInfo user : userRegistry.values()) {
            if ("active".equals(user.getStatus())) {
                activeUsers++;
            } else {
                disabledUsers++;
            }
        }
        
        status.setActiveUsers(activeUsers);
        status.setDisabledUsers(disabledUsers);
        status.setLastUpdated(System.currentTimeMillis());
        
        return Result.success(status);
    }

    @Override
    public Result<PageResult<UserInfo>> listUsers(int page, int size) {
        List<UserInfo> allUsers = new ArrayList<>(userRegistry.values());
        
        int total = allUsers.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<UserInfo> pagedUsers = start < total ? allUsers.subList(start, end) : new ArrayList<>();
        
        PageResult<UserInfo> result = new PageResult<>();
        result.setItems(pagedUsers);
        result.setTotal(total);
        result.setPageNum(page);
        result.setPageSize(size);
        
        return Result.success(result);
    }

    @Override
    public Result<UserInfo> getUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        UserInfo user = userRegistry.get(userId);
        if (user == null) {
            return Result.notFound("User not found: " + userId);
        }
        
        return Result.success(user);
    }

    @Override
    public Result<UserInfo> createUser(Map<String, Object> userData) {
        if (userData == null) {
            return Result.badRequest("User data is required");
        }
        
        String username = (String) userData.get("username");
        if (username == null || username.isEmpty()) {
            return Result.badRequest("Username is required");
        }
        
        for (UserInfo existingUser : userRegistry.values()) {
            if (username.equals(existingUser.getUsername())) {
                return Result.error("Username already exists: " + username);
            }
        }
        
        UserInfo user = new UserInfo();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);
        
        if (userData.containsKey("email")) {
            user.setEmail((String) userData.get("email"));
        }
        if (userData.containsKey("displayName")) {
            user.setDisplayName((String) userData.get("displayName"));
        }
        if (userData.containsKey("department")) {
            user.setDepartment((String) userData.get("department"));
        }
        if (userData.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) userData.get("roles");
            user.setRoles(roles);
        } else {
            user.setRoles(Arrays.asList("user"));
        }
        
        user.setStatus("active");
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        
        userRegistry.put(user.getUserId(), user);
        userPermissions.put(user.getUserId(), new ArrayList<>());
        
        addSecurityLog(user.getUserId(), "create", "user", "User created: " + username);
        
        publishUserEvent(UserEvent.created(this, user.getUserId(), username, "system"));
        
        return Result.success(user);
    }

    @Override
    public Result<UserInfo> updateUser(String userId, Map<String, Object> userData) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        UserInfo user = userRegistry.get(userId);
        if (user == null) {
            return Result.notFound("User not found: " + userId);
        }
        
        if (userData == null) {
            return Result.badRequest("User data is required");
        }
        
        if (userData.containsKey("email")) {
            user.setEmail((String) userData.get("email"));
        }
        if (userData.containsKey("displayName")) {
            user.setDisplayName((String) userData.get("displayName"));
        }
        if (userData.containsKey("department")) {
            user.setDepartment((String) userData.get("department"));
        }
        if (userData.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) userData.get("roles");
            user.setRoles(roles);
        }
        
        user.setUpdatedAt(System.currentTimeMillis());
        
        addSecurityLog(userId, "update", "user", "User updated: " + user.getUsername());
        
        publishUserEvent(UserEvent.updated(this, userId, user.getUsername(), "system"));
        
        return Result.success(user);
    }

    @Override
    public Result<Boolean> deleteUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        UserInfo user = userRegistry.remove(userId);
        if (user == null) {
            return Result.notFound("User not found: " + userId);
        }
        
        userPermissions.remove(userId);
        
        addSecurityLog(userId, "delete", "user", "User deleted: " + user.getUsername());
        
        publishUserEvent(UserEvent.deleted(this, userId, user.getUsername(), "system"));
        
        return Result.success(true);
    }

    @Override
    public Result<UserInfo> enableUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        UserInfo user = userRegistry.get(userId);
        if (user == null) {
            return Result.notFound("User not found: " + userId);
        }
        
        user.setStatus("active");
        user.setUpdatedAt(System.currentTimeMillis());
        
        addSecurityLog(userId, "enable", "user", "User enabled: " + user.getUsername());
        
        publishUserEvent(UserEvent.enabled(this, userId, user.getUsername(), "system"));
        
        return Result.success(user);
    }

    @Override
    public Result<UserInfo> disableUser(String userId) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        UserInfo user = userRegistry.get(userId);
        if (user == null) {
            return Result.notFound("User not found: " + userId);
        }
        
        user.setStatus("disabled");
        user.setUpdatedAt(System.currentTimeMillis());
        
        addSecurityLog(userId, "disable", "user", "User disabled: " + user.getUsername());
        
        publishUserEvent(UserEvent.disabled(this, userId, user.getUsername(), "system", "Disabled by administrator"));
        
        return Result.success(user);
    }

    @Override
    public Result<PageResult<Permission>> listPermissions(int page, int size) {
        List<Permission> allPermissions = new ArrayList<>(permissionRegistry.values());
        
        int total = allPermissions.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<Permission> pagedPermissions = start < total ? allPermissions.subList(start, end) : new ArrayList<>();
        
        PageResult<Permission> result = new PageResult<>();
        result.setItems(pagedPermissions);
        result.setTotal(total);
        result.setPageNum(page);
        result.setPageSize(size);
        
        return Result.success(result);
    }

    @Override
    public Result<Boolean> savePermissions(String userId, List<String> permissions) {
        if (userId == null || userId.isEmpty()) {
            return Result.badRequest("User ID is required");
        }
        
        if (!userRegistry.containsKey(userId)) {
            return Result.notFound("User not found: " + userId);
        }
        
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        
        userPermissions.put(userId, new ArrayList<>(permissions));
        
        addSecurityLog(userId, "update", "permissions", "Permissions updated");
        
        publishUserEvent(UserEvent.permissionsChanged(this, userId, "system"));
        
        return Result.success(true);
    }

    @Override
    public Result<PageResult<SecurityLog>> listSecurityLogs(int page, int size) {
        List<SecurityLog> allLogs = new ArrayList<>(securityLogs);
        Collections.reverse(allLogs);
        
        int total = allLogs.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<SecurityLog> pagedLogs = start < total ? allLogs.subList(start, end) : new ArrayList<>();
        
        PageResult<SecurityLog> result = new PageResult<>();
        result.setItems(pagedLogs);
        result.setTotal(total);
        result.setPageNum(page);
        result.setPageSize(size);
        
        return Result.success(result);
    }

    private synchronized void addSecurityLog(String userId, String action, String resource, String details) {
        SecurityLog log = new SecurityLog();
        log.setLogId(UUID.randomUUID().toString());
        log.setUserId(userId);
        log.setAction(action);
        log.setResource(resource);
        log.setTimestamp(System.currentTimeMillis());
        log.setStatus("success");
        log.setDetails(details);
        log.setIpAddress("127.0.0.1");
        
        securityLogs.add(log);
        
        while (securityLogs.size() > maxLogsSize) {
            securityLogs.remove(0);
        }
    }
    
    private void publishUserEvent(UserEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }
}

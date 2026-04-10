package net.ooder.skill.common.service;

import net.ooder.skill.common.model.LoginRequest;
import net.ooder.skill.common.model.RoleConfig;
import net.ooder.skill.common.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String SESSION_USER_KEY = "ooder_user";

    private final Map<String, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, RoleConfig> roleConfigs = new LinkedHashMap<>();
    
    private UserInfoProvider userInfoProvider;
    private RoleConfigProvider roleConfigProvider;

    public interface UserInfoProvider {
        Object login(String username, String password, String clientIp, String role);
        
        default Object login(String username, String password, String clientIp) {
            return login(username, password, clientIp, null);
        }
        
        void logout(String token);
        boolean validateToken(String token);
        Object getUser(String userId);
        List<?> getOrgTree();
        List<?> getOrgUsers(String orgId);
        Object registerUser(Object user);
        void updateUser(String userId, Object user);
    }

    public AuthService() {
        initDefaultRoleConfigs();
    }
    
    public void setUserInfoProvider(UserInfoProvider provider) {
        this.userInfoProvider = provider;
    }
    
    public void setRoleConfigProvider(RoleConfigProvider provider) {
        this.roleConfigProvider = provider;
        refreshRoleConfigs();
    }
    
    public void addRoleConfig(RoleConfig config) {
        if (config != null && config.getId() != null) {
            roleConfigs.put(config.getId(), config);
            log.info("Added role config: {}", config.getId());
        }
    }
    
    public void setRoleConfigs(List<RoleConfig> configs) {
        roleConfigs.clear();
        if (configs != null) {
            for (RoleConfig config : configs) {
                if (config.getId() != null) {
                    roleConfigs.put(config.getId(), config);
                }
            }
        }
        log.info("Set {} role configs", roleConfigs.size());
    }
    
    public List<RoleConfig> getRoleConfigs() {
        return new ArrayList<>(roleConfigs.values());
    }
    
    public RoleConfig getRoleConfig(String roleId) {
        return roleConfigs.get(roleId);
    }
    
    private void refreshRoleConfigs() {
        if (roleConfigProvider != null) {
            List<RoleConfig> configs = roleConfigProvider.getRoleConfigs();
            if (configs != null && !configs.isEmpty()) {
                setRoleConfigs(configs);
                log.info("Refreshed role configs from provider: {} configs", configs.size());
            }
        }
    }

    private void initDefaultRoleConfigs() {
        roleConfigs.put("installer", new RoleConfig(
            "installer", "系统安装者", "安装基础技能包，初始化系统环境", "ri-install-line",
            "installer", Arrays.asList("skill:install", "skill:view", "system:init")
        ));

        roleConfigs.put("admin", new RoleConfig(
            "admin", "系统管理员", "发现场景技能，配置分发，推送给参与者", "ri-admin-line",
            "admin", Arrays.asList("capability:discover", "capability:install", "capability:distribute",
                "scene:create", "scene:manage", "user:assign", "capability:view", "scene:view")
        ));

        roleConfigs.put("leader", new RoleConfig(
            "leader", "主导者", "激活场景，获取KEY，执行入网动作", "ri-user-star-line",
            "leader", Arrays.asList("scene:activate", "scene:manage", "scene:view",
                "key:generate", "participant:manage", "task:assign")
        ));

        roleConfigs.put("collaborator", new RoleConfig(
            "collaborator", "协作者", "参与业务流转，执行分配的任务", "ri-team-line",
            "collaborator", Arrays.asList("task:view", "task:execute", "task:submit", "scene:view", "todo:view")
        ));

        log.info("AuthService initialized with {} default role configs", roleConfigs.size());
    }

    public UserSession login(LoginRequest request, HttpServletRequest httpRequest) {
        String username = request.getUsername();
        String password = request.getPassword();
        String requestedRole = request.getRole();

        log.info("[login] Login attempt: username={}, role={}", username, requestedRole);
        
        String clientIp = getClientIp(httpRequest);

        if (userInfoProvider != null) {
            Object userInfo = userInfoProvider.login(username, password, clientIp, requestedRole);
            if (userInfo == null) {
                log.warn("[login] Login failed for user: {}", username);
                return null;
            }
            
            RoleConfig config = roleConfigs.get(requestedRole);
            if (config == null) {
                config = roleConfigs.get("collaborator");
            }
            
            UserSession session = createUserSessionFromInfo(userInfo, config);
            String token = extractToken(userInfo);
            if (token == null) {
                token = UUID.randomUUID().toString().replace("-", "");
            }
            session.setToken(token);
            sessions.put(token, session);

            if (httpRequest != null) {
                HttpSession httpSession = httpRequest.getSession(true);
                httpSession.setAttribute(SESSION_USER_KEY, session);
                log.info("[login] Session created for user: {}", session.getUserId());
            }

            log.info("[login] Login successful: userId={}, role={}", session.getUserId(), config.getId());
            return session;
        }

        if (username == null || username.isEmpty()) {
            username = "demo";
        }

        RoleConfig config = roleConfigs.get(requestedRole);
        if (config == null) {
            config = roleConfigs.get("collaborator");
        }

        UserSession session = createUserSession(username, config);
        String token = UUID.randomUUID().toString().replace("-", "");
        session.setToken(token);
        sessions.put(token, session);

        if (httpRequest != null) {
            HttpSession httpSession = httpRequest.getSession(true);
            httpSession.setAttribute(SESSION_USER_KEY, session);
            log.info("[login] Session created for user: {}", session.getUserId());
        }

        log.info("[login] Login successful: userId={}, role={}", session.getUserId(), config.getId());
        return session;
    }
    
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    @SuppressWarnings("unchecked")
    private UserSession createUserSessionFromInfo(Object userInfo, RoleConfig config) {
        UserSession session = new UserSession();
        
        if (userInfo instanceof Map) {
            Map<String, Object> info = (Map<String, Object>) userInfo;
            session.setUserId(getStringValue(info, "userId", "userId"));
            session.setUsername(getStringValue(info, "username", "username"));
            session.setName(getStringValue(info, "nickname", "nickname"));
            if (session.getName() == null) {
                session.setName(getStringValue(info, "name", "name"));
            }
            session.setEmail(getStringValue(info, "email", "email"));
            session.setDepartmentId(getStringValue(info, "orgId", "orgId"));
            session.setDepartmentName(getStringValue(info, "orgName", "orgName"));
            session.setTitle(getStringValue(info, "title", "title"));
            session.setAvatar(getStringValue(info, "avatar", "avatar"));
        } else {
            try {
                session.setUserId((String) userInfo.getClass().getMethod("getUserId").invoke(userInfo));
                session.setUsername((String) userInfo.getClass().getMethod("getUsername").invoke(userInfo));
                String nickname = (String) userInfo.getClass().getMethod("getNickname").invoke(userInfo);
                session.setName(nickname != null ? nickname : session.getUsername());
                session.setEmail((String) userInfo.getClass().getMethod("getEmail").invoke(userInfo));
                session.setDepartmentId((String) userInfo.getClass().getMethod("getOrgId").invoke(userInfo));
                session.setDepartmentName((String) userInfo.getClass().getMethod("getOrgName").invoke(userInfo));
            } catch (Exception e) {
                log.debug("Could not extract user info via reflection: {}", e.getMessage());
            }
        }
        
        session.setRole(config.getOrgRole());
        session.setRoleType(config.getId());
        session.setPermissions(config.getPermissions());
        session.setLoginTime(System.currentTimeMillis());
        
        return session;
    }
    
    @SuppressWarnings("unchecked")
    private String getStringValue(Map<String, Object> map, String key1, String key2) {
        Object value = map.get(key1);
        if (value == null) {
            value = map.get(key2);
        }
        return value != null ? String.valueOf(value) : null;
    }
    
    @SuppressWarnings("unchecked")
    private String extractToken(Object userInfo) {
        if (userInfo instanceof Map) {
            Object token = ((Map<String, Object>) userInfo).get("token");
            return token != null ? String.valueOf(token) : null;
        }
        try {
            return (String) userInfo.getClass().getMethod("getToken").invoke(userInfo);
        } catch (Exception e) {
            return null;
        }
    }

    private UserSession createUserSession(String username, RoleConfig config) {
        UserSession session = new UserSession();
        session.setUserId("user-" + username);
        session.setUsername(username);
        session.setName(username.equals("admin") ? "管理员" : "用户");
        session.setRole(config.getOrgRole());
        session.setRoleType(config.getId());
        session.setEmail(username + "@ooder.local");
        session.setDepartmentId("root");
        session.setDepartmentName("根组织");
        session.setPermissions(config.getPermissions());
        session.setLoginTime(System.currentTimeMillis());
        return session;
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            UserSession user = (UserSession) session.getAttribute(SESSION_USER_KEY);
            if (user != null && user.getToken() != null) {
                if (userInfoProvider != null) {
                    userInfoProvider.logout(user.getToken());
                }
                sessions.remove(user.getToken());
                log.info("[logout] Removed session for user: {}", user.getUserId());
            }
            session.invalidate();
            log.info("[logout] Session invalidated");
        }
    }

    public UserSession getCurrentUser(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);
        if (httpSession != null) {
            UserSession user = (UserSession) httpSession.getAttribute(SESSION_USER_KEY);
            if (user != null) {
                return user;
            }
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            UserSession userSession = sessions.get(token);
            if (userSession != null) {
                if (userInfoProvider != null) {
                    if (userInfoProvider.validateToken(token)) {
                        return userSession;
                    }
                } else {
                    return userSession;
                }
            }
        }

        return null;
    }

    public boolean hasPermission(HttpServletRequest request, String permission) {
        UserSession user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        List<String> permissions = user.getPermissions();
        return permissions != null && permissions.contains(permission);
    }

    public boolean hasAnyPermission(HttpServletRequest request, String... permissions) {
        UserSession user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        List<String> userPermissions = user.getPermissions();
        if (userPermissions == null) {
            return false;
        }
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRole(HttpServletRequest request, String roleType) {
        UserSession user = getCurrentUser(request);
        return user != null && roleType.equals(user.getRoleType());
    }

    public boolean hasAnyRole(HttpServletRequest request, String... roleTypes) {
        UserSession user = getCurrentUser(request);
        if (user == null) {
            return false;
        }
        for (String roleType : roleTypes) {
            if (roleType.equals(user.getRoleType())) {
                return true;
            }
        }
        return false;
    }

    public List<Map<String, Object>> getAvailableRoles() {
        List<Map<String, Object>> roles = new ArrayList<>();

        for (RoleConfig config : roleConfigs.values()) {
            Map<String, Object> role = new HashMap<>();
            role.put("id", config.getId());
            role.put("name", config.getName());
            role.put("description", config.getDescription());
            role.put("icon", config.getIcon());
            roles.add(role);
        }

        return roles;
    }

    @SuppressWarnings("unchecked")
    public List<Object> getUsersByRole(String roleType) {
        if (userInfoProvider == null) {
            return new ArrayList<>();
        }
        
        RoleConfig config = roleConfigs.get(roleType);
        if (config != null) {
            List<?> orgTree = userInfoProvider.getOrgTree();
            List<Object> allUsers = new ArrayList<>();
            for (Object org : orgTree) {
                String orgId = null;
                if (org instanceof Map) {
                    orgId = (String) ((Map<String, Object>) org).get("orgId");
                } else {
                    try {
                        orgId = (String) org.getClass().getMethod("getOrgId").invoke(org);
                    } catch (Exception e) {
                        continue;
                    }
                }
                if (orgId != null) {
                    allUsers.addAll(userInfoProvider.getOrgUsers(orgId));
                }
            }
            List<Object> filtered = new ArrayList<>();
            for (Object user : allUsers) {
                List<String> roles = null;
                if (user instanceof Map) {
                    roles = (List<String>) ((Map<String, Object>) user).get("roles");
                } else {
                    try {
                        roles = (List<String>) user.getClass().getMethod("getRoles").invoke(user);
                    } catch (Exception e) {
                        continue;
                    }
                }
                if (roles != null && roles.contains(config.getOrgRole())) {
                    filtered.add(user);
                }
            }
            return filtered;
        }
        return new ArrayList<>();
    }

    public Object getUserById(String userId) {
        if (userInfoProvider == null) {
            return null;
        }
        return userInfoProvider.getUser(userId);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getAllUsers() {
        if (userInfoProvider == null) {
            return new ArrayList<>();
        }
        
        List<?> orgTree = userInfoProvider.getOrgTree();
        List<Object> allUsers = new ArrayList<>();
        for (Object org : orgTree) {
            String orgId = null;
            if (org instanceof Map) {
                orgId = (String) ((Map<String, Object>) org).get("orgId");
            } else {
                try {
                    orgId = (String) org.getClass().getMethod("getOrgId").invoke(org);
                } catch (Exception e) {
                    continue;
                }
            }
            if (orgId != null) {
                allUsers.addAll(userInfoProvider.getOrgUsers(orgId));
            }
        }
        return allUsers;
    }

    public Object createUser(String username, String nickname, String email, String orgId, List<String> roles) {
        if (userInfoProvider == null) {
            return null;
        }
        
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("nickname", nickname);
        user.put("email", email);
        user.put("orgId", orgId);
        user.put("roles", roles);
        user.put("status", "active");
        return userInfoProvider.registerUser(user);
    }

    @SuppressWarnings("unchecked")
    public boolean bindUserToRole(String userId, String roleType) {
        if (userInfoProvider == null) {
            return false;
        }
        
        Object userObj = userInfoProvider.getUser(userId);
        if (userObj == null) {
            return false;
        }
        
        RoleConfig config = roleConfigs.get(roleType);
        if (config == null) {
            return false;
        }
        
        List<String> roles;
        if (userObj instanceof Map) {
            roles = new ArrayList<>((List<String>) ((Map<String, Object>) userObj).get("roles"));
            if (roles == null) {
                roles = new ArrayList<>();
            }
            if (!roles.contains(config.getOrgRole())) {
                roles.add(config.getOrgRole());
            }
            ((Map<String, Object>) userObj).put("roles", roles);
            userInfoProvider.updateUser(userId, userObj);
        } else {
            try {
                roles = new ArrayList<>((List<String>) userObj.getClass().getMethod("getRoles").invoke(userObj));
                if (roles == null) {
                    roles = new ArrayList<>();
                }
                if (!roles.contains(config.getOrgRole())) {
                    roles.add(config.getOrgRole());
                }
                userObj.getClass().getMethod("setRoles", List.class).invoke(userObj, roles);
                userInfoProvider.updateUser(userId, userObj);
            } catch (Exception e) {
                log.warn("Failed to bind user to role via reflection: {}", e.getMessage());
                return false;
            }
        }
        
        log.info("Bound user {} to role {}", userId, roleType);
        return true;
    }
}

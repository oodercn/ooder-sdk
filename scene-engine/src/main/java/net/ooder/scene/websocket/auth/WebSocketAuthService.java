package net.ooder.scene.websocket.auth;

import java.util.List;

/**
 * WebSocket认证服务接口
 *
 * <p>提供WebSocket连接的认证和授权能力</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>Token管理 - 生成、验证、刷新、撤销Token</li>
 *   <li>连接授权 - 检查用户对场景组的访问权限</li>
 *   <li>连接管理 - 注册、注销、查询活跃连接</li>
 * </ul>
 *
 * @author Ooder Team
 * @version 3.0.1
 * @since 3.0.1
 */
public interface WebSocketAuthService {
    
    // ========== Token管理 ==========
    
    /**
     * 生成WebSocket连接Token
     * 
     * @param userId 用户ID
     * @param sceneGroupId 场景组ID
     * @param expireSeconds 过期时间（秒）
     * @return 连接Token
     */
    WebSocketToken generateToken(String userId, String sceneGroupId, long expireSeconds);
    
    /**
     * 验证Token
     * 
     * @param token Token字符串
     * @return 验证结果
     */
    TokenValidationResult validateToken(String token);
    
    /**
     * 刷新Token
     * 
     * @param token 原Token
     * @return 新Token
     */
    WebSocketToken refreshToken(String token);
    
    /**
     * 撤销Token
     * 
     * @param token Token字符串
     */
    void revokeToken(String token);
    
    /**
     * 撤销用户所有Token
     * 
     * @param userId 用户ID
     */
    void revokeUserTokens(String userId);
    
    // ========== 连接授权 ==========
    
    /**
     * 检查连接权限
     * 
     * @param userId 用户ID
     * @param sceneGroupId 场景组ID
     * @return 是否有权限
     */
    boolean checkConnectionPermission(String userId, String sceneGroupId);
    
    /**
     * 获取用户可连接的场景组
     * 
     * @param userId 用户ID
     * @return 场景组ID列表
     */
    List<String> getAuthorizedSceneGroups(String userId);
    
    // ========== 连接管理 ==========
    
    /**
     * 注册连接
     * 
     * @param sessionId WebSocket会话ID
     * @param userId 用户ID
     * @param sceneGroupId 场景组ID
     */
    void registerConnection(String sessionId, String userId, String sceneGroupId);
    
    /**
     * 注册连接（带详细信息）
     * 
     * @param connectionInfo 连接信息
     */
    void registerConnection(ConnectionInfo connectionInfo);
    
    /**
     * 注销连接
     * 
     * @param sessionId WebSocket会话ID
     */
    void unregisterConnection(String sessionId);
    
    /**
     * 更新连接活动时间
     * 
     * @param sessionId WebSocket会话ID
     */
    void touchConnection(String sessionId);
    
    /**
     * 获取用户活跃连接
     * 
     * @param userId 用户ID
     * @return 连接信息列表
     */
    List<ConnectionInfo> getUserConnections(String userId);
    
    /**
     * 获取场景组活跃连接
     * 
     * @param sceneGroupId 场景组ID
     * @return 连接信息列表
     */
    List<ConnectionInfo> getSceneGroupConnections(String sceneGroupId);
    
    /**
     * 获取连接信息
     * 
     * @param sessionId 会话ID
     * @return 连接信息
     */
    ConnectionInfo getConnection(String sessionId);
    
    /**
     * 检查用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    boolean isUserOnline(String userId);
    
    /**
     * 检查用户在指定场景组是否在线
     * 
     * @param userId 用户ID
     * @param sceneGroupId 场景组ID
     * @return 是否在线
     */
    boolean isUserOnlineInScene(String userId, String sceneGroupId);
    
    // ========== 统计信息 ==========
    
    /**
     * 获取用户连接数
     * 
     * @param userId 用户ID
     * @return 连接数
     */
    int getUserConnectionCount(String userId);
    
    /**
     * 获取场景组连接数
     * 
     * @param sceneGroupId 场景组ID
     * @return 连接数
     */
    int getSceneGroupConnectionCount(String sceneGroupId);
    
    /**
     * 获取总连接数
     * 
     * @return 总连接数
     */
    int getTotalConnectionCount();
    
    /**
     * 清理过期连接
     * 
     * @return 清理的连接数
     */
    int cleanupExpiredConnections();
    
    /**
     * 获取服务统计信息
     * 
     * @return 统计信息
     */
    WebSocketAuthStats getStats();
    
    /**
     * WebSocket认证统计信息
     */
    class WebSocketAuthStats {
        private int activeConnections;
        private int activeUsers;
        private int activeSceneGroups;
        private int totalTokensGenerated;
        private int totalTokensRevoked;
        private long totalConnectionsCreated;
        
        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
        
        public int getActiveUsers() { return activeUsers; }
        public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }
        
        public int getActiveSceneGroups() { return activeSceneGroups; }
        public void setActiveSceneGroups(int activeSceneGroups) { this.activeSceneGroups = activeSceneGroups; }
        
        public int getTotalTokensGenerated() { return totalTokensGenerated; }
        public void setTotalTokensGenerated(int totalTokensGenerated) { this.totalTokensGenerated = totalTokensGenerated; }
        
        public int getTotalTokensRevoked() { return totalTokensRevoked; }
        public void setTotalTokensRevoked(int totalTokensRevoked) { this.totalTokensRevoked = totalTokensRevoked; }
        
        public long getTotalConnectionsCreated() { return totalConnectionsCreated; }
        public void setTotalConnectionsCreated(long totalConnectionsCreated) { this.totalConnectionsCreated = totalConnectionsCreated; }
    }
}

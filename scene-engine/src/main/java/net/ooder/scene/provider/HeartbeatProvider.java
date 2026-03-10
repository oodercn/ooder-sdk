package net.ooder.scene.provider;

/**
 * 心跳提供者接口
 *
 * @author Ooder Team
 * @version 2.3.1
 */
public interface HeartbeatProvider {

    /**
     * 发送心跳
     * @param clientId 客户端ID
     * @return 心跳结果
     */
    boolean sendHeartbeat(String clientId);

    /**
     * 检查客户端状态
     * @param clientId 客户端ID
     * @return 客户端状态
     */
    Object checkClientStatus(String clientId);

    /**
     * 注册客户端
     * @param clientId 客户端ID
     * @param info 客户端信息
     * @return 注册结果
     */
    boolean registerClient(String clientId, Object info);

    /**
     * 注销客户端
     * @param clientId 客户端ID
     * @return 注销结果
     */
    boolean unregisterClient(String clientId);
}

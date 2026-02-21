package net.ooder.scene.protocol;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 发现协议适配器接口
 *
 * <p>定义节点发现相关的操作接口，由Engine Core实现</p>
 */
public interface DiscoveryProtocolAdapter {

    /**
     * 发现节点
     *
     * @param request 发现请求
     * @return 发现结果
     */
    CompletableFuture<DiscoveryResult> discoverPeers(DiscoveryRequest request);

    /**
     * 列出已发现的节点
     *
     * @return 节点列表
     */
    CompletableFuture<List<Peer>> listDiscoveredPeers();

    /**
     * 发现MCP节点
     *
     * @return MCP节点信息
     */
    CompletableFuture<Peer> discoverMcp();

    /**
     * 获取节点信息
     *
     * @param peerId 节点ID
     * @return 节点信息
     */
    CompletableFuture<Peer> getPeer(String peerId);

    /**
     * 检查节点是否在线
     *
     * @param peerId 节点ID
     * @return 是否在线
     */
    CompletableFuture<Boolean> isPeerOnline(String peerId);

    /**
     * 添加发现监听器
     *
     * @param listener 监听器
     */
    void addDiscoveryListener(DiscoveryEventListener listener);

    /**
     * 移除发现监听器
     *
     * @param listener 监听器
     */
    void removeDiscoveryListener(DiscoveryEventListener listener);
}

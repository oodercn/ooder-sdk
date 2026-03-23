package net.ooder.scene.protocol;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UDP 发现服务
 *
 * <p>基于 UDP 广播的发现协议实现，用于局域网内的 Agent 和能力发现。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *   <li>Agent 公告 - 广播 Agent 上线信息</li>
 *   <li>能力共享 - 广播能力共享信息</li>
 *   <li>场景创建 - 广播场景创建信息</li>
 *   <li>消息接收 - 接收其他节点的广播消息</li>
 * </ul>
 *
 * <h3>消息类型：</h3>
 * <ul>
 *   <li>0x01 - AGENT_ANNOUNCE: Agent 公告</li>
 *   <li>0x02 - CAP_SHARE: 能力共享</li>
 *   <li>0x03 - SCENE_CREATE: 场景创建</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * UdpDiscoveryService service = new UdpDiscoveryService();
 * service.start();
 * service.sendAnnouncement("agent-001");
 * service.sendCapShare("cap-40");
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see DiscoveryMessageCodec
 * @see MdnsDiscoveryService
 */
@Service
public class UdpDiscoveryService {

    /** UDP 端口 */
    private static final int PORT = 48888;

    /** 缓冲区大小 */
    private static final int BUFFER_SIZE = 1024;

    /** 消息头标识 */
    private static final String HEADER = "OODE";

    /** UDP Socket */
    private DatagramSocket socket;

    /** 线程池 */
    private ExecutorService executor;

    /** 运行状态 */
    private volatile boolean running;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        // 初始化配置
    }

    /**
     * 启动服务
     *
     * @throws IOException 启动失败时抛出
     */
    public void start() throws IOException {
        socket = new DatagramSocket(PORT);
        executor = Executors.newSingleThreadExecutor();
        running = true;

        // 启动接收线程
        executor.submit(this::receivePackets);
    }

    /**
     * 停止服务
     */
    @PreDestroy
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    /**
     * 发送 Agent 公告
     *
     * @param agentId Agent ID
     * @throws IOException 发送失败时抛出
     */
    public void sendAnnouncement(String agentId) throws IOException {
        // 发送 AGENT_ANNOUNCE 消息
        byte[] payload = agentId.getBytes();
        byte[] message = DiscoveryMessageCodec.encode(HEADER, (byte) 0x01, payload);
        DatagramPacket packet = new DatagramPacket(
                message, message.length,
                InetAddress.getByName("255.255.255.255"), PORT
        );
        socket.send(packet);
    }

    /**
     * 发送能力共享
     *
     * @param capId 能力 ID
     * @throws IOException 发送失败时抛出
     */
    public void sendCapShare(String capId) throws IOException {
        // 发送 CAP_SHARE 消息
        byte[] payload = capId.getBytes();
        byte[] message = DiscoveryMessageCodec.encode(HEADER, (byte) 0x02, payload);
        DatagramPacket packet = new DatagramPacket(
                message, message.length,
                InetAddress.getByName("255.255.255.255"), PORT
        );
        socket.send(packet);
    }

    /**
     * 接收数据包
     */
    private void receivePackets() {
        byte[] buffer = new byte[BUFFER_SIZE];
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                processPacket(packet);
            } catch (IOException e) {
                if (!running) {
                    break;
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理数据包
     *
     * @param packet 数据包
     */
    private void processPacket(DatagramPacket packet) {
        byte[] data = packet.getData();
        int length = packet.getLength();

        // 解码消息
        DiscoveryMessageCodec.Message message = DiscoveryMessageCodec.decode(data, length);
        if (message != null && message.getHeader().equals(HEADER)) {
            switch (message.getType()) {
                case 0x01:
                    handleAnnouncement(message.getPayload());
                    break;
                case 0x02:
                    handleCapShare(message.getPayload());
                    break;
                case 0x03:
                    handleSceneCreate(message.getPayload());
                    break;
            }
        }
    }

    /**
     * 处理 Agent 公告
     *
     * @param payload 消息负载
     */
    private void handleAnnouncement(byte[] payload) {
        String agentId = new String(payload);
        // 处理 Agent 公告
        System.out.println("Received agent announcement: " + agentId);
    }

    /**
     * 处理能力共享
     *
     * @param payload 消息负载
     */
    private void handleCapShare(byte[] payload) {
        String capId = new String(payload);
        // 处理能力共享
        System.out.println("Received cap share: " + capId);
    }

    /**
     * 处理场景创建
     *
     * @param payload 消息负载
     */
    private void handleSceneCreate(byte[] payload) {
        String sceneId = new String(payload);
        // 处理场景创建
        System.out.println("Received scene create: " + sceneId);
    }

    /**
     * 检查是否运行中
     *
     * @return true 运行中，false 已停止
     */
    public boolean isRunning() {
        return running;
    }
}

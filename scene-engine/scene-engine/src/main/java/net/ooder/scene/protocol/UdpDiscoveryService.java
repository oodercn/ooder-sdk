package net.ooder.scene.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpDiscoveryService {
    private static final int PORT = 48888;
    private static final int BUFFER_SIZE = 1024;
    private static final String HEADER = "OODE";

    private DatagramSocket socket;
    private ExecutorService executor;
    private boolean running;

    public void start() throws IOException {
        socket = new DatagramSocket(PORT);
        executor = Executors.newSingleThreadExecutor();
        running = true;

        // 启动接收线程
        executor.submit(this::receivePackets);
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

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

    private void handleAnnouncement(byte[] payload) {
        String agentId = new String(payload);
        // 处理 Agent 公告
        System.out.println("Received agent announcement: " + agentId);
    }

    private void handleCapShare(byte[] payload) {
        String capId = new String(payload);
        // 处理能力共享
        System.out.println("Received cap share: " + capId);
    }

    private void handleSceneCreate(byte[] payload) {
        String sceneId = new String(payload);
        // 处理场景创建
        System.out.println("Received scene create: " + sceneId);
    }
}

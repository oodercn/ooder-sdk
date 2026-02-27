package net.ooder.scene.protocol;

import java.nio.ByteBuffer;

/**
 * 发现消息编解码器
 *
 * <p>用于编码和解码 UDP 发现协议消息。</p>
 *
 * <h3>消息格式：</h3>
 * <pre>
 * +--------+------+---------+---------+
 * | Header | Type | Length  | Payload |
 * | 4 bytes| 1 byte| 2 bytes | N bytes |
 * +--------+------+---------+---------+
 * </pre>
 *
 * <h3>使用示例：</h3>
 * <pre>
 * // 编码
 * byte[] message = DiscoveryMessageCodec.encode("OODE", (byte) 0x01, payload);
 *
 * // 解码
 * Message msg = DiscoveryMessageCodec.decode(data, length);
 * </pre>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 0.8.0
 * @see UdpDiscoveryService
 */
public class DiscoveryMessageCodec {

    /**
     * 编码消息
     *
     * @param header 消息头
     * @param type 消息类型
     * @param payload 消息负载
     * @return 编码后的字节数组
     */
    public static byte[] encode(String header, byte type, byte[] payload) {
        // 计算消息长度
        int headerLength = header.getBytes().length;
        int payloadLength = payload.length;
        int totalLength = headerLength + 1 + 2 + payloadLength;

        // 构建消息
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.put(header.getBytes());
        buffer.put(type);
        buffer.putShort((short) payloadLength);
        buffer.put(payload);

        return buffer.array();
    }

    /**
     * 解码消息
     *
     * @param data 字节数组
     * @param length 数据长度
     * @return 解码后的消息对象
     */
    public static Message decode(byte[] data, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);

        // 读取头部
        byte[] headerBytes = new byte[4];
        buffer.get(headerBytes);
        String header = new String(headerBytes);

        // 读取类型
        byte type = buffer.get();

        // 读取长度
        short payloadLength = buffer.getShort();

        // 读取 payload
        byte[] payload = new byte[payloadLength];
        buffer.get(payload);

        return new Message(header, type, payload);
    }

    /**
     * 消息类
     */
    public static class Message {
        /** 消息头 */
        private String header;
        /** 消息类型 */
        private byte type;
        /** 消息负载 */
        private byte[] payload;

        /**
         * 构造器
         *
         * @param header 消息头
         * @param type 消息类型
         * @param payload 消息负载
         */
        public Message(String header, byte type, byte[] payload) {
            this.header = header;
            this.type = type;
            this.payload = payload;
        }

        /**
         * 获取消息头
         *
         * @return 消息头
         */
        public String getHeader() {
            return header;
        }

        /**
         * 获取消息类型
         *
         * @return 消息类型
         */
        public byte getType() {
            return type;
        }

        /**
         * 获取消息负载
         *
         * @return 消息负载
         */
        public byte[] getPayload() {
            return payload;
        }
    }
}

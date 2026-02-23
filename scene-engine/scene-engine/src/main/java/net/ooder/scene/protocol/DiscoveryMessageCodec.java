package net.ooder.scene.protocol;

import java.nio.ByteBuffer;

public class DiscoveryMessageCodec {
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

    public static class Message {
        private String header;
        private byte type;
        private byte[] payload;

        public Message(String header, byte type, byte[] payload) {
            this.header = header;
            this.type = type;
            this.payload = payload;
        }

        public String getHeader() {
            return header;
        }

        public byte getType() {
            return type;
        }

        public byte[] getPayload() {
            return payload;
        }
    }
}

# MQTT Publisher

@id: mqtt-publisher
@version: 1.0.0
@author: ooder-team
@domain: messaging
@tags: mqtt, iot, messaging, publisher
@executor: net.ooder.sdk.skill.executor.MqttPublisherExecutor

## Description

MQTT Publisher Skill 用于向 MQTT Broker 发布消息。支持 QoS 0/1/2 三种服务质量级别，可用于物联网设备通信、消息推送等场景。

## Inputs

- `brokerUrl`: string - MQTT Broker 连接地址，例如 tcp://localhost:1883
- `clientId`: string - 客户端唯一标识符
- `topic`: string - 要发布的主题名称
- `message`: string - 要发布的消息内容
- `qos`: int - 服务质量级别 (0, 1, 2)，默认为 0
- `retain`: boolean - 是否保留消息，默认为 false
- `username`: string (optional) - 认证用户名
- `password`: string (optional) - 认证密码

## Outputs

- `success`: boolean - 发布是否成功
- `messageId`: int - 消息 ID
- `errorMessage`: string - 错误信息（如果发布失败）
- `timestamp`: long - 发布时间戳

## Examples

### 基础发布示例

发布一条简单的消息到指定主题

Input:
```json
{
  "brokerUrl": "tcp://localhost:1883",
  "clientId": "ooder-client-001",
  "topic": "sensors/temperature",
  "message": "{\"value\": 25.5, \"unit\": \"C\"}",
  "qos": 1
}
```

Output:
```json
{
  "success": true,
  "messageId": 12345,
  "timestamp": 1704067200000
}
```

### 带认证的发布示例

使用用户名密码认证发布消息

Input:
```json
{
  "brokerUrl": "ssl://mqtt.example.com:8883",
  "clientId": "ooder-secure-client",
  "topic": "devices/commands",
  "message": "{\"action\": \"reboot\"}",
  "qos": 2,
  "retain": true,
  "username": "admin",
  "password": "secret123"
}
```

Output:
```json
{
  "success": true,
  "messageId": 12346,
  "timestamp": 1704067201000
}
```

### 错误处理示例

当 Broker 不可用时返回错误

Input:
```json
{
  "brokerUrl": "tcp://unreachable.broker:1883",
  "clientId": "test-client",
  "topic": "test/topic",
  "message": "test"
}
```

Output:
```json
{
  "success": false,
  "messageId": -1,
  "errorMessage": "Connection refused: unreachable.broker",
  "timestamp": 1704067202000
}
```

## Constraints

- brokerUrl 必须符合 MQTT URL 格式
- clientId 长度不能超过 23 个字符（MQTT 3.1.1 标准）
- topic 不能包含通配符 (+, #) 当作为发布主题
- qos 必须是 0、1 或 2
- 消息大小限制为 256MB

## Dependencies

- org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5

## Configuration

```properties
# MQTT 连接超时时间（秒）
mqtt.connection.timeout=30

# MQTT 心跳间隔（秒）
mqtt.keepalive.interval=60

# 是否启用自动重连
mqtt.automatic.reconnect=true

# 最大重连尝试次数
mqtt.max.reconnect.attempts=10
```

## Notes

- 建议在发布完成后调用 disconnect() 释放资源
- 使用 QoS 2 时会有额外的性能开销
- 保留消息会被存储在 Broker 上，新订阅者会立即收到最新消息

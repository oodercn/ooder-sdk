# Ooder API

**版本**: 3.0.2

基础 API 定义模块，提供 Ooder SDK 的核心接口和常量。

## 概述

Ooder API 是 Ooder SDK 的最底层模块，零依赖，定义了整个 SDK 的基础契约。

## 核心接口

### core 包

| 接口 | 说明 |
|------|------|
| `Identifiable` | 可标识接口，提供唯一标识的获取和设置 |
| `Named` | 命名接口，提供名称的获取和设置 |
| `Versioned` | 版本化接口，提供版本信息的获取 |

### exception 包

| 类 | 说明 |
|------|------|
| `OoderException` | Ooder 统一异常类 |

### constants 包

| 类 | 说明 |
|------|------|
| `OoderConstants` | Ooder 常量定义 |

## 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-api</artifactId>
    <version>3.0.2</version>
</dependency>
```

## 使用示例

```java
import net.ooder.api.core.Identifiable;
import net.ooder.api.core.Named;
import net.ooder.api.core.Versioned;

public class MyEntity implements Identifiable, Named, Versioned {
    private String id;
    private String name;
    private String version;
    
    @Override
    public String getId() { return id; }
    
    @Override
    public void setId(String id) { this.id = id; }
    
    @Override
    public String getName() { return name; }
    
    @Override
    public void setName(String name) { this.name = name; }
    
    @Override
    public String getVersion() { return version; }
    
    @Override
    public void setVersion(String version) { this.version = version; }
}
```

## 模块关系

```
ooder-api (零依赖)
    ↑
    ├── ooder-util
    ├── ooder-annotation
    └── 其他模块
```

## 许可证

MIT License

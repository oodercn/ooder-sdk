# Ooder Util

**版本**: 3.0.2

工具类模块，提供常用工具方法和加密支持。

## 概述

Ooder Util 提供了一系列通用工具类，包括字符串处理、集合操作和加密工具。

## 工具类

### crypto 包 - 加密工具

| 类 | 说明 |
|------|------|
| `AESUtil` | AES 加密/解密工具 |
| `RSAUtil` | RSA 加密/解密工具 |
| `CryptoException` | 加密异常类 |

### lang 包 - 语言工具

| 类 | 说明 |
|------|------|
| `StringUtils` | 字符串处理工具 |

### collection 包 - 集合工具

| 类 | 说明 |
|------|------|
| `CollectionUtils` | 集合处理工具 |

## 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-util</artifactId>
    <version>3.0.2</version>
</dependency>
```

## 使用示例

### AES 加密

```java
import net.ooder.util.crypto.AESUtil;

String plainText = "Hello, Ooder!";
String key = AESUtil.generateKey();
String encrypted = AESUtil.encrypt(plainText, key);
String decrypted = AESUtil.decrypt(encrypted, key);
```

### RSA 加密

```java
import net.ooder.util.crypto.RSAUtil;

KeyPair keyPair = RSAUtil.generateKeyPair();
String encrypted = RSAUtil.encrypt(plainText, keyPair.getPublic());
String decrypted = RSAUtil.decrypt(encrypted, keyPair.getPrivate());
```

## 模块关系

```
ooder-api (基础接口)
    ↑
ooder-util (工具类)
    ↑
其他模块
```

## 许可证

MIT License

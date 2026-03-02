# Ooder SDK 二次开发手册

> **版本**: 2.3  
> **日期**: 2026-03-01  
> **目标读者**: SDK 开发者、Skills 开发者

---

## 目录

1. [快速开始](#1-快速开始)
2. [工程结构](#2-工程结构)
3. [模块开发指南](#3-模块开发指南)
4. [接口开发规范](#4-接口开发规范)
5. [测试规范](#5-测试规范)
6. [文档规范](#6-文档规范)
7. [发布流程](#7-发布流程)

---

## 1. 快速开始

### 1.1 环境要求

- **JDK**: 1.8+
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA / Eclipse
- **Git**: 2.0+

### 1.2 克隆工程

```bash
git clone https://github.com/oodercn/ooder-sdk.git
cd ooder-sdk
```

### 1.3 构建工程

```bash
# 本地快速构建（跳过测试和文档）
mvn clean install -DskipTests

# 完整构建（包含测试和文档）
mvn clean install
```

### 1.4 导入 IDE

**IntelliJ IDEA**:
1. File → Open → 选择 ooder-sdk 目录
2. 等待 Maven 自动导入完成
3. 设置 JDK 为 1.8

**Eclipse**:
1. File → Import → Existing Maven Projects
2. 选择 ooder-sdk 目录
3. 等待导入完成

---

## 2. 工程结构

```
ooder-sdk/
├── agent-sdk/                    # Agent SDK (v2.3)
│   ├── agent-sdk-api/            # API接口层
│   ├── agent-sdk-core/           # 核心实现层
│   ├── skills-framework/         # 技能框架
│   ├── llm-sdk-api/              # LLM轻量级API
│   ├── llm-sdk/                  # LLM完整实现
│   └── docs/                     # 开发文档
├── ooder-api/                    # 基础API接口
├── ooder-util/                   # 工具类
├── ooder-annotation/             # 注解定义 (v2.3)
├── ooder-common/                 # 通用组件 (v2.3)
│   ├── ooder-config/             # 配置管理
│   ├── ooder-database/           # 数据库访问层
│   ├── ooder-common-client/      # 客户端核心
│   ├── ooder-server/             # 服务器核心
│   ├── ooder-vfs-web/            # VFS存储管理
│   ├── ooder-org-web/            # 组织机构接口
│   └── ooder-msg-web/            # 消息管理
├── scene-engine/                 # 场景引擎 (v2.3)
│   └── docs/                     # 协议文档
├── docs/                         # 根目录文档
├── README.md                     # 项目说明
├── MODULE_DIVISION.md            # 模块分工文档
├── ARCHITECTURE_GUIDE.md         # 架构指南
├── DEVELOPMENT_GUIDE.md          # 本文件
└── pom.xml                       # 父POM
```

---

## 3. 模块开发指南

### 3.1 新增模块步骤

1. **创建模块目录**
```bash
mkdir -p new-module/src/main/java/net/ooder/newmodule
mkdir -p new-module/src/main/resources
mkdir -p new-module/src/test/java
```

2. **创建 pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-sdk-parent</artifactId>
        <version>1.0.0</version>
    </parent>
    <artifactId>new-module</artifactId>
    <version>2.3</version>
    <packaging>jar</packaging>
</project>
```

3. **添加到父工程**
```xml
<!-- ooder-sdk/pom.xml -->
<modules>
    <module>new-module</module>
</modules>
```

4. **添加依赖管理**
```xml
<!-- ooder-sdk/pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>new-module</artifactId>
            <version>${ooder.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 3.2 接口定义规范

**位置**: `scene-engine` 模块（引擎层接口）

**包名**: `net.ooder.scene.skill.{功能域}`

**示例**:
```java
package net.ooder.scene.skill.llm;

/**
 * LLM Provider 接口
 * 
 * @author ooder
 * @since 2.3
 */
public interface LlmProvider {
    // 接口方法
}
```

### 3.3 实现类规范

**位置**: Skills 模块或 SDK 实现模块

**命名**: `{接口名}Impl` 或 `{功能}{类型}Provider`

**示例**:
```java
package net.ooder.skill.llm.openai;

import net.ooder.scene.skill.llm.LlmProvider;

/**
 * OpenAI LLM Provider 实现
 * 
 * @author ooder
 * @since 2.3
 */
public class OpenAiLlmProvider implements LlmProvider {
    // 实现方法
}
```

---

## 4. 接口开发规范

### 4.1 分层原则

```
上层应用 (Skills)
    ↓ 依赖
scene-engine (引擎层) - 接口定义
    ↓ 依赖
agent-sdk-core (SDK层) - 核心实现
    ↓ 依赖
agent-sdk-api (API层) - 基础接口
```

### 4.2 接口定义 checklist

- [ ] 使用 `@since` 标注版本号
- [ ] 添加 JavaDoc 注释
- [ ] 参数和返回值明确
- [ ] 异常处理说明
- [ ] 线程安全说明（如需要）

### 4.3 DTO 规范

```java
/**
 * 示例 DTO
 * 
 * @author ooder
 * @since 2.3
 */
public class ExampleDTO {
    
    /** 字段说明 */
    private String field;
    
    // 必须有默认构造函数
    public ExampleDTO() {}
    
    // Getters and Setters
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
}
```

---

## 5. 测试规范

### 5.1 测试目录结构

```
src/test/
├── java/
│   └── net/ooder/{module}/
│       ├── {ClassName}Test.java      # 单元测试
│       └── {ClassName}IT.java        # 集成测试
└── resources/
    └── test-config.yaml
```

### 5.2 单元测试规范

```java
package net.ooder.scene.skill.llm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * LlmProvider 单元测试
 * 
 * @author ooder
 * @since 2.3
 */
class LlmProviderTest {
    
    @Test
    void testChat() {
        // Given
        LlmProvider provider = new MockLlmProvider();
        
        // When
        Map<String, Object> result = provider.chat(...);
        
        // Then
        assertNotNull(result);
        assertTrue(result.containsKey("choices"));
    }
}
```

### 5.3 集成测试规范

```java
package net.ooder.scene.skill.llm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * LlmProvider 集成测试
 * 
 * @author ooder
 * @since 2.3
 */
@SpringBootTest
class LlmProviderIT {
    
    @Test
    void testRealApiCall() {
        // 集成测试代码
    }
}
```

---

## 6. 文档规范

### 6.1 必须文档

| 文档 | 位置 | 说明 |
|------|------|------|
| README.md | 模块根目录 | 模块说明 |
| CHANGELOG.md | 模块根目录 | 变更日志 |
| 接口文档 | docs/ | 接口详细说明 |

### 6.2 README.md 模板

```markdown
# {模块名}

> **版本**: 2.3  
> **类型**: {jar/pom}

## 功能概述

{简要描述}

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>{artifactId}</artifactId>
    <version>2.3</version>
</dependency>
```

### 使用示例

```java
// 示例代码
```

## 接口列表

| 接口 | 说明 |
|------|------|
| {Interface} | {说明} |

## 变更日志

参见 [CHANGELOG.md](CHANGELOG.md)
```

### 6.3 JavaDoc 规范

```java
/**
 * 类/接口的简要说明
 * 
 * <p>详细说明（可选）</p>
 * 
 * @author ooder
 * @since 2.3
 * @see RelatedClass
 */
public interface Example {
    
    /**
     * 方法说明
     * 
     * @param param1 参数1说明
     * @param param2 参数2说明
     * @return 返回值说明
     * @throws ExceptionType 异常说明
     */
    ReturnType method(ParamType1 param1, ParamType2 param2) throws ExceptionType;
}
```

---

## 7. 发布流程

### 7.1 版本号规范

- **主版本**: 重大架构变更
- **次版本**: 功能新增（当前使用）
- **修订版本**: Bug 修复

当前版本: **2.3**

### 7.2 发布 checklist

- [ ] 所有测试通过
- [ ] 文档已更新
- [ ] CHANGELOG.md 已更新
- [ ] 版本号已更新
- [ ] GPG 签名可用（发布模式）

### 7.3 发布命令

```bash
# 本地安装
mvn clean install -DskipTests

# 发布到 Maven Central（需要权限）
mvn clean deploy -Prelease
```

---

## 附录

### A. 常用命令

```bash
# 编译
mvn clean compile

# 测试
mvn test

# 打包
mvn clean package

# 安装到本地仓库
mvn clean install

# 跳过测试安装
mvn clean install -DskipTests

# 生成文档
mvn javadoc:javadoc

# 分析依赖
mvn dependency:tree
```

### B. 相关资源

- [架构指南](ARCHITECTURE_GUIDE.md)
- [模块分工](MODULE_DIVISION.md)
- [Skills 协作](SKILLS_COLLABORATION.md)
- [发布说明](RELEASE_NOTES_v2.3.md)
- [HTTP 客户端最佳实践](docs/HTTP_CLIENT_BEST_PRACTICES.md)

---

## 8. 常用工具类

### 8.1 加密工具（ooder-util）

```java
import net.ooder.util.crypto.AESUtil;
import net.ooder.util.crypto.RSAUtil;

// AES 加密
String encrypted = AESUtil.encrypt(plainText, key);
String decrypted = AESUtil.decrypt(cipherText, key);
String aesKey = AESUtil.generateKey(256);

// RSA 加密
KeyPair keyPair = RSAUtil.generateKeyPair(2048);
String[] keys = RSAUtil.encodeKeyPair(keyPair);
String publicKey = keys[0];
String privateKey = keys[1];

String encrypted = RSAUtil.encryptByPublicKey(plainText, publicKey);
String decrypted = RSAUtil.decryptByPrivateKey(cipherText, privateKey);

// RSA 签名
String sign = RSAUtil.sign(data, privateKey);
boolean valid = RSAUtil.verify(data, sign, publicKey);
```

### 8.2 HTTP 客户端（Spring）

参见 [HTTP 客户端最佳实践](docs/HTTP_CLIENT_BEST_PRACTICES.md)

---

**Made with ❤️ by Ooder Team**

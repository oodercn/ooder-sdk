# Scene-Engine 目录结构重构方案

## 一、当前目录结构问题分析

### 1.1 同名文件夹问题

```
scene-engine/                    # 父项目
├── scene-engine/                # 子模块 - 与父项目同名 ❌
│   ├── src/main/java/net/ooder/scene/...
│   └── pom.xml
├── scene-gateway/               # 空模块，待移除
├── skill-*/                     # 已迁移到 ooder-skills，待移除
└── pom.xml
```

**问题：** `scene-engine/scene-engine/` 父子同名，容易造成混淆

### 1.2 无效/待移除文件夹

| 文件夹 | 状态 | 说明 |
|--------|------|------|
| `scene-gateway/` | ❌ 待移除 | 空模块，仅有 pom.xml |
| `skill-agent/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-ai/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-business/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-mqtt/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-msg/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-network/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-org/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-security/` | ❌ 待移除 | 已迁移到 ooder-skills |
| `skill-vfs/` | ❌ 待移除 | 已迁移到 ooder-skills |

### 1.3 需要保留的核心模块

| 文件夹 | 状态 | 说明 |
|--------|------|------|
| `scene-engine/` | ✅ 保留 | 核心引擎（重命名建议） |
| `config/` | ✅ 保留 | 配置文件 |
| `docs/` | ✅ 保留 | 文档 |
| `northbound-core/` | ✅ 保留 | 北向协议核心（替代 scene-gateway） |

## 二、重构方案

### 方案一：最小改动（推荐）

只移除无效文件夹，保留现有结构，仅重命名核心模块。

```
scene-engine/                    # 父项目
├── scene-engine-core/           # 重命名：原 scene-engine → scene-engine-core
│   ├── src/main/java/net/ooder/scene/...
│   └── pom.xml
├── northbound-core/             # 保留，扩展为北向协议实现
│   └── src/...
├── config/                      # 保留
├── docs/                        # 保留
└── pom.xml
```

**优点：**
- 改动最小
- 历史兼容性好
- 风险低

**缺点：**
- 父子同名问题未完全解决（但已缓解）

### 方案二：完全重构

重新设计整个目录结构。

```
scene-engine/                    # 父项目
├── core/                        # 核心引擎
│   ├── src/main/java/net/ooder/scene/core/...
│   └── pom.xml
├── northbound/                  # 北向协议
│   ├── http/                    # HTTP 协议实现
│   ├── websocket/               # WebSocket 协议实现
│   └── pom.xml
├── config/                      # 配置文件
├── docs/                        # 文档
└── pom.xml
```

**优点：**
- 结构清晰
- 职责分明
- 易于扩展

**缺点：**
- 改动大
- 需要大量代码迁移
- 风险高

## 三、推荐方案：方案一（最小改动）

### 3.1 实施步骤

#### 步骤1: 重命名核心模块

```bash
# 在 scene-engine 目录下执行
mv scene-engine scene-engine-core
```

#### 步骤2: 更新父 pom.xml

```xml
<!-- scene-engine/pom.xml -->
<modules>
    <!-- 修改前 -->
    <!-- <module>scene-engine</module> -->
    
    <!-- 修改后 -->
    <module>scene-engine-core</module>
    
    <!-- 移除其他 skill 模块 -->
    <!-- <module>skill-org</module> -->
    <!-- <module>skill-vfs</module> -->
    <!-- ... -->
    
    <!-- 保留或扩展 northbound-core -->
    <module>northbound-core</module>
</modules>
```

#### 步骤3: 更新子模块 pom.xml

```xml
<!-- scene-engine-core/pom.xml -->
<artifactId>scene-engine-core</artifactId>
<!-- 修改前 -->
<!-- <artifactId>scene-engine</artifactId> -->
```

#### 步骤4: 删除无效文件夹

```bash
# 删除空模块
rm -rf scene-gateway/

# 删除已迁移的 skill 模块
rm -rf skill-agent/
rm -rf skill-ai/
rm -rf skill-business/
rm -rf skill-mqtt/
rm -rf skill-msg/
rm -rf skill-network/
rm -rf skill-org/
rm -rf skill-security/
rm -rf skill-vfs/
```

#### 步骤5: 验证编译

```bash
cd scene-engine
mvn clean compile
```

### 3.2 更新后的目录结构

```
scene-engine/                    # 父项目
├── scene-engine-core/           # 核心引擎（重命名后）
│   ├── src/
│   │   ├── main/java/net/ooder/scene/...
│   │   └── test/java/net/ooder/scene/...
│   ├── pom.xml
│   └── docs/SDK-COLLABORATION.md
├── northbound-core/             # 北向协议核心
│   └── src/test/java/net/ooder/northbound/scene/UserStoryTest.java
├── config/                      # 配置文件
│   ├── scene-dev.yaml
│   └── scene-prod.yaml
├── docs/                        # 文档
│   ├── protocol/
│   │   ├── v0.7.3/
│   │   └── v0.8.0/
│   └── *.md
└── pom.xml
```

## 四、northbound-core 扩展方案

由于 scene-gateway 被移除，建议将 northbound-core 扩展为北向协议实现：

```
northbound-core/
├── src/
│   ├── main/
│   │   ├── java/net/ooder/northbound/
│   │   │   ├── http/                    # HTTP 协议
│   │   │   │   ├── HttpGatewayServer.java
│   │   │   │   ├── HttpProtocolAdapter.java
│   │   │   │   └── controller/          # REST API Controller
│   │   │   │       ├── SceneController.java
│   │   │   │       ├── CapController.java
│   │   │   │       └── SkillController.java
│   │   │   ├── websocket/               # WebSocket 协议
│   │   │   │   ├── WebSocketGatewayServer.java
│   │   │   │   └── WebSocketEventHandler.java
│   │   │   ├── middleware/              # 中间件
│   │   │   │   ├── AuthMiddleware.java
│   │   │   │   ├── RateLimitMiddleware.java
│   │   │   │   └── CircuitBreakerMiddleware.java
│   │   │   └── NorthboundApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 五、实施检查清单

### 阶段1：移除无效文件夹
- [ ] 删除 `scene-gateway/`
- [ ] 删除 `skill-agent/`
- [ ] 删除 `skill-ai/`
- [ ] 删除 `skill-business/`
- [ ] 删除 `skill-mqtt/`
- [ ] 删除 `skill-msg/`
- [ ] 删除 `skill-network/`
- [ ] 删除 `skill-org/`
- [ ] 删除 `skill-security/`
- [ ] 删除 `skill-vfs/`

### 阶段2：重命名核心模块
- [ ] 重命名 `scene-engine/` → `scene-engine-core/`
- [ ] 更新父 pom.xml 的 modules
- [ ] 更新子模块 pom.xml 的 artifactId

### 阶段3：验证
- [ ] 执行 `mvn clean compile`
- [ ] 执行 `mvn test`
- [ ] 检查无编译错误

### 阶段4：提交
- [ ] 提交代码
- [ ] 推送代码

## 六、回滚方案

如果出现问题，执行以下命令回滚：

```bash
cd scene-engine
git revert HEAD
git push
```

## 七、总结

### 推荐操作

1. **立即执行**：移除所有无效的 skill 模块和 scene-gateway
2. **建议执行**：重命名 `scene-engine` → `scene-engine-core`
3. **后续规划**：扩展 `northbound-core` 为北向协议实现

### 预期结果

- 目录结构清晰
- 无同名文件夹
- 无无效模块
- 易于维护

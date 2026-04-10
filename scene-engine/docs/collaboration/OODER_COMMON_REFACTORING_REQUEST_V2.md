# ooder-common 模块精简协作需求说明 V2

## 需求概述

**提出方**: Scene-Engine 团队  
**协作方**: ooder-common 团队  
**优先级**: P1 (高优先级)  
**预期完成时间**: 1周  

---

## 背景与问题分析

### 当前问题

在 Scene-Engine MVP 版本精简过程中，发现 `ooder-common-client` 引入了过多重量级依赖，但 Scene-Engine 实际只使用了其中一小部分功能。

### 依赖分析

`ooder-common-client` 当前依赖分析：

| 依赖 | 大小 | 使用场景 | Scene-Engine使用情况 |
|------|------|----------|---------------------|
| **Freemarker** | ~2MB | 模板渲染 | ❌ 未使用 |
| **MVEL2** | ~1MB | 表达式引擎 | ❌ 未使用 |
| **Jedis** | ~500KB | Redis缓存 | ⚠️ 少量使用 |
| **pinyin4j** | ~200KB | 拼音转换 | ❌ 未使用 |
| **hsqldb** | ~1.5MB | 嵌入式数据库 | ❌ 未使用 |
| **cglib** | ~700KB | 字节码生成 | ⚠️ 可能使用 |
| **ognl** | ~400KB | 表达式语言 | ⚠️ 可能使用 |
| **httpcomponents** | ~500KB | HTTP客户端 | ✅ 使用 |

**总计未使用/少量使用**: ~6MB+

---

## 精简方案

### 核心思路

**不做过度拆分**，仅将真正过重且使用场景独立的依赖剥离：

1. **保留 `ooder-common-client` 主体** - 包含核心工具类和常用依赖
2. **剥离模板引擎** - Freemarker 单独成模块
3. **剥离表达式引擎** - MVEL 单独成模块
4. **优化其他依赖** - 将不常用的改为 optional

### 新模块结构

```
ooder-common/
├── ooder-common-client/          (保留 - 精简后)
├── ooder-common-template/        (新增 - Freemarker模板)
├── ooder-common-expression/      (新增 - MVEL表达式)
├── ooder-index-web/              (现有 - 已含Lucene，保持不变)
├── ooder-vfs-web/                (现有 - 保持不变)
├── ooder-server/                 (现有 - 保持不变)
├── ooder-org-web/                (现有 - 保持不变)
├── ooder-msg-web/                (现有 - 保持不变)
├── ooder-config/                 (现有 - 保持不变)
├── ooder-config-core/            (现有 - 保持不变)
├── ooder-database/               (现有 - 保持不变)
└── ooder-annotation/             (现有 - 保持不变)
```

---

## 详细方案

### 方案1: 创建 ooder-common-template (Freemarker模板)

**原因**: Freemarker (~2MB) 是一个完整的模板引擎，仅在需要服务端渲染的场景使用，Scene-Engine 作为 API 引擎不需要。

**包含内容**:
```
ooder-common-template/
├── src/main/java/net/ooder/template/
│   ├── JDSFreemarkerManager.java
│   ├── JDSFreemarkerResult.java
│   ├── JDSClassTemplateLoader.java
│   ├── JDSScopesHashModel.java
│   └── JDSBeanWrapper.java
└── pom.xml (依赖 Freemarker)
```

**pom.xml**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
    </dependency>
</dependencies>
```

---

### 方案2: 创建 ooder-common-expression (MVEL表达式)

**原因**: MVEL (~1MB) 是表达式引擎，用于动态表达式计算，使用场景有限。

**包含内容**:
```
ooder-common-expression/
├── src/main/java/net/ooder/esb/expression/
│   ├── CustomMacro.java
│   └── CTXMacro.java
├── src/main/java/net/ooder/jds/core/esb/mvel/
│   ├── JDSMapVariableResolver.java
│   ├── JDSEsbVariableResolverFactory.java
│   ├── JDSClassVariableResolverFactory.java
│   └── JDSClassVariableResolver.java
├── src/main/java/net/ooder/web/json/
│   └── MVELUtil.java
└── pom.xml (依赖 MVEL2)
```

**pom.xml**:
```xml
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mvel</groupId>
        <artifactId>mvel2</artifactId>
    </dependency>
</dependencies>
```

---

### 方案3: 精简 ooder-common-client

**移除的依赖**:
- `org.freemarker:freemarker` → 移到 ooder-common-template
- `org.mvel:mvel2` → 移到 ooder-common-expression
- `org.hsqldb:hsqldb` → 改为 optional 或移除
- `com.belerweb:pinyin4j` → 改为 optional

**保留的依赖**:
- `cglib:cglib` - 字节码生成，通用性强
- `ognl:ognl` - 表达式语言，Spring已依赖
- `redis.clients:jedis` - 缓存，常用功能
- `org.apache.httpcomponents:httpmime/fluent-hc` - HTTP客户端，常用功能
- `org.springframework:spring-web` - 核心依赖

**改为 optional 的依赖**:
```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <optional>true</optional>
</dependency>
```

---

### 方案4: Lucene 位置说明

**现状**: Lucene 已在 `ooder-index-web` 中，不需要移动。

**说明**: `ooder-index-web` 已经是一个独立的搜索模块，Lucene 放在这里是合理的。Scene-Engine 通过 `ooder-msg-web` 间接引用了 `ooder-index-web`，这是正常的依赖关系。

---

## Scene-Engine 依赖调整

### 当前引用

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>3.0.2</version>
</dependency>
```

### 调整后引用

```xml
<!-- 精简后的 ooder-common-client -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- ooder-msg-web 保持不变 -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>3.0.3</version>
</dependency>
```

**无需额外引用** - Scene-Engine 不使用模板和表达式功能。

---

## 向后兼容方案

### 方案A: 保持 ooder-common-client 作为聚合模块 (推荐)

```xml
<!-- ooder-common-client/pom.xml -->
<dependencies>
    <!-- 核心依赖 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client-core</artifactId>
    </dependency>
    
    <!-- 可选功能 - 默认包含，保持兼容 -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-template</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-expression</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

**优点**: 
- 现有用户无感知升级
- 新用户可以按需排除

### 方案B: 创建 ooder-common-client-core

如果希望更彻底的精简，可以：
1. 将核心功能移到 `ooder-common-client-core`
2. `ooder-common-client` 变为聚合模块（依赖 core + template + expression）
3. Scene-Engine 直接引用 `ooder-common-client-core`

---

## 预期收益

### 对 Scene-Engine

| 指标 | 当前 | 预期 | 改善 |
|------|------|------|------|
| ooder-common-client 传递依赖 | ~6MB | ~2MB | 减少 ~4MB |
| 未使用功能 | Freemarker、MVEL等 | 无 | 完全移除 |
| 启动时间 | 较长 | 缩短 | 提升 ~10% |

### 对 ooder-common

1. **职责更清晰**: 模板和表达式独立维护
2. **按需使用**: 用户可根据场景选择模块
3. **升级更灵活**: 各模块可独立版本演进

---

## 实施计划

### Week 1

```
Day 1-2: 创建 ooder-common-template
├── 从 ooder-common-client 迁移 Freemarker 相关代码
├── 创建新模块 pom.xml
└── 添加单元测试

Day 3-4: 创建 ooder-common-expression
├── 从 ooder-common-client 迁移 MVEL 相关代码
├── 创建新模块 pom.xml
└── 添加单元测试

Day 5: 精简 ooder-common-client
├── 移除 Freemarker 和 MVEL 依赖
├── 将 hsqldb、pinyin4j 改为 optional
├── 更新版本号为 3.0.3
└── 发布 SNAPSHOT 版本
```

### Week 2

```
Day 1-2: 兼容性测试
├── 测试现有项目兼容性
├── 验证 Scene-Engine 集成
└── 修复问题

Day 3-5: 发布与文档
├── 发布 3.0.3 正式版
├── 更新 README.md
├── 编写迁移指南
└── 通知相关团队
```

---

## 协作事项

### 需要 ooder-common 团队提供

1. **模块拆分实施**
   - [ ] 创建 `ooder-common-template` 模块
   - [ ] 创建 `ooder-common-expression` 模块
   - [ ] 精简 `ooder-common-client`

2. **版本发布**
   - [ ] 发布 3.0.3-SNAPSHOT 供测试
   - [ ] 发布 3.0.3 正式版

3. **文档更新**
   - [ ] 更新模块说明文档
   - [ ] 提供迁移指南

4. **兼容性保证**
   - [ ] 确保向后兼容
   - [ ] 提供兼容方案

### Scene-Engine 团队配合

1. **需求确认**
   - [ ] 确认精简方案
   - [ ] 确认功能边界

2. **测试验证**
   - [ ] 测试新模块集成
   - [ ] 验证功能正常

3. **依赖更新**
   - [ ] 更新 Scene-Engine pom.xml
   - [ ] 验证打包体积

---

## 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 其他项目依赖 Freemarker/MVEL | 高 | 保持 `ooder-common-client` 作为聚合模块，默认包含所有功能 |
| 代码迁移引入 Bug | 中 | 充分单元测试，保持原有逻辑不变 |
| 版本升级成本 | 低 | 向后兼容，用户无感知升级 |

---

## 替代方案

如果拆分模块成本过高，也可以考虑：

### 方案B: 仅标记 optional（最小改动）

不创建新模块，仅将 Freemarker 和 MVEL 在 `ooder-common-client` 中标记为 optional：

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.mvel</groupId>
    <artifactId>mvel2</artifactId>
    <optional>true</optional>
</dependency>
```

**优点**: 改动最小，无代码迁移  
**缺点**: 代码耦合仍在，不够清晰

---

## 建议方案

**推荐方案**: 方案A（创建独立模块）

理由：
1. 职责分离清晰，符合单一职责原则
2. 改动量适中（1周可完成）
3. 向后兼容，不影响现有用户
4. Scene-Engine 可显著减少依赖体积

**备选方案**: 方案B（仅标记 optional）

如果资源有限，可先实施方案B快速见效，后续再考虑方案A。

---

## 联系方式

**需求提出**: Scene-Engine 团队  
**技术对接**: TBD  
**协调沟通**: 建议建立专项协作群

---

## 参考文档

- [Scene-Engine 依赖优化分析](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)
- [ooder-common-client 当前 pom.xml](e:\github\ooder-sdk\ooder-common\ooder-common-client\pom.xml)
- [ooder-index-web pom.xml](e:\github\ooder-sdk\ooder-common\ooder-index-web\pom.xml)

---

**文档版本**: 2.0  
**创建日期**: 2026-04-10  
**最后更新**: 2026-04-10

# ooder-common-client 依赖精简变更说明

## 变更概述

**版本**: 3.0.2 → 3.0.3  
**变更类型**: 依赖精简（破坏性变更）  
**变更日期**: 2026-04-10  
**影响范围**: 所有依赖 ooder-common-client 的项目  

---

## 移除的依赖

### 1. Freemarker 模板引擎

**依赖坐标**:
```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.32</version>
</dependency>
```

**移除原因**:
- 体积较大 (~2MB)
- 仅在需要服务端模板渲染的场景使用
- Scene-Engine 等 API 项目不需要

**影响代码**:
- `net.ooder.template.JDSFreemarkerManager`
- `net.ooder.template.JDSFreemarkerResult`
- `net.ooder.template.JDSClassTemplateLoader`
- `net.ooder.template.JDSScopesHashModel`
- `net.ooder.template.JDSBeanWrapper`

**迁移方案**:
如需使用 Freemarker，请在子模块的 pom.xml 中显式添加依赖：

```xml
<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.32</version>
</dependency>
```

---

### 2. MVEL 表达式引擎

**依赖坐标**:
```xml
<dependency>
    <groupId>org.mvel</groupId>
    <artifactId>mvel2</artifactId>
    <version>2.5.2.Final</version>
</dependency>
```

**移除原因**:
- 体积较大 (~1MB)
- 仅在需要动态表达式计算的场景使用
- 使用场景有限

**影响代码**:
- `net.ooder.esb.expression.CustomMacro`
- `net.ooder.esb.expression.CTXMacro`
- `net.ooder.jds.core.esb.mvel.JDSMapVariableResolver`
- `net.ooder.jds.core.esb.mvel.JDSEsbVariableResolverFactory`
- `net.ooder.jds.core.esb.mvel.JDSClassVariableResolverFactory`
- `net.ooder.jds.core.esb.mvel.JDSClassVariableResolver`
- `net.ooder.web.json.MVELUtil`

**迁移方案**:
如需使用 MVEL，请在子模块的 pom.xml 中显式添加依赖：

```xml
<dependency>
    <groupId>org.mvel</groupId>
    <artifactId>mvel2</artifactId>
    <version>2.5.2.Final</version>
</dependency>
```

---

## 改为 Optional 的依赖

### 1. HSQLDB 嵌入式数据库

**变更**:
```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.7.2</version>
    <optional>true</optional>  <!-- 新增 -->
</dependency>
```

**说明**: 默认不再传递依赖，如需使用请显式声明。

---

### 2. Pinyin4J 拼音转换

**变更**:
```xml
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.1</version>
    <optional>true</optional>  <!-- 新增 -->
</dependency>
```

**说明**: 默认不再传递依赖，如需使用请显式声明。

---

## 保留的依赖

以下依赖继续保留在 ooder-common-client 中：

| 依赖 | 说明 |
|------|------|
| `cglib:cglib` | 字节码生成，通用性强 |
| `ognl:ognl` | 表达式语言，Spring已依赖 |
| `redis.clients:jedis` | Redis缓存，常用功能 |
| `org.apache.httpcomponents:httpmime/fluent-hc` | HTTP客户端，常用功能 |
| `org.springframework:spring-web` | 核心依赖 |
| `org.jdom:jdom2` | XML处理，轻量级 |
| `com.alibaba.fastjson2:fastjson2` | JSON处理 |
| `org.webjars:font-awesome` | 前端图标库 |

---

## 子模块迁移指南

### 场景1: 使用 Freemarker 模板

**在子模块 pom.xml 中添加**:
```xml
<dependencies>
    <!-- ooder-common-client -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
        <version>3.0.3</version>
    </dependency>
    
    <!-- 显式添加 Freemarker -->
    <dependency>
        <groupId>org.freemarker</groupId>
        <artifactId>freemarker</artifactId>
        <version>2.3.32</version>
    </dependency>
</dependencies>
```

---

### 场景2: 使用 MVEL 表达式

**在子模块 pom.xml 中添加**:
```xml
<dependencies>
    <!-- ooder-common-client -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
        <version>3.0.3</version>
    </dependency>
    
    <!-- 显式添加 MVEL -->
    <dependency>
        <groupId>org.mvel</groupId>
        <artifactId>mvel2</artifactId>
        <version>2.5.2.Final</version>
    </dependency>
</dependencies>
```

---

### 场景3: 使用 HSQLDB

**在子模块 pom.xml 中添加**:
```xml
<dependencies>
    <!-- ooder-common-client -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
        <version>3.0.3</version>
    </dependency>
    
    <!-- 显式添加 HSQLDB -->
    <dependency>
        <groupId>org.hsqldb</groupId>
        <artifactId>hsqldb</artifactId>
        <version>2.7.2</version>
    </dependency>
</dependencies>
```

---

### 场景4: 使用拼音转换

**在子模块 pom.xml 中添加**:
```xml
<dependencies>
    <!-- ooder-common-client -->
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-common-client</artifactId>
        <version>3.0.3</version>
    </dependency>
    
    <!-- 显式添加 pinyin4j -->
    <dependency>
        <groupId>com.belerweb</groupId>
        <artifactId>pinyin4j</artifactId>
        <version>2.5.1</version>
    </dependency>
</dependencies>
```

---

## 影响分析

### 已知受影响模块

| 模块 | 影响 | 处理方案 |
|------|------|----------|
| `ooder-server` | 可能使用 Freemarker | 检查并添加依赖 |
| `ooder-vfs-web` | 可能使用模板 | 检查并添加依赖 |
| `ooder-bpm-web` | 可能使用 MVEL | 检查并添加依赖 |

### 检查方法

在子模块中执行以下命令检查是否使用了移除的依赖：

```bash
# 检查 Freemarker 使用
grep -r "import freemarker" src/

# 检查 MVEL 使用
grep -r "import org.mvel2" src/

# 检查 HSQLDB 使用
grep -r "org.hsqldb" src/

# 检查 pinyin4j 使用
grep -r "com.belerweb.pinyin4j" src/
```

---

## 版本升级步骤

### 步骤1: 更新父 pom.xml

```xml
<properties>
    <ooder-common.version>3.0.3</ooder-common.version>
</properties>
```

### 步骤2: 检查子模块依赖

```bash
# 生成依赖树
mvn dependency:tree > deps.txt

# 检查是否包含被移除的依赖
grep -E "freemarker|mvel2|hsqldb|pinyin4j" deps.txt
```

### 步骤3: 按需添加依赖

根据检查结果，在需要的子模块中添加显式依赖。

### 步骤4: 编译测试

```bash
mvn clean compile
```

### 步骤5: 功能测试

测试受影响的功能模块，确保正常运行。

---

## 预期收益

### 对 Scene-Engine

| 指标 | 当前 | 预期 | 改善 |
|------|------|------|------|
| 传递依赖体积 | ~6MB | ~2MB | 减少 **~4MB** |
| 未使用功能 | Freemarker、MVEL等 | 无 | 完全移除 |
| 启动时间 | 较长 | 缩短 | 提升 ~10% |

### 对其他项目

- 按需依赖，减少冗余
- 明确的依赖关系
- 更小的打包体积

---

## 回滚方案

如果升级后出现问题，可临时回滚：

```xml
<properties>
    <ooder-common.version>3.0.2</ooder-common.version>
</properties>
```

---

## 常见问题

### Q1: 编译报错 "找不到类 Freemarker/MVEL"

**原因**: 子模块使用了 Freemarker/MVEL 但没有显式添加依赖  
**解决**: 在子模块 pom.xml 中添加对应依赖

### Q2: 运行时报错 "NoClassDefFoundError"

**原因**: 依赖传递被切断  
**解决**: 检查并添加缺失的依赖

### Q3: 如何知道哪些模块需要添加依赖？

**方法**: 使用 `mvn dependency:tree` 检查依赖树，对比变更前后的差异

---

## 联系支持

如有问题，请联系：
- **ooder-common 团队**: 负责依赖变更
- **Scene-Engine 团队**: 需求提出方

---

## 参考文档

- [ooder-common-client pom.xml](e:\github\ooder-sdk\ooder-common\ooder-common-client\pom.xml)
- [Scene-Engine 依赖优化分析](DEPENDENCY_OPTIMIZATION_ANALYSIS.md)

---

**文档版本**: 1.0  
**创建日期**: 2026-04-10  
**最后更新**: 2026-04-10

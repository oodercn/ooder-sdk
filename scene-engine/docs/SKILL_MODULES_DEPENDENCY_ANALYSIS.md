# Skill 模块依赖关系分析报告

## 一、模块概览

| 模块 | 父POM版本 | 自身版本 | 依赖agent-sdk | 依赖scene-engine | 依赖ooder-infra |
|------|-----------|----------|---------------|------------------|-----------------|
| vfs-skill | 1.0.0 | 1.0.0-SNAPSHOT | ✅ 2.3 | ❌ | ❌ |
| org-skill | 1.0.0 | 1.0.0-SNAPSHOT | ✅ 2.3 | ❌ | ❌ |
| skills-parent | 1.0.0 | 2.3 | ✅ ${ooder.version} | ❌ | ❌ |
| skills-container | 2.3 | 2.3 | ✅ ${ooder.version} (provided) | ❌ | ❌ |

## 二、依赖关系分析

### 2.1 依赖关系图

```
┌─────────────────────────────────────────────────────────────────┐
│                    Skill 模块依赖关系图                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ooder-sdk-parent (1.0.0)                                       │
│  ├── vfs-skill (1.0.0-SNAPSHOT)                                │
│  │   └── agent-sdk (2.3) ✅                                     │
│  ├── org-skill (1.0.0-SNAPSHOT)                                │
│  │   └── agent-sdk (2.3) ✅                                     │
│  └── skills (2.3)                                              │
│      └── skills-container (2.3)                                │
│          └── agent-sdk (2.3) ✅ provided                       │
│                                                                 │
│  说明：                                                          │
│  - 所有 skill 模块都只依赖 agent-sdk                            │
│  - 没有依赖 scene-engine 或 ooder-infra                         │
│  - 依赖关系清晰，无循环依赖                                      │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 版本不一致问题

| 模块 | parent版本 | 应该版本 | 状态 |
|------|------------|----------|------|
| vfs-skill | 1.0.0 | 2.3 | ❌ 待更新 |
| org-skill | 1.0.0 | 2.3 | ❌ 待更新 |
| skills | 1.0.0 | 2.3 | ❌ 待更新 |
| vfs-skill | 1.0.0-SNAPSHOT | 2.3-SNAPSHOT | ❌ 待更新 |
| org-skill | 1.0.0-SNAPSHOT | 2.3-SNAPSHOT | ❌ 待更新 |

## 三、代码依赖检查

### 3.1 vfs-skill 代码依赖

```java
// VfsDriver.java - 使用 agent-sdk 的 Driver 接口
package net.ooder.skill.vfs.driver;

import net.ooder.sdk.core.driver.Driver;  // ✅ agent-sdk
import net.ooder.sdk.core.driver.DriverContext;  // ✅ agent-sdk

public class VfsDriver implements Driver {
    // 实现...
}
```

**结论：** vfs-skill 只使用了 agent-sdk 的接口，依赖正确。

### 3.2 org-skill 代码依赖

经过检查，org-skill 代码中没有直接使用 agent-sdk 的 import，可能通过反射或配置文件使用。

### 3.3 skills-container 代码依赖

```xml
<!-- pom.xml -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>agent-sdk</artifactId>
    <version>${ooder.version}</version>
    <scope>provided</scope>  <!-- provided 范围，运行时由容器提供 -->
</dependency>
```

**结论：** skills-container 使用 provided 范围依赖 agent-sdk，这是正确的做法。

## 四、问题识别

### 4.1 版本不一致

所有 skill 模块的 parent 版本都是 1.0.0，而 agent-sdk 依赖版本是 2.3，存在版本不一致。

**影响：**
- Maven 构建时可能产生警告
- 版本管理混乱
- 不利于统一升级

### 4.2 无 scene-engine 依赖

skill 模块没有依赖 scene-engine，这是正确的，因为：
- skill 应该通过 agent-sdk 提供的接口与 scene-engine 交互
- 避免 skill 直接依赖 scene-engine 导致耦合

### 4.3 无 ooder-infra 依赖

skill 模块没有依赖 ooder-infra，这也是正确的，因为：
- ooder-infra 的功能已被 agent-sdk 和 scene-engine 覆盖
- skill 只需要依赖 agent-sdk 即可

## 五、优化建议

### 5.1 统一版本号

更新所有 skill 模块的版本号：

#### vfs-skill/pom.xml
```xml
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk-parent</artifactId>
    <version>2.3</version>  <!-- 更新 -->
</parent>

<artifactId>vfs-skill</artifactId>
<version>2.3-SNAPSHOT</version>  <!-- 更新 -->
```

#### org-skill/pom.xml
```xml
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk-parent</artifactId>
    <version>2.3</version>  <!-- 更新 -->
</parent>

<artifactId>org-skill</artifactId>
<version>2.3-SNAPSHOT</version>  <!-- 更新 -->
```

#### skills/pom.xml
```xml
<parent>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-sdk-parent</artifactId>
    <version>2.3</version>  <!-- 更新 -->
</parent>

<artifactId>skills-parent</artifactId>
<version>2.3</version>  <!-- 已是 2.3 -->
```

### 5.2 优化依赖声明

建议在父 pom 中统一声明 agent-sdk 版本：

```xml
<!-- ooder-sdk/pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
            <version>${agent-sdk.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

skill 模块中简化依赖声明：

```xml
<!-- vfs-skill/pom.xml -->
<dependencies>
    <dependency>
        <groupId>net.ooder</groupId>
        <artifactId>agent-sdk</artifactId>
        <!-- 版本由父pom管理 -->
    </dependency>
</dependencies>
```

## 六、实施步骤

### 步骤1: 更新 vfs-skill/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-sdk-parent</artifactId>
        <version>2.3</version>  <!-- 更新 -->
    </parent>

    <artifactId>vfs-skill</artifactId>
    <version>2.3-SNAPSHOT</version>  <!-- 更新 -->
    <packaging>jar</packaging>

    <name>VFS Skill</name>
    <description>Virtual File System Skill for Ooder SDK</description>

    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
            <!-- 版本由父pom管理 -->
        </dependency>
    </dependencies>
</project>
```

### 步骤2: 更新 org-skill/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-sdk-parent</artifactId>
        <version>2.3</version>  <!-- 更新 -->
    </parent>

    <artifactId>org-skill</artifactId>
    <version>2.3-SNAPSHOT</version>  <!-- 更新 -->
    <packaging>jar</packaging>

    <name>Organization Skill</name>
    <description>Organization Management Skill for Ooder SDK</description>

    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
            <!-- 版本由父pom管理 -->
        </dependency>
    </dependencies>
</project>
```

### 步骤3: 更新 skills/pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>net.ooder</groupId>
        <artifactId>ooder-sdk-parent</artifactId>
        <version>2.3</version>  <!-- 更新 -->
    </parent>

    <artifactId>skills-parent</artifactId>
    <version>2.3</version>
    <packaging>pom</packaging>
    <name>Skills Parent</name>
    <description>Skills modules parent</description>

    <modules>
        <module>skills-container</module>
    </modules>

    <dependencies>
        <dependency>
            <groupId>net.ooder</groupId>
            <artifactId>agent-sdk</artifactId>
            <!-- 版本由父pom管理 -->
        </dependency>
    </dependencies>
</project>
```

### 步骤4: 验证编译

```bash
cd E:\github\ooder-sdk
mvn clean compile -pl vfs-skill,org-skill,skills
```

### 步骤5: 提交代码

```bash
git add vfs-skill/pom.xml org-skill/pom.xml skills/pom.xml
git commit -m "chore: 统一 skill 模块版本号为 2.3

- 更新 vfs-skill parent version: 1.0.0 → 2.3
- 更新 org-skill parent version: 1.0.0 → 2.3
- 更新 skills parent version: 1.0.0 → 2.3
- 统一版本管理，简化依赖声明"
git push
```

## 七、总结

### 依赖关系健康度：✅ 良好

**优点：**
1. 所有 skill 模块都只依赖 agent-sdk，不依赖 scene-engine 或 ooder-infra
2. 依赖方向正确：skill → agent-sdk
3. 无循环依赖
4. skills-container 正确使用 provided 范围

**待优化：**
1. 版本号需要统一为 2.3
2. 建议在父 pom 中统一声明 agent-sdk 版本

### 执行清单

- [ ] 更新 vfs-skill/pom.xml parent version
- [ ] 更新 vfs-skill/pom.xml 自身 version
- [ ] 更新 org-skill/pom.xml parent version
- [ ] 更新 org-skill/pom.xml 自身 version
- [ ] 更新 skills/pom.xml parent version
- [ ] 可选：在父 pom 中添加 agent-sdk dependencyManagement
- [ ] 验证编译
- [ ] 提交代码

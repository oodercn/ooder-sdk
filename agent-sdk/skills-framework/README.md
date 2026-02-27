# Skills Framework

## 简介

Skills Framework 是 Ooder Agent SDK 的技能框架模块，提供技能的加载、生成和运行时支持。

## 模块定位

作为 Agent SDK 的技能支持层，本模块：
- 提供技能加载机制
- 支持技能代码生成
- 提供运行时支持
- 依赖 agent-sdk-api

## 核心功能

### 技能加载
- `SkillLoader` - 技能加载器
- `SkillRegistry` - 技能注册表
- `SkillScanner` - 技能扫描器

### 技能生成
- `SkillGenerator` - 技能生成器
- `SkillTemplate` - 技能模板
- `SkillCodeGenerator` - 代码生成器

### 运行时支持
- `SkillRuntime` - 技能运行时
- `SkillContext` - 技能上下文
- `SkillExecutor` - 技能执行器

## 使用方式

```xml
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>skills-framework</artifactId>
    <version>2.3</version>
</dependency>
```

## 快速开始

```java
import net.ooder.sdk.skills.SkillLoader;
import net.ooder.sdk.skills.SkillRegistry;

public class SkillsExample {
    public static void main(String[] args) {
        // 加载技能
        SkillLoader loader = new SkillLoader();
        Skill skill = loader.load("my-skill");
        
        // 注册技能
        SkillRegistry registry = new SkillRegistry();
        registry.register(skill);
        
        // 执行技能
        SkillResult result = skill.execute(context);
    }
}
```

## 版本

- 当前版本: 2.3
- 兼容版本: Java 8+

## 许可证

MIT License

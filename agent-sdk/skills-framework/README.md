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

### 技能发现 (Skill Discovery)

#### GitRepositoryDiscovererAdapter

`GitRepositoryDiscovererAdapter` 支持从 Gitee/GitHub 远程仓库发现技能包。

**功能状态**: ✅ 已完成 (v3.0.1)

**支持的方法**:
| 方法 | 说明 |
|------|------|
| `discover()` | 发现仓库中所有技能包 |
| `discover(String skillId)` | 根据 skillId 发现特定技能 |
| `discoverByScene(String sceneId)` | 按场景 ID 发现技能 |
| `search(String query)` | 搜索技能 |
| `searchByCapability(String capabilityId)` | 按能力 ID 搜索 |
| `discoverByCategory(String category)` | 按分类发现技能 |
| `searchByTags(List<String> tags)` | 按标签搜索 |

**使用示例**:

```java
// Gitee 配置
GitRepositoryDiscovererAdapter adapter = new GitRepositoryDiscovererAdapter("gitee");
adapter.setDefaultOwner("ooderCN");      // 必填
adapter.setDefaultRepo("skills");        // 必填
adapter.setGiteeToken("your-token");     // 必填
adapter.setDefaultBranch("master");      // 可选，默认 main
adapter.setTimeout(30000);               // 可选，默认 60000ms
adapter.setCacheTtlMs(60000);            // 可选，缓存 TTL 默认 5 分钟

// 发现所有技能
List<SkillPackage> packages = adapter.discover().join();

// 发现特定技能
SkillPackage pkg = adapter.discover("my-skill").join();

// 按场景发现
List<SkillPackage> scenePackages = adapter.discoverByScene("scene-001").join();

// 搜索技能
List<SkillPackage> searchResults = adapter.search("关键词").join();
```

```java
// GitHub 配置
GitRepositoryDiscovererAdapter adapter = new GitRepositoryDiscovererAdapter("github");
adapter.setDefaultOwner("ooderCN");
adapter.setDefaultRepo("skills");
adapter.setGithubToken("your-github-token");

List<SkillPackage> packages = adapter.discover().join();
```

**错误处理**:

| 异常类型 | 说明 |
|----------|------|
| `DiscoveryException` | 发现过程通用异常 |
| `AuthenticationException` | 认证失败（Token 无效） |
| `RepositoryNotFoundException` | 仓库不存在 |
| `ApiRateLimitException` | API 限流 |

**缓存机制**:

适配器内置缓存机制，默认缓存 5 分钟。可通过以下方式管理：

```java
adapter.setCacheTtlMs(60000);  // 设置缓存 TTL
adapter.clearCache();           // 清除缓存
```

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
    <version>3.0.1</version>
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

- 当前版本: 3.0.1
- 兼容版本: Java 11+

### 更新日志

#### 3.0.1 (2026-04-01)
- ✅ 修复 `GitRepositoryDiscovererAdapter` 占位实现，完成 Gitee/GitHub API 集成
- ✅ 实现 skill.yaml 解析功能
- ✅ 添加缓存机制
- ✅ 完善错误处理（认证失败、仓库不存在、API 限流）
- ✅ 添加单元测试

## 许可证

MIT License

# skills-framework SDK 协作开发声明

**文档类型**: 跨团队协作开发声明

**发起团队**: SE 团队 (scene-engine)

**接收团队**: skills-framework SDK 团队

**创建日期**: 2026-04-01

**优先级**: P0 (紧急)

---

## 一、背景说明

### 1.1 问题来源

os 工程团队在集成 SE SDK 3.0.1 时发现 `GitRepositoryDiscovererAdapter` 无法正常工作，经过 SE 团队深入分析，确认该类为**占位实现**，未完成实际功能开发。

**问题反馈文档**: `e:\apex\os\docs\se-sdk-discovery-issues.md`

**问题分析文档**: `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.0.1_DISCOVERY_ISSUES_RESPONSE.md`

### 1.2 影响范围

| 影响项 | 说明 |
|--------|------|
| 受影响版本 | skills-framework 3.0.1 及之前版本 |
| 受影响功能 | Gitee/GitHub 远程技能发现 |
| 受影响项目 | os 工程、所有依赖远程发现的项目 |

---

## 二、问题定位

### 2.1 问题代码

**文件路径**: `e:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\discovery\GitRepositoryDiscovererAdapter.java`

**问题代码**:

```java
// 第 40-46 行
@Override
public CompletableFuture<List<SkillPackage>> discover() {
    return CompletableFuture.supplyAsync(() -> {
        List<SkillPackage> packages = new ArrayList<SkillPackage>();  // 空列表
        log.info("Discovering skills from Git repository: {}/{}", defaultOwner, defaultRepo);
        return packages;  // 直接返回空列表，无实际 API 调用
    });
}
```

### 2.2 问题分析

| 方法 | 当前状态 | 问题描述 |
|------|----------|----------|
| `discover()` | ❌ 占位实现 | 返回空列表，无 Gitee/GitHub API 调用 |
| `discover(String skillId)` | ⚠️ 模拟实现 | 返回硬编码的模拟数据 |
| `discoverByScene(String sceneId)` | ❌ 占位实现 | 返回空列表 |
| `search(String query)` | ❌ 占位实现 | 返回空列表 |
| `searchByCapability(String capabilityId)` | ❌ 占位实现 | 返回空列表 |
| `discoverByCategory(String category)` | ❌ 占位实现 | 返回空列表 |
| `searchByTags(List<String> tags)` | ❌ 占位实现 | 返回空列表 |

### 2.3 根本原因

1. **功能未完成**: 该类为接口占位实现，未开发实际功能
2. **版本发布问题**: 占位代码被发布到 3.0.1 正式版本
3. **文档缺失**: 未标注该类为占位实现或实验性功能

---

## 三、开发任务

### 3.1 任务清单

| 编号 | 任务 | 优先级 | 负责团队 | 状态 | 完成日期 |
|------|------|--------|----------|------|----------|
| T1 | 实现 Gitee API 集成 | P0 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T2 | 实现 GitHub API 集成 | P0 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T3 | 实现 skill.yaml 解析 | P0 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T4 | 实现缓存机制 | P1 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T5 | 实现错误处理 | P0 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T6 | 添加单元测试 | P0 | skills-framework | ✅ 已完成 | 2026-04-01 |
| T7 | 更新文档 | P1 | skills-framework | ✅ 已完成 | 2026-04-01 |

### 3.2 详细规格

#### T1: Gitee API 集成

**API 端点**:
- 获取目录树: `GET https://gitee.com/api/v5/repos/{owner}/{repo}/git/trees/{branch}?recursive=1`
- 获取文件内容: `GET https://gitee.com/api/v5/repos/{owner}/{repo}/contents/{path}?ref={branch}`

**认证方式**: `token` 参数或 `Authorization: token {token}` Header

**实现要求**:

```java
@Override
public CompletableFuture<List<SkillPackage>> discover() {
    return CompletableFuture.supplyAsync(() -> {
        List<SkillPackage> packages = new ArrayList<>();
        
        try {
            // 1. 构建请求 URL
            String apiUrl = String.format(
                "https://gitee.com/api/v5/repos/%s/%s/git/trees/%s?recursive=1",
                defaultOwner, defaultRepo, defaultBranch
            );
            
            // 2. 发送 HTTP 请求
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "token " + giteeToken)
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            // 3. 解析响应
            if (response.statusCode() == 200) {
                JsonNode tree = objectMapper.readTree(response.body()).get("tree");
                
                // 4. 查找 skill.yaml 文件
                for (JsonNode node : tree) {
                    String path = node.get("path").asText();
                    if (path.endsWith("skill.yaml") || path.endsWith("skill.yml")) {
                        SkillPackage pkg = loadSkillPackage(path);
                        if (pkg != null) {
                            packages.add(pkg);
                        }
                    }
                }
            } else {
                log.error("Gitee API returned status: {}", response.statusCode());
            }
            
        } catch (Exception e) {
            log.error("Failed to discover skills from Gitee", e);
            throw new CompletionException(e);
        }
        
        return packages;
    });
}

private SkillPackage loadSkillPackage(String skillYamlPath) {
    try {
        // 获取 skill.yaml 内容
        String contentUrl = String.format(
            "https://gitee.com/api/v5/repos/%s/%s/contents/%s?ref=%s",
            defaultOwner, defaultRepo, skillYamlPath, defaultBranch
        );
        
        // ... 解析 YAML 并构建 SkillPackage
        
    } catch (Exception e) {
        log.warn("Failed to load skill from: {}", skillYamlPath, e);
        return null;
    }
}
```

#### T2: GitHub API 集成

**API 端点**:
- 获取目录树: `GET https://api.github.com/repos/{owner}/{repo}/git/trees/{branch}?recursive=1`
- 获取文件内容: `GET https://api.github.com/repos/{owner}/{repo}/contents/{path}?ref={branch}`

**认证方式**: `Authorization: Bearer {token}` Header

#### T3: skill.yaml 解析

**解析逻辑**:

```java
private SkillPackage parseSkillPackage(String yamlContent, String path) {
    // 1. 解析 YAML
    SkillManifest manifest = yamlMapper.readValue(yamlContent, SkillManifest.class);
    
    // 2. 构建 SkillPackage
    SkillPackage pkg = new SkillPackage();
    pkg.setSkillId(manifest.getSkillId());
    pkg.setName(manifest.getName());
    pkg.setVersion(manifest.getVersion());
    pkg.setDescription(manifest.getDescription());
    pkg.setCategory(manifest.getCategory());
    pkg.setTags(manifest.getTags());
    pkg.setManifest(manifest);
    
    // 3. 设置来源信息
    pkg.setSource(source + ":" + defaultOwner + "/" + defaultRepo);
    pkg.setPath(path);
    
    return pkg;
}
```

#### T5: 错误处理

**错误类型**:

| 错误类型 | 处理方式 |
|----------|----------|
| 网络超时 | 重试 3 次，记录日志，返回空列表 |
| 认证失败 | 抛出 AuthenticationException |
| 仓库不存在 | 抛出 RepositoryNotFoundException |
| API 限流 | 等待后重试，记录日志 |
| YAML 解析失败 | 跳过该技能，记录警告日志 |

---

## 四、接口契约

### 4.1 方法行为契约

| 方法 | 前置条件 | 后置条件 | 异常情况 |
|------|----------|----------|----------|
| `discover()` | token 已配置 | 返回非 null 列表 | 网络异常时抛出 CompletionException |
| `discover(String skillId)` | token 已配置 | 返回 SkillPackage 或 null | 网络异常时抛出 CompletionException |
| `isAvailable()` | 无 | token 配置时返回 true | 无异常 |

### 4.2 配置契约

```java
// 必须配置项
GitRepositoryDiscovererAdapter adapter = new GitRepositoryDiscovererAdapter("gitee");
adapter.setDefaultOwner("ooderCN");      // 必填
adapter.setDefaultRepo("skills");        // 必填
adapter.setGiteeToken("your-token");     // 必填

// 可选配置项
adapter.setDefaultBranch("master");      // 默认: main
adapter.setTimeout(30000);               // 默认: 60000ms
```

---

## 五、测试要求

### 5.1 单元测试

```java
@Test
void testDiscoverFromGitee() {
    // Given
    GitRepositoryDiscovererAdapter adapter = new GitRepositoryDiscovererAdapter("gitee");
    adapter.setDefaultOwner("ooderCN");
    adapter.setDefaultRepo("skills");
    adapter.setGiteeToken(testToken);
    
    // When
    List<SkillPackage> packages = adapter.discover().join();
    
    // Then
    assertNotNull(packages);
    assertFalse(packages.isEmpty());
}

@Test
void testDiscoverWithInvalidToken() {
    // Given
    GitRepositoryDiscovererAdapter adapter = new GitRepositoryDiscovererAdapter("gitee");
    adapter.setGiteeToken("invalid-token");
    
    // When & Then
    assertThrows(CompletionException.class, () -> {
        adapter.discover().join();
    });
}
```

### 5.2 集成测试

- 测试真实 Gitee API 调用
- 测试真实 GitHub API 调用
- 测试缓存机制
- 测试错误恢复

---

## 六、版本规划

### 6.1 发布计划

| 版本 | 发布日期 | 内容 |
|------|----------|------|
| 3.0.1 | 2026-04-01 | 修复 GitRepositoryDiscovererAdapter 占位实现 |
| 3.0.3 | 2026-04-10 | 添加缓存机制、完善错误处理 |

### 6.2 兼容性保证

- 保持现有接口不变
- 新增方法使用 default 实现
- 废弃方法添加 @Deprecated 注解

---

## 七、协作机制

### 7.1 沟通渠道

- **问题反馈**: 在本文档评论区或 GitHub Issue
- **进度同步**: 每日更新任务状态
- **代码评审**: PR 需要双方团队 Review

### 7.2 验收流程

1. skills-framework 团队完成开发并提交 PR
2. SE 团队进行代码评审
3. os 工程团队进行集成测试
4. 三方确认无误后合并发布

### 7.3 文档更新

完成后需更新以下文档：

| 文档 | 更新内容 |
|------|----------|
| CHANGELOG.md | 添加版本更新说明 |
| README.md | 更新功能状态 |
| API 文档 | 添加方法使用说明 |

---

## 八、时间节点

| 里程碑 | 日期 | 交付物 |
|--------|------|--------|
| M1: 需求确认 | 2026-04-02 | 双方确认本文档 |
| M2: 开发完成 | 2026-04-04 | 完成所有开发任务 |
| M3: 测试通过 | 2026-04-05 | 单元测试和集成测试通过 |
| M4: 发布上线 | 2026-04-01 | 发布 3.0.1 版本 |

---

## 九、技术债务处理

### 9.1 当前技术债务

| 债务项 | 影响 | 处理方式 |
|--------|------|----------|
| 占位实现发布到正式版本 | 用户无法使用远程发现功能 | 本次修复 |
| 缺少单元测试 | 无法保证代码质量 | 本次补充 |
| 缺少错误处理 | 问题难以排查 | 本次完善 |

### 9.2 防止技术债务

1. **代码评审**: 所有 PR 必须经过评审
2. **测试覆盖**: 核心功能测试覆盖率 ≥ 80%
3. **文档同步**: 代码和文档同步更新
4. **版本标注**: 未完成功能标注 `@Incubating` 或 `@Experimental`

---

## 十、确认签字

### 10.1 发起团队确认

**团队**: SE 团队 (scene-engine)

**确认人**: ________________

**日期**: 2026-04-01

### 10.2 接收团队确认

**团队**: skills-framework SDK 团队

**确认人**: ________________

**日期**: ________________

---

## 十一、附录

### 11.1 相关文档

| 文档 | 路径 |
|------|------|
| os 工程问题反馈 | `e:\apex\os\docs\se-sdk-discovery-issues.md` |
| SE 团队问题分析 | `e:\github\ooder-sdk\scene-engine\docs\collaboration\SE_SDK_3.0.1_DISCOVERY_ISSUES_RESPONSE.md` |
| 发现服务二次开发手册 | `e:\github\ooder-sdk\scene-engine\docs\v3.0.1\discovery-secondary-development-guide.md` |

### 11.2 代码仓库

| 仓库 | 路径 |
|------|------|
| skills-framework | `e:\github\ooder-sdk\agent-sdk\skills-framework` |
| scene-engine | `e:\github\ooder-sdk\scene-engine` |

---

**文档路径**: `e:\github\ooder-sdk\scene-engine\docs\collaboration\SKILLS_FRAMEWORK_COLLABORATION_STATEMENT.md`

**文档创建时间**: 2026-04-01

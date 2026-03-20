# SE SDK Git 发现能力问题解决方案

## 问题分析

**问题现象**: 调用 `/api/v1/discovery/gitee` 返回空列表

**根因**: `SkillPackageManagerImpl` 内部创建 `GitRepositoryDiscovererAdapter` 时没有注入配置

**日志证据**:
```
Discovering skills from Git repository: null/null
```

## 解决方案

### 方案1: SkillPackageManagerImpl 添加 Discoverer 注入支持

**文件**: `e:\github\ooder-sdk\agent-sdk\skills-framework\src\main\java\net\ooder\skills\core\impl\SkillPackageManagerImpl.java`

**添加代码**:

```java
/**
 * 设置 GitHub Discoverer
 */
public void setGitHubDiscoverer(SkillDiscoverer discoverer) {
    if (discoverer != null) {
        discoverers.put(DiscoveryMethod.GITHUB, discoverer);
        log.info("Injected GitHub discoverer: {}", discoverer.getClass().getSimpleName());
    }
}

/**
 * 设置 Gitee Discoverer
 */
public void setGiteeDiscoverer(SkillDiscoverer discoverer) {
    if (discoverer != null) {
        discoverers.put(DiscoveryMethod.GITEE, discoverer);
        log.info("Injected Gitee discoverer: {}", discoverer.getClass().getSimpleName());
    }
}

/**
 * 设置 Git Repository Discoverer
 */
public void setGitRepositoryDiscoverer(SkillDiscoverer discoverer) {
    if (discoverer != null) {
        discoverers.put(DiscoveryMethod.GIT_REPOSITORY, discoverer);
        log.info("Injected Git Repository discoverer: {}", discoverer.getClass().getSimpleName());
    }
}

/**
 * 设置指定发现方法的 Discoverer
 */
public void setDiscoverer(DiscoveryMethod method, SkillDiscoverer discoverer) {
    if (method != null && discoverer != null) {
        discoverers.put(method, discoverer);
        log.info("Injected discoverer for {}: {}", method, discoverer.getClass().getSimpleName());
    }
}
```

### 方案2: Spring Boot Auto-Configuration

**文件**: `skills-framework-spring-boot-starter`

```java
@Configuration
@ConditionalOnClass(SkillPackageManager.class)
@EnableConfigurationProperties(SkillsProperties.class)
public class SkillsAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public SkillPackageManager skillPackageManager(
            SkillsProperties props,
            @Autowired(required = false) SkillDiscoverer gitHubDiscoverer,
            @Autowired(required = false) SkillDiscoverer giteeDiscoverer) {
        
        SkillPackageManagerImpl impl = new SkillPackageManagerImpl();
        impl.setSkillRootPath(props.getSkillRootPath());
        
        if (gitHubDiscoverer != null) {
            impl.setGitHubDiscoverer(gitHubDiscoverer);
        }
        if (giteeDiscoverer != null) {
            impl.setGiteeDiscoverer(giteeDiscoverer);
        }
        
        return impl;
    }
    
    @Bean
    @ConditionalOnProperty(prefix = "ooder.gitee", name = "token")
    public SkillDiscoverer giteeDiscoverer(SkillsProperties props) {
        GitRepositoryDiscovererAdapter discoverer = new GitRepositoryDiscovererAdapter("gitee");
        discoverer.setDefaultOwner(props.getGitee().getOwner());
        discoverer.setDefaultRepo(props.getGitee().getRepo());
        discoverer.setGiteeToken(props.getGitee().getToken());
        discoverer.setDefaultBranch(props.getGitee().getBranch());
        return discoverer;
    }
}
```

### MVP 临时解决方案

在 SE SDK 修复之前，MVP 可以直接使用 `GiteeDiscoverer` Bean:

```java
// DiscoveryController
@Autowired(required = false)
private SkillDiscoverer giteeDiscoverer;

@PostMapping("/gitee")
public ResultModel<GitDiscoveryResultDTO> discoverFromGitee(...) {
    if (giteeDiscoverer != null) {
        List<SkillPackage> packages = giteeDiscoverer.discover().get(60, TimeUnit.SECONDS);
        // 直接使用结果
    }
}
```

## 状态

- [x] 问题分析完成
- [x] 解决方案确认
- [ ] SE SDK 实现修复
- [ ] MVP 验证

## 联系人

- MVP 团队
- SE SDK 团队

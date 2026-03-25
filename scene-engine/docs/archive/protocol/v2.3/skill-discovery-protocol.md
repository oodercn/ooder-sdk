# 技能发现协议 - v2.3

> **版本**: 2.3  
> **发布日期**: 2026-02-23  
> **状态**: 稳定版

## 1. 协议概述

技能发现协议定义了 Skill 的发现、注册、查询和调用机制。v2.3 版本统一了发现服务抽象，简化了发现流程。

### 1.1 核心概念

| 概念 | 说明 |
|------|------|
| Scene | 场景，一组相关 Skill 的集合 |
| Capability | 能力，Skill 提供的具体功能 |
| Skill | 技能，封装了特定功能的可插拔模块 |
| Discovery | 发现，查找可用的 Scene/Capability/Skill |

### 1.2 v2.3 核心变更

- 统一 `CapabilityDiscoveryService` 接口
- 支持多种发现提供者（UDP、mDNS、SkillCenter、Local FS、DHT、GitHub、Git Repository）
- 引入发现范围概念（PERSONAL/DEPARTMENT/COMPANY/PUBLIC）
- 标准化发现查询和结果格式

## 2. 发现服务架构

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                 CapabilityDiscoveryService                      │
│                      能力发现服务层                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  UDP        │  │   mDNS      │  │ SkillCenter │             │
│  │ Provider    │  │  Provider   │  │  Provider   │             │
│  │ (优先级100) │  │ (优先级90)  │  │ (优先级80)  │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │  Local FS   │  │    DHT      │  │   GitHub    │             │
│  │  Provider   │  │  Provider   │  │  Provider   │             │
│  │ (优先级50)  │  │ (优先级70)  │  │ (优先级60)  │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
│                                                                 │
│  ┌─────────────┐  ┌─────────────┐                              │
│  │ Git Repo    │  │  Gitee      │                              │
│  │ Provider    │  │  Provider   │                              │
│  │ (优先级60)  │  │ (优先级60)  │                              │
│  └─────────────┘  └─────────────┘                              │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 发现范围

| 范围 | 说明 | 适用发现方式 |
|------|------|-------------|
| PERSONAL | 个人设备 | Local FS, UDP |
| DEPARTMENT | 部门分享 | UDP, mDNS, SkillCenter, DHT |
| COMPANY | 公司管理 | SkillCenter API, Git Repository |
| PUBLIC | 公共社区 | SkillCenter API, GitHub, Gitee |

## 3. 核心接口

### 3.1 CapabilityDiscoveryService

```java
/**
 * 能力发现服务接口
 * @since 2.3
 */
public interface CapabilityDiscoveryService {
    
    /**
     * 同步所有索引
     */
    CompletableFuture<SyncResult> syncAllIndexes();
    
    /**
     * 列出场景
     * @param category 分类过滤，null表示所有
     */
    CompletableFuture<List<DiscoveredItem>> listScenes(String category);
    
    /**
     * 搜索场景
     * @param query 查询字符串，支持通配符 *
     */
    CompletableFuture<List<DiscoveredItem>> searchScenes(String query);
    
    /**
     * 获取场景详情
     */
    CompletableFuture<SceneDetail> getSceneDetail(String sceneId);
    
    /**
     * 获取场景下可用技能
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkills(String sceneId);
    
    /**
     * 列出能力
     */
    CompletableFuture<List<DiscoveredItem>> listCapabilities(String category);
    
    /**
     * 搜索能力
     */
    CompletableFuture<List<DiscoveredItem>> searchCapabilities(String query);
    
    /**
     * 获取能力详情
     */
    CompletableFuture<CapabilityDetail> getCapabilityDetail(String capId);
    
    /**
     * 获取能力下可用技能
     */
    CompletableFuture<List<DiscoveredItem>> getAvailableSkillsForCapability(String capId);
    
    /**
     * 注册发现提供者
     */
    void registerProvider(DiscoveryProvider provider);
    
    /**
     * 注销发现提供者
     */
    void unregisterProvider(String providerName);
    
    /**
     * 设置发现范围
     */
    void setDiscoveryScope(DiscoveryScope scope);
    
    /**
     * 获取当前发现范围
     */
    DiscoveryScope getDiscoveryScope();
}
```

### 3.2 DiscoveryProvider

```java
/**
 * 发现提供者接口
 * @since 2.3
 */
public interface DiscoveryProvider {
    
    /**
     * 获取提供者名称
     */
    String getProviderName();
    
    /**
     * 初始化
     */
    void initialize(DiscoveryConfig config);
    
    /**
     * 启动
     */
    void start();
    
    /**
     * 停止
     */
    void stop();
    
    /**
     * 是否运行中
     */
    boolean isRunning();
    
    /**
     * 执行发现
     */
    CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query);
    
    /**
     * 获取优先级（数值越大优先级越高）
     */
    int getPriority();
    
    /**
     * 是否适用于指定范围
     */
    boolean isApplicable(DiscoveryScope scope);
}
```

## 4. 数据模型

### 4.1 DiscoveryQuery

```java
public class DiscoveryQuery {
    private DiscoveryType type;      // SCENE/CAPABILITY/SKILL
    private String query;            // 查询字符串
    private DiscoveryScope scope;    // 发现范围
    private Map<String, Object> filters; // 过滤条件
}
```

### 4.2 DiscoveredItem

```java
public class DiscoveredItem {
    private String id;               // 唯一标识
    private String name;             // 名称
    private String type;             // 类型：SCENE/CAPABILITY/SKILL
    private String description;      // 描述
    private String version;          // 版本
    private Map<String, Object> metadata; // 元数据
    private String source;           // 来源提供者
    private long discoveredAt;       // 发现时间
}
```

### 4.3 SceneDetail

```java
public class SceneDetail {
    private String sceneId;          // 场景ID
    private String name;             // 场景名称
    private String description;      // 场景描述
    private String category;         // 分类
    private String icon;             // 图标
    private List<String> tags;       // 标签
    private List<String> capabilities; // 包含的能力
    private Map<String, Object> config; // 配置
}
```

### 4.4 CapabilityDetail

```java
public class CapabilityDetail {
    private String capId;            // 能力ID
    private String name;             // 能力名称
    private String version;          // 版本
    private String category;         // 分类
    private String description;      // 描述
    private String status;           // 状态
    private List<String> dependencies; // 依赖
    private Map<String, Object> metadata; // 元数据
}
```

## 5. 发现流程

### 5.1 标准发现流程

```
1. 用户设置发现范围
   └── DiscoveryScope scope = DiscoveryScope.DEPARTMENT;
       discoveryService.setDiscoveryScope(scope);

2. 执行发现查询
   └── DiscoveryQuery query = new DiscoveryQuery(DiscoveryType.SCENE, "messaging");
       CompletableFuture<List<DiscoveredItem>> future = discoveryService.searchScenes("messaging");

3. 系统选择适用的发现提供者
   └── 根据 scope 过滤提供者
       └── LocalFsProvider (PERSONAL ✓)
       └── UdpProvider (PERSONAL ✓, DEPARTMENT ✓)
       └── MdnsProvider (DEPARTMENT ✓)
       └── SkillCenterProvider (DEPARTMENT ✓, COMPANY ✓, PUBLIC ✓)

4. 并行执行发现
   └── 按优先级排序执行
       └── UdpProvider (100) -> 发现结果 A
       └── MdnsProvider (90) -> 发现结果 B
       └── SkillCenterProvider (80) -> 发现结果 C

5. 聚合结果
   └── 去重、排序、返回
```

### 5.2 同步流程

```
syncAllIndexes()
    │
    ├── 1. 从所有提供者获取数据
    │   ├── LocalFsProvider.sync()
    │   ├── UdpProvider.sync()
    │   ├── MdnsProvider.sync()
    │   └── SkillCenterProvider.sync()
    │
    ├── 2. 合并索引
    │   └── 去重、验证
    │
    ├── 3. 更新本地缓存
    │   └── 写入本地文件系统
    │
    └── 4. 返回同步结果
        └── SyncResult { sceneCount, capabilityCount, skillCount }
```

## 6. 发现提供者实现详情

### 6.1 UDP Broadcast Provider

| 属性 | 值 |
|------|-----|
| 名称 | UDP-BROADCAST |
| 优先级 | 100 |
| 适用范围 | PERSONAL, DEPARTMENT |
| 端口 | 48888 |
| 协议 | UDP Broadcast |

#### 6.1.1 协议实现

```java
/**
 * UDP广播发现提供者实现
 * 适用于局域网内快速发现
 */
public class UdpBroadcastDiscoveryProvider implements DiscoveryProvider {
    
    private static final int BROADCAST_PORT = 48888;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";
    private static final byte MSG_AGENT_ANNOUNCE = 0x01;
    private static final byte MSG_CAP_SHARE = 0x02;
    private static final byte MSG_SCENE_CREATE = 0x03;
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            
            // 发送发现请求
            sendDiscoveryRequest(query);
            
            // 等待响应（超时2秒）
            List<ResponsePacket> responses = waitForResponses(2000);
            
            // 解析响应
            for (ResponsePacket response : responses) {
                DiscoveredItem item = parseResponse(response);
                if (item != null) {
                    results.add(item);
                }
            }
            
            return results;
        });
    }
    
    private void sendDiscoveryRequest(DiscoveryQuery query) {
        DiscoveryPacket packet = new DiscoveryPacket();
        packet.setType(MSG_AGENT_ANNOUNCE);
        packet.setQuery(query.getQuery());
        packet.setScope(query.getScope().name());
        
        byte[] data = serialize(packet);
        DatagramPacket datagram = new DatagramPacket(
            data, data.length,
            InetAddress.getByName(BROADCAST_ADDRESS),
            BROADCAST_PORT
        );
        
        socket.send(datagram);
    }
}
```

### 6.2 mDNS Provider

| 属性 | 值 |
|------|-----|
| 名称 | MDNS |
| 优先级 | 90 |
| 适用范围 | PERSONAL, DEPARTMENT |
| 协议 | DNS-SD |
| 服务类型 | _ooder._tcp |

#### 6.2.1 协议实现

```java
/**
 * mDNS发现提供者实现
 * 基于DNS-SD协议，适用于局域网服务发现
 */
public class MdnsDiscoveryProvider implements DiscoveryProvider {
    
    private static final String SERVICE_TYPE = "_ooder._tcp.local.";
    private static final int SERVICE_PORT = 48889;
    
    private JmDNS jmdns;
    
    @Override
    public void initialize(DiscoveryConfig config) {
        try {
            jmdns = JmDNS.create(InetAddress.getLocalHost());
            
            // 注册本地服务
            ServiceInfo serviceInfo = ServiceInfo.create(
                SERVICE_TYPE,
                config.getServiceName(),
                SERVICE_PORT,
                config.getServiceDescription()
            );
            jmdns.registerService(serviceInfo);
        } catch (IOException e) {
            throw new DiscoveryException("mDNS初始化失败", e);
        }
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            
            // 发现服务
            ServiceInfo[] services = jmdns.list(SERVICE_TYPE);
            
            for (ServiceInfo service : services) {
                DiscoveredItem item = new DiscoveredItem();
                item.setId(service.getName());
                item.setName(service.getName());
                item.setType("AGENT");
                item.setSource(getProviderName());
                item.setDiscoveredAt(System.currentTimeMillis());
                
                // 解析TXT记录中的元数据
                Map<String, String> props = service.getPropertyNames();
                item.setMetadata(new HashMap<>(props));
                
                results.add(item);
            }
            
            return results;
        });
    }
}
```

### 6.3 DHT Provider

| 属性 | 值 |
|------|-----|
| 名称 | DHT-KADEMLIA |
| 优先级 | 70 |
| 适用范围 | DEPARTMENT, COMPANY |
| 协议 | Kademlia DHT |
| 引导节点 | 可配置 |

#### 6.3.1 协议实现

```java
/**
 * DHT发现提供者实现
 * 基于Kademlia协议，适用于分布式网络发现
 */
public class DhtDiscoveryProvider implements DiscoveryProvider {
    
    private KademliaNode kademliaNode;
    private List<InetAddress> bootstrapNodes;
    
    @Override
    public void initialize(DiscoveryConfig config) {
        this.bootstrapNodes = config.getBootstrapNodes();
        
        // 创建Kademlia节点
        kademliaNode = new KademliaNode(
            config.getNodeId(),
            config.getLocalAddress(),
            config.getLocalPort()
        );
        
        // 加入网络
        for (InetAddress bootstrap : bootstrapNodes) {
            kademliaNode.bootstrap(bootstrap);
        }
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            
            // 生成查询Key
            Key queryKey = generateKey(query.getQuery());
            
            // DHT查找
            List<Node> closestNodes = kademliaNode.findNode(queryKey);
            
            for (Node node : closestNodes) {
                // 向节点发送查询请求
                List<DiscoveredItem> nodeResults = queryNode(node, query);
                results.addAll(nodeResults);
            }
            
            return results;
        });
    }
    
    /**
     * 发布能力到DHT网络
     */
    public void publishCapability(Capability capability) {
        Key key = generateKey(capability.getId());
        Value value = serializeCapability(capability);
        kademliaNode.store(key, value);
    }
}
```

### 6.4 SkillCenter Provider

| 属性 | 值 |
|------|-----|
| 名称 | SKILL-CENTER |
| 优先级 | 80 |
| 适用范围 | DEPARTMENT, COMPANY, PUBLIC |
| 协议 | HTTP REST API |
| 认证 | Token |

#### 6.4.1 RESTful API实现

```java
/**
 * SkillCenter发现提供者实现
 * 基于RESTful API，适用于企业级发现
 */
public class SkillCenterDiscoveryProvider implements DiscoveryProvider {
    
    private static final String API_BASE = "/api/v2/discovery";
    private String baseUrl;
    private String authToken;
    private HttpClient httpClient;
    
    @Override
    public void initialize(DiscoveryConfig config) {
        this.baseUrl = config.getBaseUrl();
        this.authToken = config.getAuthToken();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 构建请求URL
                String url = buildSearchUrl(query);
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + authToken)
                    .header("Accept", "application/json")
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(
                    request, 
                    HttpResponse.BodyHandlers.ofString()
                );
                
                if (response.statusCode() == 200) {
                    return parseSearchResults(response.body());
                } else {
                    throw new DiscoveryException(
                        "SkillCenter查询失败: " + response.statusCode()
                    );
                }
            } catch (Exception e) {
                throw new DiscoveryException("发现请求失败", e);
            }
        });
    }
    
    private String buildSearchUrl(DiscoveryQuery query) {
        StringBuilder url = new StringBuilder(baseUrl + API_BASE + "/search");
        url.append("?type=").append(query.getType());
        url.append("&scope=").append(query.getScope());
        if (query.getQuery() != null) {
            url.append("&q=").append(URLEncoder.encode(query.getQuery(), "UTF-8"));
        }
        return url.toString();
    }
}
```

### 6.5 GitHub/Gitee Provider

| 属性 | 值 |
|------|-----|
| 名称 | GITHUB/GITEE |
| 优先级 | 60 |
| 适用范围 | PUBLIC |
| 协议 | GitHub/Gitee API |
| 认证 | OAuth Token |

#### 6.5.1 实现

```java
/**
 * GitHub发现提供者实现
 * 从GitHub/Gitee仓库发现公开技能
 */
public class GitHubDiscoveryProvider implements DiscoveryProvider {
    
    private static final String GITHUB_API = "https://api.github.com";
    private static final String GITEE_API = "https://gitee.com/api/v5";
    
    private String platform; // "github" or "gitee"
    private String apiToken;
    private HttpClient httpClient;
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            List<DiscoveredItem> results = new ArrayList<>();
            
            // 搜索包含ooder-skill标签的仓库
            String searchQuery = "topic:ooder-skill " + query.getQuery();
            
            String apiUrl = platform.equals("github") ? GITHUB_API : GITEE_API;
            String endpoint = apiUrl + "/search/repositories?q=" + 
                URLEncoder.encode(searchQuery, "UTF-8");
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "token " + apiToken)
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();
            
            try {
                HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
                );
                
                if (response.statusCode() == 200) {
                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray items = json.getAsJsonArray("items");
                    
                    for (JsonElement item : items) {
                        DiscoveredItem discovered = parseRepository(item.getAsJsonObject());
                        results.add(discovered);
                    }
                }
            } catch (Exception e) {
                logger.error("GitHub发现失败", e);
            }
            
            return results;
        });
    }
    
    private DiscoveredItem parseRepository(JsonObject repo) {
        DiscoveredItem item = new DiscoveredItem();
        item.setId(repo.get("full_name").getAsString());
        item.setName(repo.get("name").getAsString());
        item.setDescription(repo.get("description").getAsString());
        item.setType("SKILL");
        item.setSource(getProviderName());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("url", repo.get("html_url").getAsString());
        metadata.put("stars", repo.get("stargazers_count").getAsInt());
        metadata.put("language", repo.get("language").getAsString());
        item.setMetadata(metadata);
        
        return item;
    }
}
```

### 6.6 Git Repository Provider

| 属性 | 值 |
|------|-----|
| 名称 | GIT-REPOSITORY |
| 优先级 | 60 |
| 适用范围 | COMPANY |
| 协议 | Git API / Webhook |
| 认证 | SSH Key / Token |

#### 6.6.1 实现

```java
/**
 * Git仓库发现提供者实现
 * 适用于企业内部Git仓库（GitLab、Bitbucket等）
 */
public class GitRepositoryDiscoveryProvider implements DiscoveryProvider {
    
    private String repoUrl;
    private String branch;
    private Git git;
    private Path localPath;
    
    @Override
    public void initialize(DiscoveryConfig config) {
        this.repoUrl = config.getRepositoryUrl();
        this.branch = config.getBranch();
        this.localPath = Paths.get(config.getCacheDir(), "git-repo");
        
        try {
            if (Files.exists(localPath)) {
                git = Git.open(localPath.toFile());
            } else {
                git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(localPath.toFile())
                    .setBranch(branch)
                    .call();
            }
        } catch (Exception e) {
            throw new DiscoveryException("Git仓库初始化失败", e);
        }
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 拉取最新代码
                git.pull().call();
                
                // 扫描技能定义文件
                List<DiscoveredItem> results = new ArrayList<>();
                Path skillsDir = localPath.resolve("skills");
                
                if (Files.exists(skillsDir)) {
                    Files.walk(skillsDir)
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".json"))
                        .forEach(path -> {
                            try {
                                DiscoveredItem item = parseSkillDefinition(path);
                                if (matchesQuery(item, query)) {
                                    results.add(item);
                                }
                            } catch (IOException e) {
                                logger.warn("解析技能定义失败: " + path, e);
                            }
                        });
                }
                
                return results;
            } catch (Exception e) {
                throw new DiscoveryException("Git仓库发现失败", e);
            }
        });
    }
}
```

### 6.7 Local FS Provider

| 属性 | 值 |
|------|-----|
| 名称 | LOCAL-FS |
| 优先级 | 50 |
| 适用范围 | PERSONAL, DEPARTMENT, COMPANY, PUBLIC |
| 存储 | 本地文件系统 |
| 缓存 | 是 |

#### 6.7.1 实现

```java
/**
 * 本地文件系统发现提供者实现
 * 作为缓存层和离线发现机制
 */
public class LocalFsDiscoveryProvider implements DiscoveryProvider {
    
    private Path cacheDir;
    private Path indexFile;
    private ObjectMapper mapper;
    
    @Override
    public void initialize(DiscoveryConfig config) {
        this.cacheDir = Paths.get(config.getCacheDir());
        this.indexFile = cacheDir.resolve("discovery-index.json");
        this.mapper = new ObjectMapper();
        
        // 确保目录存在
        try {
            Files.createDirectories(cacheDir);
        } catch (IOException e) {
            throw new DiscoveryException("缓存目录创建失败", e);
        }
    }
    
    @Override
    public CompletableFuture<List<DiscoveredItem>> discover(DiscoveryQuery query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!Files.exists(indexFile)) {
                    return Collections.emptyList();
                }
                
                // 读取索引
                List<DiscoveredItem> allItems = mapper.readValue(
                    indexFile.toFile(),
                    new TypeReference<List<DiscoveredItem>>() {}
                );
                
                // 过滤
                return allItems.stream()
                    .filter(item -> matchesQuery(item, query))
                    .collect(Collectors.toList());
                    
            } catch (IOException e) {
                logger.error("本地索引读取失败", e);
                return Collections.emptyList();
            }
        });
    }
    
    /**
     * 更新本地缓存
     */
    public void updateCache(List<DiscoveredItem> items) {
        try {
            // 合并现有索引
            List<DiscoveredItem> existing = new ArrayList<>();
            if (Files.exists(indexFile)) {
                existing = mapper.readValue(
                    indexFile.toFile(),
                    new TypeReference<List<DiscoveredItem>>() {}
                );
            }
            
            // 去重合并
            Map<String, DiscoveredItem> merged = new HashMap<>();
            existing.forEach(item -> merged.put(item.getId(), item));
            items.forEach(item -> merged.put(item.getId(), item));
            
            // 写入文件
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(indexFile.toFile(), new ArrayList<>(merged.values()));
                
        } catch (IOException e) {
            logger.error("缓存更新失败", e);
        }
    }
}
```

## 7. 错误处理

### 7.1 错误码

| 错误码 | 说明 | 处理建议 |
|--------|------|---------|
| 4001 | 发现范围不支持 | 检查 scope 设置 |
| 4002 | 提供者未找到 | 检查提供者注册 |
| 4003 | 发现超时 | 增加超时时间或重试 |
| 4004 | 同步失败 | 检查网络连接 |
| 4005 | 无效的查询条件 | 检查 query 参数 |
| 4006 | 认证失败 | 检查 Token 有效性 |
| 4007 | 提供者初始化失败 | 检查配置参数 |
| 4008 | 网络不可达 | 检查网络连接 |

### 7.2 异常处理策略

```java
/**
 * 发现异常处理
 */
public class DiscoveryExceptionHandler {
    
    /**
     * 处理发现异常
     */
    public static List<DiscoveredItem> handleException(
            DiscoveryProvider provider,
            Throwable error,
            DiscoveryQuery query) {
        
        logger.error("发现提供者 [{}] 执行失败: {}", 
            provider.getProviderName(), error.getMessage());
        
        if (error instanceof TimeoutException) {
            // 超时：返回空结果，不影响其他提供者
            return Collections.emptyList();
        } else if (error instanceof AuthenticationException) {
            // 认证失败：标记提供者状态
            provider.markUnauthenticated();
            return Collections.emptyList();
        } else if (error instanceof NetworkException) {
            // 网络异常：使用缓存
            return provider.getCachedResults(query);
        } else {
            // 其他异常：返回空结果
            return Collections.emptyList();
        }
    }
}
```

## 8. 上层可视化发现流程指导

### 8.1 发现流程UI设计

```
┌─────────────────────────────────────────────────────────────┐
│  发现范围选择                                                  │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐           │
│  │  个人   │ │  部门   │ │  公司   │ │  公共   │           │
│  │ PERSONAL│ │DEPT     │ │COMPANY  │ │ PUBLIC  │           │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  搜索框                                                       │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 🔍 搜索 Scene、Capability、Skill...                    │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  发现状态: ● UDP扫描中... ● mDNS搜索中... ● SkillCenter已连接 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│  发现结果                                                     │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ 📦 Scene: 消息协作  [UDP发现] [v2.1.0]                  ││
│  │    描述: 团队消息协作场景                                ││
│  │    能力: 消息发送、文件共享、视频会议                     ││
│  ├─────────────────────────────────────────────────────────┤│
│  │ 🔧 Capability: 文件存储  [mDNS发现] [v1.5.0]            ││
│  │    描述: 分布式文件存储能力                              ││
│  │    来源: 192.168.1.100:8080                            ││
│  ├─────────────────────────────────────────────────────────┤│
│  │ ⚡ Skill: 邮件发送器  [SkillCenter] [v3.0.0]            ││
│  │    描述: 支持SMTP协议的邮件发送技能                      ││
│  │    评分: ⭐⭐⭐⭐⭐ (128次安装)                          ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
```

### 8.2 实时发现状态展示

```java
/**
 * 发现状态管理
 */
public class DiscoveryStatusManager {
    
    private Map<String, ProviderStatus> providerStatuses = new ConcurrentHashMap<>();
    
    /**
     * 更新提供者状态
     */
    public void updateStatus(String providerName, DiscoveryStatus status) {
        ProviderStatus ps = providerStatuses.computeIfAbsent(
            providerName, 
            k -> new ProviderStatus(k)
        );
        ps.setStatus(status);
        ps.setLastUpdate(System.currentTimeMillis());
        
        // 通知UI更新
        notifyUIUpdate();
    }
    
    /**
     * 获取所有提供者状态
     */
    public List<ProviderStatus> getAllStatuses() {
        return new ArrayList<>(providerStatuses.values());
    }
}

/**
 * 提供者状态
 */
public class ProviderStatus {
    private String providerName;
    private DiscoveryStatus status; // SCANNING, CONNECTED, ERROR, IDLE
    private String message;
    private long lastUpdate;
    private int foundCount;
}
```

### 8.3 渐进式发现交互

```
用户操作流程:

1. 打开发现面板
   └── 自动启动所有适用提供者
   └── 显示扫描进度动画

2. 输入搜索关键词
   └── 实时过滤本地缓存结果
   └── 同时触发远程搜索

3. 查看结果
   └── 本地结果立即显示
   └── 远程结果渐进式添加
   └── 显示来源标签 [UDP] [mDNS] [SkillCenter]

4. 选择项目
   └── 显示详细信息
   └── 提供"添加到工作区"按钮
   └── 显示依赖检查状态

5. 安装/使用
   └── 一键安装本地Skill
   └── 订阅远程Capability
   └── 创建Scene关联
```

## 9. 实现优先级建议

### 9.1 优先级矩阵

| 提供者 | 优先级 | 复杂度 | 建议实现阶段 |
|--------|--------|--------|-------------|
| Local FS | 50 | 低 | 第一阶段（基础） |
| UDP Broadcast | 100 | 中 | 第一阶段（基础） |
| SkillCenter API | 80 | 中 | 第二阶段（核心） |
| mDNS | 90 | 中 | 第二阶段（核心） |
| DHT | 70 | 高 | 第三阶段（扩展） |
| GitHub/Gitee | 60 | 低 | 第三阶段（扩展） |
| Git Repository | 60 | 中 | 第三阶段（扩展） |

### 9.2 实施路线图

```
第一阶段（基础发现）
├── Local FS Provider
│   └── 本地缓存机制
│   └── 离线发现支持
├── UDP Broadcast Provider
│   └── 局域网快速发现
│   └── 协议消息定义
└── 基础UI集成

第二阶段（核心发现）
├── SkillCenter API Provider
│   └── RESTful API实现
│   └── 认证机制
├── mDNS Provider
│   └── DNS-SD服务注册
│   └── 服务发现实现
└── 发现聚合层

第三阶段（扩展发现）
├── DHT Provider
│   └── Kademlia实现
│   └── 分布式发现
├── GitHub/Gitee Provider
│   └── 公开技能发现
├── Git Repository Provider
│   └── 企业技能发现
└── 高级搜索功能
```

## 10. 版本历史

| 版本 | 发布日期 | 主要变更 |
|------|----------|----------|
| 2.3 | 2026-02-23 | 统一发现服务抽象，标准化接口，完整实现指南 |

## 11. 参考资料

- [Agent 协议](./agent-protocol.md)
- [协议主文档](./protocol-main.md)
- [场景引擎架构](../architecture/scene-engine-architecture.md)

---

**Ooder Team | Version 2.3 | 2026-02-23**

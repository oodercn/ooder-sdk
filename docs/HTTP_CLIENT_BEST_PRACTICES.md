# HTTP 客户端最佳实践

> **版本**: 2.3  
> **日期**: 2026-03-02  
> **目标读者**: Skills 开发者

---

## 目录

1. [概述](#1-概述)
2. [基础配置](#2-基础配置)
3. [Token 管理](#3-token-管理)
4. [熔断器](#4-熔断器)
5. [重试策略](#5-重试策略)
6. [错误处理](#6-错误处理)
7. [完整示例](#7-完整示例)

---

## 1. 概述

本文档提供基于 Spring 生态的 HTTP 客户端最佳实践，包括：

- **RestTemplate**: Spring 标准 HTTP 客户端
- **Resilience4j**: 熔断器、限流、重试
- **Spring Retry**: 声明式重试
- **Jackson**: JSON 序列化/反序列化

---

## 2. 基础配置

### 2.1 Maven 依赖

```xml
<dependencies>
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Resilience4j -->
    <dependency>
        <groupId>io.github.resilience4j</groupId>
        <artifactId>resilience4j-spring-boot2</artifactId>
        <version>1.7.1</version>
    </dependency>
    
    <!-- Spring Retry -->
    <dependency>
        <groupId>org.springframework.retry</groupId>
        <artifactId>spring-retry</artifactId>
    </dependency>
    
    <!-- Jackson -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### 2.2 RestTemplate 配置

```java
@Configuration
public class HttpClientConfig {
    
    @Bean
    public RestTemplate restTemplate(
            ClientHttpRequestInterceptor tokenInterceptor,
            ResponseErrorHandler errorHandler) {
        
        RestTemplate template = new RestTemplate();
        
        // 添加拦截器
        template.getInterceptors().add(tokenInterceptor);
        
        // 设置错误处理器
        template.setErrorHandler(errorHandler);
        
        // 配置消息转换器
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        
        return template;
    }
    
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(30));
    }
}
```

---

## 3. Token 管理

### 3.1 Token 提供者接口

```java
public interface TokenProvider {
    String getAccessToken();
    void refreshToken();
    boolean isTokenExpired();
}

@Component
public class FeishuTokenProvider implements TokenProvider {
    
    @Value("${feishu.app-id}")
    private String appId;
    
    @Value("${feishu.app-secret}")
    private String appSecret;
    
    private volatile String accessToken;
    private volatile long expireTime;
    
    @Override
    public String getAccessToken() {
        if (isTokenExpired()) {
            refreshToken();
        }
        return accessToken;
    }
    
    @Override
    public synchronized void refreshToken() {
        // 调用飞书 API 获取 Token
        String url = "https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal";
        Map<String, String> body = new HashMap<>();
        body.put("app_id", appId);
        body.put("app_secret", appSecret);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        
        this.accessToken = (String) data.get("tenant_access_token");
        this.expireTime = System.currentTimeMillis() + ((Integer) data.get("expire") - 300) * 1000;
    }
    
    @Override
    public boolean isTokenExpired() {
        return accessToken == null || System.currentTimeMillis() > expireTime;
    }
}
```

### 3.2 Token 拦截器

```java
@Component
public class TokenInterceptor implements ClientHttpRequestInterceptor {
    
    @Autowired
    private TokenProvider tokenProvider;
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        
        // 添加 Token 到请求头
        String token = tokenProvider.getAccessToken();
        request.getHeaders().set("Authorization", "Bearer " + token);
        
        return execution.execute(request, body);
    }
}
```

---

## 4. 熔断器

### 4.1 Resilience4j 配置

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      feishu-api:
        baseConfig: default
      dingding-api:
        baseConfig: default
```

### 4.2 熔断器使用

```java
@Service
public class FeishuApiService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @CircuitBreaker(name = "feishu-api", fallbackMethod = "getUserFallback")
    public FeishuUser getUser(String userId) {
        String url = "https://open.feishu.cn/open-apis/contact/v3/users/{userId}";
        return restTemplate.getForObject(url, FeishuUser.class, userId);
    }
    
    public FeishuUser getUserFallback(String userId, Exception ex) {
        log.warn("获取飞书用户失败，使用降级策略", ex);
        // 返回本地缓存或默认数据
        return userCache.get(userId);
    }
    
    @CircuitBreaker(name = "feishu-api", fallbackMethod = "getDepartmentFallback")
    public FeishuDepartment getDepartment(String deptId) {
        String url = "https://open.feishu.cn/open-apis/contact/v3/departments/{deptId}";
        return restTemplate.getForObject(url, FeishuDepartment.class, deptId);
    }
    
    public FeishuDepartment getDepartmentFallback(String deptId, Exception ex) {
        log.warn("获取飞书部门失败，使用降级策略", ex);
        return departmentCache.get(deptId);
    }
}
```

---

## 5. 重试策略

### 5.1 Spring Retry 配置

```java
@Configuration
@EnableRetry
public class RetryConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        
        // 重试策略：最多3次
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3);
        
        // 退避策略：指数退避
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2);
        backOffPolicy.setMaxInterval(10000);
        
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);
        
        return template;
    }
}
```

### 5.2 声明式重试

```java
@Service
public class DingdingApiService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Retryable(
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2),
        retryFor = {IOException.class, HttpServerErrorException.class},
        noRetryFor = {HttpClientErrorException.class}
    )
    public DingdingUser getUser(String userId) {
        String url = "https://oapi.dingtalk.com/topapi/v2/user/get?userid={userId}";
        return restTemplate.getForObject(url, DingdingUser.class, userId);
    }
    
    @Recover
    public DingdingUser getUserRecover(Exception ex, String userId) {
        log.error("获取钉钉用户失败，已重试3次", ex);
        throw new ApiException("获取用户失败", ex);
    }
}
```

### 5.3 编程式重试

```java
@Service
public class WeComApiService {
    
    @Autowired
    private RetryTemplate retryTemplate;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public WeComUser getUser(String userId) {
        return retryTemplate.execute(
            context -> {
                String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get?userid={userId}";
                return restTemplate.getForObject(url, WeComUser.class, userId);
            },
            context -> {
                // 恢复逻辑
                log.error("获取企业微信用户失败", context.getLastThrowable());
                return userCache.get(userId);
            }
        );
    }
}
```

---

## 6. 错误处理

### 6.1 自定义错误处理器

```java
@Component
public class ApiErrorHandler extends DefaultResponseErrorHandler {
    
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        
        if (statusCode.is4xxClientError()) {
            // 客户端错误
            throw new ApiClientException("API客户端错误: " + body, statusCode.value());
        } else if (statusCode.is5xxServerError()) {
            // 服务端错误
            throw new ApiServerException("API服务端错误: " + body, statusCode.value());
        }
        
        super.handleError(response);
    }
}
```

### 6.2 自定义异常

```java
public class ApiException extends RuntimeException {
    private final int statusCode;
    
    public ApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

public class ApiClientException extends ApiException {
    public ApiClientException(String message, int statusCode) {
        super(message, statusCode);
    }
}

public class ApiServerException extends ApiException {
    public ApiServerException(String message, int statusCode) {
        super(message, statusCode);
    }
}
```

---

## 7. 完整示例

### 7.1 飞书组织架构同步服务

```java
@Service
public class FeishuOrgSyncService {
    
    @Autowired
    @Qualifier("feishuRestTemplate")
    private RestTemplate restTemplate;
    
    @Autowired
    private FeishuUserRepository userRepository;
    
    @Autowired
    private FeishuDepartmentRepository departmentRepository;
    
    /**
     * 同步用户
     */
    @CircuitBreaker(name = "feishu-api", fallbackMethod = "syncUsersFallback")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public SyncResult syncUsers(String departmentId) {
        String url = "https://open.feishu.cn/open-apis/contact/v3/users/find_by_department";
        
        Map<String, Object> params = new HashMap<>();
        params.put("department_id", departmentId);
        params.put("page_size", 50);
        
        ResponseEntity<FeishuUserListResponse> response = restTemplate.postForEntity(
            url, params, FeishuUserListResponse.class
        );
        
        List<FeishuUser> users = response.getBody().getData().getItems();
        
        // 保存到数据库
        int saved = 0;
        int updated = 0;
        for (FeishuUser user : users) {
            if (userRepository.existsById(user.getUserId())) {
                userRepository.update(user);
                updated++;
            } else {
                userRepository.save(user);
                saved++;
            }
        }
        
        return SyncResult.builder()
            .total(users.size())
            .saved(saved)
            .updated(updated)
            .build();
    }
    
    public SyncResult syncUsersFallback(String departmentId, Exception ex) {
        log.error("同步飞书用户失败", ex);
        return SyncResult.builder()
            .success(false)
            .errorMessage(ex.getMessage())
            .build();
    }
    
    /**
     * 同步部门
     */
    @CircuitBreaker(name = "feishu-api", fallbackMethod = "syncDepartmentsFallback")
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public SyncResult syncDepartments(String parentDepartmentId) {
        String url = "https://open.feishu.cn/open-apis/contact/v3/departments/{department_id}/children";
        
        ResponseEntity<FeishuDepartmentListResponse> response = restTemplate.getForEntity(
            url, FeishuDepartmentListResponse.class, parentDepartmentId
        );
        
        List<FeishuDepartment> departments = response.getBody().getData().getItems();
        
        // 递归同步子部门
        for (FeishuDepartment dept : departments) {
            departmentRepository.save(dept);
            // 递归同步
            syncDepartments(dept.getDepartmentId());
        }
        
        return SyncResult.builder()
            .total(departments.size())
            .success(true)
            .build();
    }
}
```

### 7.2 配置类

```java
@Configuration
public class FeishuConfig {
    
    @Bean
    @Qualifier("feishuRestTemplate")
    public RestTemplate feishuRestTemplate(
            FeishuTokenProvider tokenProvider,
            ApiErrorHandler errorHandler) {
        
        RestTemplate template = new RestTemplate();
        
        // Token 拦截器
        template.getInterceptors().add((request, body, execution) -> {
            String token = tokenProvider.getAccessToken();
            request.getHeaders().set("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        });
        
        // 错误处理器
        template.setErrorHandler(errorHandler);
        
        return template;
    }
}
```

---

## 8. 总结

| 功能 | 推荐方案 | 说明 |
|------|----------|------|
| HTTP 客户端 | RestTemplate | Spring 标准方案 |
| Token 管理 | Interceptor | 自动添加 Token |
| 熔断器 | Resilience4j | 业界标准 |
| 重试 | Spring Retry | 声明式/编程式 |
| 错误处理 | ErrorHandler | 统一异常处理 |
| JSON 解析 | Jackson | Spring 默认 |

---

**参考文档**:
- [Spring RestTemplate](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestTemplate.html)
- [Resilience4j](https://resilience4j.readme.io/)
- [Spring Retry](https://github.com/spring-projects/spring-retry)

**Made with ❤️ by Ooder Team**

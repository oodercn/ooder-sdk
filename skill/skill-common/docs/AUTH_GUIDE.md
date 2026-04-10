# skill-common 认证模块使用指南

## 一、登录认证

### 1.1 前端登录请求

```javascript
// 登录请求示例
async function login(username, password, role) {
    const response = await fetch('/api/v1/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',  // ⚠️ 必须设置，否则 Session 无法传递
        body: JSON.stringify({
            username: username,
            password: password,
            role: role  // 可选角色: installer, admin, leader, collaborator
        })
    });
    
    const result = await response.json();
    // ⚠️ 注意：使用 code === 200 或 status === 'success' 判断，不要使用 result.success
    if (result.code === 200 && result.data) {
        console.log('登录成功:', result.data);
        // result.data 包含 UserSession 信息
    }
    return result;
}
```

### 1.2 获取当前用户

```javascript
// 获取当前登录用户
async function getCurrentUser() {
    const response = await fetch('/api/v1/auth/session', {
        credentials: 'include'  // ⚠️ 必须设置
    });
    return await response.json();
}
```

### 1.3 登出

```javascript
async function logout() {
    await fetch('/api/v1/auth/logout', {
        method: 'POST',
        credentials: 'include'
    });
}
```

---

## 二、CORS 配置

### 2.1 AuthApi 已内置 CORS 支持

`AuthApi` 已添加 `@CrossOrigin` 注解，支持 `credentials: 'include'` 模式：

```java
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
public class AuthApi {
    // ...
}
```

### 2.2 启用全局 CORS 配置

在 `application.yml` 中添加：

```yaml
skill:
  common:
    cors:
      enabled: true
```

这将自动注册 `CorsFilter`，为所有 `/api/**` 路径提供 CORS 支持。

---

## 三、UserInfoProvider 接口

### 3.1 接口定义

```java
public interface UserInfoProvider {
    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @param clientIp 客户端IP
     * @param role 用户选择的角色
     * @return 用户信息对象，返回 null 表示登录失败
     */
    Object login(String username, String password, String clientIp, String role);
    
    void logout(String token);
    boolean validateToken(String token);
    Object getUser(String userId);
    List<?> getOrgTree();
    List<?> getOrgUsers(String orgId);
    Object registerUser(Object user);
    void updateUser(String userId, Object user);
}
```

### 3.2 实现示例

```java
@Component
public class MyUserInfoProvider implements AuthService.UserInfoProvider {
    
    @Override
    public Object login(String username, String password, String clientIp, String role) {
        // 1. 验证用户名密码
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;  // 登录失败
        }
        
        // 2. 根据 role 参数进行角色验证
        if (role != null && !user.getRoles().contains(role)) {
            return null;  // 用户没有选择的角色
        }
        
        // 3. 返回用户信息 (Map 或 POJO)
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("nickname", user.getNickname());
        userInfo.put("email", user.getEmail());
        userInfo.put("orgId", user.getOrgId());
        userInfo.put("orgName", user.getOrgName());
        userInfo.put("token", generateToken(user));
        return userInfo;
    }
    
    // ... 其他方法实现
}
```

### 3.3 注册 UserInfoProvider

```java
@Configuration
public class MyAuthConfig {
    
    @Bean
    public AuthService.UserInfoProvider userInfoProvider() {
        return new MyUserInfoProvider();
    }
    
    // 或者通过 setter 注入
    @Autowired
    public void setUserInfoProvider(AuthService authService, 
                                     AuthService.UserInfoProvider provider) {
        authService.setUserInfoProvider(provider);
    }
}
```

---

## 四、Session 管理

### 4.1 Session 存储方式

- **HttpSession**: 服务端 Session，通过 JSESSIONID Cookie 传递
- **Token**: 支持 Bearer Token 方式（通过 Authorization Header）

### 4.2 前端注意事项

| 场景 | 配置 |
|------|------|
| fetch 请求 | `credentials: 'include'` |
| axios 请求 | `withCredentials: true` |
| 开发环境跨域 | 需要配置代理或 CORS |

### 4.3 开发环境代理配置

**Vite 配置示例：**

```javascript
// vite.config.js
export default {
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
}
```

---

## 五、角色权限

### 5.1 内置角色

| 角色 ID | 名称 | 权限 |
|---------|------|------|
| installer | 系统安装者 | skill:install, skill:view, system:init |
| admin | 系统管理员 | capability:discover, capability:install, scene:create, scene:manage, user:assign |
| leader | 主导者 | scene:activate, scene:manage, key:generate, participant:manage, task:assign |
| collaborator | 协作者 | task:view, task:execute, task:submit, scene:view, todo:view |

### 5.2 权限检查

```java
// 在 Controller 中检查权限
@GetMapping("/admin-only")
public ResultModel<?> adminOnly(HttpServletRequest request) {
    if (!authService.hasPermission(request, "capability:install")) {
        return ResultModel.error(403, "无权限");
    }
    // ...
}
```

---

## 六、常见问题

### Q1: 登录成功但 getCurrentUser 返回未登录？

**原因**: 前端请求未携带 Cookie/Session

**解决**: 确保 fetch 请求设置 `credentials: 'include'`

### Q2: CORS 错误？

**原因**: 服务端未正确配置 CORS

**解决**: 
1. 确认 AuthApi 有 `@CrossOrigin` 注解
2. 或启用全局 CORS: `skill.common.cors.enabled=true`

### Q3: 如何自定义角色？

**解决**: 通过 `AuthService.setUserInfoProvider()` 注入自定义实现，在 `login()` 方法中处理角色逻辑。

---

*文档版本: 3.0.1*

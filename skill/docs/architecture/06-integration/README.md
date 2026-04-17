# 第六册：集成指南

## 目录

1. [Skill 开发指南](#1-skill-开发指南)
2. [CLI 扩展指南](#2-cli-扩展指南)
3. [最佳实践](#3-最佳实践)
4. [示例代码](#4-示例代码)

---

## 1. Skill 开发指南

### 1.1 Skill 基本结构

```
my-skill/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/
│   │   │       └── ooder/
│   │   │           └── skill/
│   │   │               └── myskill/
│   │   │                   ├── MySkillAutoConfiguration.java
│   │   │                   ├── MySkillService.java
│   │   │                   ├── MySkillController.java
│   │   │                   └── cli/
│   │   │                       └── MySkillCliExtension.java
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── spring.factories
│   │       └── skill.yaml
│   └── test/
├── pom.xml
└── README.md
```

### 1.2 skill.yaml 配置

```yaml
# skill.yaml
skill:
  id: my-skill
  name: My Skill
  version: 1.0.0
  description: A sample skill for demonstration
  
  # 作者信息
  author:
    name: Your Name
    email: your.email@example.com
    organization: Your Organization
  
  # 依赖
  dependencies:
    - skill: skill-common
      version: ">=3.0.0"
    - skill: scene-engine
      version: ">=3.0.0"
  
  # 路由配置
  routes:
    - path: /api/v1/my-skill/hello
      method: GET
      controllerClass: net.ooder.skill.myskill.MySkillController
      methodName: hello
      
  # 服务配置
  services:
    - interface: net.ooder.skill.myskill.MySkillService
      implementation: net.ooder.skill.myskill.MySkillServiceImpl
      
  # CLI 扩展
  cli:
    extensions:
      - command: greet
        description: Greet a user
        handler: net.ooder.skill.myskill.cli.MySkillCliExtension
        parameters:
          - name: name
            type: string
            required: true
            description: User name to greet
          - name: times
            type: integer
            required: false
            default: 1
            description: Number of times to greet
            
  # 场景能力
  capabilities:
    - id: my-skill:greet
      description: Greeting capability
      supportedSceneTypes:
        - meeting
        - chat
      parameters:
        - name: name
          type: string
          required: true
```

### 1.3 核心类实现

```java
/**
 * Skill 自动配置类
 */
@Configuration
@ConditionalOnProperty(name = "my-skill.enabled", havingValue = "true")
public class MySkillAutoConfiguration {
    
    @Bean
    public MySkillService mySkillService() {
        return new MySkillServiceImpl();
    }
    
    @Bean
    public MySkillController mySkillController(MySkillService service) {
        return new MySkillController(service);
    }
    
    @Bean
    public MySkillCliExtension mySkillCliExtension(MySkillService service) {
        return new MySkillCliExtension(service);
    }
}

/**
 * Skill 服务接口
 */
public interface MySkillService {
    
    String greet(String name);
    
    String greet(String name, int times);
    
    CompletableFuture<String> greetAsync(String name);
}

/**
 * Skill 服务实现
 */
@Service
public class MySkillServiceImpl implements MySkillService {
    
    @Override
    public String greet(String name) {
        return greet(name, 1);
    }
    
    @Override
    public String greet(String name, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append("Hello, ").append(name).append("!\n");
        }
        return sb.toString().trim();
    }
    
    @Override
    public CompletableFuture<String> greetAsync(String name) {
        return CompletableFuture.supplyAsync(() -> greet(name));
    }
}

/**
 * Skill 控制器
 */
@RestController
@RequestMapping("/api/v1/my-skill")
public class MySkillController {
    
    private final MySkillService service;
    
    public MySkillController(MySkillService service) {
        this.service = service;
    }
    
    @GetMapping("/hello")
    public ResultModel<String> hello(@RequestParam String name) {
        return ResultModel.success(service.greet(name));
    }
    
    @GetMapping("/hello/{times}")
    public ResultModel<String> helloMultiple(
            @RequestParam String name,
            @PathVariable int times) {
        return ResultModel.success(service.greet(name, times));
    }
}
```

---

## 2. CLI 扩展指南

### 2.1 CLI 扩展接口

```java
/**
 * Skill CLI 扩展接口
 */
public interface SkillCliExtension {
    
    /**
     * 获取 Skill ID
     */
    String getSkillId();
    
    /**
     * 获取命令名称
     */
    String getCommand();
    
    /**
     * 获取命令描述
     */
    String getDescription();
    
    /**
     * 获取命令用法
     */
    String getUsage();
    
    /**
     * 获取参数定义
     */
    List<ParamDefinition> getParameters();
    
    /**
     * 获取所需权限
     */
    List<String> getRequiredPermissions();
    
    /**
     * 执行命令
     */
    CliResult execute(String[] args, SceneContext context);
}

/**
 * 参数定义
 */
public interface ParamDefinition {
    
    String getName();
    
    ParamType getType();
    
    boolean isRequired();
    
    String getDefaultValue();
    
    String getDescription();
}
```

### 2.2 CLI 扩展实现

```java
/**
 * My Skill CLI 扩展
 */
@Component
public class MySkillCliExtension implements SkillCliExtension {
    
    private final MySkillService service;
    
    public MySkillCliExtension(MySkillService service) {
        this.service = service;
    }
    
    @Override
    public String getSkillId() {
        return "my-skill";
    }
    
    @Override
    public String getCommand() {
        return "greet";
    }
    
    @Override
    public String getDescription() {
        return "Greet a user with customizable options";
    }
    
    @Override
    public String getUsage() {
        return "skill exec my-skill greet --name=<name> [--times=<n>]";
    }
    
    @Override
    public List<ParamDefinition> getParameters() {
        return List.of(
            ParamDefinition.builder()
                .name("name")
                .type(ParamType.STRING)
                .required(true)
                .description("User name to greet")
                .build(),
            ParamDefinition.builder()
                .name("times")
                .type(ParamType.INTEGER)
                .required(false)
                .defaultValue("1")
                .description("Number of times to greet")
                .build()
        );
    }
    
    @Override
    public List<String> getRequiredPermissions() {
        return List.of("my-skill:execute");
    }
    
    @Override
    public CliResult execute(String[] args, SceneContext context) {
        try {
            // 解析参数
            Map<String, String> params = parseArgs(args);
            
            String name = params.get("name");
            int times = Integer.parseInt(params.getOrDefault("times", "1"));
            
            // 执行业务逻辑
            String result = service.greet(name, times);
            
            return CliResult.success(result);
            
        } catch (Exception e) {
            return CliResult.error("Failed to greet: " + e.getMessage());
        }
    }
    
    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("--")) 
                    ? args[++i] : "true";
                params.put(key, value);
            }
        }
        return params;
    }
}
```

### 2.3 注册 CLI 扩展

```java
/**
 * CLI 扩展注册器
 */
@Component
public class CliExtensionRegistry {
    
    private final Map<String, SkillCliExtension> extensions = new ConcurrentHashMap<>();
    
    /**
     * 注册扩展
     */
    public void register(SkillCliExtension extension) {
        String key = extension.getSkillId() + ":" + extension.getCommand();
        extensions.put(key, extension);
        log.info("Registered CLI extension: {}", key);
    }
    
    /**
     * 获取扩展
     */
    public SkillCliExtension get(String skillId, String command) {
        return extensions.get(skillId + ":" + command);
    }
    
    /**
     * 获取 Skill 的所有扩展
     */
    public List<SkillCliExtension> getBySkill(String skillId) {
        return extensions.values().stream()
            .filter(e -> e.getSkillId().equals(skillId))
            .collect(Collectors.toList());
    }
    
    /**
     * 列出所有扩展
     */
    public List<SkillCliExtension> listAll() {
        return new ArrayList<>(extensions.values());
    }
}

/**
 * 自动注册器
 */
@Component
public class CliExtensionAutoRegistrar {
    
    @Autowired
    private CliExtensionRegistry registry;
    
    /**
     * 自动注册所有 SkillCliExtension Bean
     */
    @EventListener(ApplicationReadyEvent.class)
    public void autoRegister(List<SkillCliExtension> extensions) {
        for (SkillCliExtension extension : extensions) {
            registry.register(extension);
        }
    }
}
```

---

## 3. 最佳实践

### 3.1 Skill 开发最佳实践

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          Skill 开发最佳实践                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 设计原则                                                                │
│     ✓ 单一职责：一个 Skill 只做一件事                                       │
│     ✓ 接口优先：先定义接口，再实现                                          │
│     ✓ 松耦合：减少对其他 Skill 的依赖                                       │
│     ✓ 可测试：编写单元测试和集成测试                                        │
│                                                                             │
│  2. 配置管理                                                                │
│     ✓ 使用 skill.yaml 声明所有元数据                                        │
│     ✓ 配置项使用 @ConfigurationProperties                                   │
│     ✓ 提供合理的默认值                                                      │
│     ✓ 敏感配置使用加密存储                                                  │
│                                                                             │
│  3. 错误处理                                                                │
│     ✓ 使用统一的错误码                                                      │
│     ✓ 错误信息要清晰可读                                                    │
│     ✓ 记录详细的错误日志                                                    │
│     ✓ 提供错误恢复机制                                                      │
│                                                                             │
│  4. 性能优化                                                                │
│     ✓ 异步处理耗时操作                                                      │
│     ✓ 使用缓存减少重复计算                                                  │
│     ✓ 控制并发数防止资源耗尽                                                │
│     ✓ 监控关键指标                                                          │
│                                                                             │
│  5. 文档编写                                                                │
│     ✓ 提供清晰的 README                                                     │
│     ✓ 编写 API 文档                                                         │
│     ✓ 提供使用示例                                                          │
│     ✓ 记录变更日志                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 CLI 扩展最佳实践

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CLI 扩展最佳实践                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 命令设计                                                                │
│     ✓ 命令名称简洁明了                                                      │
│     ✓ 参数命名符合惯例                                                      │
│     ✓ 提供详细的帮助信息                                                    │
│     ✓ 支持 --help 查看用法                                                  │
│                                                                             │
│  2. 参数处理                                                                │
│     ✓ 验证所有必需参数                                                      │
│     ✓ 提供合理的默认值                                                      │
│     ✓ 支持参数类型转换                                                      │
│     ✓ 过滤危险字符                                                          │
│                                                                             │
│  3. 错误处理                                                                │
│     ✓ 返回清晰的错误信息                                                    │
│     ✓ 使用适当的退出码                                                      │
│     ✓ 记录错误日志                                                          │
│     ✓ 提供解决建议                                                          │
│                                                                             │
│  4. 输出格式                                                                │
│     ✓ 支持多种输出格式（text/json/yaml）                                    │
│     ✓ 使用表格展示列表数据                                                  │
│     ✓ 高亮重要信息                                                          │
│     ✓ 支持分页显示                                                          │
│                                                                             │
│  5. 安全考虑                                                                │
│     ✓ 声明所需权限                                                          │
│     ✓ 验证用户权限                                                          │
│     ✓ 记录审计日志                                                          │
│     ✓ 防止命令注入                                                          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. 示例代码

### 4.1 完整 Skill 示例

```java
/**
 * 完整的 Skill 示例：天气查询 Skill
 */

// skill.yaml
/*
skill:
  id: weather-skill
  name: Weather Skill
  version: 1.0.0
  description: Query weather information
  
  routes:
    - path: /api/v1/weather/current
      method: GET
      controllerClass: net.ooder.skill.weather.WeatherController
      methodName: getCurrentWeather
      
  services:
    - interface: net.ooder.skill.weather.WeatherService
      implementation: net.ooder.skill.weather.WeatherServiceImpl
      
  cli:
    extensions:
      - command: query
        description: Query weather for a city
        handler: net.ooder.skill.weather.cli.WeatherCliExtension
        parameters:
          - name: city
            type: string
            required: true
          - name: days
            type: integer
            required: false
            default: 1
*/

// WeatherService.java
public interface WeatherService {
    WeatherInfo getCurrentWeather(String city);
    List<WeatherInfo> getWeatherForecast(String city, int days);
}

// WeatherServiceImpl.java
@Service
public class WeatherServiceImpl implements WeatherService {
    
    @Value("${weather.api.key}")
    private String apiKey;
    
    @Value("${weather.api.url}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public WeatherInfo getCurrentWeather(String city) {
        String url = String.format("%s/current?city=%s&key=%s", apiUrl, city, apiKey);
        return restTemplate.getForObject(url, WeatherInfo.class);
    }
    
    @Override
    public List<WeatherInfo> getWeatherForecast(String city, int days) {
        String url = String.format("%s/forecast?city=%s&days=%d&key=%s", 
            apiUrl, city, days, apiKey);
        WeatherForecastResponse response = restTemplate.getForObject(url, 
            WeatherForecastResponse.class);
        return response.getForecast();
    }
}

// WeatherCliExtension.java
@Component
public class WeatherCliExtension implements SkillCliExtension {
    
    private final WeatherService weatherService;
    
    public WeatherCliExtension(WeatherService weatherService) {
        this.weatherService = weatherService;
    }
    
    @Override
    public String getSkillId() {
        return "weather-skill";
    }
    
    @Override
    public String getCommand() {
        return "query";
    }
    
    @Override
    public String getDescription() {
        return "Query weather information for a city";
    }
    
    @Override
    public String getUsage() {
        return "skill exec weather-skill query --city=<city> [--days=<n>]";
    }
    
    @Override
    public List<ParamDefinition> getParameters() {
        return List.of(
            ParamDefinition.builder()
                .name("city")
                .type(ParamType.STRING)
                .required(true)
                .description("City name")
                .build(),
            ParamDefinition.builder()
                .name("days")
                .type(ParamType.INTEGER)
                .required(false)
                .defaultValue("1")
                .description("Number of days for forecast")
                .build()
        );
    }
    
    @Override
    public List<String> getRequiredPermissions() {
        return List.of("weather-skill:execute");
    }
    
    @Override
    public CliResult execute(String[] args, SceneContext context) {
        try {
            Map<String, String> params = parseArgs(args);
            
            String city = params.get("city");
            int days = Integer.parseInt(params.getOrDefault("days", "1"));
            
            if (days == 1) {
                WeatherInfo current = weatherService.getCurrentWeather(city);
                return CliResult.success(formatCurrentWeather(current));
            } else {
                List<WeatherInfo> forecast = weatherService.getWeatherForecast(city, days);
                return CliResult.success(formatForecast(forecast));
            }
            
        } catch (Exception e) {
            return CliResult.error("Failed to query weather: " + e.getMessage());
        }
    }
    
    private String formatCurrentWeather(WeatherInfo info) {
        return String.format("""
            Current Weather for %s:
            Temperature: %d°C
            Condition: %s
            Humidity: %d%%
            Wind: %d km/h
            """,
            info.getCity(),
            info.getTemperature(),
            info.getCondition(),
            info.getHumidity(),
            info.getWindSpeed()
        );
    }
    
    private String formatForecast(List<WeatherInfo> forecast) {
        StringBuilder sb = new StringBuilder("Weather Forecast:\n");
        for (WeatherInfo info : forecast) {
            sb.append(String.format("%s: %d°C, %s\n",
                info.getDate(),
                info.getTemperature(),
                info.getCondition()
            ));
        }
        return sb.toString();
    }
    
    private Map<String, String> parseArgs(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("--")) 
                    ? args[++i] : "true";
                params.put(key, value);
            }
        }
        return params;
    }
}
```

### 4.2 使用示例

```bash
# 安装 Weather Skill
skill install weather-skill --version=1.0.0

# 查询当前天气
skill exec weather-skill query --city=Beijing

# 查询未来 7 天天气
skill exec weather-skill query --city=Shanghai --days=7

# 在场景中调用
skill scene invoke my-scene weather-skill:query --city=Guangzhou
```

---

## 系列文档结束

感谢您阅读 Ooder Skills 架构文档系列！

### 文档列表

1. [第一册：总体架构概述](../01-overview/README.md)
2. [第二册：Agent SDK 深度解析](../02-agent-sdk/README.md)
3. [第三册：SceneEngine 场景引擎](../03-scene-engine/README.md)
4. [第四册：CLI 设计实现](../04-cli-design/README.md)
5. [第五册：安全与权限](../05-security/README.md)
6. [第六册：集成指南](../06-integration/README.md) (本文档)

### 相关资源

- [Ooder Skills GitHub](https://github.com/ooderCN/ooder-skills)
- [Scene Engine 文档](https://docs.ooder.cn/scene-engine)
- [CLI 使用手册](https://docs.ooder.cn/cli)

# JDSConfig 配置问题解决方案

## 问题分析

`JDSConfig.currServerHome()` 返回 null 导致 `NullPointerException`，阻塞应用启动。

### 根本原因

```java
// JDSConfig.java 第267行
File serverHome = new File(applicationHome().getAbsolutePath() + File.separator + getConfigName().getType());
```

`getConfigName()` 依赖 `UserBean.getInstance().getConfigName()`，如果未初始化则返回 null。

## 最小配置要求

### 必需配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `JDSHome` | JDS服务器主目录 | `./JDSHome` |
| `ConfigName` | 当前系统配置代码 | `scene` |

### 配置加载优先级

1. **JVM系统属性** (`-Dkey=value`)
2. **环境变量**
3. **application.properties** (Spring)
4. **jds_init.properties**
5. **jdsclient_init.properties**
6. **engine_config.xml**

## 解决方案

### 方案1：Spring Boot 配置（推荐）

```yaml
# application.yml
ooder:
  jds:
    server:
      home: ./JDSHome
    config:
      name: scene
```

### 方案2：Properties 文件

```properties
# jds_init.properties（放在classpath根目录）
JDSHome=./JDSHome
classPath=./
ConfigName=scene
```

### 方案3：JVM 参数

```bash
java -DJDSHome=./JDSHome \
     -Dooder.jds.config.name=scene \
     -jar your-app.jar
```

### 方案4：环境变量

```bash
export JDSHome=./JDSHome
export OODER_JDS_CONFIG_NAME=scene
```

## 目录结构要求

```
./JDSHome/                          # JDSHome 目录
├── application/                    # 应用目录
│   └── scene/                      # 场景引擎应用
│       ├── config/                 # 配置文件目录
│       │   └── engine_config.xml   # 引擎配置文件
│       ├── lib/                    # 库文件目录
│       ├── classes/                # 类文件目录
│       ├── data/                   # 数据目录
│       └── temp/                   # 临时目录
└── config/                         # 公共配置目录
    └── engine_config.xml           # 公共引擎配置
```

## 默认 engine_config.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<engine-config>
    <!-- 服务器配置 -->
    <property name="server.host">localhost</property>
    <property name="server.port">10523</property>
    
    <!-- 管理员配置 -->
    <property name="admin.host">localhost</property>
    <property name="admin.port">10523</property>
    <property name="admin.key">NA</property>
    <property name="admin.StartAdminThread">false</property>
    
    <!-- 登录配置 -->
    <property name="singleLogin">false</property>
    
    <!-- 缓存配置 -->
    <property name="server.dumpCache">true</property>
    <property name="server.cacheDbUser">sa</property>
    <property name="server.cacheDbPassword"></property>
    <property name="server.cacheDbURL">jdbc:hsqldb:hsql://localhost</property>
</engine-config>
```

## 代码层面的修复建议

### 1. 在 SceneEngineAutoConfiguration 中设置默认值

```java
@Configuration
public class SceneEngineAutoConfiguration {
    
    @PostConstruct
    public void initJDSConfig() {
        // 设置默认的 JDSHome
        if (System.getProperty("JDSHome") == null) {
            System.setProperty("JDSHome", "./JDSHome");
        }
        
        // 设置默认的 ConfigName
        if (System.getProperty("ConfigName") == null) {
            System.setProperty("ConfigName", "scene");
        }
        
        // 创建必要的目录结构
        createJDSDirectoryStructure();
    }
    
    private void createJDSDirectoryStructure() {
        String jdsHome = System.getProperty("JDSHome", "./JDSHome");
        String[] dirs = {
            jdsHome + "/application/scene/config",
            jdsHome + "/application/scene/lib",
            jdsHome + "/application/scene/classes",
            jdsHome + "/application/scene/data",
            jdsHome + "/application/scene/temp",
            jdsHome + "/config"
        };
        
        for (String dir : dirs) {
            File file = new File(dir);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }
}
```

### 2. 创建默认配置文件

```java
@Component
public class JDSConfigInitializer implements ApplicationRunner {
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 检查并创建默认的 engine_config.xml
        createDefaultEngineConfig();
    }
    
    private void createDefaultEngineConfig() throws IOException {
        String jdsHome = System.getProperty("JDSHome", "./JDSHome");
        File configFile = new File(jdsHome, "application/scene/config/engine_config.xml");
        
        if (!configFile.exists()) {
            // 创建默认配置文件
            String defaultConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<engine-config>\n" +
                "    <property name=\"server.host\">localhost</property>\n" +
                "    <property name=\"server.port\">10523</property>\n" +
                "</engine-config>";
            
            FileUtils.writeStringToFile(configFile, defaultConfig, StandardCharsets.UTF_8);
        }
    }
}
```

## 验证配置

```java
@Test
public void testJDSConfig() {
    // 验证 JDSHome
    String jdsHome = JDSConfig.getServerHome();
    assertNotNull(jdsHome);
    System.out.println("JDSHome: " + jdsHome);
    
    // 验证 ConfigName
    ConfigCode configName = JDSConfig.getConfigName();
    assertNotNull(configName);
    System.out.println("ConfigName: " + configName);
    
    // 验证 currServerHome
    File currServerHome = JDSConfig.Config.currServerHome();
    assertNotNull(currServerHome);
    System.out.println("currServerHome: " + currServerHome.getAbsolutePath());
}
```

## 总结

1. **最小配置**：设置 `JDSHome` 和 `ConfigName` 两个参数
2. **推荐方式**：使用 Spring Boot 的 `application.yml`
3. **代码修复**：在 `SceneEngineAutoConfiguration` 中提供默认值和目录结构创建
4. **向后兼容**：保留 XML 配置支持，但提供现代化的配置方式

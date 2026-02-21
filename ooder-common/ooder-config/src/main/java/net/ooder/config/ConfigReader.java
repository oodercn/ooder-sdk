package net.ooder.config;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.core.env.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 独立配置读取器
 * 用于在非Spring环境中读取配置文件
 */
public class ConfigReader {
    
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    
    /**
     * 读取默认配置文件（application.properties）
     * @return DatabaseProperties 数据库配置属性
     */
    public static DatabaseProperties readDatabaseConfig() {
        return readDatabaseConfig(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * 读取指定配置文件
     * @param configFile 配置文件路径
     * @return DatabaseProperties 数据库配置属性
     */
    public static DatabaseProperties readDatabaseConfig(String configFile) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载配置文件
            Resource resource = new ClassPathResource(configFile);
            if (resource.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                propertySources.addLast(new PropertiesPropertySource(configFile, properties));
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<DatabaseProperties> bindable = Bindable.of(DatabaseProperties.class);
            
            return binder.bind("ooder.database", bindable).orElse(new DatabaseProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read database configuration", e);
        }
    }
    
    /**
     * 读取多个配置文件
     * @param configFiles 配置文件路径列表
     * @return DatabaseProperties 数据库配置属性
     */
    public static DatabaseProperties readDatabaseConfig(List<String> configFiles) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载所有配置文件
            for (String configFile : configFiles) {
                Resource resource = new ClassPathResource(configFile);
                if (resource.exists()) {
                    Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    propertySources.addLast(new PropertiesPropertySource(configFile, properties));
                }
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<DatabaseProperties> bindable = Bindable.of(DatabaseProperties.class);
            
            return binder.bind("ooder.database", bindable).orElse(new DatabaseProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read database configuration", e);
        }
    }
    
    /**
     * 读取默认配置文件中的 AIServer 配置
     * @return AIServerProperties AIServer 配置属性
     */
    public static AIServerProperties readAIServerConfig() {
        return readAIServerConfig(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * 读取指定配置文件中的 AIServer 配置
     * @param configFile 配置文件路径
     * @return AIServerProperties AIServer 配置属性
     */
    public static AIServerProperties readAIServerConfig(String configFile) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载配置文件
            Resource resource = new ClassPathResource(configFile);
            if (resource.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                propertySources.addLast(new PropertiesPropertySource(configFile, properties));
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<AIServerProperties> bindable = Bindable.of(AIServerProperties.class);
            
            return binder.bind("ooder.aiserver", bindable).orElse(new AIServerProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read AIServer configuration", e);
        }
    }
    
    /**
     * 读取多个配置文件中的 AIServer 配置
     * @param configFiles 配置文件路径列表
     * @return AIServerProperties AIServer 配置属性
     */
    public static AIServerProperties readAIServerConfig(List<String> configFiles) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载所有配置文件
            for (String configFile : configFiles) {
                Resource resource = new ClassPathResource(configFile);
                if (resource.exists()) {
                    Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    propertySources.addLast(new PropertiesPropertySource(configFile, properties));
                }
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<AIServerProperties> bindable = Bindable.of(AIServerProperties.class);
            
            return binder.bind("ooder.aiserver", bindable).orElse(new AIServerProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read AIServer configuration", e);
        }
    }
    
    /**
     * 读取默认配置文件中的用户配置
     * @return UserProperties 用户配置属性
     */
    public static UserProperties readUserConfig() {
        return readUserConfig(DEFAULT_CONFIG_FILE);
    }
    
    /**
     * 读取指定配置文件中的用户配置
     * @param configFile 配置文件路径
     * @return UserProperties 用户配置属性
     */
    public static UserProperties readUserConfig(String configFile) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载配置文件
            Resource resource = new ClassPathResource(configFile);
            if (resource.exists()) {
                Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                propertySources.addLast(new PropertiesPropertySource(configFile, properties));
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<UserProperties> bindable = Bindable.of(UserProperties.class);
            
            return binder.bind("user", bindable).orElse(new UserProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user configuration", e);
        }
    }
    
    /**
     * 读取多个配置文件中的用户配置
     * @param configFiles 配置文件路径列表
     * @return UserProperties 用户配置属性
     */
    public static UserProperties readUserConfig(List<String> configFiles) {
        try {
            // 创建PropertySources
            MutablePropertySources propertySources = new MutablePropertySources();
            
            // 加载所有配置文件
            for (String configFile : configFiles) {
                Resource resource = new ClassPathResource(configFile);
                if (resource.exists()) {
                    Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                    propertySources.addLast(new PropertiesPropertySource(configFile, properties));
                }
            }
            
            // 添加系统属性和环境变量
            propertySources.addLast(new SystemEnvironmentPropertySource("systemEnvironment", convertEnvMap(System.getenv())));
            propertySources.addLast(new PropertiesPropertySource("systemProperties", System.getProperties()));
            
            // 创建Binder并绑定配置
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources));
            Bindable<UserProperties> bindable = Bindable.of(UserProperties.class);
            
            return binder.bind("user", bindable).orElse(new UserProperties());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user configuration", e);
        }
    }
    
    /**
     * 将 Map<String, String> 转换为 Map<String, Object>
     * @param envMap 环境变量映射
     * @return Map<String, Object> 转换后的映射
     */
    private static Map<String, Object> convertEnvMap(Map<String, String> envMap) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
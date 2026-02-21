package net.ooder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置类
 * 用于配置数据库相关的Bean
 */
@Configuration
public class DatabaseConfig {
    
    @Autowired
    private DatabaseProperties databaseProperties;
    
    /**
     * 获取数据库配置属性实例
     * @return DatabaseProperties 数据库配置属性
     */
    @Bean
    public DatabaseProperties databaseProperties() {
        return new DatabaseProperties();
    }
    
    /**
     * 示例：获取完整的数据库URL
     * 可以根据需要添加更多的配置方法
     * @return String 完整的数据库URL
     */
    public String getFullDatabaseUrl() {
        StringBuilder urlBuilder = new StringBuilder(databaseProperties.getUrl());
        
        // 如果是MySQL数据库，添加Unicode相关参数
        if (databaseProperties.getUrl().contains("mysql") && databaseProperties.getMysqlUseUnicode()) {
            if (databaseProperties.getUrl().contains("?")) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            urlBuilder.append("useUnicode=true")
                    .append("&characterEncoding=")
                    .append(databaseProperties.getEncoding());
        }
        
        return urlBuilder.toString();
    }
}
package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 实体映射注解
 * 用于将Java类映射到数据库表或其他持久化存储结构
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityMapping {
    
    /**
     * 数据库表名（必选）
     */
    String table();
    
    /**
     * 数据库模式（可选）
     * 默认为空，表示使用默认模式
     */
    String schema() default "";
    
    /**
     * 主键字段名（可选）
     * 默认为"id"
     */
    String primaryKey() default "id";
    
    /**
     * 主键生成策略（可选）
     * 默认为AUTO（自动生成）
     */
    PrimaryKeyStrategy primaryKeyStrategy() default PrimaryKeyStrategy.AUTO;
    
    /**
     * 是否自动生成CRUD操作（可选）
     * 默认为false
     */
    boolean autoCRUD() default false;
    
    /**
     * 是否启用乐观锁（可选）
     * 默认为false
     */
    boolean enableOptimisticLocking() default false;
    
    /**
     * 乐观锁字段名（可选）
     * 当enableOptimisticLocking为true时有效
     */
    String versionField() default "version";
    
    /**
     * 是否启用逻辑删除（可选）
     * 默认为false
     */
    boolean enableLogicDelete() default false;
    
    /**
     * 逻辑删除字段名（可选）
     * 当enableLogicDelete为true时有效
     */
    String deleteField() default "deleted";
    
    /**
     * 逻辑未删除值（可选）
     * 当enableLogicDelete为true时有效
     */
    String notDeletedValue() default "0";
    
    /**
     * 逻辑已删除值（可选）
     * 当enableLogicDelete为true时有效
     */
    String deletedValue() default "1";
    
    /**
     * 数据源名称（可选）
     * 指定该实体使用的数据源
     */
    String dataSource() default "";
    
    /**
     * 表注释（可选）
     * 用于生成数据库文档
     */
    String comment() default "";
    
    /**
     * 索引定义（可选）
     * 可以定义多个索引
     */
    Index[] indexes() default {};
    
    /**
     * 唯一约束定义（可选）
     * 可以定义多个唯一约束
     */
    UniqueConstraint[] uniqueConstraints() default {};
    
    /**
     * 自动生成时间戳字段（可选）
     */
    boolean autoTimestamp() default false;
    
    /**
     * 创建时间字段名（可选）
     * 当autoTimestamp为true时有效
     */
    String createTimeField() default "create_time";
    
    /**
     * 更新时间字段名（可选）
     * 当autoTimestamp为true时有效
     */
    String updateTimeField() default "update_time";
    
    /**
     * 主键生成策略枚举
     */
    enum PrimaryKeyStrategy {
        AUTO,       // 自动生成（数据库自增）
        UUID,       // UUID
        SEQUENCE,   // 序列（如Oracle）
        ASSIGNED,   // 手动分配
        SNOWFLAKE   // 雪花算法
    }
    
    /**
     * 索引定义
     */
    @interface Index {
        /**
         * 索引名称
         */
        String name();
        
        /**
         * 索引字段
         */
        String[] fields();
        
        /**
         * 是否唯一索引
         */
        boolean unique() default false;
    }
    
    /**
     * 唯一约束定义
     */
    @interface UniqueConstraint {
        /**
         * 约束名称
         */
        String name();
        
        /**
         * 约束字段
         */
        String[] fields();
    }
}
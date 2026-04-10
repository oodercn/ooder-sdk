package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 业务模块注解
 * 用于标识和定义业务系统中的模块单元
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BusinessModule {

    /**
     * 业务模块名称（必选）
     * 建议使用中文全称，如"客户管理"、"订单处理"
     */
    String value();

    /**
     * 业务模块描述（可选）
     * 对模块功能的详细说明
     */
    String desc() default "";

    /**
     * 模块编码（可选）
     * 系统内部使用的唯一标识，建议采用大写字母+短横线的形式
     * 如：HR-EMPLOYEE, FIN-ACCOUNT
     */
    String moduleCode() default "";

    /**
     * 父模块名称（可选）
     * 用于构建模块层级结构
     */
    String parentModule() default "";

    /**
     * 模块版本（可选）
     * 默认为"1.0.0"
     */
    String version() default "1.0.0";

    /**
     * 模块所有者（可选）
     * 通常为负责人工号或姓名
     */
    String owner() default "";

    /**
     * 模块状态（可选）
     * 默认为ACTIVE（活跃）
     */
    ModuleStatus status() default ModuleStatus.ACTIVE;

    /**
     * 模块优先级（可选）
     * 用于排序和资源分配
     */
    int priority() default 5;

    /**
     * 业务领域（可选）
     * 如：EDUCATION（教育）, FINANCE（金融）
     */
    BusinessDomain domain() default BusinessDomain.GENERAL;

    /**
     * 模块图标（可选）
     * 用于UI展示的图标标识
     */
    String icon() default "";

    /**
     * 模块路径（可选）
     * 用于系统导航的URL路径
     */
    String path() default "";

    /**
     * 模块标签（可选）
     * 用于分类和搜索的关键词
     */
    String[] tags() default {};

    /**
     * 模块依赖（可选）
     * 指定该模块依赖的其他模块编码
     */
    String[] dependencies() default {};

    /**
     * 模块扩展属性（可选）
     * 用于存储自定义配置的键值对
     */
    Property[] properties() default {};

    /**
     * 模块状态枚举
     */
    enum ModuleStatus {
        ACTIVE,      // 活跃（默认）
        DEVELOPING,  // 开发中
        TESTING,     // 测试中
        DEPRECATED,  // 已废弃
        ARCHIVED     // 已归档
    }

    /**
     * 业务领域枚举
     */
    enum BusinessDomain {
        GENERAL,     // 通用（默认）
        EDUCATION,   // 教育
        FINANCE,     // 金融
        HEALTHCARE,  // 医疗
        RETAIL,      // 零售
        MANUFACTURING, // 制造
        TRANSPORTATION, // 交通
        ENTERTAINMENT  // 娱乐
    }

    /**
     * 扩展属性结构
     */
    @interface Property {
        String key();

        String value();
    }
}
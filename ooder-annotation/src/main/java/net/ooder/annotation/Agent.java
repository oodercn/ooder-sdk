package net.ooder.annotation;

import java.lang.annotation.*;

/**
 * 标记类为AI Agent组件，定义核心元数据
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Agent {
    /** Agent唯一标识 */
    String id();
    
    /** Agent名称 */
    String name() default "";
    
    /** 功能描述 */
    String description() default "";
    
    /** 版本号 */
    String version() default "1.0.0";
    
    /** 所属领域分类 */
    AgentDomain domain() default AgentDomain.GENERAL;

    /**
     * 日志等级
     *
     *
     * @author ooder
     * @since 2025-08-25
     * @version <1.0>
     *
     */
    enum Accesslevel {

        DEBUG("调试"), INFO("信息"), WARN("警告"), ERROR("错误");

        /**
         * 定义枚举类型
         *
         * @param desc
         */
        private Accesslevel(String desc) {
            this.desc = desc;
        }

        /**
         * 显示
         */
        private String desc;

        /**
         * 数据域访问方法
         *
         * @return 描述信息
         */
        public String getDesc() {
            return desc;
        }

    }
}
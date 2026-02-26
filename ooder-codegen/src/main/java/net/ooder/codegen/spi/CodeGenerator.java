package net.ooder.codegen.spi;

import java.util.Map;

/**
 * 代码生成器接口
 * SPI 接口，允许不同的代码生成实现
 */
public interface CodeGenerator {
    
    /**
     * 获取生成器名称
     * @return 生成器名称
     */
    String getName();
    
    /**
     * 获取生成器版本
     * @return 版本号
     */
    String getVersion();
    
    /**
     * 生成代码
     * @param context 生成上下文
     * @return 生成的代码内容
     */
    String generate(GenerationContext context);
    
    /**
     * 是否支持该类型
     * @param type 类型标识
     * @return 是否支持
     */
    boolean supports(String type);
}

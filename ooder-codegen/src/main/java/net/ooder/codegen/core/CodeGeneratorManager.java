package net.ooder.codegen.core;

import net.ooder.codegen.spi.CodeGenerator;
import net.ooder.codegen.spi.GenerationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 代码生成器管理器
 * 通过 SPI 加载所有代码生成器
 */
public class CodeGeneratorManager {
    
    private static volatile CodeGeneratorManager instance;
    private static final Object LOCK = new Object();
    
    private final List<CodeGenerator> generators = new ArrayList<>();
    
    private CodeGeneratorManager() {
        loadGenerators();
    }
    
    public static CodeGeneratorManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new CodeGeneratorManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * 通过 SPI 加载所有代码生成器
     */
    private void loadGenerators() {
        ServiceLoader<CodeGenerator> loader = ServiceLoader.load(CodeGenerator.class);
        for (CodeGenerator generator : loader) {
            generators.add(generator);
        }
    }
    
    /**
     * 注册代码生成器
     * @param generator 生成器
     */
    public void registerGenerator(CodeGenerator generator) {
        generators.add(generator);
    }
    
    /**
     * 生成代码
     * @param type 类型
     * @param context 上下文
     * @return 生成的代码
     */
    public String generate(String type, GenerationContext context) {
        for (CodeGenerator generator : generators) {
            if (generator.supports(type)) {
                return generator.generate(context);
            }
        }
        throw new IllegalArgumentException("No generator found for type: " + type);
    }
    
    /**
     * 获取所有生成器
     * @return 生成器列表
     */
    public List<CodeGenerator> getAllGenerators() {
        return new ArrayList<>(generators);
    }
}

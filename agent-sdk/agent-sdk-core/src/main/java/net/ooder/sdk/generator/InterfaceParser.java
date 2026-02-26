package net.ooder.sdk.generator;

import net.ooder.sdk.core.driver.model.InterfaceDefinition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 接口定义解析器
 */
public interface InterfaceParser {

    /**
     * 从 JSON 字符串解析接口定义
     */
    InterfaceDefinition parse(String json);

    /**
     * 从文件解析接口定义
     */
    InterfaceDefinition parse(File file) throws IOException;

    /**
     * 从路径解析接口定义
     */
    InterfaceDefinition parse(Path path) throws IOException;

    /**
     * 验证接口定义格式
     */
    boolean validate(String json);
}

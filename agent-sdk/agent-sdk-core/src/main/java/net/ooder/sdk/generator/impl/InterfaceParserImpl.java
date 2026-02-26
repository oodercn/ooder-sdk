package net.ooder.sdk.generator.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ooder.sdk.core.driver.model.InterfaceDefinition;
import net.ooder.sdk.generator.InterfaceParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 接口定义解析器实现
 */
public class InterfaceParserImpl implements InterfaceParser {

    @Override
    public InterfaceDefinition parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return JSON.parseObject(json, InterfaceDefinition.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse interface definition: " + e.getMessage(), e);
        }
    }

    @Override
    public InterfaceDefinition parse(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("File not found: " + (file != null ? file.getPath() : "null"));
        }
        String content = new String(Files.readAllBytes(file.toPath()));
        return parse(content);
    }

    @Override
    public InterfaceDefinition parse(Path path) throws IOException {
        if (path == null) {
            throw new IOException("Path is null");
        }
        String content = new String(Files.readAllBytes(path));
        return parse(content);
    }

    @Override
    public boolean validate(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            JSONObject obj = JSON.parseObject(json);
            return obj.containsKey("interfaceId") && obj.containsKey("interfaceName");
        } catch (Exception e) {
            return false;
        }
    }
}

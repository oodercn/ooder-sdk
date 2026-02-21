package net.ooder.sdk.skill.driver.impl;

import com.alibaba.fastjson.JSON;
import net.ooder.sdk.skill.driver.InterfaceDefinition;
import net.ooder.sdk.skill.driver.InterfaceParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class InterfaceParserImpl implements InterfaceParser {
    
    @Override
    public InterfaceDefinition parse(InputStream input) {
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            // 读取输入流内容
            StringBuilder content = new StringBuilder();
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) != -1) {
                content.append(buffer, 0, len);
            }
            
            String text = content.toString();
            
            // 根据内容判断是YAML还是JSON
            if (text.trim().startsWith("{") || text.trim().startsWith("[")) {
                return parseFromJson(text);
            } else {
                return parseFromYaml(text);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse interface definition", e);
        }
    }
    
    @Override
    public InterfaceDefinition parseFromYaml(String yaml) {
        try {
            // 使用SnakeYAML解析YAML
            org.yaml.snakeyaml.Yaml snakeYaml = new org.yaml.snakeyaml.Yaml();
            Map<String, Object> map = snakeYaml.load(yaml);
            
            // 转换为InterfaceDefinition对象
            return convertToInterfaceDefinition(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse YAML interface definition", e);
        }
    }
    
    @Override
    public InterfaceDefinition parseFromJson(String json) {
        try {
            // 使用fastjson解析JSON
            Map<String, Object> map = JSON.parseObject(json, Map.class);
            
            // 转换为InterfaceDefinition对象
            return convertToInterfaceDefinition(map);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON interface definition", e);
        }
    }
    
    private InterfaceDefinition convertToInterfaceDefinition(Map<String, Object> map) {
        InterfaceDefinition interfaceDef = new InterfaceDefinition();
        
        // 设置基本属性
        if (map.containsKey("sceneId")) {
            interfaceDef.setSceneId(map.get("sceneId").toString());
        }
        if (map.containsKey("version")) {
            interfaceDef.setVersion(map.get("version").toString());
        }
        
        // 处理capabilities
        if (map.containsKey("capabilities")) {
            Map<String, Object> capabilitiesMap = (Map<String, Object>) map.get("capabilities");
            java.util.Map<String, InterfaceDefinition.CapabilityDefinition> capabilities = new java.util.HashMap<>();
            
            for (Map.Entry<String, Object> entry : capabilitiesMap.entrySet()) {
                String capName = entry.getKey();
                Map<String, Object> capMap = (Map<String, Object>) entry.getValue();
                
                InterfaceDefinition.CapabilityDefinition capability = new InterfaceDefinition.CapabilityDefinition();
                capability.setName(capMap.getOrDefault("name", capName).toString());
                capability.setDescription(capMap.getOrDefault("description", "").toString());
                
                // 处理methods
                if (capMap.containsKey("methods")) {
                    Map<String, Object> methodsMap = (Map<String, Object>) capMap.get("methods");
                    java.util.Map<String, InterfaceDefinition.MethodDefinition> methods = new java.util.HashMap<>();
                    
                    for (Map.Entry<String, Object> methodEntry : methodsMap.entrySet()) {
                        String methodName = methodEntry.getKey();
                        Map<String, Object> methodMap = (Map<String, Object>) methodEntry.getValue();
                        
                        InterfaceDefinition.MethodDefinition method = new InterfaceDefinition.MethodDefinition();
                        method.setName(methodMap.getOrDefault("name", methodName).toString());
                        method.setDescription(methodMap.getOrDefault("description", "").toString());
                        
                        // 处理input
                        if (methodMap.containsKey("input")) {
                            Map<String, Object> inputMap = (Map<String, Object>) methodMap.get("input");
                            InterfaceDefinition.SchemaDefinition input = convertToSchemaDefinition(inputMap);
                            method.setInput(input);
                        }
                        
                        // 处理output
                        if (methodMap.containsKey("output")) {
                            Map<String, Object> outputMap = (Map<String, Object>) methodMap.get("output");
                            InterfaceDefinition.SchemaDefinition output = convertToSchemaDefinition(outputMap);
                            method.setOutput(output);
                        }
                        
                        methods.put(methodName, method);
                    }
                    
                    capability.setMethods(methods);
                }
                
                capabilities.put(capName, capability);
            }
            
            interfaceDef.setCapabilities(capabilities);
        }
        
        return interfaceDef;
    }
    
    private InterfaceDefinition.SchemaDefinition convertToSchemaDefinition(Map<String, Object> map) {
        InterfaceDefinition.SchemaDefinition schema = new InterfaceDefinition.SchemaDefinition();
        
        if (map.containsKey("type")) {
            schema.setType(map.get("type").toString());
        }
        
        if (map.containsKey("required")) {
            schema.setRequired((java.util.List<String>) map.get("required"));
        }
        
        if (map.containsKey("properties")) {
            Map<String, Object> propertiesMap = (Map<String, Object>) map.get("properties");
            java.util.Map<String, InterfaceDefinition.PropertyDefinition> properties = new java.util.HashMap<>();
            
            for (Map.Entry<String, Object> entry : propertiesMap.entrySet()) {
                String propName = entry.getKey();
                Map<String, Object> propMap = (Map<String, Object>) entry.getValue();
                
                InterfaceDefinition.PropertyDefinition property = new InterfaceDefinition.PropertyDefinition();
                property.setDescription(propMap.getOrDefault("description", "").toString());
                
                if (propMap.containsKey("schema")) {
                    Map<String, Object> schemaMap = (Map<String, Object>) propMap.get("schema");
                    property.setSchema(convertToSchemaDefinition(schemaMap));
                }
                
                properties.put(propName, property);
            }
            
            schema.setProperties(properties);
        }
        
        return schema;
    }
}
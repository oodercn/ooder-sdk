package net.ooder.scene.core.driver;

import net.ooder.scene.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterfaceParser {
    
    private static final Logger logger = LoggerFactory.getLogger(InterfaceParser.class);
    
    private final Yaml yaml = new Yaml();
    
    public InterfaceDefinition parse(InputStream input) {
        Map<String, Object> data = yaml.load(input);
        return parseFromMap(data);
    }
    
    public InterfaceDefinition parseFromYaml(String yamlContent) {
        Map<String, Object> data = yaml.load(yamlContent);
        return parseFromMap(data);
    }
    
    @SuppressWarnings("unchecked")
    private InterfaceDefinition parseFromMap(Map<String, Object> data) {
        InterfaceDefinition def = new InterfaceDefinition();
        
        Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
        if (metadata != null) {
            def.setCategory((String) metadata.get("category"));
            def.setVersion((String) metadata.get("version"));
            def.setInterfaceHash((String) metadata.get("interfaceHash"));
        }
        
        Map<String, Object> spec = (Map<String, Object>) data.get("spec");
        if (spec == null) {
            return def;
        }
        
        Map<String, Object> capabilities = (Map<String, Object>) spec.get("capabilities");
        if (capabilities != null) {
            for (Map.Entry<String, Object> entry : capabilities.entrySet()) {
                String capName = entry.getKey();
                Map<String, Object> capData = (Map<String, Object>) entry.getValue();
                
                InterfaceDefinition.CapabilityDefinition capDef = parseCapability(capName, capData);
                def.addCapability(capName, capDef);
            }
        }
        
        return def;
    }
    
    @SuppressWarnings("unchecked")
    private InterfaceDefinition.CapabilityDefinition parseCapability(String name, Map<String, Object> data) {
        InterfaceDefinition.CapabilityDefinition cap = new InterfaceDefinition.CapabilityDefinition();
        cap.setName(name);
        cap.setDescription((String) data.get("description"));
        
        Map<String, Object> methods = (Map<String, Object>) data.get("methods");
        if (methods != null) {
            for (Map.Entry<String, Object> entry : methods.entrySet()) {
                String methodName = entry.getKey();
                Map<String, Object> methodData = (Map<String, Object>) entry.getValue();
                
                InterfaceDefinition.MethodDefinition methodDef = parseMethod(methodName, methodData);
                cap.addMethod(methodName, methodDef);
            }
        }
        
        return cap;
    }
    
    @SuppressWarnings("unchecked")
    private InterfaceDefinition.MethodDefinition parseMethod(String name, Map<String, Object> data) {
        InterfaceDefinition.MethodDefinition method = new InterfaceDefinition.MethodDefinition();
        method.setName(name);
        method.setDescription((String) data.get("description"));
        
        Map<String, Object> input = (Map<String, Object>) data.get("input");
        if (input != null) {
            method.setInput(parseSchema(input));
        }
        
        Map<String, Object> output = (Map<String, Object>) data.get("output");
        if (output != null) {
            method.setOutput(parseSchema(output));
        }
        
        List<Map<String, Object>> errors = (List<Map<String, Object>>) data.get("errors");
        if (errors != null) {
            for (Map<String, Object> errorData : errors) {
                String code = (String) errorData.get("code");
                if (code != null) {
                    InterfaceDefinition.ErrorDefinition errorDef = new InterfaceDefinition.ErrorDefinition();
                    errorDef.setCode(code);
                    errorDef.setMessage((String) errorData.get("message"));
                    method.addError(code, errorDef);
                }
            }
        }
        
        return method;
    }
    
    @SuppressWarnings("unchecked")
    private InterfaceDefinition.SchemaDefinition parseSchema(Map<String, Object> data) {
        InterfaceDefinition.SchemaDefinition schema = new InterfaceDefinition.SchemaDefinition();
        schema.setType((String) data.get("type"));
        schema.setDescription((String) data.get("description"));
        
        List<String> required = (List<String>) data.get("required");
        if (required != null) {
            schema.setRequired(required);
        }
        
        List<String> enumValues = (List<String>) data.get("enum");
        if (enumValues != null) {
            schema.setEnumValues(enumValues.toArray(new String[0]));
        }
        
        Object defaultValue = data.get("default");
        if (defaultValue != null) {
            schema.setDefaultValue(defaultValue);
        }
        
        Map<String, Object> properties = (Map<String, Object>) data.get("properties");
        if (properties != null) {
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String propName = entry.getKey();
                Map<String, Object> propData = (Map<String, Object>) entry.getValue();
                
                InterfaceDefinition.PropertyDefinition propDef = parseProperty(propName, propData);
                if (required != null && required.contains(propName)) {
                    propDef.setRequired(true);
                }
                schema.addProperty(propName, propDef);
            }
        }
        
        Map<String, Object> items = (Map<String, Object>) data.get("items");
        if (items != null) {
            schema.setItems(parseSchema(items));
        }
        
        return schema;
    }
    
    @SuppressWarnings("unchecked")
    private InterfaceDefinition.PropertyDefinition parseProperty(String name, Map<String, Object> data) {
        InterfaceDefinition.PropertyDefinition prop = new InterfaceDefinition.PropertyDefinition();
        prop.setName(name);
        prop.setType((String) data.get("type"));
        prop.setDescription((String) data.get("description"));
        
        List<String> enumValues = (List<String>) data.get("enum");
        if (enumValues != null) {
            prop.setEnumValues(enumValues.toArray(new String[0]));
        }
        
        Object defaultValue = data.get("default");
        if (defaultValue != null) {
            prop.setDefaultValue(defaultValue);
        }
        
        return prop;
    }
}

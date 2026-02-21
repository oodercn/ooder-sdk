package net.ooder.sdk.skill.driver;

import java.io.InputStream;

public interface InterfaceParser {
    
    InterfaceDefinition parse(InputStream input);
    
    InterfaceDefinition parseFromYaml(String yaml);
    
    InterfaceDefinition parseFromJson(String json);
}
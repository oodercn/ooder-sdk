package net.ooder.sdk.generator;

import java.io.File;
import net.ooder.sdk.skill.driver.InterfaceDefinition;

public interface CodeGenerator {
    
    GeneratedCode generate(InterfaceDefinition interfaceDef, GeneratorOptions options);
    
    void generateTo(InterfaceDefinition interfaceDef, GeneratorOptions options, File outputDir);
}
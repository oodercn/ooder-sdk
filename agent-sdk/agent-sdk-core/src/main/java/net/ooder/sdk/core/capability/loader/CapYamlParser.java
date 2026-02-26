package net.ooder.sdk.core.capability.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.ooder.sdk.api.capability.CapAddress;
import net.ooder.sdk.core.capability.model.CapDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CapYamlParser {
    
    private static final Logger log = LoggerFactory.getLogger(CapYamlParser.class);
    
    private final ObjectMapper yamlMapper;
    
    public CapYamlParser() {
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }
    
    public CapDefinition parse(Path yamlPath) throws IOException {
        log.debug("Parsing CAP from file: {}", yamlPath);
        try (InputStream is = Files.newInputStream(yamlPath)) {
            return parse(is);
        }
    }
    
    public CapDefinition parse(InputStream inputStream) throws IOException {
        log.debug("Parsing CAP from input stream");
        try {
            CapDefinition definition = yamlMapper.readValue(inputStream, CapDefinition.class);
            validate(definition);
            return definition;
        } catch (Exception e) {
            throw new IOException("Failed to parse CAP definition", e);
        }
    }
    
    private void validate(CapDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("CAP definition cannot be null");
        }
        if (definition.getMetadata() == null) {
            throw new IllegalArgumentException("CAP metadata is required");
        }
        if (definition.getMetadata().getCapId() == null || definition.getMetadata().getCapId().isEmpty()) {
            throw new IllegalArgumentException("CAP ID is required");
        }
        if (definition.getMetadata().getName() == null || definition.getMetadata().getName().isEmpty()) {
            throw new IllegalArgumentException("CAP name is required");
        }
        if (definition.getMetadata().getVersion() == null || definition.getMetadata().getVersion().isEmpty()) {
            throw new IllegalArgumentException("CAP version is required");
        }
        if (!CapAddress.isValidAddress(definition.getMetadata().getCapId())) {
            throw new IllegalArgumentException("Invalid CAP address: " + definition.getMetadata().getCapId());
        }
        log.debug("CAP definition validated: {} ({})", 
            definition.getMetadata().getName(), 
            definition.getMetadata().getCapId());
    }
}

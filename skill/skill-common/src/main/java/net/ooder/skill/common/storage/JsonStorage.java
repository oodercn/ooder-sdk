package net.ooder.skill.common.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonStorage {

    private static final Logger log = LoggerFactory.getLogger(JsonStorage.class);
    
    private final String basePath;
    private final ObjectMapper objectMapper;

    public JsonStorage(String basePath) {
        this.basePath = basePath;
        this.objectMapper = new ObjectMapper();
        initDirectory();
    }

    private void initDirectory() {
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void save(String key, Object data) {
        if (key == null || data == null) {
            return;
        }
        try {
            File file = new File(basePath, key + ".json");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
            log.debug("Saved data to: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save data for key: {}", key, e);
        }
    }

    public <T> T load(String key, Class<T> type) {
        if (key == null || type == null) {
            return null;
        }
        try {
            File file = new File(basePath, key + ".json");
            if (!file.exists()) {
                return null;
            }
            return objectMapper.readValue(file, type);
        } catch (IOException e) {
            log.error("Failed to load data for key: {}", key, e);
            return null;
        }
    }

    public <T> List<T> loadList(String key, Class<T> elementType) {
        if (key == null || elementType == null) {
            return new ArrayList<>();
        }
        try {
            File file = new File(basePath, key + ".json");
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            log.error("Failed to load list for key: {}", key, e);
            return new ArrayList<>();
        }
    }

    public void delete(String key) {
        if (key == null) {
            return;
        }
        File file = new File(basePath, key + ".json");
        if (file.exists()) {
            file.delete();
            log.debug("Deleted data for key: {}", key);
        }
    }

    public boolean exists(String key) {
        if (key == null) {
            return false;
        }
        File file = new File(basePath, key + ".json");
        return file.exists();
    }

    public List<String> listKeys() {
        List<String> keys = new ArrayList<>();
        File dir = new File(basePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    keys.add(name.substring(0, name.length() - 5));
                }
            }
        }
        return keys;
    }

    public void clear() {
        File dir = new File(basePath);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }
}

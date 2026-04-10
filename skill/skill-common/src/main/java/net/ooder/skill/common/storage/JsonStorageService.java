package net.ooder.skill.common.storage;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JsonStorageService {

    private static final Logger log = LoggerFactory.getLogger(JsonStorageService.class);

    private final String storagePath;
    private final String backupPath;
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, ReadWriteLock> locks = new ConcurrentHashMap<>();

    private boolean autoBackup = true;
    private int maxBackups = 5;

    public JsonStorageService(String storagePath) {
        this.storagePath = storagePath;
        this.backupPath = storagePath + "/backup";
    }

    public JsonStorageService() {
        this.storagePath = "./data";
        this.backupPath = "./data/backup";
    }

    public JsonStorageService(String storagePath, String backupPath) {
        this.storagePath = storagePath;
        this.backupPath = backupPath;
    }

    @PostConstruct
    public void init() {
        File storageDir = new File(storagePath);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
            log.info("Created storage directory: {}", storagePath);
        }
        
        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
            log.info("Created backup directory: {}", backupPath);
        }
    }

    private ReadWriteLock getLock(String collection) {
        return locks.computeIfAbsent(collection, k -> new ReentrantReadWriteLock());
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String collection, String id) {
        getLock(collection).readLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.get(collection);
            if (data != null) {
                return (T) data.get(id);
            }
            return null;
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void put(String collection, String id, T entity) {
        getLock(collection).writeLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.computeIfAbsent(collection, k -> new HashMap<>());
            data.put(id, entity);
            saveToFileAtomic(collection, data);
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> boolean remove(String collection, String id) {
        getLock(collection).writeLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.get(collection);
            if (data != null && data.remove(id) != null) {
                saveToFileAtomic(collection, data);
                return true;
            }
            return false;
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> list(String collection) {
        getLock(collection).readLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.get(collection);
            if (data != null) {
                return new ArrayList<>((Collection<T>) data.values());
            }
            return new ArrayList<>();
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getAll(String collection) {
        getLock(collection).readLock().lock();
        try {
            return (Map<String, T>) cache.getOrDefault(collection, new HashMap<>());
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    public void putAll(String collection, Map<String, Object> entities) {
        getLock(collection).writeLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.computeIfAbsent(collection, k -> new HashMap<>());
            data.putAll(entities);
            saveToFileAtomic(collection, data);
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    public void clear(String collection) {
        getLock(collection).writeLock().lock();
        try {
            cache.remove(collection);
            File file = new File(storagePath, collection + ".json");
            if (file.exists()) {
                file.delete();
            }
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    public boolean exists(String collection, String id) {
        getLock(collection).readLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.get(collection);
            return data != null && data.containsKey(id);
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    public int count(String collection) {
        getLock(collection).readLock().lock();
        try {
            Map<String, Object> data = (Map<String, Object>) cache.get(collection);
            return data != null ? data.size() : 0;
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    private void saveToFileAtomic(String collection, Map<String, Object> data) {
        try {
            File file = new File(storagePath, collection + ".json");
            File tempFile = new File(storagePath, collection + ".json.tmp");
            
            String json = JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
            
            Files.write(tempFile.toPath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
            
            log.debug("Saved {} items to {}", data.size(), collection);
            
            if (autoBackup) {
                createBackup(collection);
            }
        } catch (IOException e) {
            log.error("Failed to save collection: {}", collection, e);
        }
    }

    private void saveToFile(String collection, Map<String, Object> data) {
        try {
            File file = new File(storagePath, collection + ".json");
            String json = JSON.toJSONString(data, JSONWriter.Feature.PrettyFormat);
            Files.write(file.toPath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            log.debug("Saved {} to {}", collection, file.getName());
        } catch (IOException e) {
            log.error("Failed to save collection: {}", collection, e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String collection) {
        getLock(collection).writeLock().lock();
        try {
            File file = new File(storagePath, collection + ".json");
            if (file.exists()) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                    Map<String, Object> data = JSON.parseObject(json, Map.class, JSONReader.Feature.FieldBased);
                    cache.put(collection, data);
                    log.info("Loaded {} records from {}", data.size(), collection);
                } catch (IOException e) {
                    log.warn("Failed to load collection: {}", collection);
                }
            }
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    public void loadAll() {
        File storageDir = new File(storagePath);
        File[] files = storageDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                String collection = file.getName().replace(".json", "");
                loadFromFile(collection);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> loadList(String collection, Class<T> clazz) {
        getLock(collection).readLock().lock();
        try {
            File file = new File(storagePath, collection + ".json");
            if (file.exists()) {
                try {
                    String json = new String(Files.readAllBytes(file.toPath()), java.nio.charset.StandardCharsets.UTF_8);
                    return JSON.parseArray(json, clazz, JSONReader.Feature.FieldBased);
                } catch (IOException e) {
                    log.warn("Failed to load list: {}", collection);
                }
            }
            return new ArrayList<>();
        } finally {
            getLock(collection).readLock().unlock();
        }
    }

    public <T> void saveList(String collection, List<T> list) {
        getLock(collection).writeLock().lock();
        try {
            File file = new File(storagePath, collection + ".json");
            File tempFile = new File(storagePath, collection + ".json.tmp");
            
            String json = JSON.toJSONString(list, JSONWriter.Feature.PrettyFormat);
            
            Files.write(tempFile.toPath(), json.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
            
            log.debug("Saved {} items to {}", list.size(), collection);
        } catch (IOException e) {
            log.error("Failed to save list: {}", collection, e);
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    public void createBackup(String collection) {
        try {
            File sourceFile = new File(storagePath, collection + ".json");
            if (!sourceFile.exists()) {
                return;
            }
            
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = collection + "_" + timestamp + ".json";
            File backupFile = new File(backupPath, backupFileName);
            
            Files.copy(sourceFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.debug("Created backup: {}", backupFileName);
            
            cleanupOldBackups(collection);
        } catch (IOException e) {
            log.warn("Failed to create backup for: {}", collection);
        }
    }

    public void backupAll() {
        for (String collection : cache.keySet()) {
            createBackup(collection);
        }
    }

    private void cleanupOldBackups(String collection) {
        File backupDir = new File(backupPath);
        File[] backupFiles = backupDir.listFiles((dir, name) -> 
            name.startsWith(collection + "_") && name.endsWith(".json"));
        
        if (backupFiles != null && backupFiles.length > maxBackups) {
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < backupFiles.length - maxBackups; i++) {
                backupFiles[i].delete();
                log.debug("Deleted old backup: {}", backupFiles[i].getName());
            }
        }
    }

    public boolean restoreFromBackup(String collection, String backupFileName) {
        getLock(collection).writeLock().lock();
        try {
            File backupFile = new File(backupPath, backupFileName);
            if (!backupFile.exists()) {
                log.warn("Backup file not found: {}", backupFileName);
                return false;
            }
            
            File targetFile = new File(storagePath, collection + ".json");
            Files.copy(backupFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            loadFromFile(collection);
            log.info("Restored {} from backup: {}", collection, backupFileName);
            return true;
        } catch (IOException e) {
            log.error("Failed to restore from backup: {}", backupFileName, e);
            return false;
        } finally {
            getLock(collection).writeLock().unlock();
        }
    }

    public List<String> listBackups(String collection) {
        File backupDir = new File(backupPath);
        File[] backupFiles = backupDir.listFiles((dir, name) -> 
            name.startsWith(collection + "_") && name.endsWith(".json"));
        
        List<String> backups = new ArrayList<>();
        if (backupFiles != null) {
            for (File file : backupFiles) {
                backups.add(file.getName());
            }
            Collections.sort(backups, Collections.reverseOrder());
        }
        return backups;
    }

    public void setAutoBackup(boolean autoBackup) {
        this.autoBackup = autoBackup;
    }

    public void setMaxBackups(int maxBackups) {
        this.maxBackups = maxBackups;
    }

    public String getStorageRoot() {
        return this.storagePath;
    }
}

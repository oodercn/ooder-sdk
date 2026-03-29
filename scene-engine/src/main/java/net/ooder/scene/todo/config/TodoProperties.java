package net.ooder.scene.todo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Todo 服务配置属性
 *
 * <p>通过 application.yml 配置：</p>
 * <pre>
 * ooder:
 *   scene:
 *     todo:
 *       enabled: true
 *       storage:
 *         type: json
 *         path: ./data/todos
 *         max-size: 10000
 *         expire-days: 30
 * </pre>
 *
 * @author SE Team
 * @version 2.3.1
 * @since 2.3.1
 */
@ConfigurationProperties(prefix = "ooder.scene.todo")
public class TodoProperties {

    private boolean enabled = true;

    private StorageProperties storage = new StorageProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public StorageProperties getStorage() {
        return storage;
    }

    public void setStorage(StorageProperties storage) {
        this.storage = storage;
    }

    public static class StorageProperties {
        private String type = "json";
        private String path = System.getProperty("user.home") + "/.ooder/data/todos";
        private int maxSize = 10000;
        private int expireDays = 30;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public int getExpireDays() {
            return expireDays;
        }

        public void setExpireDays(int expireDays) {
            this.expireDays = expireDays;
        }
    }
}

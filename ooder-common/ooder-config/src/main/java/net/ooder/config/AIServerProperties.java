package net.ooder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AIServer 配置属性类
 * 使用@ConfigurationProperties注解自动绑定配置文件中的属性
 */

@Component
@ConfigurationProperties(prefix = "ooder.aiserver")
public class AIServerProperties {
    
    /**
     * AIServer 是否启用
     */
    private boolean enabled = true;
    
    /**
     * AIServer 名称
     */
    private String name = "ooder-ai-server";
    
    /**
     * AIServer 版本
     */
    private String version = "1.0.0";
    
    /**
     * 模型配置
     */
    private Model model = new Model();
    
    /**
     * 推理引擎配置
     */
    private Engine engine = new Engine();
    
    /**
     * 资源配置
     */
    private Resource resource = new Resource();
    
    /**
     * 服务器配置
     */
    private Server server = new Server();
    
    /**
     * 日志配置
     */
    private Logging logging = new Logging();


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    /**
     * 模型配置内部类
     */

    public static class Model {
        private String path;
        private String type = "pytorch";
        private String version = "latest";
        private ModelParams params = new ModelParams();

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public ModelParams getParams() {
            return params;
        }

        public void setParams(ModelParams params) {
            this.params = params;
        }


        public static class ModelParams {
            private Integer batchSize = 16;
            private Integer maxSequenceLength = 512;

            public Integer getBatchSize() {
                return batchSize;
            }

            public void setBatchSize(Integer batchSize) {
                this.batchSize = batchSize;
            }

            public Integer getMaxSequenceLength() {
                return maxSequenceLength;
            }

            public void setMaxSequenceLength(Integer maxSequenceLength) {
                this.maxSequenceLength = maxSequenceLength;
            }
        }
    }
    
    /**
     * 推理引擎配置内部类
     */

    public static class Engine {
        private String type = "onnxruntime";
        private Integer timeout = 30000;
        private Integer maxBatchSize = 32;
        private Integer threadCount = 4;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getTimeout() {
            return timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }

        public Integer getMaxBatchSize() {
            return maxBatchSize;
        }

        public void setMaxBatchSize(Integer maxBatchSize) {
            this.maxBatchSize = maxBatchSize;
        }

        public Integer getThreadCount() {
            return threadCount;
        }

        public void setThreadCount(Integer threadCount) {
            this.threadCount = threadCount;
        }
    }
    
    /**
     * 资源配置内部类
     */

    public static class Resource {
        private String gpuIds = "0";
        private String memoryLimit = "8g";
        private Integer cpuCores = 8;
        private Integer maxConcurrentRequests = 100;

        public String getGpuIds() {
            return gpuIds;
        }

        public void setGpuIds(String gpuIds) {
            this.gpuIds = gpuIds;
        }

        public String getMemoryLimit() {
            return memoryLimit;
        }

        public void setMemoryLimit(String memoryLimit) {
            this.memoryLimit = memoryLimit;
        }

        public Integer getCpuCores() {
            return cpuCores;
        }

        public void setCpuCores(Integer cpuCores) {
            this.cpuCores = cpuCores;
        }

        public Integer getMaxConcurrentRequests() {
            return maxConcurrentRequests;
        }

        public void setMaxConcurrentRequests(Integer maxConcurrentRequests) {
            this.maxConcurrentRequests = maxConcurrentRequests;
        }
    }
    
    /**
     * 服务器配置内部类
     */

    public static class Server {
        private String host = "0.0.0.0";
        private Integer port = 8080;
        private Integer maxConnections = 500;
        private Integer connectionTimeout = 10000;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public Integer getMaxConnections() {
            return maxConnections;
        }

        public void setMaxConnections(Integer maxConnections) {
            this.maxConnections = maxConnections;
        }

        public Integer getConnectionTimeout() {
            return connectionTimeout;
        }

        public void setConnectionTimeout(Integer connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
        }
    }
    
    /**
     * 日志配置内部类
     */

    public static class Logging {

        private String level = "info";
        private String path = "/var/log/ooder-aiserver";
        private String format = "json";

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }
}
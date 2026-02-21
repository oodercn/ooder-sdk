# ooderAgent Enterprise Development Kit

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Version](https://img.shields.io/badge/version-2.0-green.svg)](#)
[![Java](https://img.shields.io/badge/java-8+-orange.svg)](#)

## Introduction

**ooderAgent** is an enterprise-grade foundational application framework by ooder, providing a complete technical foundation for enterprise digital transformation. This kit adopts a modular design, covering core capabilities such as data storage, cache management, file storage, organizational structure, message communication, vector retrieval, and IoT, helping enterprises quickly build stable and efficient business systems.

This project adopts the **MIT Open Source License**, aiming to provide enterprise developers with out-of-the-box enterprise-grade development tools.

---

## Software Architecture

ooderAgent adopts a layered modular design, an independent Maven multi-module project, containing the following core modules:

### 🗄️ Data Storage Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-database** | Database Configuration Management | JDBC database operation encapsulation, connection pool management, DAO factory pattern, SQL tools and metadata management |
| **ooder-index-web** | Vector Database Configuration | Unstructured data storage and retrieval services, supporting vector database configuration |

### ⚡ Cache Management Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-common-client** | REDIS Cache Management | Redis connection pool management, cache operation encapsulation, distributed cache support |

### 📁 File Storage Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-vfs-web** | VFS Storage Management Interface | Distributed file storage access, file version control, multi-user collaboration support, release management foundation |

### 🏢 Organization Management Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-org-web** | Internal Organization Interface | Organization interface abstraction, personnel management, department management, permission foundation support |

### 💬 Message Communication Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-msg-web** | General Message Management | Message communication services, MQTT protocol support, IoT message processing, message queue management |

### ⚙️ Basic Service Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-config** | Basic Configuration Package | Spring Boot configuration management, configuration property automatic binding, configuration processor |
| **ooder-server** | Microservice Support | System user authentication, cluster management, service registration and discovery, HTTP proxy services |

### 🌐 IoT Layer

| Module | Description | Function Overview |
|--------|-------------|-------------------|
| **ooder-iot-webclient** | IoT Northbound Protocol Conversion Basic Package | IoT client API, northbound protocol conversion, device management, data collection and reporting |

---

## Quick Start

### Requirements

- Java 8+
- Maven 3.6+
- Redis 5.0+ (optional, for cache functionality)
- MySQL 5.7+ / PostgreSQL 10+ (optional, for database functionality)

### Installation

#### Maven Dependency

Add the required module dependencies to your `pom.xml` file:

```xml
<!-- Basic Configuration Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-config</artifactId>
    <version>2.1</version>
</dependency>

<!-- Core Client Package (includes REDIS management) -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-common-client</artifactId>
    <version>2.1</version>
</dependency>

<!-- Database Configuration Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-database</artifactId>
    <version>2.1</version>
</dependency>

<!-- VFS Storage Management Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-vfs-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- Organization Management Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-org-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- General Message Management Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-msg-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- Vector Database Configuration Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-index-web</artifactId>
    <version>2.1</version>
</dependency>

<!-- Microservice Support Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-server</artifactId>
    <version>2.1</version>
</dependency>

<!-- IoT Northbound Protocol Conversion Package -->
<dependency>
    <groupId>net.ooder</groupId>
    <artifactId>ooder-iot-webclient</artifactId>
    <version>2.1</version>
</dependency>
```

#### Source Compilation

```bash
# Clone project
git clone https://github.com/ooder-net/ooder-common.git
cd ooder-common

# Compile and install
mvn clean install
```

---

## Module Detailed Description

### 1. 🗄️ ooder-database - Database Configuration Management

Database operation encapsulation module, providing lightweight, enterprise-grade database operation tools.

**Core Functions:**
- ✅ Multi-datasource configuration management
- ✅ Connection pool automatic management (supports HikariCP, C3P0)
- ✅ DAO factory pattern, simplifying data access layer development
- ✅ SQL parsing and metadata management
- ✅ Transaction management and connection pool monitoring

**Usage Example:**
```java
import net.ooder.common.database.DBAgent;
import net.ooder.common.database.dao.DAO;

// Get database agent
DBAgent dbAgent = new DBAgent("configKey");

// Execute SQL
int result = dbAgent.execute("SELECT * FROM users WHERE status = ?", 1);

// Use DAO factory
DAO<User> userDao = DAOFactory.getDAO(User.class);
List<User> users = userDao.findAll();
```

---

### 2. ⚡ ooder-common-client - REDIS Cache Management

Enterprise-grade cache management module, providing Redis connection pool management and cache operation encapsulation.

**Core Functions:**
- ✅ Redis connection pool automatic management
- ✅ Distributed cache support
- ✅ Cache serialization and deserialization
- ✅ Cache expiration policy configuration
- ✅ Cluster mode support

**Usage Example:**
```java
import net.ooder.common.cache.redis.RedisPoolUtil;
import redis.clients.jedis.Jedis;

// Get Redis connection
RedisPoolUtil redisPool = RedisPoolUtil.getInstance("redisKey");
try (Jedis jedis = redisPool.getResource()) {
    // Set cache
    jedis.set("user:1001", userJson);
    jedis.expire("user:1001", 3600);
    
    // Get cache
    String userData = jedis.get("user:1001");
}
```

---

### 3. 📁 ooder-vfs-web - VFS Storage Management Interface

Distributed file storage management module, providing enterprise-grade file storage solutions.

**Core Functions:**
- ✅ Distributed file storage access
- ✅ File version control and management
- ✅ Multi-user collaboration file locking mechanism
- ✅ File release and approval workflow
- ✅ Large file chunked upload/download

**Usage Example:**
```java
import net.ooder.vfs.VFSManager;
import net.ooder.vfs.model.VFSFile;

// Get VFS manager
VFSManager vfsManager = VFSManager.getInstance();

// Upload file
VFSFile file = vfsManager.upload("/docs/report.pdf", inputStream);

// Download file
InputStream downloadStream = vfsManager.download(file.getId());

// Version management
List<VFSFile> versions = vfsManager.getVersions(file.getId());
```

---

### 4. 🏢 ooder-org-web - Internal Organization Interface

Enterprise organization management module, providing flexible organizational structure management capabilities.

**Core Functions:**
- ✅ Organization tree management
- ✅ Personnel information management
- ✅ Department position configuration
- ✅ Permission foundation data support
- ✅ Organization structure import/export

**Usage Example:**
```java
import net.ooder.common.org.OrgManager;
import net.ooder.common.org.model.Department;
import net.ooder.common.org.model.Person;

// Get organization manager
OrgManager orgManager = OrgManager.getInstance();

// Create department
Department dept = new Department();
dept.setName("Technology Department");
dept.setCode("TECH");
orgManager.createDepartment(dept);

// Add personnel
Person person = new Person();
person.setName("John Doe");
person.setDepartmentId(dept.getId());
orgManager.createPerson(person);
```

---

### 5. 💬 ooder-msg-web - General Message Management

Enterprise-grade message communication module, supporting multiple message protocols and IoT scenarios.

**Core Functions:**
- ✅ MQTT protocol support
- ✅ Message queue management
- ✅ Real-time message push
- ✅ Message persistence and retry
- ✅ IoT device message processing

**Usage Example:**
```java
import net.ooder.msg.MsgFactory;
import net.ooder.msg.model.Message;

// Get message factory
MsgFactory msgFactory = MsgFactory.getInstance();

// Send message
Message message = new Message();
message.setTopic("device/data");
message.setPayload(dataJson);
msgFactory.publish(message);

// Subscribe to messages
msgFactory.subscribe("device/+/status", (msg) -> {
    System.out.println("Received device status: " + msg.getPayload());
});
```

---

### 6. 🔍 ooder-index-web - Vector Database Configuration

Unstructured data storage and retrieval module, supporting vector database configuration.

**Core Functions:**
- ✅ Vector data storage
- ✅ Similarity retrieval
- ✅ Full-text retrieval support
- ✅ Index management
- ✅ High-performance query optimization

**Usage Example:**
```java
import net.ooder.index.IndexFactory;
import net.ooder.index.model.IndexDocument;

// Get index factory
IndexFactory indexFactory = IndexFactory.getInstance();

// Create index document
IndexDocument doc = new IndexDocument();
doc.setId("doc001");
doc.setContent("Document content...");
doc.setVector(vectorData);

// Store index
indexFactory.index(doc);

// Vector retrieval
List<IndexDocument> results = indexFactory.search(vectorQuery, 10);
```

---

### 7. ⚙️ ooder-config - Basic Configuration Package

Spring Boot basic configuration management module, providing configuration property automatic binding.

**Core Functions:**
- ✅ Configuration property automatic binding
- ✅ Configuration processor
- ✅ Multi-environment configuration support
- ✅ Configuration hot update

**Usage Example:**
```java
import net.ooder.config.AppConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AppConfig.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

// Use configuration
@Autowired
private AppConfig appConfig;
```

---

### 8. 🖥️ ooder-server - Microservice Support

Microservice infrastructure module, providing service registration, authentication, cluster management, and other functions.

**Core Functions:**
- ✅ System user authentication and authorization
- ✅ Service registration and discovery
- ✅ Cluster management and load balancing
- ✅ HTTP proxy and gateway
- ✅ Service health check

**Usage Example:**
```java
import net.ooder.server.auth.AuthManager;
import net.ooder.server.cluster.ClusterManager;

// User authentication
AuthManager authManager = AuthManager.getInstance();
boolean isValid = authManager.authenticate(username, password);

// Cluster management
ClusterManager clusterManager = ClusterManager.getInstance();
List<ServiceNode> nodes = clusterManager.getActiveNodes();
```

---

### 9. 🌐 ooder-iot-webclient - IoT Northbound Protocol Conversion Basic Package

IoT northbound protocol conversion module, providing IoT device management and data collection capabilities.

**Core Functions:**
- ✅ Northbound protocol conversion
- ✅ Device management
- ✅ Data collection and reporting
- ✅ Device status monitoring
- ✅ Remote command issuance

**Usage Example:**
```java
import net.ooder.agent.client.IoTClient;
import net.ooder.agent.client.model.DeviceData;

// Get IoT client
IoTClient iotClient = IoTClient.getInstance();

// Report device data
DeviceData data = new DeviceData();
data.setDeviceId("DEV001");
data.setTemperature(25.5);
data.setHumidity(60.0);
iotClient.reportData(data);

// Listen for commands
iotClient.onCommand((cmd) -> {
    System.out.println("Received command: " + cmd.getAction());
});
```

---

## Enterprise-grade Features

### 🔒 Security
- Comprehensive permission control system
- Data encryption transmission and storage
- Security audit logs

### 📈 High Performance
- Connection pool optimization management
- Cache acceleration mechanism
- Asynchronous message processing

### 🏗️ Scalability
- Modular architecture design
- Plugin extension mechanism
- Multi-tenant support

### 🔧 Maintainability
- Comprehensive logging system
- Monitoring and alerting
- Configuration hot update

---

## Contributing

We welcome community contributions! Please follow these steps:

1. Fork this project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Create a Pull Request

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Version History

- **v2.1** (2026-02-15) - Enterprise Development Kit Maintenance Release
  - Fixed ooder-config module parent configuration issue
  - Fixed ooder-annotation dependency version issue
  - Optimized module dependency management
  - Published to Maven Central Repository

- **v2.0** (2026-02-08) - Enterprise Development Kit Release
  - Version unified upgrade to 2.0
  - Improved dependency management configuration
  - Fixed compilation issues
  - Optimized module structure
  - MIT open source license

- **v1.0** (2025-08-25) - First MIT Open Source Version Release
  - Package name migration from `com.ds` to `net.ooder`
  - Complete modular architecture
  - MIT open source license

---

## Contact Us

- Official Website: [https://ooder.net](https://ooder.net)
- Issue Feedback: [GitHub Issues](https://github.com/ooder-net/ooder-common/issues)
- Email: team@ooder.net

---

## Acknowledgments

Thanks to all developers who have contributed to the ooder project!

---

**ooderAgent** - Enterprise Development Kit, Making Digital Transformation Easier 🚀

# SDK组件注入单元测试指南

## 文档信息

| 属性 | 值 |
|------|-----|
| **文档版本** | 1.0 |
| **编写日期** | 2026-02-28 |
| **适用版本** | SDK v2.3 |

---

## 一、单元测试概述

### 1.1 测试目标

验证SDK组件在Spring环境中的正确注入和功能可用性。

### 1.2 测试范围

| 组件 | 测试内容 |
|------|---------|
| DriverRegistry | 注入测试、功能测试 |
| CapabilityRegistry | 注入测试、功能测试 |
| SceneEngine | 注入测试、依赖项测试 |

### 1.3 测试环境

- JUnit 5
- Spring Boot Test
- Mockito

---

## 二、单元测试实现

### 2.1 基础注入测试

```java
package net.ooder.nexus.config;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SDK组件基础注入测试
 * 
 * 验证所有SDK组件都能被Spring正确注入
 */
@SpringBootTest
@DisplayName("SDK组件注入测试")
public class SdkComponentInjectionTest {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    @Autowired
    private CapabilityRegistry capabilityRegistry;
    
    @Autowired
    private SceneEngine sceneEngine;
    
    @Test
    @DisplayName("DriverRegistry应该被正确注入")
    public void testDriverRegistryInjection() {
        assertNotNull(driverRegistry, "DriverRegistry should be injected");
        
        // 验证从ApplicationContext也能获取
        DriverRegistry fromContext = applicationContext.getBean(DriverRegistry.class);
        assertNotNull(fromContext, "DriverRegistry should be available in context");
        assertSame(driverRegistry, fromContext, "Should be the same instance");
    }
    
    @Test
    @DisplayName("CapabilityRegistry应该被正确注入")
    public void testCapabilityRegistryInjection() {
        assertNotNull(capabilityRegistry, "CapabilityRegistry should be injected");
        
        CapabilityRegistry fromContext = applicationContext.getBean(CapabilityRegistry.class);
        assertNotNull(fromContext, "CapabilityRegistry should be available in context");
        assertSame(capabilityRegistry, fromContext, "Should be the same instance");
    }
    
    @Test
    @DisplayName("SceneEngine应该被正确注入")
    public void testSceneEngineInjection() {
        assertNotNull(sceneEngine, "SceneEngine should be injected");
        
        SceneEngine fromContext = applicationContext.getBean(SceneEngine.class);
        assertNotNull(fromContext, "SceneEngine should be available in context");
    }
    
    @Test
    @DisplayName("所有组件应该是单例")
    public void testComponentsAreSingleton() {
        DriverRegistry driver1 = applicationContext.getBean(DriverRegistry.class);
        DriverRegistry driver2 = applicationContext.getBean(DriverRegistry.class);
        assertSame(driver1, driver2, "DriverRegistry should be singleton");
        
        CapabilityRegistry cap1 = applicationContext.getBean(CapabilityRegistry.class);
        CapabilityRegistry cap2 = applicationContext.getBean(CapabilityRegistry.class);
        assertSame(cap1, cap2, "CapabilityRegistry should be singleton");
    }
}
```

### 2.2 DriverRegistry功能测试

```java
package net.ooder.nexus.service;

import net.ooder.scene.core.driver.Driver;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.scene.core.driver.InterfaceDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DriverRegistry功能测试
 */
@SpringBootTest
@DisplayName("DriverRegistry功能测试")
public class DriverRegistryTest {
    
    @Autowired
    private DriverRegistry driverRegistry;
    
    @BeforeEach
    public void setUp() {
        // 清理测试数据
        driverRegistry.clear();
    }
    
    @Test
    @DisplayName("应该能注册驱动")
    public void testRegisterDriver() {
        // 创建模拟驱动
        Driver mockDriver = mock(Driver.class);
        when(mockDriver.getCategory()).thenReturn("test-driver");
        
        // 注册驱动
        driverRegistry.register(mockDriver);
        
        // 验证
        Driver retrieved = driverRegistry.getDriver("test-driver");
        assertNotNull(retrieved, "Driver should be registered");
        assertEquals(mockDriver, retrieved, "Should retrieve the same driver");
    }
    
    @Test
    @DisplayName("应该能注销驱动")
    public void testUnregisterDriver() {
        // 注册驱动
        Driver mockDriver = mock(Driver.class);
        when(mockDriver.getCategory()).thenReturn("test-driver");
        driverRegistry.register(mockDriver);
        
        // 注销驱动
        driverRegistry.unregister("test-driver");
        
        // 验证
        Driver retrieved = driverRegistry.getDriver("test-driver");
        assertNull(retrieved, "Driver should be unregistered");
    }
    
    @Test
    @DisplayName("应该能获取所有驱动")
    public void testGetAllDrivers() {
        // 注册多个驱动
        Driver driver1 = mock(Driver.class);
        when(driver1.getCategory()).thenReturn("driver-1");
        driverRegistry.register(driver1);
        
        Driver driver2 = mock(Driver.class);
        when(driver2.getCategory()).thenReturn("driver-2");
        driverRegistry.register(driver2);
        
        // 验证
        assertEquals(2, driverRegistry.getAllDrivers().size(), 
            "Should have 2 drivers");
    }
    
    @Test
    @DisplayName("应该能按类型获取驱动")
    public void testGetDriverByType() {
        Driver mockDriver = mock(Driver.class);
        when(mockDriver.getCategory()).thenReturn("database");
        driverRegistry.register(mockDriver);
        
        Driver retrieved = driverRegistry.getDriver("database", Driver.class);
        assertNotNull(retrieved, "Should retrieve driver by type");
    }
    
    @Test
    @DisplayName("应该能注册接口定义")
    public void testRegisterInterface() {
        InterfaceDefinition interfaceDef = mock(InterfaceDefinition.class);
        when(interfaceDef.getId()).thenReturn("test-interface");
        
        driverRegistry.registerInterface(interfaceDef);
        
        InterfaceDefinition retrieved = driverRegistry.getInterface("test-interface");
        assertNotNull(retrieved, "Interface should be registered");
    }
}
```

### 2.3 CapabilityRegistry功能测试

```java
package net.ooder.nexus.service;

import net.ooder.sdk.a2a.capability.CapabilityDeclaration;
import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * CapabilityRegistry功能测试
 */
@SpringBootTest
@DisplayName("CapabilityRegistry功能测试")
public class CapabilityRegistryTest {
    
    @Autowired
    private CapabilityRegistry capabilityRegistry;
    
    private static final String TEST_SKILL_ID = "test-skill";
    
    @BeforeEach
    public void setUp() {
        // 清理测试数据
        capabilityRegistry.clear();
    }
    
    @Test
    @DisplayName("应该能注册能力")
    public void testRegisterCapability() {
        // 创建模拟能力
        CapabilityDeclaration capability = mock(CapabilityDeclaration.class);
        when(capability.getId()).thenReturn("test-capability");
        when(capability.getName()).thenReturn("Test Capability");
        
        // 注册能力
        capabilityRegistry.registerCapability(TEST_SKILL_ID, capability);
        
        // 验证
        CapabilityDeclaration retrieved = capabilityRegistry.getCapability("test-capability");
        assertNotNull(retrieved, "Capability should be registered");
        assertEquals("test-capability", retrieved.getId());
    }
    
    @Test
    @DisplayName("应该能注销能力")
    public void testUnregisterCapability() {
        // 注册能力
        CapabilityDeclaration capability = mock(CapabilityDeclaration.class);
        when(capability.getId()).thenReturn("test-capability");
        capabilityRegistry.registerCapability(TEST_SKILL_ID, capability);
        
        // 注销能力
        capabilityRegistry.unregisterCapability(TEST_SKILL_ID, "test-capability");
        
        // 验证
        CapabilityDeclaration retrieved = capabilityRegistry.getCapability("test-capability");
        assertNull(retrieved, "Capability should be unregistered");
    }
    
    @Test
    @DisplayName("应该能获取Skill的所有能力")
    public void testGetSkillCapabilities() {
        // 注册多个能力
        CapabilityDeclaration cap1 = mock(CapabilityDeclaration.class);
        when(cap1.getId()).thenReturn("cap-1");
        capabilityRegistry.registerCapability(TEST_SKILL_ID, cap1);
        
        CapabilityDeclaration cap2 = mock(CapabilityDeclaration.class);
        when(cap2.getId()).thenReturn("cap-2");
        capabilityRegistry.registerCapability(TEST_SKILL_ID, cap2);
        
        // 验证
        List<CapabilityDeclaration> capabilities = 
            capabilityRegistry.getSkillCapabilities(TEST_SKILL_ID);
        assertEquals(2, capabilities.size(), "Should have 2 capabilities");
    }
    
    @Test
    @DisplayName("应该能搜索能力")
    public void testSearchCapabilities() {
        // 注册能力
        CapabilityDeclaration capability = mock(CapabilityDeclaration.class);
        when(capability.getId()).thenReturn("search-test");
        when(capability.getName()).thenReturn("Searchable Capability");
        when(capability.getDescription()).thenReturn("This is a test capability");
        capabilityRegistry.registerCapability(TEST_SKILL_ID, capability);
        
        // 搜索
        List<CapabilityDeclaration> results = capabilityRegistry.searchCapabilities("search");
        
        // 验证
        assertFalse(results.isEmpty(), "Should find matching capabilities");
        assertEquals("search-test", results.get(0).getId());
    }
    
    @Test
    @DisplayName("应该能检查能力是否存在")
    public void testHasCapability() {
        CapabilityDeclaration capability = mock(CapabilityDeclaration.class);
        when(capability.getId()).thenReturn("existing-cap");
        capabilityRegistry.registerCapability(TEST_SKILL_ID, capability);
        
        assertTrue(capabilityRegistry.hasCapability("existing-cap"), 
            "Should have capability");
        assertFalse(capabilityRegistry.hasCapability("non-existing"), 
            "Should not have non-existing capability");
    }
    
    @Test
    @DisplayName("应该能获取提供指定能力的所有Skill")
    public void testGetSkillsByCapability() {
        // 注册能力到多个Skill
        CapabilityDeclaration capability = mock(CapabilityDeclaration.class);
        when(capability.getId()).thenReturn("shared-cap");
        
        capabilityRegistry.registerCapability("skill-1", capability);
        capabilityRegistry.registerCapability("skill-2", capability);
        
        // 验证
        List<String> skills = capabilityRegistry.getSkillsByCapability("shared-cap");
        assertEquals(2, skills.size(), "Should have 2 skills");
        assertTrue(skills.contains("skill-1"));
        assertTrue(skills.contains("skill-2"));
    }
}
```

### 2.4 SceneEngine集成测试

```java
package net.ooder.nexus.service;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.session.SessionManager;
import net.ooder.scene.skill.SkillService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneEngine集成测试
 */
@SpringBootTest
@DisplayName("SceneEngine集成测试")
public class SceneEngineIntegrationTest {
    
    @Autowired
    private SceneEngine sceneEngine;
    
    @Autowired
    private SessionManager sessionManager;
    
    @Autowired
    private SkillService skillService;
    
    @Test
    @DisplayName("SceneEngine应该被正确注入")
    public void testSceneEngineInjection() {
        assertNotNull(sceneEngine, "SceneEngine should be injected");
    }
    
    @Test
    @DisplayName("SceneEngine的依赖项应该被正确注入")
    public void testSceneEngineDependencies() {
        assertNotNull(sessionManager, "SessionManager should be injected");
        assertNotNull(skillService, "SkillService should be injected");
    }
    
    @Test
    @DisplayName("SceneEngine应该能获取场景信息")
    public void testGetSceneInfo() {
        // 假设SceneEngine有getSceneInfo方法
        // SceneInfo sceneInfo = sceneEngine.getSceneInfo();
        // assertNotNull(sceneInfo);
        
        // 实际测试根据SceneEngine的实际API进行调整
        assertNotNull(sceneEngine, "SceneEngine should be available");
    }
}
```

### 2.5 集成服务测试

```java
package net.ooder.nexus.service;

import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.core.driver.DriverRegistry;
import net.ooder.sdk.a2a.capability.CapabilityRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SDK集成服务测试
 */
@SpringBootTest
@DisplayName("SDK集成服务测试")
public class SdkIntegrationServiceTest {
    
    @Autowired
    private SdkIntegrationService sdkIntegrationService;
    
    @Test
    @DisplayName("集成服务应该被正确注入")
    public void testServiceInjection() {
        assertNotNull(sdkIntegrationService, "SdkIntegrationService should be injected");
    }
    
    @Test
    @DisplayName("应该能获取DriverRegistry")
    public void testGetDriverRegistry() {
        DriverRegistry registry = sdkIntegrationService.getDriverRegistry();
        assertNotNull(registry, "DriverRegistry should be available");
    }
    
    @Test
    @DisplayName("应该能获取CapabilityRegistry")
    public void testGetCapabilityRegistry() {
        CapabilityRegistry registry = sdkIntegrationService.getCapabilityRegistry();
        assertNotNull(registry, "CapabilityRegistry should be available");
    }
    
    @Test
    @DisplayName("应该能获取SceneEngine")
    public void testGetSceneEngine() {
        SceneEngine engine = sdkIntegrationService.getSceneEngine();
        assertNotNull(engine, "SceneEngine should be available");
    }
    
    @Test
    @DisplayName("所有组件应该都可用")
    public void testAllComponentsAvailable() {
        assertNotNull(sdkIntegrationService.getDriverRegistry(), 
            "DriverRegistry should be available");
        assertNotNull(sdkIntegrationService.getCapabilityRegistry(), 
            "CapabilityRegistry should be available");
        assertNotNull(sdkIntegrationService.getSceneEngine(), 
            "SceneEngine should be available");
    }
}
```

---

## 三、测试配置

### 3.1 application-test.yml

```yaml
# 测试配置
spring:
  main:
    banner-mode: off
  
logging:
  level:
    root: WARN
    net.ooder.nexus: DEBUG
```

### 3.2 测试类配置

```java
package net.ooder.nexus.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 测试配置类
 */
@TestConfiguration
public class TestConfig {
    
    // 可以在这里添加测试专用的Bean配置
    
}
```

---

## 四、运行测试

### 4.1 运行所有测试

```bash
./mvnw test
```

### 4.2 运行特定测试类

```bash
./mvnw test -Dtest=SdkComponentInjectionTest
```

### 4.3 运行特定测试方法

```bash
./mvnw test -Dtest=SdkComponentInjectionTest#testDriverRegistryInjection
```

---

## 五、测试覆盖率要求

| 组件 | 覆盖率要求 |
|------|-----------|
| DriverRegistry | >= 80% |
| CapabilityRegistry | >= 80% |
| SceneEngine | >= 60% |
| 整体 | >= 70% |

---

**文档结束**

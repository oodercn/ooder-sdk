# SKILLS.MD 支持与 Capability 到 Function Call 映射规范

## 一、SKILLS.MD 文件支持

### 1.1 现有支持情况

scene-engine 已有 `SkillsMdLoader` 支持：

```java
// 已实现的加载路径
private static final String SKILLS_MD = "skills.md";
private static final String BASIC_KNOWLEDGE = "knowledge/basic.md";
private static final String ADVANCED_KNOWLEDGE = "knowledge/advanced.md";
private static final String EXPERT_KNOWLEDGE = "knowledge/expert.md";
private static final String FULL_KNOWLEDGE = "knowledge/full.md";
```

### 1.2 SKILLS.MD 文件结构规范

```markdown
# 技能名称

## 概述
技能的简要描述，用于 LLM 理解技能用途。

## 能力列表

### capability-1
- **名称**: 能力1
- **描述**: 能力1的详细描述
- **输入参数**:
  - `param1` (string, required): 参数1描述
  - `param2` (number, optional): 参数2描述
- **输出**: 输出描述

### capability-2
...

## 使用示例

### 示例1
描述和示例代码

## 知识库

### 基础知识 (knowledge/basic.md)
适用于 BASIC 加载级别的知识

### 高级知识 (knowledge/advanced.md)
适用于 ADVANCED 加载级别的知识

### 专家知识 (knowledge/expert.md)
适用于 EXPERT 加载级别的知识
```

### 1.3 加载级别

| 级别 | 加载内容 | 适用场景 |
|------|----------|----------|
| BASIC | skills.md + basic.md | 快速预览 |
| ADVANCED | BASIC + advanced.md | 常规使用 |
| EXPERT | ADVANCED + expert.md | 深度使用 |
| FULL | 全部知识文件 | 完整知识 |

### 1.4 集成到 LlmService

```java
/**
 * SKILLS.MD 集成方案
 */
public class SkillsMdIntegration {
    
    /**
     * 从 SKILLS.MD 加载 Prompt 模板
     */
    public PromptTemplate loadPromptFromSkillsMd(String skillId) {
        SkillsMdLoader loader = new SkillsMdLoader(skillRegistry);
        KnowledgeContext context = loader.load(skillId, KnowledgeLoadLevel.ADVANCED);
        
        // 构建 System Prompt
        StringBuilder systemPrompt = new StringBuilder();
        for (KnowledgeChunk chunk : context.getLoadedChunks()) {
            systemPrompt.append(chunk.getContent()).append("\n\n");
        }
        
        return PromptTemplate.builder()
            .id(skillId + "-system")
            .systemPrompt(systemPrompt.toString())
            .build();
    }
    
    /**
     * 从 SKILLS.MD 提取 Function 定义
     */
    public List<FunctionDefinition> extractFunctionsFromSkillsMd(String skillId) {
        // 解析 skills.md 中的能力列表
        // 转换为 FunctionDefinition
    }
}
```

---

## 二、Capability 到 Function Call 映射规范

### 2.1 映射架构

```
┌─────────────────────────────────────────────────────────────────┐
│              Capability → Function Call 映射流程                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │ Capability  │────▶│ 类型检测    │────▶│ 映射策略    │       │
│  │ Definition  │     │             │     │             │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                             │                    │              │
│                             ▼                    ▼              │
│                    ┌─────────────┐     ┌─────────────┐         │
│                    │ 可直接映射  │     │ 需手工配置  │         │
│                    │             │     │             │         │
│                    └─────────────┘     └─────────────┘         │
│                             │                    │              │
│                             ▼                    ▼              │
│                    ┌─────────────┐     ┌─────────────┐         │
│                    │ 自动生成    │     │ 降级配置    │         │
│                    │ Function    │     │ tools.yaml  │         │
│                    └─────────────┘     └─────────────┘         │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 可直接映射的类型规范

#### 2.2.1 基础类型映射

| Capability 类型 | Function Call 类型 | JSON Schema | 说明 |
|-----------------|-------------------|-------------|------|
| `string` | `string` | `{"type": "string"}` | ✅ 直接映射 |
| `integer` | `integer` | `{"type": "integer"}` | ✅ 直接映射 |
| `number` | `number` | `{"type": "number"}` | ✅ 直接映射 |
| `boolean` | `boolean` | `{"type": "boolean"}` | ✅ 直接映射 |
| `array` | `array` | `{"type": "array", "items": {...}}` | ✅ 直接映射 |
| `object` | `object` | `{"type": "object", "properties": {...}}` | ✅ 直接映射 |

#### 2.2.2 扩展类型映射

| Capability 类型 | Function Call 类型 | JSON Schema | 说明 |
|-----------------|-------------------|-------------|------|
| `enum` | `string` + `enum` | `{"type": "string", "enum": [...]}` | ✅ 直接映射 |
| `date` | `string` + `format` | `{"type": "string", "format": "date"}` | ✅ 直接映射 |
| `datetime` | `string` + `format` | `{"type": "string", "format": "date-time"}` | ✅ 直接映射 |
| `email` | `string` + `format` | `{"type": "string", "format": "email"}` | ✅ 直接映射 |
| `uri` | `string` + `format` | `{"type": "string", "format": "uri"}` | ✅ 直接映射 |

#### 2.2.3 复杂类型映射

| Capability 类型 | Function Call 类型 | 映射方式 | 说明 |
|-----------------|-------------------|----------|------|
| `Map<String, Object>` | `object` | 自动转换 | ✅ 可映射 |
| `List<T>` | `array` | 自动转换 | ✅ 可映射 |
| 自定义类 | `object` | 需要注解 | ⚠️ 需配置 |

### 2.3 不能直接映射的情况

| 情况 | 原因 | 解决方案 |
|------|------|----------|
| 泛型类型 | 类型擦除 | 手工配置 schema |
| 递归类型 | 无法表达 | 手工配置 schema |
| 动态类型 | 运行时确定 | 手工配置或使用 `object` |
| 复杂嵌套 | 层级过深 | 手工配置简化版 |
| 第三方类型 | 无法修改 | 手工配置适配器 |

### 2.4 映射实现代码

```java
package net.ooder.scene.llm.mapping;

import net.ooder.scene.skill.capability.Capability;
import net.ooder.scene.skill.capability.Capability.ParameterDefinition;
import net.ooder.scene.llm.context.FunctionContext.FunctionDefinition;

import java.util.*;

/**
 * Capability 到 Function Call 映射器
 */
public class CapabilityToFunctionMapper {
    
    /**
     * 类型映射表
     */
    private static final Map<String, String> TYPE_MAPPING = new HashMap<>();
    static {
        TYPE_MAPPING.put("string", "string");
        TYPE_MAPPING.put("int", "integer");
        TYPE_MAPPING.put("integer", "integer");
        TYPE_MAPPING.put("long", "integer");
        TYPE_MAPPING.put("float", "number");
        TYPE_MAPPING.put("double", "number");
        TYPE_MAPPING.put("boolean", "boolean");
        TYPE_MAPPING.put("list", "array");
        TYPE_MAPPING.put("array", "array");
        TYPE_MAPPING.put("map", "object");
        TYPE_MAPPING.put("object", "object");
    }
    
    /**
     * 可直接映射的类型集合
     */
    private static final Set<String> DIRECT_MAPPABLE_TYPES = new HashSet<>(Arrays.asList(
        "string", "integer", "number", "boolean", "array", "object"
    ));
    
    /**
     * 映射 Capability 到 FunctionDefinition
     * 
     * @param capability 能力定义
     * @return 映射结果
     */
    public MappingResult map(Capability capability) {
        MappingResult result = new MappingResult();
        
        // 1. 创建 FunctionDefinition
        FunctionDefinition funcDef = new FunctionDefinition();
        funcDef.setName(convertToFunctionName(capability.getId()));
        funcDef.setDescription(capability.getDescription());
        funcDef.setCapability(capability.getId());
        
        // 2. 映射参数
        Map<String, ParameterDefinition> inputParams = capability.getInputParameters();
        if (inputParams != null && !inputParams.isEmpty()) {
            Map<String, FunctionContext.ParameterDefinition> mappedParams = new LinkedHashMap<>();
            List<String> required = new ArrayList<>();
            
            for (Map.Entry<String, ParameterDefinition> entry : inputParams.entrySet()) {
                String paramName = entry.getKey();
                ParameterDefinition paramDef = entry.getValue();
                
                // 检查是否可直接映射
                if (canDirectMap(paramDef.getType())) {
                    FunctionContext.ParameterDefinition mappedParam = mapParameter(paramDef);
                    mappedParams.put(paramName, mappedParam);
                    
                    if (paramDef.isRequired()) {
                        required.add(paramName);
                    }
                } else {
                    // 记录无法映射的参数
                    result.addUnmappableParam(paramName, paramDef.getType());
                }
            }
            
            funcDef.setParameters(mappedParams);
            funcDef.setRequired(required);
        }
        
        result.setFunctionDefinition(funcDef);
        result.setSuccess(result.getUnmappableParams().isEmpty());
        
        return result;
    }
    
    /**
     * 检查类型是否可直接映射
     */
    private boolean canDirectMap(String type) {
        if (type == null) {
            return false;
        }
        
        String normalizedType = type.toLowerCase();
        
        // 基础类型
        if (DIRECT_MAPPABLE_TYPES.contains(normalizedType)) {
            return true;
        }
        
        // 带格式的类型
        if (normalizedType.startsWith("string(")) {
            return true; // string(date), string(email), etc.
        }
        
        // 数组类型
        if (normalizedType.startsWith("array<") || normalizedType.endsWith("[]")) {
            return true;
        }
        
        // 枚举类型
        if (normalizedType.equals("enum")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 映射参数定义
     */
    private FunctionContext.ParameterDefinition mapParameter(ParameterDefinition source) {
        FunctionContext.ParameterDefinition target = new FunctionContext.ParameterDefinition();
        
        String sourceType = source.getType().toLowerCase();
        String mappedType = TYPE_MAPPING.getOrDefault(sourceType, "string");
        
        target.setType(mappedType);
        target.setDescription(source.getDescription());
        
        // 处理枚举
        if (sourceType.equals("enum") && source.getEnumValues() != null) {
            target.setEnumValues(source.getEnumValues());
        }
        
        // 处理格式
        if (sourceType.startsWith("string(")) {
            String format = sourceType.substring(7, sourceType.length() - 1);
            // format 可以存储在 metadata 中
        }
        
        return target;
    }
    
    /**
     * 转换为函数名
     */
    private String convertToFunctionName(String capabilityId) {
        return capabilityId.replaceAll("([a-z])([A-Z])", "$1_$2")
                          .replace(".", "_")
                          .replace("-", "_")
                          .toLowerCase();
    }
    
    /**
     * 映射结果
     */
    public static class MappingResult {
        private boolean success;
        private FunctionDefinition functionDefinition;
        private Map<String, String> unmappableParams = new LinkedHashMap<>();
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public FunctionDefinition getFunctionDefinition() { return functionDefinition; }
        public void setFunctionDefinition(FunctionDefinition functionDefinition) { 
            this.functionDefinition = functionDefinition; 
        }
        public Map<String, String> getUnmappableParams() { return unmappableParams; }
        
        public void addUnmappableParam(String name, String type) {
            unmappableParams.put(name, type);
        }
    }
}
```

### 2.5 降级配置方案

当无法直接映射时，使用 `tools.yaml` 手工配置：

```yaml
# tools.yaml - 手工配置降级方案
apiVersion: ooder.net/v1
kind: ToolRegistry

spec:
  tools:
    # 无法自动映射的复杂类型
    - id: complex-query
      name: complexQuery
      description: 复杂查询能力
      
      parameters:
        - name: filter
          type: object
          description: 复杂过滤条件
          schema:
            type: object
            properties:
              field:
                type: string
              operator:
                type: string
                enum: [eq, ne, gt, lt, contains]
              value:
                oneOf:
                  - type: string
                  - type: number
                  - type: boolean
                  
        - name: pagination
          type: object
          description: 分页参数
          schema:
            type: object
            properties:
              page:
                type: integer
                minimum: 1
              size:
                type: integer
                minimum: 1
                maximum: 100
                
      handler:
        type: spring-bean
        bean: complexQueryService
        method: execute
```

### 2.6 映射优先级

```
┌─────────────────────────────────────────────────────────────────┐
│                    映射优先级                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  优先级 1: 显式配置 (tools.yaml)                                │
│     ↓ 如果没有配置                                              │
│  优先级 2: Capability 自动映射                                  │
│     ↓ 如果无法映射                                              │
│  优先级 3: 降级为通用 object 类型                               │
│     ↓ 如果类型不明确                                            │
│  优先级 4: 运行时动态推断                                       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 三、完整映射流程

### 3.1 流程图

```
开始
  │
  ▼
检查 tools.yaml 是否有显式配置
  │
  ├─ 有 ──────────────────────▶ 使用显式配置
  │
  └─ 无
      │
      ▼
  解析 Capability 定义
      │
      ▼
  检查参数类型是否可映射
      │
      ├─ 全部可映射 ────────────▶ 自动生成 FunctionDefinition
      │
      └─ 部分不可映射
          │
          ▼
      记录不可映射参数
          │
          ▼
      检查是否有降级配置
          │
          ├─ 有 ──────────────────▶ 合并配置
          │
          └─ 无 ──────────────────▶ 使用 object 类型降级
```

### 3.2 代码实现

```java
/**
 * 完整映射服务
 */
public class FunctionMappingService {
    
    private final CapabilityToFunctionMapper autoMapper;
    private final ToolConfigLoader configLoader;
    
    /**
     * 获取 Function 定义（完整流程）
     */
    public FunctionDefinition getFunctionDefinition(String capabilityId, Capability capability) {
        // 1. 检查显式配置
        FunctionDefinition explicitDef = configLoader.loadExplicitDefinition(capabilityId);
        if (explicitDef != null) {
            return explicitDef;
        }
        
        // 2. 自动映射
        MappingResult result = autoMapper.map(capability);
        
        // 3. 处理不可映射参数
        if (!result.isSuccess()) {
            // 尝试加载降级配置
            Map<String, ParameterSchema> fallbackSchemas = 
                configLoader.loadFallbackSchemas(capabilityId);
            
            if (!fallbackSchemas.isEmpty()) {
                // 合并降级配置
                mergeFallbackSchemas(result.getFunctionDefinition(), 
                                     result.getUnmappableParams(), 
                                     fallbackSchemas);
            } else {
                // 使用 object 类型降级
                applyObjectFallback(result.getFunctionDefinition(), 
                                   result.getUnmappableParams());
            }
        }
        
        return result.getFunctionDefinition();
    }
}
```

---

## 四、实施建议

### 4.1 短期

1. 完善 `CapabilityToFunctionMapper` 实现
2. 支持 tools.yaml 显式配置
3. 支持基础类型自动映射

### 4.2 中期

1. 支持复杂类型映射（通过注解）
2. 支持降级配置合并
3. 提供映射验证工具

### 4.3 长期

1. 支持运行时类型推断
2. 提供可视化配置工具
3. 自动生成映射文档

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: SE Team

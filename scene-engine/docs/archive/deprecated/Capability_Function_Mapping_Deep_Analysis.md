# Capability 到 Function Call 映射深入分析

## 一、JavaScript 转换支持

### 1.1 为什么需要 JavaScript 转换

```
┌─────────────────────────────────────────────────────────────────┐
│                    JavaScript 转换场景                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  场景1: 前端直接使用                                            │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │ Java Class  │────▶│ JSON Schema │────▶│ TypeScript  │       │
│  │ (后端)      │     │ (中间格式)   │     │ (前端)      │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                                                                 │
│  场景2: 动态类型转换                                            │
│  ┌─────────────┐     ┌─────────────┐     ┌─────────────┐       │
│  │ Java Type   │────▶│ JS Type     │────▶│ JSON Schema │       │
│  │ (强类型)    │     │ (弱类型)     │     │ (标准格式)  │       │
│  └─────────────┘     └─────────────┘     └─────────────┘       │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 Java 类型到 JavaScript/JSON Schema 转换规则

#### 基础类型映射

| Java 类型 | JavaScript 类型 | JSON Schema | 转换规则 |
|-----------|-----------------|-------------|----------|
| `String` | `string` | `{"type": "string"}` | 直接映射 |
| `int/Integer` | `number` | `{"type": "integer"}` | 直接映射 |
| `long/Long` | `number` | `{"type": "integer"}` | 精度警告 |
| `float/Float` | `number` | `{"type": "number"}` | 直接映射 |
| `double/Double` | `number` | `{"type": "number"}` | 直接映射 |
| `boolean/Boolean` | `boolean` | `{"type": "boolean"}` | 直接映射 |
| `BigDecimal` | `number` | `{"type": "number"}` | 精度处理 |
| `BigInteger` | `number` | `{"type": "integer"}` | 精度警告 |

#### 日期时间类型映射

| Java 类型 | JavaScript 类型 | JSON Schema | 转换规则 |
|-----------|-----------------|-------------|----------|
| `java.util.Date` | `string` | `{"type": "string", "format": "date-time"}` | ISO 8601 |
| `java.time.LocalDate` | `string` | `{"type": "string", "format": "date"}` | ISO 8601 |
| `java.time.LocalDateTime` | `string` | `{"type": "string", "format": "date-time"}` | ISO 8601 |
| `java.time.Instant` | `string` | `{"type": "string", "format": "date-time"}` | ISO 8601 |

#### 集合类型映射

| Java 类型 | JavaScript 类型 | JSON Schema | 转换规则 |
|-----------|-----------------|-------------|----------|
| `List<T>` | `Array<T>` | `{"type": "array", "items": {...}}` | 递归映射 T |
| `Set<T>` | `Array<T>` | `{"type": "array", "items": {...}, "uniqueItems": true}` | 去重 |
| `Map<K,V>` | `Object` | `{"type": "object"}` | 动态键值 |
| `T[]` | `Array<T>` | `{"type": "array", "items": {...}}` | 数组映射 |

#### 特殊类型映射

| Java 类型 | JavaScript 类型 | JSON Schema | 转换规则 |
|-----------|-----------------|-------------|----------|
| `Enum` | `string` | `{"type": "string", "enum": [...]}` | 枚举值列表 |
| `Optional<T>` | `T \| null` | `{"oneOf": [{...}, {"type": "null"}]}` | 可空类型 |
| `void/Void` | `undefined/null` | `{"type": "null"}` | 无返回值 |
| `Object` | `any` | `{}` | 任意类型 |

### 1.3 转换实现代码

```java
package net.ooder.scene.llm.mapping;

import java.lang.reflect.*;
import java.math.*;
import java.time.*;
import java.util.*;

/**
 * Java 类型到 JSON Schema 转换器
 */
public class JavaToJsonSchemaConverter {
    
    /**
     * 将 Java 类型转换为 JSON Schema
     */
    public Map<String, Object> convert(Class<?> javaType) {
        return convert(javaType, new HashSet<>());
    }
    
    private Map<String, Object> convert(Class<?> javaType, Set<Class<?>> visited) {
        Map<String, Object> schema = new LinkedHashMap<>();
        
        // 防止循环引用
        if (visited.contains(javaType)) {
            schema.put("type", "object");
            schema.put("$ref", "#/definitions/" + javaType.getSimpleName());
            return schema;
        }
        visited.add(javaType);
        
        // 基础类型
        if (javaType == String.class) {
            schema.put("type", "string");
        } else if (javaType == Integer.class || javaType == int.class) {
            schema.put("type", "integer");
        } else if (javaType == Long.class || javaType == long.class) {
            schema.put("type", "integer");
            schema.put("format", "int64");
        } else if (javaType == Double.class || javaType == double.class ||
                   javaType == Float.class || javaType == float.class) {
            schema.put("type", "number");
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            schema.put("type", "boolean");
        }
        // 日期时间
        else if (javaType == Date.class || javaType == Instant.class ||
                 javaType == LocalDateTime.class) {
            schema.put("type", "string");
            schema.put("format", "date-time");
        } else if (javaType == LocalDate.class) {
            schema.put("type", "string");
            schema.put("format", "date");
        }
        // 集合
        else if (List.class.isAssignableFrom(javaType) || 
                 javaType.isArray()) {
            schema.put("type", "array");
            // 需要泛型信息才能确定 items
            schema.put("items", Map.of("type", "object"));
        } else if (Set.class.isAssignableFrom(javaType)) {
            schema.put("type", "array");
            schema.put("uniqueItems", true);
        } else if (Map.class.isAssignableFrom(javaType)) {
            schema.put("type", "object");
        }
        // 枚举
        else if (javaType.isEnum()) {
            schema.put("type", "string");
            List<String> enumValues = new ArrayList<>();
            for (Object e : javaType.getEnumConstants()) {
                enumValues.add(e.toString());
            }
            schema.put("enum", enumValues);
        }
        // 自定义对象
        else {
            schema.put("type", "object");
            schema.put("properties", convertProperties(javaType, visited));
        }
        
        return schema;
    }
    
    /**
     * 转换对象属性
     */
    private Map<String, Object> convertProperties(Class<?> javaType, Set<Class<?>> visited) {
        Map<String, Object> properties = new LinkedHashMap<>();
        
        for (Field field : javaType.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            
            String fieldName = field.getName();
            Map<String, Object> fieldSchema = convert(field.getType(), visited);
            
            // 处理注解
            processFieldAnnotations(field, fieldSchema);
            
            properties.put(fieldName, fieldSchema);
        }
        
        return properties;
    }
    
    /**
     * 处理字段注解
     */
    private void processFieldAnnotations(Field field, Map<String, Object> schema) {
        // @JsonProperty 注解
        // @ApiModelProperty 注解
        // @Schema 注解
        // ... 可以扩展
    }
}
```

### 1.4 TypeScript 类型生成

```typescript
// 从 JSON Schema 生成 TypeScript 类型

interface TypeMapping {
  string: string;
  integer: number;
  number: number;
  boolean: boolean;
  array: T[];
  object: Record<string, any>;
}

// 示例：生成的 TypeScript 类型
interface CapabilityParameter {
  name: string;
  type: 'string' | 'number' | 'boolean' | 'object' | 'array';
  description?: string;
  required: boolean;
  enum?: string[];
  format?: string;
}

interface FunctionDefinition {
  name: string;
  description: string;
  parameters: Record<string, CapabilityParameter>;
  required: string[];
}
```

---

## 二、表达式引擎支持

### 2.1 支持的表达式引擎

| 引擎 | 适用场景 | 性能 | 复杂度 |
|------|----------|------|--------|
| **SpEL** (Spring Expression Language) | Spring 集成 | 高 | 中 |
| **MVEL** | 规则引擎、动态脚本 | 高 | 低 |
| **Groovy** | 复杂逻辑、DSL | 中 | 高 |
| **JavaScript (Nashorn/GraalVM)** | 前端兼容 | 中 | 中 |
| **Aviator** | 轻量级表达式 | 高 | 低 |

### 2.2 表达式引擎在 Function Call 中的应用

```
┌─────────────────────────────────────────────────────────────────┐
│                  表达式引擎应用场景                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  1. 参数转换                                                    │
│     LLM 输出 ──▶ 表达式转换 ──▶ Capability 输入                │
│                                                                 │
│  2. 条件判断                                                    │
│     if (user.role == 'admin' && context.level > 3)             │
│                                                                 │
│  3. 动态计算                                                    │
│     price * quantity * discount                                │
│                                                                 │
│  4. 数据提取                                                    │
│     response.data.items[0].name                                │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 2.3 SpEL 集成实现

```java
package net.ooder.scene.llm.expression;

import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

/**
 * SpEL 表达式引擎支持
 */
public class SpelExpressionEngine implements ExpressionEngine {
    
    private final ExpressionParser parser = new SpelExpressionParser();
    
    @Override
    public String getName() {
        return "spel";
    }
    
    @Override
    public Object evaluate(String expression, Map<String, Object> context) {
        Expression exp = parser.parseExpression(expression);
        EvaluationContext evalContext = createEvaluationContext(context);
        return exp.getValue(evalContext);
    }
    
    @Override
    public <T> T evaluate(String expression, Map<String, Object> context, Class<T> resultType) {
        Expression exp = parser.parseExpression(expression);
        EvaluationContext evalContext = createEvaluationContext(context);
        return exp.getValue(evalContext, resultType);
    }
    
    @Override
    public boolean evaluateAsBoolean(String expression, Map<String, Object> context) {
        return evaluate(expression, context, Boolean.class);
    }
    
    private EvaluationContext createEvaluationContext(Map<String, Object> context) {
        StandardEvaluationContext evalContext = new StandardEvaluationContext();
        
        // 注册上下文变量
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            evalContext.setVariable(entry.getKey(), entry.getValue());
        }
        
        // 注册自定义函数
        registerCustomFunctions(evalContext);
        
        return evalContext;
    }
    
    private void registerCustomFunctions(StandardEvaluationContext context) {
        // 注册字符串处理函数
        // 注册日期处理函数
        // 注册类型转换函数
    }
}
```

### 2.4 MVEL 集成实现

```java
package net.ooder.scene.llm.expression;

import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVEL 表达式引擎支持
 */
public class MvelExpressionEngine implements ExpressionEngine {
    
    private final ParserContext parserContext = new ParserContext();
    private final Map<String, Serializable> compiledCache = new ConcurrentHashMap<>();
    
    @Override
    public String getName() {
        return "mvel";
    }
    
    @Override
    public Object evaluate(String expression, Map<String, Object> context) {
        Serializable compiled = compile(expression);
        return MVEL.executeExpression(compiled, context);
    }
    
    @Override
    public <T> T evaluate(String expression, Map<String, Object> context, Class<T> resultType) {
        Serializable compiled = compile(expression);
        return (T) MVEL.executeExpression(compiled, context, resultType);
    }
    
    @Override
    public boolean evaluateAsBoolean(String expression, Map<String, Object> context) {
        return evaluate(expression, context, Boolean.class);
    }
    
    private Serializable compile(String expression) {
        return compiledCache.computeIfAbsent(expression, 
            e -> MVEL.compileExpression(e, parserContext));
    }
}
```

### 2.5 表达式在 Function Mapping 中的应用

```yaml
# function-mapping.yaml
mappings:
  - function: searchDocuments
    capability: documentSearch
    parameterMappings:
      # 表达式转换
      - source: query
        target: keywords
        expression: "query.split(' ')"  # SpEL/MVEL 表达式
      
      - source: limit
        target: maxResults
        expression: "limit != null ? limit : 10"  # 默认值
        
      - source: filters
        target: filterConditions
        expression: "filters.stream().filter(f -> f.enabled).toList()"  # 过滤
    
    # 条件执行
    condition: "user.role == 'admin' || context.permission.contains('search')"
    
    # 结果转换
    resultMapping:
      expression: "results.stream().map(r -> {name: r.title, score: r.relevance}).toList()"
```

---

## 三、注解桥梁方案

### 3.1 注解设计

```java
package net.ooder.scene.llm.annotation;

import java.lang.annotation.*;

/**
 * 标记方法为 LLM 可调用的 Function
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmFunction {
    
    /**
     * 函数名称（默认使用方法名）
     */
    String name() default "";
    
    /**
     * 函数描述
     */
    String description();
    
    /**
     * 是否启用
     */
    boolean enabled() default true;
}

/**
 * 标记参数为 Function 参数
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmParam {
    
    /**
     * 参数名称
     */
    String name();
    
    /**
     * 参数描述
     */
    String description() default "";
    
    /**
     * 是否必填
     */
    boolean required() default true;
    
    /**
     * 默认值（SpEL 表达式）
     */
    String defaultValue() default "";
    
    /**
     * 枚举值
     */
    String[] enumValues() default {};
}

/**
 * 标记返回值的描述
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LlmReturn {
    
    /**
     * 返回值描述
     */
    String description();
    
    /**
     * 返回类型 Schema（JSON 格式）
     */
    String schema() default "";
}
```

### 3.2 使用示例

```java
package net.ooder.scene.skill.example;

import net.ooder.scene.llm.annotation.*;

/**
 * 文档处理技能 - 使用注解定义 Function
 */
public class DocumentSkill {
    
    @LlmFunction(
        name = "search_documents",
        description = "搜索文档库中的文档，支持关键词和过滤条件"
    )
    @LlmReturn(description = "搜索结果列表", schema = "{\"type\": \"array\", \"items\": {\"type\": \"object\"}}")
    public List<Document> searchDocuments(
        @LlmParam(name = "query", description = "搜索关键词", required = true)
        String query,
        
        @LlmParam(name = "limit", description = "返回数量限制", defaultValue = "10")
        Integer limit,
        
        @LlmParam(name = "category", description = "文档分类", enumValues = {"report", "memo", "contract"})
        String category
    ) {
        // 实现逻辑
    }
    
    @LlmFunction(
        name = "summarize_document",
        description = "生成文档摘要"
    )
    public String summarizeDocument(
        @LlmParam(name = "documentId", description = "文档ID", required = true)
        String documentId,
        
        @LlmParam(name = "maxLength", description = "摘要最大长度", defaultValue = "500")
        int maxLength
    ) {
        // 实现逻辑
    }
}
```

### 3.3 注解扫描与注册

```java
package net.ooder.scene.llm.annotation;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

/**
 * LLM Function 注解扫描处理器
 */
@Component
public class LlmFunctionAnnotationProcessor implements BeanPostProcessor {
    
    private final FunctionRegistry functionRegistry;
    
    public LlmFunctionAnnotationProcessor(FunctionRegistry functionRegistry) {
        this.functionRegistry = functionRegistry;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        
        // 扫描所有方法
        for (Method method : beanClass.getDeclaredMethods()) {
            LlmFunction functionAnno = AnnotationUtils.findAnnotation(method, LlmFunction.class);
            
            if (functionAnno != null && functionAnno.enabled()) {
                FunctionDefinition definition = buildFunctionDefinition(method, functionAnno);
                functionRegistry.register(definition, bean, method);
            }
        }
        
        return bean;
    }
    
    /**
     * 构建函数定义
     */
    private FunctionDefinition buildFunctionDefinition(Method method, LlmFunction anno) {
        FunctionDefinition definition = new FunctionDefinition();
        
        // 函数名称
        String name = anno.name().isEmpty() ? method.getName() : anno.name();
        definition.setName(name);
        
        // 函数描述
        definition.setDescription(anno.description());
        
        // 参数定义
        Map<String, ParameterDefinition> parameters = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();
        
        for (java.lang.reflect.Parameter param : method.getParameters()) {
            LlmParam paramAnno = AnnotationUtils.findAnnotation(param, LlmParam.class);
            
            if (paramAnno != null) {
                ParameterDefinition paramDef = new ParameterDefinition();
                paramDef.setName(paramAnno.name());
                paramDef.setDescription(paramAnno.description());
                paramDef.setType(inferType(param.getType()));
                
                if (paramAnno.enumValues().length > 0) {
                    paramDef.setEnumValues(Arrays.asList(paramAnno.enumValues()));
                }
                
                if (!paramAnno.defaultValue().isEmpty()) {
                    paramDef.setDefaultValue(paramAnno.defaultValue());
                }
                
                parameters.put(paramAnno.name(), paramDef);
                
                if (paramAnno.required()) {
                    required.add(paramAnno.name());
                }
            }
        }
        
        definition.setParameters(parameters);
        definition.setRequired(required);
        
        // 返回值定义
        LlmReturn returnAnno = AnnotationUtils.findAnnotation(method, LlmReturn.class);
        if (returnAnno != null) {
            definition.setReturnDescription(returnAnno.description());
            if (!returnAnno.schema().isEmpty()) {
                definition.setReturnSchema(returnAnno.schema());
            }
        }
        
        return definition;
    }
    
    /**
     * 推断参数类型
     */
    private String inferType(Class<?> type) {
        if (type == String.class) return "string";
        if (type == Integer.class || type == int.class) return "integer";
        if (type == Double.class || type == double.class) return "number";
        if (type == Boolean.class || type == boolean.class) return "boolean";
        if (type.isArray() || List.class.isAssignableFrom(type)) return "array";
        return "object";
    }
}
```

### 3.4 注解与表达式引擎结合

```java
/**
 * 支持表达式默认值的参数解析器
 */
public class AnnotatedParameterResolver {
    
    private final ExpressionEngine expressionEngine;
    
    /**
     * 解析参数值
     */
    public Object resolveParameter(LlmParam annotation, Map<String, Object> input, 
                                    Class<?> paramType) {
        String name = annotation.name();
        
        // 1. 从输入获取
        if (input.containsKey(name)) {
            return convertValue(input.get(name), paramType);
        }
        
        // 2. 使用默认值表达式
        if (!annotation.defaultValue().isEmpty()) {
            return expressionEngine.evaluate(
                annotation.defaultValue(), 
                input, 
                paramType
            );
        }
        
        // 3. 必填检查
        if (annotation.required()) {
            throw new IllegalArgumentException("Required parameter missing: " + name);
        }
        
        return null;
    }
}
```

---

## 四、综合方案对比

### 4.1 方案对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **JavaScript 转换** | 前端兼容、标准化 | 类型信息丢失 | 前后端交互 |
| **表达式引擎** | 灵活、动态 | 性能开销、安全风险 | 动态计算、规则引擎 |
| **注解桥梁** | 类型安全、IDE 支持 | 需要编译 | 强类型场景 |

### 4.2 推荐组合方案

```
┌─────────────────────────────────────────────────────────────────┐
│                    推荐组合方案                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  第一层：注解定义（编译时）                                      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ @LlmFunction + @LlmParam                                │   │
│  │ - 类型安全                                               │   │
│  │ - IDE 支持                                               │   │
│  │ - 自动生成文档                                           │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          ↓                                      │
│  第二层：表达式增强（配置时）                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ YAML 配置 + SpEL/MVEL 表达式                            │   │
│  │ - 参数转换                                               │   │
│  │ - 默认值计算                                             │   │
│  │ - 条件判断                                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                          ↓                                      │
│  第三层：JavaScript 输出（运行时）                               │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ JSON Schema + TypeScript 类型                           │   │
│  │ - 前端使用                                               │   │
│  │ - LLM API 标准                                          │   │
│  │ - 跨语言兼容                                             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 实施优先级

| 优先级 | 内容 | 工作量 |
|--------|------|--------|
| P0 | 注解定义 + 扫描注册 | 1周 |
| P1 | Java 到 JSON Schema 转换 | 1周 |
| P1 | SpEL 表达式集成 | 3天 |
| P2 | MVEL 表达式集成 | 3天 |
| P2 | TypeScript 类型生成 | 3天 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: SE Team

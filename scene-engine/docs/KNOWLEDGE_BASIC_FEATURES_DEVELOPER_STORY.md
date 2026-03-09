# 知识库基础功能开发者故事

**版本**: v2.4.0  
**日期**: 2026-03-07  
**状态**: 开发者故事

---

## 一、概述

本文档描述知识库直接支持的四个基础功能，以及 LLM 如何参与增强这些功能：

| 功能 | 说明 | LLM 角色 |
|------|------|----------|
| 表单增强 | 动态字典表转换、联想输入 | 智能推荐、语义匹配 |
| 转换代填 | Excel 不同页面数据转换 | 数据理解、格式转换 |
| 增强展示 | 图表动态转换为统计图 | 数据分析、图表推荐 |
| 跨场景数据交互 | 场景间数据共享与转换 | 数据映射、语义桥接 |

---

## 二、故事一：表单增强

### 2.1 用户故事

> **作为** HR 招聘专员  
> **我希望** 在填写简历表单时，系统能自动联想输入岗位名称、技能标签  
> **以便** 提高录入效率和准确性

### 2.2 场景描述

```
┌─────────────────────────────────────────────────────────────┐
│  简历录入表单                                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  应聘岗位: [Java开发工程师    ▼]  ← 联想输入                 │
│           ┌─────────────────────┐                          │
│           │ Java开发工程师      │ ← 知识库匹配              │
│           │ Java高级工程师      │                          │
│           │ Java架构师          │                          │
│           │ 前端开发工程师      │                          │
│           └─────────────────────┘                          │
│                                                             │
│  技能标签: [Java, Spring,    ▼]  ← 动态字典                 │
│           ┌─────────────────────┐                          │
│           │ MySQL (推荐)        │ ← LLM 智能推荐            │
│           │ Redis (推荐)        │                          │
│           │ Spring Boot         │                          │
│           │ MyBatis             │                          │
│           └─────────────────────┘                          │
│                                                             │
│  学历要求: [本科        ▼]  ← 字典表转换                     │
│           ┌─────────────────────┐                          │
│           │ 本科                │                          │
│           │ 硕士                │                          │
│           │ 博士                │                          │
│           └─────────────────────┘                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.3 技术实现

#### 2.3.1 注解定义

```java
/**
 * 表单增强注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormEnhance {
    
    /**
     * 增强类型
     */
    EnhanceType type();
    
    /**
     * 知识库来源
     */
    String knowledgeSource() default "";
    
    /**
     * 是否启用 LLM 增强
     */
    boolean llmEnhanced() default true;
    
    /**
     * 联想字段
     */
    String suggestField() default "";
    
    enum EnhanceType {
        DICT,           // 字典表转换
        SUGGEST,        // 联想输入
        AUTO_FILL,      // 自动填充
        VALIDATE        // 数据校验
    }
}

/**
 * 字典表映射注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DictMapping {
    
    /**
     * 字典类型
     */
    String dictType();
    
    /**
     * 存储字段（存储代码）
     */
    String valueField() default "code";
    
    /**
     * 显示字段（显示名称）
     */
    String labelField() default "name";
}
```

#### 2.3.2 实体定义

```java
@Entity
@Table(name = "resume")
public class Resume {
    
    // 联想输入 - 从知识库岗位要求库联想
    @FormEnhance(
        type = EnhanceType.SUGGEST,
        knowledgeSource = "job_requirements",
        llmEnhanced = true,
        suggestField = "positionName"
    )
    @KnowledgeField(vectorize = true, filterable = true)
    private String position;
    
    // 动态字典 - 技能标签
    @FormEnhance(
        type = EnhanceType.SUGGEST,
        knowledgeSource = "skill_dictionary",
        llmEnhanced = true
    )
    @KnowledgeField(vectorize = true, filterable = true)
    private List<String> skills;
    
    // 字典表转换 - 学历
    @DictMapping(
        dictType = "education_level",
        valueField = "code",
        labelField = "name"
    )
    private String education;
}
```

#### 2.3.3 表单增强服务

```java
/**
 * 表单增强服务
 */
@Service
public class FormEnhanceService {
    
    @Autowired
    private KnowledgeCapability knowledgeCapability;
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private DictService dictService;
    
    /**
     * 获取联想建议
     */
    public List<SuggestItem> getSuggestions(
            String field, 
            String keyword, 
            Map<String, Object> context) {
        
        // 1. 从知识库检索
        KnowledgeResult knowledge = knowledgeCapability.retrieve(
            keyword,
            KnowledgeLayer.PROFESSIONAL,
            Map.of("field", field, "topK", 10)
        );
        
        List<SuggestItem> items = new ArrayList<>();
        
        // 2. 转换知识库结果
        for (RetrievedItem item : knowledge.getItems()) {
            items.add(SuggestItem.builder()
                .value(item.getContent())
                .score(item.getScore())
                .source("knowledge")
                .build());
        }
        
        // 3. LLM 智能推荐增强
        if (items.size() < 5) {
            List<SuggestItem> llmItems = getLlmSuggestions(field, keyword, context);
            items.addAll(llmItems);
        }
        
        // 4. 去重排序
        return items.stream()
            .distinct()
            .sorted(Comparator.comparingDouble(SuggestItem::getScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }
    
    /**
     * LLM 智能推荐
     */
    private List<SuggestItem> getLlmSuggestions(
            String field, 
            String keyword,
            Map<String, Object> context) {
        
        String prompt = String.format("""
            用户正在填写表单的"%s"字段，已输入关键词"%s"。
            
            上下文信息：%s
            
            请推荐5个相关的选项，格式为JSON数组：
            [{"value": "选项名称", "reason": "推荐理由"}]
            """, field, keyword, toJson(context));
        
        String response = llmProvider.chat("表单助手", prompt);
        return parseLlmSuggestions(response);
    }
    
    /**
     * 字典表转换
     */
    public DictResult convertDict(String dictType, String value) {
        DictItem item = dictService.getItem(dictType, value);
        if (item == null) {
            // LLM 尝试理解并转换
            return convertWithLlm(dictType, value);
        }
        return DictResult.builder()
            .code(item.getCode())
            .name(item.getName())
            .build();
    }
    
    /**
     * LLM 字典转换
     */
    private DictResult convertWithLlm(String dictType, String value) {
        String prompt = String.format("""
            字典类型：%s
            用户输入：%s
            
            请将用户输入转换为标准字典值，返回JSON：
            {"code": "标准代码", "name": "标准名称", "confidence": 0.9}
            
            如果无法确定，返回 {"code": null, "name": null, "confidence": 0}
            """, dictType, value);
        
        String response = llmProvider.chat("字典转换助手", prompt);
        return parseDictResult(response);
    }
}
```

#### 2.3.4 API 接口

```java
@RestController
@RequestMapping("/api/form/enhance")
public class FormEnhanceController {
    
    @Autowired
    private FormEnhanceService formEnhanceService;
    
    /**
     * 获取联想建议
     */
    @GetMapping("/suggest")
    public List<SuggestItem> suggest(
            @RequestParam String field,
            @RequestParam String keyword,
            @RequestParam(required = false) Map<String, Object> context) {
        return formEnhanceService.getSuggestions(field, keyword, context);
    }
    
    /**
     * 字典转换
     */
    @GetMapping("/dict/convert")
    public DictResult convertDict(
            @RequestParam String dictType,
            @RequestParam String value) {
        return formEnhanceService.convertDict(dictType, value);
    }
}
```

---

## 三、故事二：转换代填

### 3.1 用户故事

> **作为** HR 招聘专员  
> **我希望** 从 Excel 简历模板中导入数据，自动填充到系统表单  
> **以便** 快速录入批量简历数据

### 3.2 场景描述

```
┌─────────────────────────────────────────────────────────────┐
│  Excel 简历导入                                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Excel 文件: 候选人简历汇总.xlsx                     │   │
│  │  ├── Sheet1: 简历列表                                │   │
│  │  ├── Sheet2: 教育经历                                │   │
│  │  └── Sheet3: 工作经历                                │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [开始导入]                                                  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  字段映射 (LLM 自动识别)                             │   │
│  │                                                      │   │
│  │  Excel 列          →    系统字段                     │   │
│  │  ─────────────────────────────────────              │   │
│  │  姓名              →    name              ✓          │   │
│  │  手机号            →    phone            ✓          │   │
│  │  应聘职位          →    position         ✓          │   │
│  │  工作年限          →    workYears        ✓          │   │
│  │  技能特长          →    skills           ✓          │   │
│  │  期望薪资          →    expectedSalary   ✓          │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  导入进度: ████████████░░░░ 75% (15/20 条)                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 技术实现

#### 3.3.1 注解定义

```java
/**
 * 数据转换注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataTransform {
    
    /**
     * 支持的源格式
     */
    String[] sourceFormats() default {"excel", "csv", "json"};
    
    /**
     * 是否启用 LLM 智能映射
     */
    boolean llmMapping() default true;
    
    /**
     * 映射模板
     */
    String mappingTemplate() default "";
}

/**
 * 字段映射注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {
    
    /**
     * 源字段名（支持多个别名）
     */
    String[] sourceNames() default {};
    
    /**
     * 转换表达式
     */
    String transform() default "";
    
    /**
     * 是否必填
     */
    boolean required() default false;
    
    /**
     * 默认值
     */
    String defaultValue() default "";
}
```

#### 3.3.2 实体定义

```java
@Entity
@Table(name = "resume")
@DataTransform(
    sourceFormats = {"excel", "csv"},
    llmMapping = true
)
public class Resume {
    
    @FieldMapping(
        sourceNames = {"姓名", "名字", "name", "候选人姓名"},
        required = true
    )
    private String name;
    
    @FieldMapping(
        sourceNames = {"手机号", "电话", "phone", "联系方式"},
        transform = "formatPhone(value)"
    )
    private String phone;
    
    @FieldMapping(
        sourceNames = {"应聘职位", "岗位", "position", "意向岗位"},
        required = true
    )
    private String position;
    
    @FieldMapping(
        sourceNames = {"工作年限", "经验", "experience"},
        transform = "parseYears(value)"
    )
    private Integer workYears;
    
    @FieldMapping(
        sourceNames = {"技能特长", "技能", "skills"},
        transform = "parseSkills(value)"
    )
    private List<String> skills;
    
    @FieldMapping(
        sourceNames = {"期望薪资", "薪资要求", "salary"},
        transform = "parseSalary(value)"
    )
    private String expectedSalary;
}
```

#### 3.3.3 转换服务

```java
/**
 * 数据转换服务
 */
@Service
public class DataTransformService {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private KnowledgeCapability knowledgeCapability;
    
    /**
     * 解析 Excel 并映射
     */
    public TransformResult transformExcel(
            MultipartFile file, 
            Class<?> targetClass,
            TransformOptions options) {
        
        // 1. 读取 Excel
        Map<String, List<Map<String, Object>>> sheets = readExcel(file);
        
        // 2. 获取映射配置
        DataTransform transformConfig = targetClass.getAnnotation(DataTransform.class);
        
        // 3. LLM 智能映射
        Map<String, String> fieldMapping;
        if (transformConfig != null && transformConfig.llmMapping()) {
            fieldMapping = buildLlmMapping(sheets, targetClass);
        } else {
            fieldMapping = buildAnnotationMapping(targetClass);
        }
        
        // 4. 转换数据
        List<Object> results = new ArrayList<>();
        List<TransformError> errors = new ArrayList<>();
        
        for (Map<String, Object> row : sheets.values().iterator().next()) {
            try {
                Object entity = transformRow(row, fieldMapping, targetClass);
                results.add(entity);
            } catch (Exception e) {
                errors.add(TransformError.of(row, e.getMessage()));
            }
        }
        
        return TransformResult.builder()
            .data(results)
            .errors(errors)
            .mapping(fieldMapping)
            .build();
    }
    
    /**
     * LLM 智能字段映射
     */
    private Map<String, String> buildLlmMapping(
            Map<String, List<Map<String, Object>>> sheets,
            Class<?> targetClass) {
        
        // 获取 Excel 列名
        Set<String> excelColumns = sheets.values().stream()
            .flatMap(List::stream)
            .flatMap(row -> row.keySet().stream())
            .collect(Collectors.toSet());
        
        // 获取目标字段
        List<String> targetFields = Arrays.stream(targetClass.getDeclaredFields())
            .map(Field::getName)
            .collect(Collectors.toList());
        
        // LLM 映射
        String prompt = String.format("""
            请分析以下 Excel 列名与目标字段的映射关系：
            
            Excel 列名：%s
            
            目标字段：%s
            
            请返回 JSON 格式的映射关系：
            {"excel列名": "目标字段名", ...}
            
            如果某列无法映射，值为 null。
            """, excelColumns, targetFields);
        
        String response = llmProvider.chat("数据映射助手", prompt);
        return parseMapping(response);
    }
    
    /**
     * 转换单行数据
     */
    private Object transformRow(
            Map<String, Object> row,
            Map<String, String> mapping,
            Class<?> targetClass) throws Exception {
        
        Object instance = targetClass.getDeclaredConstructor().newInstance();
        
        for (Field field : targetClass.getDeclaredFields()) {
            FieldMapping fm = field.getAnnotation(FieldMapping.class);
            if (fm == null) continue;
            
            // 查找源值
            Object value = findSourceValue(row, mapping, field.getName(), fm.sourceNames());
            
            if (value == null) {
                if (fm.required()) {
                    throw new TransformException("必填字段缺失: " + field.getName());
                }
                value = fm.defaultValue();
            }
            
            // 转换值
            Object convertedValue = convertValue(value, field.getType(), fm.transform());
            
            field.setAccessible(true);
            field.set(instance, convertedValue);
        }
        
        return instance;
    }
    
    /**
     * 值转换
     */
    private Object convertValue(
            Object value, 
            Class<?> targetType, 
            String transformExpr) {
        
        if (value == null) return null;
        
        // 执行转换表达式
        if (transformExpr != null && !transformExpr.isEmpty()) {
            Map<String, Object> context = Map.of("value", value);
            return ruleEngine.execute(transformExpr, context);
        }
        
        // 类型转换
        return TypeConverter.convert(value, targetType);
    }
}
```

#### 3.3.4 跨 Sheet 数据合并

```java
/**
 * 跨 Sheet 数据合并
 */
public Map<String, Object> mergeSheets(
        Map<String, List<Map<String, Object>>> sheets,
        String primaryKey) {
    
    // LLM 理解 Sheet 关系
    String prompt = String.format("""
        分析以下 Excel Sheet 之间的关系：
        
        Sheet 名称：%s
        
        请说明：
        1. 各 Sheet 之间的关联关系
        2. 主键字段
        3. 合并策略
        
        返回 JSON 格式：
        {
            "relations": [
                {"from": "Sheet1", "to": "Sheet2", "key": "姓名", "type": "one-to-many"}
            ],
            "mergeStrategy": "..."
        }
        """, sheets.keySet());
    
    String response = llmProvider.chat("数据分析助手", prompt);
    MergeStrategy strategy = parseMergeStrategy(response);
    
    // 执行合并
    return executeMerge(sheets, strategy);
}
```

---

## 四、故事三：增强展示

### 4.1 用户故事

> **作为** HR 招聘主管  
> **我希望** 系统能自动将简历数据转换为统计图表  
> **以便** 直观了解招聘进展和人才分布

### 4.2 场景描述

```
┌─────────────────────────────────────────────────────────────┐
│  招聘数据统计                                                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  原始数据                                            │   │
│  │  ├── 简历总数: 150                                   │   │
│  │  ├── 待筛选: 80                                      │   │
│  │  ├── 已面试: 40                                      │   │
│  │  └── 已录用: 10                                      │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  [LLM 生成图表]                                              │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  招聘漏斗图 (LLM 推荐)                               │   │
│  │                                                      │   │
│  │     ┌──────────────────┐                            │   │
│  │     │   简历 150       │                            │   │
│  │     └────────┬─────────┘                            │   │
│  │       ┌──────┴───────┐                              │   │
│  │       │  待筛选 80    │                              │   │
│  │       └──────┬───────┘                              │   │
│  │         ┌────┴─────┐                                │   │
│  │         │ 已面试 40 │                                │   │
│  │         └────┬─────┘                                │   │
│  │           ┌──┴───┐                                  │   │
│  │           │录用 10│                                  │   │
│  │           └──────┘                                  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  技能分布饼图 (LLM 推荐)                             │   │
│  │                                                      │   │
│  │           Java ████████████████ 45%                 │   │
│  │         Python ██████████ 30%                       │   │
│  │           Go ████████ 20%                           │   │
│  │        Other ████ 5%                                │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.3 技术实现

#### 4.3.1 注解定义

```java
/**
 * 增强展示注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnhancedDisplay {
    
    /**
     * 支持的图表类型
     */
    ChartType[] supportedCharts() default {};
    
    /**
     * 是否启用 LLM 推荐
     */
    boolean llmRecommend() default true;
    
    /**
     * 默认图表
     */
    ChartType defaultChart() default ChartType.AUTO;
    
    enum ChartType {
        AUTO,           // 自动选择
        BAR,            // 柱状图
        LINE,           // 折线图
        PIE,            // 饼图
        FUNNEL,         // 漏斗图
        SCATTER,        // 散点图
        TABLE,          // 表格
        CARD            // 卡片
    }
}

/**
 * 图表字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChartField {
    
    /**
     * 字段角色
     */
    ChartRole role();
    
    /**
     * 显示名称
     */
    String label() default "";
    
    /**
     * 聚合方式
     */
    Aggregation aggregation() default Aggregation.NONE;
    
    enum ChartRole {
        DIMENSION,      // 维度
        MEASURE,        // 度量
        SERIES          // 系列
    }
    
    enum Aggregation {
        NONE,
        SUM,
        AVG,
        COUNT,
        MAX,
        MIN
    }
}
```

#### 4.3.2 实体定义

```java
@Entity
@Table(name = "recruitment_stat")
@EnhancedDisplay(
    supportedCharts = {ChartType.FUNNEL, ChartType.BAR, ChartType.PIE},
    llmRecommend = true,
    defaultChart = ChartType.FUNNEL
)
public class RecruitmentStat {
    
    @ChartField(role = ChartRole.DIMENSION, label = "阶段")
    private String stage;
    
    @ChartField(role = ChartRole.MEASURE, label = "人数", aggregation = Aggregation.SUM)
    private Integer count;
    
    @ChartField(role = ChartRole.MEASURE, label = "转化率")
    private Double conversionRate;
}
```

#### 4.3.3 增强展示服务

```java
/**
 * 增强展示服务
 */
@Service
public class EnhancedDisplayService {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private KnowledgeCapability knowledgeCapability;
    
    /**
     * 生成图表配置
     */
    public ChartConfig generateChart(
            List<?> data,
            Class<?> dataClass,
            DisplayContext context) {
        
        // 1. 获取注解配置
        EnhancedDisplay displayConfig = dataClass.getAnnotation(EnhancedDisplay.class);
        
        // 2. 分析数据特征
        DataProfile profile = analyzeData(data, dataClass);
        
        // 3. LLM 推荐图表类型
        ChartType recommendedType;
        if (displayConfig != null && displayConfig.llmRecommend()) {
            recommendedType = recommendChartType(profile, context);
        } else {
            recommendedType = displayConfig.defaultChart();
        }
        
        // 4. 生成图表配置
        return buildChartConfig(data, profile, recommendedType);
    }
    
    /**
     * LLM 推荐图表类型
     */
    private ChartType recommendChartType(DataProfile profile, DisplayContext context) {
        String prompt = String.format("""
            请根据以下数据特征推荐最合适的图表类型：
            
            数据特征：
            - 数据行数：%d
            - 维度字段：%s
            - 度量字段：%s
            - 数据分布：%s
            
            用户意图：%s
            
            请从以下图表类型中选择最合适的一个：
            - BAR: 柱状图，适合比较
            - LINE: 折线图，适合趋势
            - PIE: 饼图，适合占比
            - FUNNEL: 漏斗图，适合流程转化
            - SCATTER: 散点图，适合相关性
            - TABLE: 表格，适合详细数据
            - CARD: 卡片，适合关键指标
            
            返回 JSON：{"type": "图表类型", "reason": "推荐理由"}
            """, 
            profile.getRowCount(),
            profile.getDimensions(),
            profile.getMeasures(),
            profile.getDistribution(),
            context.getUserIntent()
        );
        
        String response = llmProvider.chat("图表推荐助手", prompt);
        return parseChartRecommendation(response);
    }
    
    /**
     * 构建图表配置
     */
    private ChartConfig buildChartConfig(
            List<?> data,
            DataProfile profile,
            ChartType chartType) {
        
        ChartConfig config = new ChartConfig();
        config.setType(chartType);
        
        // 根据图表类型生成配置
        switch (chartType) {
            case FUNNEL:
                config.setData(buildFunnelData(data, profile));
                config.setOptions(buildFunnelOptions());
                break;
            case PIE:
                config.setData(buildPieData(data, profile));
                config.setOptions(buildPieOptions());
                break;
            case BAR:
                config.setData(buildBarData(data, profile));
                config.setOptions(buildBarOptions());
                break;
            default:
                config.setData(buildTableData(data, profile));
        }
        
        return config;
    }
    
    /**
     * LLM 生成图表说明
     */
    public String generateChartInsight(ChartConfig config, List<?> data) {
        String prompt = String.format("""
            请分析以下图表数据，生成简短的洞察说明（100字以内）：
            
            图表类型：%s
            数据：%s
            
            请指出：
            1. 关键发现
            2. 异常点
            3. 建议
            """, config.getType(), toJson(data));
        
        return llmProvider.chat("数据分析助手", prompt);
    }
}
```

---

## 五、故事四：跨场景数据交互

### 5.1 用户故事

> **作为** HR 招聘专员  
> **我希望** 招聘场景的数据能自动同步到培训场景  
> **以便** 新员工入职后自动进入培训流程

### 5.2 场景描述

```
┌─────────────────────────────────────────────────────────────┐
│  跨场景数据交互                                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  招聘场景                          培训场景                  │
│  ┌─────────────────┐              ┌─────────────────┐       │
│  │ 候选人信息       │              │ 新员工信息       │       │
│  │ ├── 姓名        │   LLM 映射   │ ├── 姓名        │       │
│  │ ├── 岗位        │ ──────────▶ │ ├── 岗位        │       │
│  │ ├── 技能        │              │ ├── 培训需求    │       │
│  │ └── 面试评价    │              │ └── 入职日期    │       │
│  └─────────────────┘              └─────────────────┘       │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  LLM 数据映射规则                                    │   │
│  │                                                      │   │
│  │  招聘.技能 → 培训.培训需求                            │   │
│  │  规则: 根据技能推荐培训课程                           │   │
│  │                                                      │   │
│  │  招聘.面试评价 → 培训.初始能力评估                    │   │
│  │  规则: 提取评价中的能力关键词                         │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 5.3 技术实现

#### 5.3.1 注解定义

```java
/**
 * 跨场景交互注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossSceneInteraction {
    
    /**
     * 源场景
     */
    String sourceScene();
    
    /**
     * 目标场景
     */
    String targetScene();
    
    /**
     * 触发条件
     */
    String triggerCondition();
    
    /**
     * 是否启用 LLM 映射
     */
    boolean llmMapping() default true;
}

/**
 * 场景字段映射注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SceneFieldMapping {
    
    /**
     * 源场景字段
     */
    String sourceField();
    
    /**
     * 转换规则
     */
    String transformRule() default "";
    
    /**
     * 是否需要 LLM 转换
     */
    boolean llmTransform() default false;
}
```

#### 5.3.2 实体定义

```java
// 招聘场景实体
@Entity
@Table(name = "candidate")
public class Candidate {
    
    private String name;
    private String position;
    private List<String> skills;
    private String interviewComment;
    private String status;
}

// 培训场景实体
@Entity
@Table(name = "trainee")
@CrossSceneInteraction(
    sourceScene = "recruitment",
    targetScene = "training",
    triggerCondition = "#candidate.status == 'HIRED'",
    llmMapping = true
)
public class Trainee {
    
    @SceneFieldMapping(sourceField = "name")
    private String name;
    
    @SceneFieldMapping(sourceField = "position")
    private String position;
    
    @SceneFieldMapping(
        sourceField = "skills",
        transformRule = "recommendTrainingCourses",
        llmTransform = true
    )
    private List<String> trainingNeeds;
    
    @SceneFieldMapping(
        sourceField = "interviewComment",
        transformRule = "extractAbilityKeywords",
        llmTransform = true
    )
    private Map<String, Integer> initialAssessment;
    
    private LocalDate onboardDate;
}
```

#### 5.3.3 跨场景交互服务

```java
/**
 * 跨场景交互服务
 */
@Service
public class CrossSceneInteractionService {
    
    @Autowired
    private LlmProvider llmProvider;
    
    @Autowired
    private KnowledgeCapability knowledgeCapability;
    
    @Autowired
    private AsyncEventBus eventBus;
    
    /**
     * 触发跨场景数据传输
     */
    public void triggerInteraction(
            Object sourceEntity,
            String targetScene) {
        
        // 1. 获取目标实体类
        Class<?> targetClass = findTargetClass(targetScene);
        
        // 2. 获取映射配置
        CrossSceneInteraction config = targetClass.getAnnotation(CrossSceneInteraction.class);
        
        // 3. 检查触发条件
        if (!checkTriggerCondition(sourceEntity, config.triggerCondition())) {
            return;
        }
        
        // 4. 执行数据映射
        Object targetEntity = mapEntity(sourceEntity, targetClass, config);
        
        // 5. 发布事件（异步保存）
        eventBus.publish(new CrossSceneDataEvent(targetEntity, targetScene));
    }
    
    /**
     * 实体映射
     */
    private Object mapEntity(
            Object sourceEntity,
            Class<?> targetClass,
            CrossSceneInteraction config) {
        
        try {
            Object targetInstance = targetClass.getDeclaredConstructor().newInstance();
            
            for (Field targetField : targetClass.getDeclaredFields()) {
                SceneFieldMapping mapping = targetField.getAnnotation(SceneFieldMapping.class);
                if (mapping == null) continue;
                
                // 获取源字段值
                Object sourceValue = getFieldValue(sourceEntity, mapping.sourceField());
                
                // 转换值
                Object targetValue;
                if (mapping.llmTransform()) {
                    targetValue = transformWithLlm(
                        sourceValue, 
                        targetField.getType(), 
                        mapping.transformRule()
                    );
                } else if (!mapping.transformRule().isEmpty()) {
                    targetValue = executeTransformRule(
                        sourceValue, 
                        mapping.transformRule()
                    );
                } else {
                    targetValue = sourceValue;
                }
                
                targetField.setAccessible(true);
                targetField.set(targetInstance, targetValue);
            }
            
            return targetInstance;
            
        } catch (Exception e) {
            throw new CrossSceneException("Entity mapping failed", e);
        }
    }
    
    /**
     * LLM 值转换
     */
    private Object transformWithLlm(
            Object sourceValue,
            Class<?> targetType,
            String transformRule) {
        
        String prompt = String.format("""
            请执行以下数据转换：
            
            转换规则：%s
            源数据：%s
            目标类型：%s
            
            请返回转换后的数据。
            如果是列表，返回 JSON 数组。
            如果是对象，返回 JSON 对象。
            """, transformRule, toJson(sourceValue), targetType.getSimpleName());
        
        String response = llmProvider.chat("数据转换助手", prompt);
        return parseTransformResult(response, targetType);
    }
    
    /**
     * 技能到培训需求的转换
     */
    public List<String> recommendTrainingCourses(List<String> skills) {
        // 从知识库检索相关培训课程
        KnowledgeResult result = knowledgeCapability.retrieve(
            String.join(",", skills),
            KnowledgeLayer.PROFESSIONAL,
            Map.of("type", "training_course", "topK", 10)
        );
        
        // LLM 推荐最匹配的课程
        String prompt = String.format("""
            根据以下技能，推荐最合适的培训课程：
            
            员工技能：%s
            
            可选课程：%s
            
            请返回最匹配的5个课程名称（JSON数组）。
            """, skills, result.getItems().stream()
                .map(RetrievedItem::getContent)
                .collect(Collectors.toList()));
        
        String response = llmProvider.chat("培训推荐助手", prompt);
        return parseStringList(response);
    }
    
    /**
     * 面试评价到能力评估的转换
     */
    public Map<String, Integer> extractAbilityKeywords(String interviewComment) {
        String prompt = String.format("""
            请从以下面试评价中提取能力关键词并评分（1-5分）：
            
            面试评价：
            %s
            
            请返回 JSON 格式：
            {"能力名称": 分数, ...}
            
            例如：{"沟通能力": 4, "技术能力": 5, "团队协作": 4}
            """, interviewComment);
        
        String response = llmProvider.chat("能力评估助手", prompt);
        return parseAbilityMap(response);
    }
}
```

---

## 六、知识库与 LLM 协同总结

### 6.1 协同模式

| 功能 | 知识库角色 | LLM 角色 |
|------|------------|----------|
| 表单增强 | 提供字典数据、联想候选项 | 智能推荐、语义匹配、字典转换 |
| 转换代填 | 存储映射规则、历史模板 | 智能字段映射、数据格式转换 |
| 增强展示 | 存储图表模板、展示规则 | 图表推荐、数据洞察生成 |
| 跨场景交互 | 存储场景映射规则、数据模板 | 数据转换、语义桥接 |

### 6.2 异步处理原则

```
用户操作 → 同步返回 → 异步处理（知识库 + LLM）
    │           │              │
    │           │              ├── 知识库索引更新
    │           │              ├── LLM 智能推荐
    │           │              └── 跨场景数据同步
    │           │
    │           └── 立即响应用户
    │
    └── 不阻塞用户操作
```

### 6.3 失败处理

| 场景 | 失败处理 |
|------|----------|
| 知识库检索失败 | 返回空结果，LLM 继续处理 |
| LLM 调用失败 | 使用规则引擎降级 |
| 跨场景同步失败 | 记录日志，重试队列 |

---

**文档维护**: Ooder Team  
**最后更新**: 2026-03-07

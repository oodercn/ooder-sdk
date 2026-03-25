# 术语服务

## 接口

**`TerminologyService`** - `net.ooder.scene.skill.knowledge.TerminologyService`

## 核心方法

| 方法 | 说明 | 返回值 |
|------|------|--------|
| `preprocess(query)` | 预处理查询 | `PreprocessedQuery` |
| `expandAbbreviations(query)` | 扩展缩写 | `String` |
| `normalize(query)` | 规范化查询 | `String` |
| `getSynonyms(term)` | 获取同义词 | `List<String>` |
| `addTerminology(mapping)` | 添加术语 | `void` |

## 使用示例

### 查询预处理

```java
PreprocessedQuery preprocessed = terminologyService.preprocess(
    "JD的招聘流程是什么？"
);

System.out.println("原始: " + preprocessed.getOriginalQuery());
System.out.println("扩展: " + preprocessed.getExpandedQuery());
System.out.println("类型: " + preprocessed.getQueryType());
```

### 缩写扩展

```java
String expanded = terminologyService.expandAbbreviations(
    "JD和HR的区别是什么？"
);
// 输出: Job Description和Human Resources的区别是什么？
```

### 术语管理

```java
TerminologyMapping mapping = new TerminologyMapping();
mapping.setTerm("Job Description");
mapping.setDefinition("职位描述");
mapping.addAbbreviation("JD");
mapping.addAlias("职位描述");
mapping.setCategory("recruitment");

terminologyService.addTerminology(mapping);
```

## 内置缩写词典

| 缩写 | 全称 | 说明 |
|------|------|------|
| JD | Job Description | 职位描述 |
| HR | Human Resources | 人力资源 |
| CV | Curriculum Vitae | 简历 |
| OKR | Objectives and Key Results | 目标与关键成果 |
| KPI | Key Performance Indicator | 关键绩效指标 |
| SOP | Standard Operating Procedure | 标准操作流程 |
| FAQ | Frequently Asked Questions | 常见问题 |
| API | Application Programming Interface | 应用程序接口 |
| AI | Artificial Intelligence | 人工智能 |
| ML | Machine Learning | 机器学习 |
| LLM | Large Language Model | 大语言模型 |
| RAG | Retrieval-Augmented Generation | 检索增强生成 |

# 3.3 Tree递归关系规范

## 1. 概述

Tree组件是ooder平台中用于展示具有层级关系数据的重要视图组件。它支持递归数据结构的展示，能够清晰地表达父子节点关系，适用于组织架构、分类目录、文件系统等场景。

## 2. Tree组件核心特性

### 2.1 递归数据结构支持
- 支持无限层级的节点嵌套
- 每个节点可包含子节点列表
- 支持懒加载子节点数据

### 2.2 节点操作
- 节点展开/折叠
- 节点选择
- 节点编辑
- 节点拖拽排序

### 2.3 视觉呈现
- 树形缩进展示
- 连接线显示父子关系
- 节点图标自定义
- 高亮选中节点

## 3. Tree注解体系

### 3.1 @TreeAnnotation
Tree组件的核心注解，用于定义Tree视图的基本属性。

```java
@TreeAnnotation(
    customService = {OrganizationTreeService.class},
    showRoot = true,
    selectable = true,
    checkable = false,
    draggable = true
)
public class OrganizationTreeView {
    // 字段定义
}
```

### 3.2 @TreeNodeAnnotation
用于标记Tree节点字段，定义节点的显示属性。

```java
@TreeNodeAnnotation(
    titleField = "name",
    keyField = "id",
    childrenField = "children",
    iconField = "icon"
)
private List<OrganizationNode> nodes;
```

## 4. Tree字段注解

### 4.1 @TreeNodeField
用于定义节点字段的显示和行为特性。

```java
@TreeNodeField(
    title = "部门名称",
    editable = true,
    searchable = true
)
private String name;
```

## 5. Tree菜单和事件处理

### 5.1 标准菜单项
- 添加节点
- 删除节点
- 编辑节点
- 刷新树

### 5.2 事件处理
- 节点点击事件
- 节点展开事件
- 节点拖拽事件
- 节点选择变更事件

## 6. Tree服务规范

### 6.1 数据加载服务
```java
@Service
public class OrganizationTreeService implements TreeDataService<OrganizationNode> {
    @Override
    public List<OrganizationNode> loadRootNodes() {
        // 加载根节点
    }
    
    @Override
    public List<OrganizationNode> loadChildrenNodes(String parentId) {
        // 加载子节点
    }
}
```

### 6.2 节点操作服务
```java
@Service
public class OrganizationNodeOperationService implements TreeNodeOperationService<OrganizationNode> {
    @Override
    public OrganizationNode addNode(OrganizationNode parent, OrganizationNode newNode) {
        // 添加节点逻辑
    }
    
    @Override
    public void deleteNode(OrganizationNode node) {
        // 删除节点逻辑
    }
    
    @Override
    public OrganizationNode updateNode(OrganizationNode node) {
        // 更新节点逻辑
    }
}
```

## 7. Tree递归关系实现

### 7.1 递归数据模型
```java
public class OrganizationNode {
    private String id;
    private String name;
    private String parentId;
    private List<OrganizationNode> children;
    
    // getter和setter方法
}
```

### 7.2 递归解析机制
- 服务端提供扁平数据列表
- 前端自动构建树形结构
- 支持按需加载子节点

## 8. 最佳实践

### 8.1 性能优化
- 大数据量时使用虚拟滚动
- 合理设置节点缓存策略
- 懒加载子节点数据

### 8.2 用户体验
- 提供搜索过滤功能
- 支持键盘导航
- 明确的节点状态反馈

### 8.3 数据一致性
- 确保父子节点数据同步
- 提供撤销操作支持
- 验证节点操作的合法性
# 3.5 Tree组合规范

## 1. 概述

Tree组合规范定义了Tree组件与其他视图组件（如Tabs、Group、Form、TreeGrid等）协同工作的设计原则和实现方式。通过合理的组合使用，可以构建更加复杂和功能丰富的用户界面，满足多样化的业务需求。

## 2. Tree与Tabs组合

### 2.1 设计理念
Tree与Tabs的组合通常用于构建主从结构的界面，Tree作为导航组件，Tabs作为内容展示区域。用户通过Tree选择不同的节点，Tabs区域显示相应的内容。

### 2.2 实现方式
```java
@TabsAnnotation(
    customService = {TreeBasedTabsService.class},
    tabPosition = TabPosition.TOP
)
public enum TreeBasedTabs implements TabItem {
    NODE_DETAILS("节点详情", "fas fa-info-circle", NodeDetailsService.class),
    NODE_CHILDREN("子节点列表", "fas fa-list", NodeChildrenTreeGridService.class),
    NODE_HISTORY("操作历史", "fas fa-history", NodeHistoryService.class);
}
```

### 2.3 数据联动机制
```java
@Service
public class TreeBasedTabsService implements TabsDataService {
    @Autowired
    private TreeNodeSelectionService selectionService;
    
    @Override
    public List<TabItem> loadTabs() {
        // 根据选中的Tree节点动态调整Tabs
        TreeNode selectedNode = selectionService.getSelectedNode();
        if (selectedNode != null) {
            return Arrays.asList(TreeBasedTabs.values());
        }
        return Collections.emptyList();
    }
}
```

## 3. Tree与Group组合

### 3.1 设计理念
Tree与Group的组合通常用于构建层次化的信息展示界面，Tree作为整体结构导航，Group用于展示具体节点的详细信息。

### 3.2 实现方式
```java
@GroupAnnotation(
    title = "节点详细信息",
    borderType = BorderType.inset,
    collapsible = true
)
public class TreeNodeDetailsGroup {
    @TextField(label = "节点名称")
    private String nodeName;
    
    @TextField(label = "节点类型")
    private String nodeType;
    
    @DateField(label = "创建时间")
    private Date createTime;
    
    // getter和setter方法
}
```

### 3.3 动态内容更新
```java
@Service
public class TreeNodeDetailsGroupService implements GroupDataService<TreeNodeDetailsGroup> {
    @Autowired
    private TreeNodeSelectionService selectionService;
    
    @Override
    public void loadData(TreeNodeDetailsGroup group) {
        TreeNode selectedNode = selectionService.getSelectedNode();
        if (selectedNode != null) {
            group.setNodeName(selectedNode.getName());
            group.setNodeType(selectedNode.getType());
            group.setCreateTime(selectedNode.getCreateTime());
        }
    }
}
```

## 4. Tree与Form组合

### 4.1 设计理念
Tree与Form的组合用于构建节点编辑界面，Tree作为导航结构，Form用于编辑选中节点的属性信息。

### 4.2 实现方式
```java
@FormAnnotation(
    borderType = BorderType.none,
    col = 1,
    row = 5,
    customService = {TreeNodeEditFormService.class}
)
public class TreeNodeEditForm {
    @HiddenField
    private String nodeId;
    
    @TextField(label = "节点名称", required = true)
    private String name;
    
    @SelectField(label = "节点类型", options = {"folder", "file", "link"})
    private String type;
    
    @TextAreaField(label = "节点描述")
    private String description;
    
    // getter和setter方法
}
```

### 4.3 数据绑定与验证
```java
@Service
public class TreeNodeEditFormService implements FormDataService<TreeNodeEditForm> {
    @Autowired
    private TreeNodeOperationService operationService;
    
    @Override
    public void loadData(TreeNodeEditForm form) {
        // 加载选中节点数据到表单
    }
    
    @Override
    public boolean saveData(TreeNodeEditForm form) {
        // 保存表单数据到节点
        return operationService.updateNode(form);
    }
    
    @Override
    public List<ValidationError> validateData(TreeNodeEditForm form) {
        // 表单数据验证
        List<ValidationError> errors = new ArrayList<>();
        if (StringUtils.isEmpty(form.getName())) {
            errors.add(new ValidationError("name", "节点名称不能为空"));
        }
        return errors;
    }
}
```

## 5. Tree与TreeGrid组合

### 5.1 设计理念
Tree与TreeGrid的组合用于展示具有层级关系的数据列表，Tree作为分类导航，TreeGrid展示具体的数据项。

### 5.2 实现方式
```java
@TreeGridAnnotation(
    customService = {TreeNodeChildrenTreeGridService.class},
    showHeader = true,
    colSortable = true,
    altRowsBg = true
)
public class TreeNodeChildrenTreeGrid {
    @TreeGridColumn(title = "名称", field = "name", sortable = true)
    private String name;
    
    @TreeGridColumn(title = "类型", field = "type")
    private String type;
    
    @TreeGridColumn(title = "创建时间", field = "createTime", formatter = "date")
    private Date createTime;
    
    // getter和setter方法
}
```

### 5.3 数据过滤与排序
```java
@Service
public class TreeNodeChildrenTreeGridService implements TreeGridDataService<TreeNodeChildrenTreeGrid> {
    @Autowired
    private TreeNodeSelectionService selectionService;
    
    @Override
    public List<TreeNodeChildrenTreeGrid> loadData(TreeGridQueryParams params) {
        TreeNode selectedNode = selectionService.getSelectedNode();
        if (selectedNode != null) {
            // 根据选中的Tree节点加载子节点数据
            return loadChildrenData(selectedNode.getId(), params);
        }
        return Collections.emptyList();
    }
    
    private List<TreeNodeChildrenTreeGrid> loadChildrenData(String parentId, TreeGridQueryParams params) {
        // 实现子节点数据加载逻辑
        // 支持分页、排序、过滤等操作
    }
}
```

## 6. 复杂组合场景

### 6.1 Tree-Tabs-Form-TreeGrid组合
构建一个完整的节点管理界面，包含Tree导航、Tabs内容区域、Form编辑区和TreeGrid数据列表。

```java
// 主视图结构
@TabsAnnotation(customService = {NodeManagementTabsService.class})
public enum NodeManagementTabs implements TabItem {
    NODE_EDIT("节点编辑", "fas fa-edit", TreeNodeEditForm.class),
    NODE_CHILDREN("子节点管理", "fas fa-list", TreeNodeChildrenTreeGrid.class);
}
```

### 6.2 数据流设计
1. Tree节点选择事件触发
2. Tabs内容区域刷新
3. Form加载选中节点数据
4. TreeGrid加载子节点列表数据

## 7. 服务规范

### 7.1 组合服务协调
```java
@Service
public class TreeCombinationService {
    @Autowired
    private TreeNodeSelectionService selectionService;
    
    @Autowired
    private TabsRefreshService tabsService;
    
    @Autowired
    private FormLoadService formService;
    
    @Autowired
    private TreeGridRefreshService gridService;
    
    public void onTreeNodeSelected(TreeNode node) {
        // 协调各个组件的刷新操作
        tabsService.refreshTabs(node);
        formService.loadFormData(node);
        gridService.refreshTreeGridData(node.getId());
    }
}
```

## 8. 最佳实践

### 8.1 性能优化
- 实现组件间的懒加载机制
- 合理使用数据缓存策略
- 避免不必要的重复渲染

### 8.2 用户体验
- 保持组件间操作的一致性
- 提供清晰的状态反馈
- 支持键盘快捷操作

### 8.3 数据一致性
- 确保组合组件间数据同步
- 实现统一的错误处理机制
- 提供撤销/重做功能支持
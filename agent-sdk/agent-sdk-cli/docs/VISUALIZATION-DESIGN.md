# Agent SDK CLI - 可视化展现设计（Skills模式）

## 概述

本文档规划CLI的可视化展现方案，采用Skills模式开发，将CLI功能以可视化组件形式呈现，支持Web界面、桌面应用和嵌入式展示。

## 1. 可视化架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        可视化展现层 (Presentation Layer)                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐          │
│  │   Web Dashboard  │  │  Desktop Client  │  │  Embedded Panel  │          │
│  │   (React/Vue)    │  │   (Electron)     │  │   (A2UI/Ooder)   │          │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘          │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        可视化组件层 (Component Layer)                         │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │
│  │ SkillPanel   │ │ SceneCanvas  │ │ TaskMonitor  │ │ LogViewer    │       │
│  │              │ │              │ │              │ │              │       │
│  │ - SkillCard  │ │ - NodeGraph  │ │ - Timeline   │ │ - StreamView │       │
│  │ - StatusIcon │ │ - EdgeFlow   │ │ - Progress   │ │ - FilterBar  │       │
│  │ - ActionBtn  │ │ - Property   │ │ - AlertList  │ │ - SearchBox  │       │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘       │
│                                                                             │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐       │
│  │ CommandInput │ │ ResultPanel  │ │ ConfigEditor │ │ ChartWidget  │       │
│  │              │ │              │ │              │ │              │       │
│  │ - AutoComplete│ │ - TableView  │ │ - FormLayout │ │ - LineChart  │       │
│  │ - History    │ │ - JsonTree   │ │ - YamlEditor │ │ - PieChart   │       │
│  │ - Suggestion │ │ - CodeBlock  │ │ - Validator  │ │ - Heatmap    │       │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        数据适配层 (Adapter Layer)                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    VisualizationDataAdapter                         │   │
│  │                                                                     │   │
│  │  - SkillDTO → SkillViewModel                                       │   │
│  │  - SceneDTO → NodeGraphData                                        │   │
│  │  - TaskDTO  → TimelineData                                         │   │
│  │  - Event    → Notification                                         │   │
│  │                                                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                        应用服务层 (Application Layer)                         │
│                    (SkillAppService / SceneAppService)                       │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 2. Skills模式组件设计

### 2.1 Skill 可视化组件

```yaml
# skill-visualization.yaml
skill:
  id: cli-visualization
  name: CLI Visualization Components
  version: 3.1.0
  
  capabilities:
    # Skill 面板展示
    - id: skill-panel
      name: Skill Panel
      description: 展示和管理所有已安装 Skills
      input:
        - name: filter
          type: string
          description: 过滤条件
        - name: viewMode
          type: enum[grid, list, tree]
          default: grid
      output:
        type: SkillPanelViewModel
        
    # 场景画布
    - id: scene-canvas
      name: Scene Canvas
      description: 可视化展示场景拓扑结构
      input:
        - name: sceneId
          type: string
          required: true
        - name: layout
          type: enum[force, hierarchical, circular]
          default: force
      output:
        type: GraphData
        
    # 任务监控面板
    - id: task-monitor
      name: Task Monitor
      description: 实时监控任务执行状态
      input:
        - name: taskIds
          type: array[string]
        - name: autoRefresh
          type: boolean
          default: true
      output:
        type: TaskMonitorViewModel
        
    # 日志查看器
    - id: log-viewer
      name: Log Viewer
      description: 实时查看任务日志
      input:
        - name: taskId
          type: string
        - name: level
          type: enum[debug, info, warn, error]
          default: info
      output:
        type: LogStream
        
    # 命令输入组件
    - id: command-input
      name: Command Input
      description: 智能命令输入框
      input:
        - name: placeholder
          type: string
        - name: commands
          type: array[CommandDefinition]
      output:
        type: CommandExecutionRequest
        
    # 结果展示面板
    - id: result-panel
      name: Result Panel
      description: 多格式结果展示
      input:
        - name: result
          type: any
        - name: format
          type: enum[table, json, tree, text]
          default: auto
      output:
        type: RenderedView
        
    # 配置编辑器
    - id: config-editor
      name: Config Editor
      description: 可视化配置编辑器
      input:
        - name: configKey
          type: string
        - name: schema
          type: JsonSchema
      output:
        type: ConfigUpdateRequest
        
    # 统计图表
    - id: statistics-chart
      name: Statistics Chart
      description: 各类统计图表展示
      input:
        - name: chartType
          type: enum[line, bar, pie, heatmap, gauge]
        - name: dataSource
          type: DataSourceConfig
      output:
        type: ChartViewModel
```

### 2.2 Ooder UI 组件映射

```javascript
// Ooder UI 组件映射表
const ComponentMapping = {
    // Skill 面板组件
    'skill-panel': {
        container: 'ood.UI.Block',
        items: {
            skillCard: 'ood.UI.InfoBlock',
            statusBadge: 'ood.UI.Label',
            actionButton: 'ood.UI.Button',
            filterInput: 'ood.UI.Input',
            viewToggle: 'ood.UI.StatusButtons'
        }
    },
    
    // 场景画布组件
    'scene-canvas': {
        container: 'ood.svg.SVGPaper',
        items: {
            node: 'ood.svg.rectComb',
            edge: 'ood.svg.arrow2',
            group: 'ood.svg.pathComb',
            label: 'ood.svg.text'
        }
    },
    
    // 任务监控组件
    'task-monitor': {
        container: 'ood.UI.Block',
        items: {
            timeline: 'ood.UI.ECharts',
            progressBar: 'ood.UI.ProgressBar',
            statusList: 'ood.UI.FoldingList',
            alertPanel: 'ood.UI.InfoBlock'
        }
    },
    
    // 日志查看器组件
    'log-viewer': {
        container: 'ood.UI.Block',
        items: {
            logStream: 'ood.UI.List',
            filterBar: 'ood.UI.ComboInput',
            searchBox: 'ood.UI.Input',
            levelSelector: 'ood.UI.StatusButtons'
        }
    },
    
    // 命令输入组件
    'command-input': {
        container: 'ood.UI.FormLayout',
        items: {
            input: 'ood.UI.ComboInput',
            suggestion: 'ood.UI.PopMenu',
            history: 'ood.UI.FoldingList',
            executeBtn: 'ood.UI.Button'
        }
    },
    
    // 结果展示组件
    'result-panel': {
        container: 'ood.UI.Block',
        items: {
            tableView: 'ood.UI.TreeGrid',
            jsonTree: 'ood.UI.TreeView',
            codeBlock: 'ood.UI.ContentBlock',
            textView: 'ood.UI.Label'
        }
    },
    
    // 配置编辑器组件
    'config-editor': {
        container: 'ood.UI.FormLayout',
        items: {
            form: 'ood.UI.FormLayout',
            yamlEditor: 'ood.UI.ContentBlock',
            validator: 'ood.UI.Label',
            saveBtn: 'ood.UI.Button'
        }
    },
    
    // 统计图表组件
    'statistics-chart': {
        container: 'ood.UI.Block',
        items: {
            chart: 'ood.UI.FusionChartsXT',
            legend: 'ood.UI.List',
            filter: 'ood.UI.ComboInput'
        }
    }
};
```

## 3. 核心可视化组件详细设计

### 3.1 SkillPanel 组件

```javascript
// SkillPanel.class.js
ood.Class('CLI.SkillPanel', 'ood.UI.Block', {
    
    // 组件属性
    Attrs: {
        skills: { type: 'array', default: [] },
        filter: { type: 'string', default: '' },
        viewMode: { type: 'string', default: 'grid' },
        selectedSkill: { type: 'string', default: null }
    },
    
    // 组件事件
    Events: ['skillSelected', 'skillAction', 'filterChanged'],
    
    // 模板定义
    Template: {
        class: 'cli-skill-panel',
        layout: 'vertical',
        children: [
            {
                type: 'ood.UI.Block',
                class: 'panel-header',
                children: [
                    {
                        type: 'ood.UI.Input',
                        id: 'filterInput',
                        placeholder: '搜索 Skills...',
                        onChange: 'onFilterChange'
                    },
                    {
                        type: 'ood.UI.StatusButtons',
                        id: 'viewToggle',
                        items: ['grid', 'list', 'tree'],
                        onChange: 'onViewModeChange'
                    }
                ]
            },
            {
                type: 'ood.UI.Block',
                id: 'skillContainer',
                class: 'skill-container'
            }
        ]
    },
    
    // 方法定义
    Methods: {
        
        // 初始化
        init: function() {
            this.loadSkills();
            this.startAutoRefresh();
        },
        
        // 加载 Skills
        loadSkills: function() {
            var self = this;
            CLI.AppService.getAllSkills()
                .then(function(skills) {
                    self.set('skills', skills);
                    self.renderSkills();
                });
        },
        
        // 渲染 Skill 列表
        renderSkills: function() {
            var skills = this.get('skills');
            var filter = this.get('filter');
            var viewMode = this.get('viewMode');
            
            // 过滤
            if (filter) {
                skills = skills.filter(function(s) {
                    return s.name.toLowerCase().includes(filter.toLowerCase()) ||
                           s.skillId.toLowerCase().includes(filter.toLowerCase());
                });
            }
            
            // 根据视图模式渲染
            var container = this.getChild('skillContainer');
            container.clear();
            
            if (viewMode === 'grid') {
                this.renderGridView(container, skills);
            } else if (viewMode === 'list') {
                this.renderListView(container, skills);
            } else {
                this.renderTreeView(container, skills);
            }
        },
        
        // 网格视图
        renderGridView: function(container, skills) {
            var self = this;
            skills.forEach(function(skill) {
                var card = ood.create('ood.UI.InfoBlock', {
                    class: 'skill-card',
                    title: skill.name,
                    subtitle: skill.skillId + ' v' + skill.version,
                    status: skill.status,
                    actions: [
                        { label: '启动', action: 'start', visible: skill.canStart },
                        { label: '停止', action: 'stop', visible: skill.canStop },
                        { label: '详情', action: 'info' }
                    ],
                    onAction: function(action) {
                        self.fireEvent('skillAction', skill.skillId, action);
                    },
                    onClick: function() {
                        self.set('selectedSkill', skill.skillId);
                        self.fireEvent('skillSelected', skill);
                    }
                });
                container.add(card);
            });
        },
        
        // 过滤变更
        onFilterChange: function(value) {
            this.set('filter', value);
            this.renderSkills();
            this.fireEvent('filterChanged', value);
        },
        
        // 视图模式变更
        onViewModeChange: function(mode) {
            this.set('viewMode', mode);
            this.renderSkills();
        },
        
        // 自动刷新
        startAutoRefresh: function() {
            var self = this;
            setInterval(function() {
                self.loadSkills();
            }, 5000);
        }
    }
});
```

### 3.2 SceneCanvas 组件

```javascript
// SceneCanvas.class.js
ood.Class('CLI.SceneCanvas', 'ood.svg.SVGPaper', {
    
    Attrs: {
        sceneId: { type: 'string', required: true },
        nodes: { type: 'array', default: [] },
        edges: { type: 'array', default: [] },
        layout: { type: 'string', default: 'force' }
    },
    
    Events: ['nodeSelected', 'nodeAction', 'canvasReady'],
    
    Template: {
        class: 'cli-scene-canvas',
        width: '100%',
        height: '600px'
    },
    
    Methods: {
        
        init: function() {
            this.loadSceneData();
            this.setupInteractions();
        },
        
        // 加载场景数据
        loadSceneData: function() {
            var self = this;
            var sceneId = this.get('sceneId');
            
            CLI.AppService.getScene(sceneId)
                .then(function(scene) {
                    self.set('nodes', self.buildNodes(scene));
                    self.set('edges', self.buildEdges(scene));
                    self.renderGraph();
                });
        },
        
        // 构建节点
        buildNodes: function(scene) {
            var nodes = [];
            
            // 主节点
            var mainMember = scene.members.find(function(m) { return m.isMain; });
            if (mainMember) {
                nodes.push({
                    id: mainMember.memberId,
                    type: 'main',
                    label: mainMember.skillId,
                    x: 400,
                    y: 300,
                    style: {
                        fill: '#4CAF50',
                        stroke: '#2E7D32',
                        'stroke-width': 2
                    }
                });
            }
            
            // 协作节点
            scene.members.forEach(function(member) {
                if (!member.isMain) {
                    nodes.push({
                        id: member.memberId,
                        type: 'collaborator',
                        label: member.skillId,
                        x: Math.random() * 600 + 100,
                        y: Math.random() * 400 + 100,
                        style: {
                            fill: '#2196F3',
                            stroke: '#1565C0'
                        }
                    });
                }
            });
            
            return nodes;
        },
        
        // 构建边
        buildEdges: function(scene) {
            var edges = [];
            var mainMember = scene.members.find(function(m) { return m.isMain; });
            
            if (mainMember) {
                scene.members.forEach(function(member) {
                    if (!member.isMain) {
                        edges.push({
                            from: mainMember.memberId,
                            to: member.memberId,
                            label: member.role,
                            style: {
                                stroke: '#999',
                                'stroke-width': 1,
                                'arrow-end': 'classic'
                            }
                        });
                    }
                });
            }
            
            return edges;
        },
        
        // 渲染图
        renderGraph: function() {
            var self = this;
            var nodes = this.get('nodes');
            var edges = this.get('edges');
            
            // 清空画布
            this.clear();
            
            // 渲染边
            edges.forEach(function(edge) {
                self.drawEdge(edge);
            });
            
            // 渲染节点
            nodes.forEach(function(node) {
                self.drawNode(node);
            });
            
            // 应用布局
            this.applyLayout();
            
            this.fireEvent('canvasReady');
        },
        
        // 绘制节点
        drawNode: function(node) {
            var self = this;
            
            var rect = this.rect(node.x - 60, node.y - 30, 120, 60, 5);
            rect.attr(node.style);
            
            var text = this.text(node.x, node.y, node.label);
            text.attr({
                'font-size': 12,
                'text-anchor': 'middle',
                fill: '#fff'
            });
            
            // 交互
            rect.click(function() {
                self.fireEvent('nodeSelected', node);
            });
            
            rect.hover(function() {
                this.attr({ 'stroke-width': 3 });
            }, function() {
                this.attr({ 'stroke-width': node.style['stroke-width'] || 1 });
            });
        },
        
        // 绘制边
        drawEdge: function(edge) {
            var fromNode = this.get('nodes').find(function(n) { return n.id === edge.from; });
            var toNode = this.get('nodes').find(function(n) { return n.id === edge.to; });
            
            if (fromNode && toNode) {
                var path = this.path(
                    'M' + fromNode.x + ',' + fromNode.y +
                    'L' + toNode.x + ',' + toNode.y
                );
                path.attr(edge.style);
            }
        },
        
        // 应用布局
        applyLayout: function() {
            var layout = this.get('layout');
            
            if (layout === 'force') {
                this.applyForceLayout();
            } else if (layout === 'hierarchical') {
                this.applyHierarchicalLayout();
            } else if (layout === 'circular') {
                this.applyCircularLayout();
            }
        },
        
        // 力导向布局
        applyForceLayout: function() {
            // 使用 D3.js 或自定义力导向算法
            // 简化实现...
        },
        
        // 设置交互
        setupInteractions: function() {
            var self = this;
            
            // 拖拽
            // 缩放
            // 选择
        }
    }
});
```

### 3.3 TaskMonitor 组件

```javascript
// TaskMonitor.class.js
ood.Class('CLI.TaskMonitor', 'ood.UI.Block', {
    
    Attrs: {
        tasks: { type: 'array', default: [] },
        selectedTask: { type: 'string', default: null },
        autoRefresh: { type: 'boolean', default: true }
    },
    
    Events: ['taskSelected', 'taskAction', 'refresh'],
    
    Template: {
        class: 'cli-task-monitor',
        layout: 'vertical',
        children: [
            {
                type: 'ood.UI.Block',
                class: 'monitor-header',
                children: [
                    {
                        type: 'ood.UI.Label',
                        text: '任务监控',
                        class: 'panel-title'
                    },
                    {
                        type: 'ood.UI.CheckBox',
                        id: 'autoRefresh',
                        label: '自动刷新',
                        checked: true,
                        onChange: 'onAutoRefreshChange'
                    }
                ]
            },
            {
                type: 'ood.UI.TreeGrid',
                id: 'taskTable',
                columns: [
                    { field: 'taskId', title: '任务ID', width: 150 },
                    { field: 'skillId', title: 'Skill', width: 120 },
                    { field: 'status', title: '状态', width: 80, renderer: 'statusRenderer' },
                    { field: 'progress', title: '进度', width: 120, renderer: 'progressRenderer' },
                    { field: 'submitTime', title: '提交时间', width: 150 },
                    { field: 'actions', title: '操作', width: 100, renderer: 'actionRenderer' }
                ],
                onRowClick: 'onTaskSelect'
            },
            {
                type: 'ood.UI.ECharts',
                id: 'timelineChart',
                height: 200
            }
        ]
    },
    
    Methods: {
        
        init: function() {
            this.loadTasks();
            if (this.get('autoRefresh')) {
                this.startAutoRefresh();
            }
        },
        
        // 加载任务
        loadTasks: function() {
            var self = this;
            
            CLI.AppService.queryTasks({ status: 'RUNNING' })
                .then(function(tasks) {
                    self.set('tasks', tasks);
                    self.renderTaskTable();
                    self.renderTimeline();
                });
        },
        
        // 渲染任务表格
        renderTaskTable: function() {
            var table = this.getChild('taskTable');
            var tasks = this.get('tasks');
            
            table.loadData(tasks);
        },
        
        // 状态渲染器
        statusRenderer: function(value) {
            var colors = {
                'PENDING': '#FFC107',
                'RUNNING': '#2196F3',
                'COMPLETED': '#4CAF50',
                'FAILED': '#F44336',
                'CANCELLED': '#9E9E9E'
            };
            
            return '<span style="color:' + colors[value] + '">' + value + '</span>';
        },
        
        // 进度渲染器
        progressRenderer: function(value, row) {
            var progress = row.progress || 0;
            return ood.create('ood.UI.ProgressBar', {
                value: progress,
                height: 16
            });
        },
        
        // 操作渲染器
        actionRenderer: function(value, row) {
            var self = this;
            var actions = [];
            
            if (row.status === 'RUNNING') {
                actions.push({
                    label: '取消',
                    handler: function() {
                        self.cancelTask(row.taskId);
                    }
                });
            }
            
            actions.push({
                label: '日志',
                handler: function() {
                    self.showLogs(row.taskId);
                }
            });
            
            return actions;
        },
        
        // 渲染时间线
        renderTimeline: function() {
            var chart = this.getChild('timelineChart');
            var tasks = this.get('tasks');
            
            var option = {
                title: { text: '任务执行时间线' },
                xAxis: { type: 'time' },
                yAxis: { type: 'category', data: tasks.map(function(t) { return t.taskId; }) },
                series: [{
                    type: 'custom',
                    renderItem: function(params, api) {
                        // 自定义渲染任务条
                    },
                    data: tasks.map(function(t) {
                        return {
                            name: t.taskId,
                            value: [
                                t.submitTime,
                                t.endTime || new Date(),
                                t.status
                            ]
                        };
                    })
                }]
            };
            
            chart.setOption(option);
        },
        
        // 选择任务
        onTaskSelect: function(row) {
            this.set('selectedTask', row.taskId);
            this.fireEvent('taskSelected', row);
        },
        
        // 取消任务
        cancelTask: function(taskId) {
            var self = this;
            
            CLI.AppService.cancelTask(taskId)
                .then(function() {
                    self.loadTasks();
                });
        },
        
        // 显示日志
        showLogs: function(taskId) {
            this.fireEvent('taskAction', taskId, 'viewLogs');
        },
        
        // 自动刷新
        startAutoRefresh: function() {
            var self = this;
            this.refreshInterval = setInterval(function() {
                self.loadTasks();
                self.fireEvent('refresh');
            }, 3000);
        },
        
        // 停止自动刷新
        stopAutoRefresh: function() {
            if (this.refreshInterval) {
                clearInterval(this.refreshInterval);
            }
        },
        
        // 自动刷新变更
        onAutoRefreshChange: function(checked) {
            this.set('autoRefresh', checked);
            if (checked) {
                this.startAutoRefresh();
            } else {
                this.stopAutoRefresh();
            }
        },
        
        // 销毁
        destroy: function() {
            this.stopAutoRefresh();
            this.base();
        }
    }
});
```

## 4. Web Dashboard 实现

```html
<!-- cli-dashboard.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Agent SDK CLI Dashboard</title>
    <link rel="stylesheet" href="ooder-ui.css">
    <script src="ooder-core.js"></script>
    <script src="ooder-ui.js"></script>
    <script src="cli-components.js"></script>
    <style>
        .cli-dashboard {
            display: flex;
            height: 100vh;
        }
        .cli-sidebar {
            width: 250px;
            background: #1a1a2e;
            color: #fff;
            padding: 20px;
        }
        .cli-main {
            flex: 1;
            display: flex;
            flex-direction: column;
            background: #f5f5f5;
        }
        .cli-header {
            height: 60px;
            background: #fff;
            border-bottom: 1px solid #ddd;
            display: flex;
            align-items: center;
            padding: 0 20px;
        }
        .cli-content {
            flex: 1;
            padding: 20px;
            overflow: auto;
        }
        .cli-footer {
            height: 40px;
            background: #fff;
            border-top: 1px solid #ddd;
            display: flex;
            align-items: center;
            padding: 0 20px;
            font-size: 12px;
            color: #666;
        }
    </style>
</head>
<body>
    <div id="dashboard" class="cli-dashboard"></div>
    
    <script>
        // 初始化 Dashboard
        ood.ready(function() {
            var dashboard = ood.create('CLI.Dashboard', {
                renderTo: 'dashboard'
            });
        });
        
        // Dashboard 组件
        ood.Class('CLI.Dashboard', 'ood.UI.Block', {
            
            Template: {
                class: 'cli-dashboard',
                children: [
                    {
                        type: 'ood.UI.Block',
                        class: 'cli-sidebar',
                        children: [
                            {
                                type: 'ood.UI.Label',
                                text: 'CLI Dashboard',
                                class: 'sidebar-title',
                                style: { 'font-size': '20px', 'font-weight': 'bold', 'margin-bottom': '20px' }
                            },
                            {
                                type: 'ood.UI.TreeBar',
                                id: 'navMenu',
                                items: [
                                    { id: 'skills', text: 'Skills', icon: 'skill' },
                                    { id: 'scenes', text: 'Scenes', icon: 'scene' },
                                    { id: 'tasks', text: 'Tasks', icon: 'task' },
                                    { id: 'logs', text: 'Logs', icon: 'log' },
                                    { id: 'config', text: 'Config', icon: 'config' }
                                ],
                                onSelect: 'onNavSelect'
                            }
                        ]
                    },
                    {
                        type: 'ood.UI.Block',
                        class: 'cli-main',
                        children: [
                            {
                                type: 'ood.UI.Block',
                                class: 'cli-header',
                                children: [
                                    {
                                        type: 'ood.UI.Label',
                                        id: 'pageTitle',
                                        text: 'Skills',
                                        style: { 'font-size': '18px', 'font-weight': 'bold' }
                                    }
                                ]
                            },
                            {
                                type: 'ood.UI.Block',
                                id: 'contentArea',
                                class: 'cli-content'
                            },
                            {
                                type: 'ood.UI.Block',
                                class: 'cli-footer',
                                children: [
                                    {
                                        type: 'ood.UI.Label',
                                        id: 'statusBar',
                                        text: 'Ready'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            },
            
            Methods: {
                
                init: function() {
                    // 默认显示 Skills 页面
                    this.showSkillsPage();
                },
                
                // 导航选择
                onNavSelect: function(item) {
                    var contentArea = this.getChild('contentArea');
                    contentArea.clear();
                    
                    this.getChild('pageTitle').setText(item.text);
                    
                    switch(item.id) {
                        case 'skills':
                            this.showSkillsPage();
                            break;
                        case 'scenes':
                            this.showScenesPage();
                            break;
                        case 'tasks':
                            this.showTasksPage();
                            break;
                        case 'logs':
                            this.showLogsPage();
                            break;
                        case 'config':
                            this.showConfigPage();
                            break;
                    }
                },
                
                // Skills 页面
                showSkillsPage: function() {
                    var contentArea = this.getChild('contentArea');
                    
                    var skillPanel = ood.create('CLI.SkillPanel', {
                        onSkillSelected: function(skill) {
                            console.log('Selected skill:', skill);
                        },
                        onSkillAction: function(skillId, action) {
                            console.log('Skill action:', skillId, action);
                        }
                    });
                    
                    contentArea.add(skillPanel);
                },
                
                // Scenes 页面
                showScenesPage: function() {
                    var contentArea = this.getChild('contentArea');
                    
                    // 场景列表 + 画布
                    var sceneList = ood.create('ood.UI.List', {
                        dataUrl: '/api/scenes',
                        onSelect: function(scene) {
                            canvas.loadScene(scene.sceneId);
                        }
                    });
                    
                    var canvas = ood.create('CLI.SceneCanvas', {
                        width: '100%',
                        height: 500
                    });
                    
                    contentArea.add(sceneList);
                    contentArea.add(canvas);
                },
                
                // Tasks 页面
                showTasksPage: function() {
                    var contentArea = this.getChild('contentArea');
                    
                    var taskMonitor = ood.create('CLI.TaskMonitor', {
                        autoRefresh: true
                    });
                    
                    contentArea.add(taskMonitor);
                },
                
                // Logs 页面
                showLogsPage: function() {
                    var contentArea = this.getChild('contentArea');
                    
                    var logViewer = ood.create('CLI.LogViewer', {
                        taskId: null  // 显示所有日志
                    });
                    
                    contentArea.add(logViewer);
                },
                
                // Config 页面
                showConfigPage: function() {
                    var contentArea = this.getChild('contentArea');
                    
                    var configEditor = ood.create('CLI.ConfigEditor', {
                        configKey: 'cli'
                    });
                    
                    contentArea.add(configEditor);
                }
            }
        });
    </script>
</body>
</html>
```

## 5. 数据流设计

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           数据流架构                                         │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌──────────────┐     WebSocket      ┌──────────────┐
  │  CLI Backend │ ◄────────────────► │   Frontend   │
  │              │    实时推送事件     │              │
  └──────────────┘                    └──────────────┘
         │                                   │
         │ REST API                          │ State Update
         ▼                                   ▼
  ┌──────────────┐                    ┌──────────────┐
  │  App Service │                    │   ViewModel  │
  │              │                    │              │
  │ - SkillApp   │ ──Data Transform──►│ - Observable │
  │ - SceneApp   │                    │ - Computed   │
  │ - TaskApp    │                    │ - Actions    │
  └──────────────┘                    └──────────────┘
                                             │
                                             ▼
                                      ┌──────────────┐
                                      │   UI Comp    │
                                      │              │
                                      │ - Props      │
                                      │ - Events     │
                                      │ - Render     │
                                      └──────────────┘
```

## 6. 组件清单

| 组件名 | 类型 | 功能描述 | 依赖 |
|--------|------|----------|------|
| CLI.SkillPanel | Block | Skill 管理面板 | ood.UI.InfoBlock, ood.UI.Input |
| CLI.SceneCanvas | SVGPaper | 场景拓扑图 | ood.svg.* |
| CLI.TaskMonitor | Block | 任务监控面板 | ood.UI.TreeGrid, ood.UI.ECharts |
| CLI.LogViewer | Block | 日志查看器 | ood.UI.List, ood.UI.Input |
| CLI.CommandInput | FormLayout | 命令输入 | ood.UI.ComboInput |
| CLI.ResultPanel | Block | 结果展示 | ood.UI.TreeGrid, ood.UI.TreeView |
| CLI.ConfigEditor | FormLayout | 配置编辑 | ood.UI.FormLayout |
| CLI.StatisticsChart | Block | 统计图表 | ood.UI.FusionChartsXT |
| CLI.Dashboard | Block | 主仪表盘 | 所有上述组件 |

---

**文档版本**: 3.1.0  
**最后更新**: 2026-04-16  
**维护团队**: Agent SDK Team

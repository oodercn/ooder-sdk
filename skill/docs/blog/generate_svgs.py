#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
为 SceneEngine 3.0.3 CLI 支持设计博客生成 SVG 图表
"""

import os

# SVG 颜色方案
COLORS = {
    'bg': '#fafafa',
    'box': '#ffffff',
    'box_stroke': '#2563eb',
    'box_alt': '#dbeafe',
    'box_alt_stroke': '#1e40af',
    'box_highlight': '#fef3c7',
    'box_highlight_stroke': '#d97706',
    'title_box': '#2563eb',
    'arrow': '#64748b',
    'text': '#334155',
    'text_title': '#1e40af',
    'text_header': '#ffffff',
    'text_small': '#64748b'
}

def create_svg_header(width, height):
    """创建 SVG 头部"""
    return f'''<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}px" height="{height}px">
<defs>
    <style>
        .bg {{ fill: {COLORS['bg']}; }}
        .box {{ fill: {COLORS['box']}; stroke: {COLORS['box_stroke']}; stroke-width: 2; rx: 8; }}
        .box-alt {{ fill: {COLORS['box_alt']}; stroke: {COLORS['box_alt_stroke']}; stroke-width: 2; rx: 8; }}
        .box-highlight {{ fill: {COLORS['box_highlight']}; stroke: {COLORS['box_highlight_stroke']}; stroke-width: 2; rx: 8; }}
        .title-box {{ fill: {COLORS['title_box']}; stroke: none; rx: 4; }}
        .arrow {{ fill: none; stroke: {COLORS['arrow']}; stroke-width: 2; marker-end: url(#arrowhead); }}
        .text {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 13px; fill: {COLORS['text']}; }}
        .text-title {{ font-size: 15px; font-weight: bold; fill: {COLORS['text_title']}; }}
        .text-header {{ font-size: 16px; font-weight: bold; fill: {COLORS['text_header']}; }}
        .text-small {{ font-size: 11px; fill: {COLORS['text_small']}; }}
    </style>
    <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
        <polygon points="0 0, 10 3.5, 0 7" fill="{COLORS['arrow']}"/>
    </marker>
</defs>
<rect class="bg" width="100%" height="100%"/>
'''

def create_svg_footer():
    """创建 SVG 尾部"""
    return "</svg>"

def generate_three_layer_architecture():
    """生成三层架构图"""
    width, height = 800, 490
    svg = create_svg_header(width, height)
    
    # 标题
    svg += f'<rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>\n'
    svg += f'<text class="text-header" x="400" y="38" text-anchor="middle">三层架构设计</text>\n\n'
    
    # 用户交互层
    y1 = 70
    svg += f'<rect class="box" x="30" y="{y1}" width="740" height="90"/>\n'
    svg += f'<text class="text-title" x="50" y="{y1 + 25}">用户交互层 (Interaction Layer)</text>\n'
    svg += f'<text class="text" x="70" y="{y1 + 50}">CLI (命令行)</text>\n'
    svg += f'<text class="text" x="220" y="{y1 + 50}">llm-chat (智能对话)</text>\n'
    svg += f'<text class="text" x="390" y="{y1 + 50}">Web UI (Web 界面)</text>\n'
    svg += f'<text class="text" x="560" y="{y1 + 50}">API (开放接口)</text>\n'
    
    # 箭头 1
    svg += f'<line class="arrow" x1="400" y1="{y1 + 90}" x2="400" y2="{y1 + 110}"/>\n\n'
    
    # Agent SDK 协议层
    y2 = y1 + 120
    svg += f'<rect class="box-alt" x="30" y="{y2}" width="740" height="110"/>\n'
    svg += f'<text class="text-title" x="50" y="{y2 + 25}">Agent SDK 协议层 (Protocol Layer)</text>\n'
    svg += f'<text class="text" x="70" y="{y2 + 50}">• Command Registry (命令注册中心)</text>\n'
    svg += f'<text class="text" x="70" y="{y2 + 70}">• Command Executor (命令执行器)</text>\n'
    svg += f'<text class="text" x="400" y="{y2 + 50}">• Task Queue (任务队列)</text>\n'
    svg += f'<text class="text" x="400" y="{y2 + 70}">• Messaging Service (消息服务)</text>\n'
    
    # 箭头 2
    svg += f'<line class="arrow" x1="400" y1="{y2 + 110}" x2="400" y2="{y2 + 130}"/>\n\n'
    
    # SceneEngine 场景层
    y3 = y2 + 140
    svg += f'<rect class="box-highlight" x="30" y="{y3}" width="740" height="110"/>\n'
    svg += f'<text class="text-title" x="50" y="{y3 + 25}">SceneEngine 场景层 (Orchestration Layer)</text>\n'
    svg += f'<text class="text" x="70" y="{y3 + 50}">• 场景管理 (有状态)</text>\n'
    svg += f'<text class="text" x="250" y="{y3 + 50}">• 编排协调 (多 Skill)</text>\n'
    svg += f'<text class="text" x="450" y="{y3 + 50}">• 事件驱动 (发布订阅)</text>\n'
    svg += f'<text class="text" x="70" y="{y3 + 70}">• SceneContextApi</text>\n'
    svg += f'<text class="text" x="250" y="{y3 + 70}">• CapabilityBindingService</text>\n'
    svg += f'<text class="text" x="450" y="{y3 + 70}">• SceneEventBus</text>\n'
    
    # 箭头 3
    svg += f'<line class="arrow" x1="400" y1="{y3 + 110}" x2="400" y2="{y3 + 130}"/>\n\n'
    
    # Skill 运行时层
    y4 = y3 + 140
    svg += f'<text class="text-small" x="400" y="{y4}" text-anchor="middle">Skill 运行时层 (Plugin Runtime)</text>\n'
    
    svg += create_svg_footer()
    
    return svg

def generate_async_task_flow():
    """生成异步任务执行流程图"""
    width, height = 800, 580
    svg = create_svg_header(width, height)
    
    # 标题
    svg += f'<rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>\n'
    svg += f'<text class="text-header" x="400" y="38" text-anchor="middle">异步任务执行流程</text>\n\n'
    
    # 步骤 1: 任务提交
    y1 = 70
    svg += f'<rect class="box" x="30" y="{y1}" width="740" height="50"/>\n'
    svg += f'<text class="text-title" x="50" y="{y1 + 20}">1. 任务提交</text>\n'
    svg += f'<text class="text" x="70" y="{y1 + 40}">CLI/Chat → skill exec rag-skill reindex --knowledgeBase=docs</text>\n'
    
    # 箭头 1
    svg += f'<line class="arrow" x1="400" y1="{y1 + 50}" x2="400" y2="{y1 + 70}"/>\n\n'
    
    # 步骤 2: 命令转换
    y2 = y1 + 80
    svg += f'<rect class="box" x="30" y="{y2}" width="740" height="70"/>\n'
    svg += f'<text class="text-title" x="50" y="{y2 + 20}">2. 命令转换</text>\n'
    svg += f'<text class="text" x="70" y="{y2 + 45}">CLI Command → Agent SDK Command</text>\n'
    svg += f'<text class="text-small" x="70" y="{y2 + 62}">commandId: "rag-skill:reindex", type: ASYNC, parameters: {{knowledgeBase: "docs"}}</text>\n'
    
    # 箭头 2
    svg += f'<line class="arrow" x1="400" y1="{y2 + 70}" x2="400" y2="{y2 + 90}"/>\n\n'
    
    # 步骤 3: 任务入队
    y3 = y2 + 100
    svg += f'<rect class="box-alt" x="30" y="{y3}" width="740" height="90"/>\n'
    svg += f'<text class="text-title" x="50" y="{y3 + 20}">3. 任务入队 (Task Queue)</text>\n'
    svg += f'<text class="text" x="70" y="{y3 + 45}">Task ID: task-abc-123</text>\n'
    svg += f'<text class="text" x="250" y="{y3 + 45}">Status: PENDING</text>\n'
    svg += f'<text class="text" x="450" y="{y3 + 45}">Priority: NORMAL</text>\n'
    svg += f'<text class="text" x="70" y="{y3 + 65}">Created: 2024-01-15T10:30:00Z</text>\n'
    svg += f'<text class="text" x="250" y="{y3 + 65}">Command: rag-skill:reindex</text>\n'
    
    # 箭头 3
    svg += f'<line class="arrow" x1="400" y1="{y3 + 90}" x2="400" y2="{y3 + 110}"/>\n\n'
    
    # 步骤 4: 异步执行
    y4 = y3 + 120
    svg += f'<rect class="box-highlight" x="30" y="{y4}" width="740" height="90"/>\n'
    svg += f'<text class="text-title" x="50" y="{y4 + 20}">4. 异步执行 (Task Worker)</text>\n'
    svg += f'<text class="text" x="70" y="{y4 + 45}">1. Dequeue task</text>\n'
    svg += f'<text class="text" x="250" y="{y4 + 45}">2. Update status: RUNNING</text>\n'
    svg += f'<text class="text" x="450" y="{y4 + 45}">3. Execute Command.execute()</text>\n'
    svg += f'<text class="text" x="70" y="{y4 + 65}">4. Update status: COMPLETED/FAILED</text>\n'
    svg += f'<text class="text" x="350" y="{y4 + 65}">5. Notify listeners</text>\n'
    
    # 箭头 4
    svg += f'<line class="arrow" x1="400" y1="{y4 + 90}" x2="400" y2="{y4 + 110}"/>\n\n'
    
    # 步骤 5: 状态查询
    y5 = y4 + 120
    svg += f'<rect class="box" x="30" y="{y5}" width="740" height="70"/>\n'
    svg += f'<text class="text-title" x="50" y="{y5 + 20}">5. 状态查询</text>\n'
    svg += f'<text class="text" x="70" y="{y5 + 45}">CLI/Chat → skill task status task-abc-123</text>\n'
    svg += f'<text class="text-small" x="70" y="{y5 + 62}">Result: {{taskId: "task-abc-123", status: "COMPLETED", result: {{documentsIndexed: 1500}}}}</text>\n'
    
    svg += create_svg_footer()
    
    return svg

def generate_multi_active_deployment():
    """生成多活部署架构图"""
    width, height = 800, 490
    svg = create_svg_header(width, height)
    
    # 标题
    svg += f'<rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>\n'
    svg += f'<text class="text-header" x="400" y="38" text-anchor="middle">多活部署架构</text>\n\n'
    
    # Load Balancer
    y1 = 70
    svg += f'<rect class="box-alt" x="250" y="{y1}" width="300" height="50"/>\n'
    svg += f'<text class="text-title" x="400" y="{y1 + 20}" text-anchor="middle">Load Balancer</text>\n'
    svg += f'<text class="text-small" x="400" y="{y1 + 38}" text-anchor="middle">(Round Robin)</text>\n'
    
    # 箭头
    svg += f'<line class="arrow" x1="400" y1="{y1 + 50}" x2="400" y2="{y1 + 70}"/>\n\n'
    
    # Node 1, 2, 3
    y2 = y1 + 80
    node_width = 220
    node_height = 90
    spacing = 30
    
    # Node 1
    x1 = 50
    svg += f'<rect class="box" x="{x1}" y="{y2}" width="{node_width}" height="{node_height}"/>\n'
    svg += f'<text class="text-title" x="{x1 + 110}" y="{y2 + 20}" text-anchor="middle">Node 1</text>\n'
    svg += f'<text class="text" x="{x1 + 110}" y="{y2 + 45}" text-anchor="middle">Agent SDK</text>\n'
    svg += f'<text class="text-small" x="{x1 + 110}" y="{y2 + 65}" text-anchor="middle">Instance 1</text>\n'
    
    # Node 2
    x2 = 290
    svg += f'<rect class="box" x="{x2}" y="{y2}" width="{node_width}" height="{node_height}"/>\n'
    svg += f'<text class="text-title" x="{x2 + 110}" y="{y2 + 20}" text-anchor="middle">Node 2</text>\n'
    svg += f'<text class="text" x="{x2 + 110}" y="{y2 + 45}" text-anchor="middle">Agent SDK</text>\n'
    svg += f'<text class="text-small" x="{x2 + 110}" y="{y2 + 65}" text-anchor="middle">Instance 2</text>\n'
    
    # Node 3
    x3 = 530
    svg += f'<rect class="box" x="{x3}" y="{y2}" width="{node_width}" height="{node_height}"/>\n'
    svg += f'<text class="text-title" x="{x3 + 110}" y="{y2 + 20}" text-anchor="middle">Node 3</text>\n'
    svg += f'<text class="text" x="{x3 + 110}" y="{y2 + 45}" text-anchor="middle">Agent SDK</text>\n'
    svg += f'<text class="text-small" x="{x3 + 110}" y="{y2 + 65}" text-anchor="middle">Instance 3</text>\n'
    
    # 箭头到共享存储
    svg += f'<line class="arrow" x1="400" y1="{y2 + 90}" x2="400" y2="{y2 + 120}"/>\n\n'
    
    # Shared Storage
    y3 = y2 + 130
    svg += f'<rect class="box-highlight" x="30" y="{y3}" width="740" height="120"/>\n'
    svg += f'<text class="text-title" x="400" y="{y3 + 25}" text-anchor="middle">Shared Storage</text>\n'
    
    # Task Queue
    svg += f'<rect class="box-alt" x="50" y="{y3 + 35}" width="200" height="70"/>\n'
    svg += f'<text class="text" x="150" y="{y3 + 55}" text-anchor="middle">Task Queue</text>\n'
    svg += f'<text class="text-small" x="150" y="{y3 + 75}" text-anchor="middle">(Redis)</text>\n'
    
    # Command Registry
    svg += f'<rect class="box-alt" x="300" y="{y3 + 35}" width="200" height="70"/>\n'
    svg += f'<text class="text" x="400" y="{y3 + 55}" text-anchor="middle">Command Registry</text>\n'
    svg += f'<text class="text-small" x="400" y="{y3 + 75}" text-anchor="middle">(Database)</text>\n'
    
    # Audit Log
    svg += f'<rect class="box-alt" x="550" y="{y3 + 35}" width="200" height="70"/>\n'
    svg += f'<text class="text" x="650" y="{y3 + 55}" text-anchor="middle">Audit Log</text>\n'
    svg += f'<text class="text-small" x="650" y="{y3 + 75}" text-anchor="middle">(Elasticsearch)</text>\n'
    
    # 关键特性
    y4 = y3 + 130
    svg += f'<text class="text-small" x="400" y="{y4}" text-anchor="middle">关键特性：无状态 · 共享存储 · 负载均衡 · 故障转移</text>\n'
    
    svg += create_svg_footer()
    
    return svg

def main():
    """主函数"""
    output_dir = os.path.dirname(os.path.abspath(__file__))
    
    # 生成 SVG 文件
    svgs = {
        'diagram_three_layer_architecture.svg': generate_three_layer_architecture(),
        'diagram_async_task_flow.svg': generate_async_task_flow(),
        'diagram_multi_active_deployment.svg': generate_multi_active_deployment()
    }
    
    for filename, content in svgs.items():
        filepath = os.path.join(output_dir, filename)
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"✓ Generated: {filepath}")
    
    print(f"\n所有 SVG 文件已生成到：{output_dir}")

if __name__ == '__main__':
    main()

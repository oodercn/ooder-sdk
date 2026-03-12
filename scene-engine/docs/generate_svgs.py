#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成SVG图表脚本 - 将ASCII流程图转换为SVG格式
适用于公众号等富文本环境
"""

import os

# SVG模板基础
SVG_HEADER = '''<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width} {height}" width="{width}px" height="{height}px">
<defs>
    <style>
        .bg {{ fill: #fafafa; }}
        .box {{ fill: #ffffff; stroke: #2563eb; stroke-width: 2; rx: 8; }}
        .box-alt {{ fill: #dbeafe; stroke: #1e40af; stroke-width: 2; rx: 8; }}
        .box-highlight {{ fill: #fef3c7; stroke: #d97706; stroke-width: 2; rx: 8; }}
        .title-box {{ fill: #2563eb; stroke: none; rx: 4; }}
        .arrow {{ fill: none; stroke: #64748b; stroke-width: 2; marker-end: url(#arrowhead); }}
        .arrow-highlight {{ fill: none; stroke: #2563eb; stroke-width: 2; marker-end: url(#arrowhead-blue); }}
        .text {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 13px; fill: #334155; }}
        .text-title {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 15px; font-weight: bold; fill: #1e40af; }}
        .text-header {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 16px; font-weight: bold; fill: #ffffff; }}
        .text-small {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 11px; fill: #64748b; }}
        .text-white {{ font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; font-size: 13px; fill: #ffffff; }}
        .divider {{ stroke: #e2e8f0; stroke-width: 1; }}
        .loop-arrow {{ fill: none; stroke: #2563eb; stroke-width: 2; stroke-dasharray: 5,3; }}
    </style>
    <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
        <polygon points="0 0, 10 3.5, 0 7" fill="#64748b"/>
    </marker>
    <marker id="arrowhead-blue" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
        <polygon points="0 0, 10 3.5, 0 7" fill="#2563eb"/>
    </marker>
</defs>
<rect class="bg" width="100%" height="100%"/>
'''

SVG_FOOTER = '</svg>'

def save_svg(filename, content, width, height):
    """保存SVG文件"""
    svg_content = SVG_HEADER.format(width=width, height=height) + content + SVG_FOOTER
    filepath = os.path.join(os.path.dirname(__file__), filename)
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(svg_content)
    print(f"Generated: {filepath}")
    return filepath

# ========== 图表1: 三层协作模型 ==========
def generate_three_layer_model():
    """生成三层协作模型SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="660" height="35" rx="4"/>
    <text class="text-header" x="350" y="38" text-anchor="middle">三层协作模型</text>
    
    <!-- 第一层：技能定义层 -->
    <rect class="box" x="30" y="70" width="640" height="130"/>
    <text class="text-title" x="50" y="95">第一层：技能定义层 (Skill Definition Layer)</text>
    
    <rect class="box-alt" x="50" y="105" width="120" height="35" rx="6"/>
    <text class="text" x="110" y="127" text-anchor="middle">开发者</text>
    
    <line class="arrow" x1="170" y1="122" x2="210" y2="122"/>
    
    <rect class="box" x="210" y="105" width="180" height="80" rx="6"/>
    <text class="text" x="300" y="125" text-anchor="middle" font-weight="bold">开发新技能</text>
    <text class="text-small" x="300" y="145" text-anchor="middle">• 定义 Skill 接口</text>
    <text class="text-small" x="300" y="160" text-anchor="middle">• 实现 Capability</text>
    <text class="text-small" x="300" y="175" text-anchor="middle">• 编写单元测试</text>
    
    <line class="arrow" x1="390" y1="145" x2="430" y2="145"/>
    
    <rect class="box-alt" x="430" y="115" width="220" height="60" rx="6"/>
    <text class="text" x="540" y="140" text-anchor="middle" font-weight="bold">Skill Registry</text>
    <text class="text-small" x="540" y="160" text-anchor="middle">技能注册中心</text>
    
    <!-- 连接箭头 -->
    <line class="arrow" x1="350" y1="200" x2="350" y2="230"/>
    
    <!-- 第二层：知识贡献层 -->
    <rect class="box" x="30" y="230" width="640" height="130"/>
    <text class="text-title" x="50" y="255">第二层：知识贡献层 (Knowledge Contribution Layer)</text>
    
    <rect class="box-alt" x="50" y="265" width="120" height="35" rx="6"/>
    <text class="text" x="110" y="287" text-anchor="middle">终端用户</text>
    
    <line class="arrow" x1="170" y1="282" x2="210" y2="282"/>
    
    <rect class="box" x="210" y="265" width="180" height="80" rx="6"/>
    <text class="text" x="300" y="285" text-anchor="middle" font-weight="bold">贡献领域知识</text>
    <text class="text-small" x="300" y="305" text-anchor="middle">• 上传业务文档</text>
    <text class="text-small" x="300" y="320" text-anchor="middle">• 输入文本知识</text>
    <text class="text-small" x="300" y="335" text-anchor="middle">• 导入外部资源</text>
    
    <line class="arrow" x1="390" y1="305" x2="430" y2="305"/>
    
    <rect class="box-alt" x="430" y="265" width="220" height="80" rx="6"/>
    <text class="text" x="540" y="290" text-anchor="middle" font-weight="bold">Knowledge Base</text>
    <text class="text-small" x="540" y="310" text-anchor="middle">• 自动分块</text>
    <text class="text-small" x="540" y="325" text-anchor="middle">• 向量化</text>
    <text class="text-small" x="540" y="340" text-anchor="middle">• 索引构建</text>
    
    <!-- 连接箭头 -->
    <line class="arrow" x1="350" y1="360" x2="350" y2="390"/>
    
    <!-- 第三层：运维管理层 -->
    <rect class="box" x="30" y="390" width="640" height="100"/>
    <text class="text-title" x="50" y="415">第三层：运维管理层 (Operations Management Layer)</text>
    
    <rect class="box-alt" x="50" y="425" width="120" height="35" rx="6"/>
    <text class="text" x="110" y="447" text-anchor="middle">运维人员</text>
    
    <line class="arrow" x1="170" y1="442" x2="210" y2="442"/>
    
    <rect class="box" x="210" y="425" width="180" height="50" rx="6"/>
    <text class="text" x="300" y="445" text-anchor="middle" font-weight="bold">配置与监控</text>
    <text class="text-small" x="300" y="462" text-anchor="middle">配置参数 · 监控状态 · 管理权限</text>
    
    <line class="arrow" x1="390" y1="450" x2="430" y2="450"/>
    
    <rect class="box-alt" x="430" y="425" width="220" height="50" rx="6"/>
    <text class="text" x="540" y="455" text-anchor="middle" font-weight="bold">PermissionEngine</text>
'''
    return save_svg('diagram_three_layer.svg', svg, 700, 510)

# ========== 图表2: Agent用户模型架构 ==========
def generate_agent_user_model():
    """生成Agent用户模型架构SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
    <text class="text-header" x="400" y="38" text-anchor="middle">Agent 用户模型架构</text>
    
    <!-- 人类用户层 -->
    <rect class="box" x="30" y="70" width="740" height="90"/>
    <text class="text-title" x="50" y="95">人类用户层</text>
    
    <rect class="box-highlight" x="50" y="105" width="200" height="40" rx="6"/>
    <text class="text" x="150" y="125" text-anchor="middle" font-weight="bold">User: alice@company.com</text>
    <text class="text-small" x="150" y="140" text-anchor="middle">Role: HR_MANAGER</text>
    
    <text class="text" x="320" y="130">操作：创建 Agent、分配角色、设置配额、查看审计日志</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="160" x2="400" y2="190"/>
    <text class="text-small" x="430" y="180">创建/管理</text>
    
    <!-- Agent用户层 -->
    <rect class="box" x="30" y="190" width="740" height="140"/>
    <text class="text-title" x="50" y="215">Agent 用户层</text>
    
    <!-- Agent 1 -->
    <rect class="box-alt" x="50" y="230" width="210" height="85" rx="6"/>
    <text class="text" x="155" y="250" text-anchor="middle" font-weight="bold">HR Agent</text>
    <text class="text-small" x="155" y="268" text-anchor="middle">userId: agent_001</text>
    <text class="text-small" x="155" y="283" text-anchor="middle">Role: HR</text>
    <text class="text-small" x="155" y="298" text-anchor="middle">Quota: 1000/天</text>
    
    <!-- Agent 2 -->
    <rect class="box-alt" x="295" y="230" width="210" height="85" rx="6"/>
    <text class="text" x="400" y="250" text-anchor="middle" font-weight="bold">面试官 Agent</text>
    <text class="text-small" x="400" y="268" text-anchor="middle">userId: agent_002</text>
    <text class="text-small" x="400" y="283" text-anchor="middle">Role: INTERVIEWER</text>
    <text class="text-small" x="400" y="298" text-anchor="middle">Quota: 500/天</text>
    
    <!-- Agent 3 -->
    <rect class="box-alt" x="540" y="230" width="210" height="85" rx="6"/>
    <text class="text" x="645" y="250" text-anchor="middle" font-weight="bold">助理 Agent</text>
    <text class="text-small" x="645" y="268" text-anchor="middle">userId: agent_003</text>
    <text class="text-small" x="645" y="283" text-anchor="middle">Role: ASSISTANT</text>
    <text class="text-small" x="645" y="298" text-anchor="middle">Quota: 200/天</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="155" y1="330" x2="155" y2="360"/>
    <line class="arrow" x1="400" y1="330" x2="400" y2="360"/>
    <line class="arrow" x1="645" y1="330" x2="645" y2="360"/>
    
    <!-- 权限与资源层 -->
    <rect class="box" x="30" y="360" width="740" height="130"/>
    <text class="text-title" x="50" y="385">权限与资源层</text>
    
    <rect class="box-alt" x="50" y="400" width="700" height="75" rx="6"/>
    <text class="text" x="70" y="425" font-weight="bold">PermissionEngine.calculateDataScope(agentUserId, skillId)</text>
    
    <text class="text-small" x="70" y="450">• 知识库访问权限（基于角色和数据范围）</text>
    <text class="text-small" x="70" y="465">• LLM 调用配额（基于 agentType 和配置）</text>
    
    <text class="text-small" x="400" y="450">• 对话历史存储（独立隔离，支持长期记忆）</text>
    <text class="text-small" x="400" y="465">• 审计日志（记录所有操作，支持溯源）</text>
'''
    return save_svg('diagram_agent_user_model.svg', svg, 800, 510)

# ========== 图表3: 数据飞轮模型 ==========
def generate_data_flywheel():
    """生成数据飞轮模型SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
    <text class="text-header" x="400" y="38" text-anchor="middle">数据飞轮模型</text>
    
    <!-- 用户使用 Skill -->
    <rect class="box" x="50" y="70" width="140" height="100" rx="8"/>
    <text class="text" x="120" y="95" text-anchor="middle" font-weight="bold">用户使用</text>
    <text class="text" x="120" y="115" text-anchor="middle" font-weight="bold">Skill</text>
    <text class="text-small" x="120" y="140" text-anchor="middle">触发点：</text>
    <text class="text-small" x="120" y="155" text-anchor="middle">提问 · 调用能力</text>
    
    <line class="arrow" x1="190" y1="120" x2="240" y2="120"/>
    
    <!-- 数据收集 -->
    <rect class="box" x="240" y="70" width="140" height="100" rx="8"/>
    <text class="text" x="310" y="95" text-anchor="middle" font-weight="bold">数据收集</text>
    <text class="text" x="310" y="115" text-anchor="middle" font-weight="bold">AuditLog</text>
    <text class="text-small" x="310" y="140" text-anchor="middle">查询内容 · 响应结果</text>
    <text class="text-small" x="310" y="155" text-anchor="middle">执行时长</text>
    
    <line class="arrow" x1="380" y1="120" x2="430" y2="120"/>
    
    <!-- 知识贡献 -->
    <rect class="box" x="430" y="70" width="140" height="100" rx="8"/>
    <text class="text" x="500" y="95" text-anchor="middle" font-weight="bold">知识贡献</text>
    <text class="text" x="500" y="115" text-anchor="middle" font-weight="bold">Contribution</text>
    <text class="text-small" x="500" y="140" text-anchor="middle">上传文档 · 输入文本</text>
    <text class="text-small" x="500" y="155" text-anchor="middle">URL导入</text>
    
    <line class="arrow" x1="570" y1="120" x2="620" y2="120"/>
    
    <!-- 知识库更新 -->
    <rect class="box-alt" x="620" y="70" width="140" height="100" rx="8"/>
    <text class="text" x="690" y="95" text-anchor="middle" font-weight="bold">知识库更新</text>
    <text class="text-small" x="690" y="120" text-anchor="middle">新增文档</text>
    <text class="text-small" x="690" y="140" text-anchor="middle">更新索引</text>
    <text class="text-small" x="690" y="160" text-anchor="middle">版本管理</text>
    
    <!-- 向下箭头 -->
    <line class="arrow" x1="690" y1="170" x2="690" y2="210"/>
    
    <!-- 数据分析 -->
    <rect class="box" x="580" y="210" width="140" height="100" rx="8"/>
    <text class="text" x="650" y="235" text-anchor="middle" font-weight="bold">数据分析</text>
    <text class="text" x="650" y="255" text-anchor="middle" font-weight="bold">Analytics</text>
    <text class="text-small" x="650" y="280" text-anchor="middle">使用模式 · 知识缺口</text>
    <text class="text-small" x="650" y="295" text-anchor="middle">Agent性能</text>
    
    <line class="arrow" x1="580" y1="260" x2="530" y2="260"/>
    
    <!-- Skill改进 -->
    <rect class="box" x="390" y="210" width="140" height="100" rx="8"/>
    <text class="text" x="460" y="235" text-anchor="middle" font-weight="bold">Skill改进</text>
    <text class="text" x="460" y="255" text-anchor="middle" font-weight="bold">Optimize</text>
    <text class="text-small" x="460" y="280" text-anchor="middle">策略调优 · 知识补充</text>
    <text class="text-small" x="460" y="295" text-anchor="middle">权限优化</text>
    
    <line class="arrow" x1="390" y1="260" x2="340" y2="260"/>
    
    <!-- 体验提升 -->
    <rect class="box-highlight" x="200" y="210" width="140" height="100" rx="8"/>
    <text class="text" x="270" y="235" text-anchor="middle" font-weight="bold">体验提升</text>
    <text class="text" x="270" y="255" text-anchor="middle" font-weight="bold">Better UX</text>
    <text class="text-small" x="270" y="280" text-anchor="middle">响应更快 · 答案更准</text>
    <text class="text-small" x="270" y="295" text-anchor="middle">功能更强</text>
    
    <!-- 循环箭头 -->
    <path class="loop-arrow" d="M 200 280 Q 120 280 120 170 Q 120 120 50 120" fill="none"/>
    <text class="text-small" x="100" y="240" fill="#2563eb" font-style="italic">正反馈循环</text>
'''
    return save_svg('diagram_data_flywheel.svg', svg, 800, 340)

# ========== 图表4: 四大亮点逻辑演进 ==========
def generate_four_highlights():
    """生成四大亮点逻辑演进SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
    <text class="text-header" x="400" y="38" text-anchor="middle">四大亮点的逻辑演进</text>
    
    <!-- 第一层：基础架构层 -->
    <rect class="box" x="30" y="70" width="740" height="90"/>
    <text class="text-title" x="50" y="95">第一层：基础架构层</text>
    
    <rect class="box-alt" x="50" y="110" width="700" height="40" rx="6"/>
    <text class="text" x="70" y="125" font-weight="bold">Skill-Centric Architecture（技能中心架构）</text>
    <text class="text-small" x="70" y="142">统一抽象：所有 AI 能力都是 Skill · 三维分类：形态/分类/目的 · 能力暴露：通过 Capability 接口</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="160" x2="400" y2="185"/>
    
    <!-- 第二层：用户参与层 -->
    <rect class="box" x="30" y="185" width="740" height="130"/>
    <text class="text-title" x="50" y="210">第二层：用户参与层</text>
    
    <!-- 亮点一 -->
    <rect class="box-alt" x="50" y="225" width="340" height="80" rx="6"/>
    <text class="text" x="70" y="245" font-weight="bold">亮点一：多方参与、自主维护</text>
    <text class="text-small" x="70" y="265">开发者定义 Skill 接口</text>
    <text class="text-small" x="70" y="280">终端用户贡献知识</text>
    <text class="text-small" x="70" y="295">运维人员配置权限</text>
    
    <!-- 亮点二 -->
    <rect class="box-alt" x="410" y="225" width="340" height="80" rx="6"/>
    <text class="text" x="430" y="245" font-weight="bold">亮点二：Agent 作为独立账号用户</text>
    <text class="text-small" x="430" y="265">Agent 拥有独立 userId</text>
    <text class="text-small" x="430" y="280">多角色协作（RoleConfig）</text>
    <text class="text-small" x="430" y="295">精细化权限控制（DataScope）</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="315" x2="400" y2="340"/>
    
    <!-- 第三层：知识增强层 -->
    <rect class="box" x="30" y="340" width="740" height="90"/>
    <text class="text-title" x="50" y="365">第三层：知识增强层</text>
    
    <rect class="box-alt" x="50" y="380" width="700" height="40" rx="6"/>
    <text class="text" x="70" y="395" font-weight="bold">亮点三：每个 Skill 独立的知识资料库</text>
    <text class="text-small" x="70" y="412">KnowledgeBinding：Skill 与知识库绑定 · 完整 RAG 支持：分块/向量化/索引/检索 · AdaptiveRag：自适应检索策略</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="430" x2="400" y2="455"/>
    
    <!-- 第四层：数据驱动层 -->
    <rect class="box" x="30" y="455" width="740" height="110"/>
    <text class="text-title" x="50" y="480">第四层：数据驱动层</text>
    
    <rect class="box-alt" x="50" y="495" width="340" height="60" rx="6"/>
    <text class="text" x="70" y="515" font-weight="bold">亮点四：用户数据深度挖掘的数据飞轮</text>
    <text class="text-small" x="70" y="535">AuditLogger · 数据挖掘 · PushFeedback</text>
    
    <line class="arrow-highlight" x1="390" y1="525" x2="450" y2="525"/>
    <text class="text" x="520" y="520" text-anchor="middle" font-weight="bold" fill="#2563eb">数据飞轮输出</text>
    
    <line class="arrow-highlight" x1="590" y1="525" x2="650" y2="525"/>
    
    <rect class="box-highlight" x="650" y="505" width="100" height="40" rx="6"/>
    <text class="text" x="700" y="525" text-anchor="middle" font-weight="bold" font-size="12">优化 Skill</text>
    <text class="text" x="700" y="538" text-anchor="middle" font-size="11">和知识库</text>
    
    <line class="arrow-highlight" x1="700" y1="505" x2="700" y2="470"/>
    <line class="arrow-highlight" x1="700" y1="470" x2="400" y2="470"/>
    <line class="arrow-highlight" x1="400" y1="470" x2="400" y2="455"/>
    
    <text class="text-small" x="550" y="465" fill="#2563eb" font-style="italic">更好的用户体验</text>
'''
    return save_svg('diagram_four_highlights.svg', svg, 800, 580)

# ========== 图表5: 招聘场景示例 ==========
def generate_recruitment_example():
    """生成招聘场景示例SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
    <text class="text-header" x="400" y="38" text-anchor="middle">招聘场景：四大亮点协同工作示例</text>
    
    <!-- 场景描述 -->
    <rect class="box-highlight" x="30" y="60" width="740" height="35" rx="6"/>
    <text class="text" x="400" y="83" text-anchor="middle">【场景】HR Alice 使用"智能招聘助手" Skill 进行候选人筛选</text>
    
    <!-- Step 1 -->
    <rect class="box" x="30" y="110" width="740" height="90"/>
    <text class="text-title" x="50" y="135">Step 1: Skill 定义与发现</text>
    
    <text class="text" x="50" y="160">开发者开发了"智能招聘助手" Skill：</text>
    <text class="text-small" x="70" y="180">• SkillForm: SCENE（场景技能） · SkillCategory: knowledge（知识型技能）</text>
    <text class="text-small" x="70" y="195">• Capabilities: [简历解析, 面试安排, 候选人评估]</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="200" x2="400" y2="220"/>
    
    <!-- Step 2 -->
    <rect class="box" x="30" y="220" width="740" height="90"/>
    <text class="text-title" x="50" y="245">Step 2: Agent 创建与协作</text>
    
    <text class="text" x="50" y="270">Alice 创建了三个 Agent：</text>
    <rect class="box-alt" x="70" y="280" width="200" height="20" rx="4"/>
    <text class="text-small" x="170" y="294" text-anchor="middle">HR Agent (agent_001): 简历筛选</text>
    <rect class="box-alt" x="300" y="280" width="200" height="20" rx="4"/>
    <text class="text-small" x="400" y="294" text-anchor="middle">面试官 Agent (agent_002): 技术面试</text>
    <rect class="box-alt" x="530" y="280" width="200" height="20" rx="4"/>
    <text class="text-small" x="630" y="294" text-anchor="middle">候选人 Agent (agent_003): 回答问题</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="310" x2="400" y2="330"/>
    
    <!-- Step 3 -->
    <rect class="box" x="30" y="330" width="740" height="90"/>
    <text class="text-title" x="50" y="355">Step 3: 知识贡献与检索</text>
    
    <text class="text" x="50" y="380">Alice 上传了公司招聘手册，HR Agent 查询"Java高级工程师面试要点"</text>
    <text class="text-small" x="70" y="400">• AdaptiveRag 自动选择 HIGH_PRECISION 策略 · PermissionEngine 过滤有权访问的内容 · 返回最相关的 3 个知识片段</text>
    
    <!-- 箭头 -->
    <line class="arrow" x1="400" y1="420" x2="400" y2="440"/>
    
    <!-- Step 4 -->
    <rect class="box" x="30" y="440" width="740" height="90"/>
    <text class="text-title" x="50" y="465">Step 4: 数据收集与飞轮</text>
    
    <text class="text" x="50" y="490">系统记录 AuditEntry，Alice 提交反馈</text>
    <text class="text-small" x="70" y="510">• 数据飞轮识别知识缺口：缺少 Spring Cloud 内容 · 自动通知管理员补充相关内容 · 下次查询时，回答将更加完整</text>
'''
    return save_svg('diagram_recruitment_example.svg', svg, 800, 550)

# ========== 图表6: 技术架构全景 ==========
def generate_architecture_overview():
    """生成技术架构全景SVG"""
    svg = '''
    <!-- 标题 -->
    <rect class="title-box" x="20" y="15" width="760" height="35" rx="4"/>
    <text class="text-header" x="400" y="38" text-anchor="middle">Ooder-Skills 技术架构全景</text>
    
    <!-- 应用层 -->
    <rect class="box" x="30" y="70" width="740" height="80"/>
    <text class="text-title" x="50" y="95">应用层 (Application)</text>
    
    <rect class="box-alt" x="50" y="110" width="150" height="30" rx="6"/>
    <text class="text-small" x="125" y="130" text-anchor="middle">UserContributionService</text>
    
    <rect class="box-alt" x="230" y="110" width="120" height="30" rx="6"/>
    <text class="text-small" x="290" y="130" text-anchor="middle">BatchImportService</text>
    
    <rect class="box-alt" x="380" y="110" width="120" height="30" rx="6"/>
    <text class="text-small" x="440" y="130" text-anchor="middle">PushService</text>
    
    <rect class="box-alt" x="530" y="110" width="120" height="30" rx="6"/>
    <text class="text-small" x="590" y="130" text-anchor="middle">AuditLogger</text>
    
    <!-- 连接线 -->
    <line class="arrow" x1="400" y1="150" x2="400" y2="170"/>
    
    <!-- 技能层 -->
    <rect class="box" x="30" y="170" width="740" height="80"/>
    <text class="text-title" x="50" y="195">技能层 (Skill Layer)</text>
    
    <rect class="box-alt" x="50" y="210" width="700" height="30" rx="6"/>
    <text class="text-small" x="70" y="230">Skill (核心实体) · SkillForm: SCENE/STANDALONE · SkillCategory: knowledge/llm/tool/workflow · getCapabilities()</text>
    
    <!-- 连接线 -->
    <line class="arrow" x1="400" y1="250" x2="400" y2="270"/>
    
    <!-- 知识增强层 -->
    <rect class="box" x="30" y="270" width="740" height="80"/>
    <text class="text-title" x="50" y="295">知识增强层 (Knowledge Layer)</text>
    
    <rect class="box-alt" x="50" y="310" width="150" height="30" rx="6"/>
    <text class="text-small" x="125" y="330" text-anchor="middle">KnowledgeBaseService</text>
    
    <rect class="box-alt" x="230" y="310" width="120" height="30" rx="6"/>
    <text class="text-small" x="290" y="330" text-anchor="middle">RagPipeline</text>
    
    <rect class="box-alt" x="380" y="310" width="120" height="30" rx="6"/>
    <text class="text-small" x="440" y="330" text-anchor="middle">AdaptiveRag</text>
    
    <rect class="box-alt" x="530" y="310" width="150" height="30" rx="6"/>
    <text class="text-small" x="605" y="330" text-anchor="middle">PermissionEngine</text>
    
    <!-- 连接线 -->
    <line class="arrow" x1="400" y1="350" x2="400" y2="370"/>
    
    <!-- Agent层 -->
    <rect class="box" x="30" y="370" width="740" height="100"/>
    <text class="text-title" x="50" y="395">Agent 层 (Agent Layer)</text>
    
    <rect class="box-alt" x="50" y="410" width="700" height="50" rx="6"/>
    <text class="text-small" x="70" y="430">AgentLlmSessionContext (Agent 用户会话上下文)</text>
    <text class="text-small" x="70" y="450">agentId / userId / agentType · llmConfig / connectionPool / conversationHistory · quota / state · RoleContext / RoleConfig</text>
'''
    return save_svg('diagram_architecture_overview.svg', svg, 800, 490)

# ========== 主函数 ==========
def main():
    """生成所有SVG图表"""
    print("开始生成SVG图表...")
    
    # 生成所有图表
    generate_three_layer_model()
    generate_agent_user_model()
    generate_data_flywheel()
    generate_four_highlights()
    generate_recruitment_example()
    generate_architecture_overview()
    
    print("\n所有SVG图表生成完成！")
    print("\n生成的文件：")
    print("  1. diagram_three_layer.svg - 三层协作模型")
    print("  2. diagram_agent_user_model.svg - Agent用户模型架构")
    print("  3. diagram_data_flywheel.svg - 数据飞轮模型")
    print("  4. diagram_four_highlights.svg - 四大亮点逻辑演进")
    print("  5. diagram_recruitment_example.svg - 招聘场景示例")
    print("  6. diagram_architecture_overview.svg - 技术架构全景")

if __name__ == "__main__":
    main()

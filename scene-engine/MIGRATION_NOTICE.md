# northbound-services 仓库迁移通知

> **本仓库已迁移，请访问新地址**

## 迁移信息

| 项目 | 内容 |
|------|------|
| 原仓库 | `northbound-services` |
| 新仓库 | `skills` (技能部分) / `super-Agent` (引擎部分) |
| 迁移日期 | 2026-02-20 |
| 迁移版本 | v0.7.3 |

## 迁移映射

| 原路径 | 新仓库 | 新路径 |
|--------|--------|--------|
| `/skill-org/*` | skills | `/skill-org/*` |
| `/skill-vfs/*` | skills | `/skill-vfs/*` |
| `/skill-mqtt` | skills | `/skill-mqtt` |
| `/skill-msg` | skills | `/skill-msg` |
| `/skill-agent` | skills | `/skill-agent` |
| `/scene-engine` | super-Agent | `/scene-engine` |
| `/northbound-gateway` | super-Agent | `/northbound-gateway` |
| `/northbound-core` | super-Agent | `/northbound-core` |

## 新仓库地址

### skills 仓库
- **GitHub**: https://github.com/ooderCN/skills
- **Gitee**: https://gitee.com/ooderCN/skills

### super-Agent 仓库
- **GitHub**: https://github.com/ooderCN/super-Agent
- **Gitee**: https://gitee.com/ooderCN/super-Agent

## 对用户的影响

### Maven 依赖

Maven 依赖坐标**保持不变**，无需修改。

### Git Clone

请根据需要克隆新仓库：

```bash
# 技能仓库
git clone https://github.com/ooderCN/skills.git

# 框架仓库
git clone https://github.com/ooderCN/super-Agent.git
```

## 详细迁移公告

完整迁移公告请查看：[MIGRATION_ANNOUNCEMENT.md](https://github.com/ooderCN/overall-design/blob/main/MIGRATION_ANNOUNCEMENT.md)

---

**Ooder Team**  
2026-02-20

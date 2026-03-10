# Engine v3.0 争议点回答

## 一、争议点一：分类体系不一致

### 问题回顾

| Engine v3.0 | skill-index.yaml | 状态 |
|-------------|------------------|------|
| 8个功能分类 | 11个业务分类 | 不一致 |

### Engine 团队回答

**推荐方案：方案C - 两层分类体系**

**理由**：

1. **功能分类 vs 业务分类 是不同维度**
   - Engine 的 `SkillCategory` 是"功能类型"（类比文件扩展名）
   - skill-index 的分类是"业务领域"（类比文件夹位置）
   - 两者不冲突，可以组合使用

2. **类比文件系统**
   ```
   文件扩展名: .doc, .exe, .ai     → SkillCategory (功能分类)
   文件夹位置: /finance, /hr, /dev → 业务领域 (业务分类)
   
   一个文件可以同时有：
   - 扩展名: .doc (功能类型)
   - 位置: /finance (业务领域)
   ```

3. **建议实现**
   ```yaml
   - skillId: skill-document-assistant
     category: KNOWLEDGE          # 功能分类 (Engine 定义)
     domain: knowledge            # 业务领域 (skill-index 定义)
     form: SCENE
     sceneType: AUTO
   ```

**结论**：
- Engine 保持 8 个功能分类不变
- skill-index 可以保留业务领域作为 `domain` 字段
- 两者独立，可组合查询

---

## 二、争议点二：ASS (自驱系统场景) 的归属

### 问题回顾

| Engine v3.0 | 现有概念 |
|-------------|----------|
| AUTO/TRIGGER/HYBRID | ABS/ASS/TBS |

### Engine 团队回答

**推荐方案：方案A - ASS 合并到 AUTO，通过 visibility 区分**

**理由**：

1. **ASS 的本质是 AUTO + 内部可见**
   - ASS = 自驱系统场景 = 自主场景 + 内部可见
   - 核心特征是"自驱"，与 AUTO 一致
   - "系统场景"只是可见性不同

2. **v3.0 模型设计理念**
   ```
   场景类型 (SceneType): 描述"如何运行"
   可见性 (visibility): 描述"谁能看到"
   
   这是两个独立的维度，不应该混在一起
   ```

3. **建议实现**
   ```yaml
   # ABS (自驱业务场景)
   - skillId: skill-document-assistant
     form: SCENE
     sceneType: AUTO
     visibility: PUBLIC           # 业务场景，公开可见
   
   # ASS (自驱系统场景)
   - skillId: skill-system-monitor
     form: SCENE
     sceneType: AUTO
     visibility: INTERNAL         # 系统场景，内部可见
   ```

4. **visibility 枚举定义**
   ```java
   public enum Visibility {
       PUBLIC,     // 公开，所有用户可见
       INTERNAL,   // 内部，仅系统/管理员可见
       PRIVATE     // 私有，仅创建者可见
   }
   ```

**结论**：
- ASS 合并到 AUTO
- Engine 增加 `visibility` 字段
- 通过 visibility 区分业务场景和系统场景

---

## 三、争议点三：skill-index 中的错误分类

### 问题回顾

```yaml
# 错误使用
- skillId: skill-document-assistant
  category: abs              # ❌ ABS 是场景类型，不是分类
```

### Engine 团队回答

**这是明确的错误，必须修复**

**理由**：

1. **概念混淆**
   - `abs/tbs/ass` 是场景类型，不是技能分类
   - 把场景类型当作分类使用，破坏了模型的一致性

2. **修复方案**
   ```yaml
   # 修复前
   - skillId: skill-document-assistant
     category: abs              # ❌ 错误
   
   # 修复后
   - skillId: skill-document-assistant
     category: KNOWLEDGE        # ✅ 技能分类
     form: SCENE                # ✅ 技能形态
     sceneType: AUTO            # ✅ 场景类型
   ```

3. **迁移映射表**
   | 旧值 | category | form | sceneType |
   |------|----------|------|-----------|
   | abs | 根据实际功能 | SCENE | AUTO |
   | ass | 根据实际功能 | SCENE | AUTO (visibility=INTERNAL) |
   | tbs | 根据实际功能 | SCENE | TRIGGER |

**结论**：
- 必须修复，不能保留错误用法
- Skills 团队需要准备数据迁移脚本

---

## 四、争议点四：nexus-ui 分类

### 问题回顾

| Engine v3.0 | skill-index.yaml |
|-------------|------------------|
| UI 分类 | nexus-ui 独立分类 |

### Engine 团队回答

**推荐方案：方案A - nexus-ui 合并到 UI，通过 form 区分**

**理由**：

1. **nexus-ui 本质是 UI 类技能**
   - nexus-ui 技能提供前端页面
   - 功能类型就是 UI
   - 不需要单独的分类

2. **不同之处通过其他字段区分**
   ```yaml
   # 后端 UI 技能
   - skillId: skill-admin-panel
     category: UI
     form: STANDALONE
     backend: true
   
   # 前端 UI 技能 (nexus-ui)
   - skillId: skill-nexus-dashboard
     category: UI
     form: STANDALONE
     frontend: true
     loadMode: LAZY              # 懒加载
   ```

3. **保持分类体系简洁**
   - 分类描述"是什么"
   - 其他属性描述"怎么运行/加载"
   - 不要为每个变体创建新分类

**结论**：
- nexus-ui 合并到 UI 分类
- 通过 `frontend/loadMode` 等字段区分加载方式

---

## 五、最终方案总结

### 5.1 Engine v3.0 扩展

| 扩展项 | 说明 |
|--------|------|
| 增加 `visibility` 字段 | 区分 PUBLIC/INTERNAL/PRIVATE |
| 保持 8 个功能分类 | 不扩展业务分类 |
| `domain` 字段可选 | 业务领域由 Skills 团队自行定义 |

### 5.2 skill-index 修复

```yaml
# 标准格式
- skillId: skill-document-assistant
  # Engine v3.0 字段
  category: KNOWLEDGE          # 功能分类
  form: SCENE                  # 技能形态
  sceneType: AUTO              # 场景类型
  visibility: PUBLIC           # 可见性
  
  # Skills 扩展字段
  domain: knowledge            # 业务领域
  mainFirst: true              # 自驱配置
  mainFirstConfig: {...}
```

### 5.3 迁移脚本

```sql
-- 1. 添加新字段
ALTER TABLE skills ADD COLUMN form VARCHAR(20) DEFAULT 'STANDALONE';
ALTER TABLE skills ADD COLUMN scene_type VARCHAR(20);
ALTER TABLE skills ADD COLUMN visibility VARCHAR(20) DEFAULT 'PUBLIC';

-- 2. 迁移数据
UPDATE skills SET 
  form = 'SCENE',
  scene_type = CASE 
    WHEN category IN ('abs', 'ass') THEN 'AUTO'
    WHEN category = 'tbs' THEN 'TRIGGER'
    ELSE NULL
  END,
  visibility = CASE 
    WHEN category = 'ass' THEN 'INTERNAL'
    ELSE 'PUBLIC'
  END
WHERE category IN ('abs', 'tbs', 'ass');

-- 3. 修复分类
UPDATE skills SET category = 'KNOWLEDGE' WHERE skill_id = 'skill-document-assistant';
UPDATE skills SET category = 'WORKFLOW' WHERE skill_id = 'skill-meeting-minutes';
UPDATE skills SET category = 'UI' WHERE category = 'nexus-ui';
UPDATE skills SET category = 'TOOL' WHERE category = 'util';
```

---

## 六、待讨论问题回答

| 序号 | 问题 | Engine 回答 |
|------|------|-------------|
| 1 | Engine 是否扩展分类体系？ | **C: 两层分类** - Engine 保持功能分类，Skills 可定义业务领域 |
| 2 | ASS 如何处理？ | **A: 合并到AUTO** - 通过 visibility 字段区分 |
| 3 | nexus-ui 如何归类？ | **A: 合并到UI** - 通过其他字段区分加载方式 |
| 4 | util vs TOOL 命名统一？ | **TOOL** - 与其他分类命名风格一致 |
| 5 | HYBRID 场景类型使用场景？ | 既需要自驱又需要响应外部触发的场景 |
| 6 | visibility 字段是否纳入 Engine？ | **是** - 建议纳入 Engine 核心模型 |

---

**文档版本**: 1.0.0  
**创建日期**: 2026-03-10  
**作者**: Engine Team

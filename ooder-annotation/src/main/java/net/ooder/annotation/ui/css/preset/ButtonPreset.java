package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.enums.*;

/**
 * Button组件预设样式
 * 基于现代UI框架（Material Design, Ant Design, Element Plus）的按钮样式方案
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【预设分类】
 * 本枚举定义了19种按钮预设，分为以下几类:
 *
 * 1. Material Design系列 (3种):
 *    - MATERIAL_CONTAINED: 实心按钮，主色背景，白色文字
 *    - MATERIAL_OUTLINED: 描边按钮，透明背景，主色边框
 *    - MATERIAL_TEXT: 文字按钮，无边框，主色文字
 *
 * 2. Ant Design系列 (4种):
 *    - ANT_PRIMARY: 主按钮，蓝色背景，白色文字
 *    - ANT_DEFAULT: 默认按钮，白色背景，灰色边框
 *    - ANT_DASHED: 虚线按钮，白色背景，虚线边框
 *    - ANT_LINK: 链接按钮，无边框，蓝色文字
 *
 * 3. Element Plus系列 (5种):
 *    - ELEMENT_PRIMARY: 主按钮，蓝色背景
 *    - ELEMENT_SUCCESS: 成功按钮，绿色背景
 *    - ELEMENT_WARNING: 警告按钮，橙色背景
 *    - ELEMENT_DANGER: 危险按钮，红色背景
 *    - ELEMENT_ROUND: 圆角按钮，大圆角(20px)
 *
 * 4. Bootstrap系列 (3种):
 *    - BOOTSTRAP_PRIMARY: 主按钮，蓝色背景
 *    - BOOTSTRAP_SECONDARY: 次要按钮，灰色背景
 *    - BOOTSTRAP_OUTLINE_PRIMARY: 描边主按钮
 *
 * 5. 特殊按钮 (4种):
 *    - ICON_BUTTON: 图标按钮，圆形，透明背景
 *    - FAB: 浮动操作按钮，大尺寸(56px)，圆形
 *    - FAB_MINI: 迷你浮动按钮，小尺寸(40px)，圆形
 *    - UNSET: 不应用任何预设
 *
 * 【预设属性对照表】(完整CSS属性值):
 *
 * ┌──────────────────────┬──────────┬─────────────┬──────────┬─────────────┬──────────────────┐
 * │ 预设                 │ 背景色   │ 文字色      │ 边框     │ 圆角        │ 内边距           │
 * ├──────────────────────┼──────────┼─────────────┼──────────┼─────────────┼──────────────────┤
 * │ MATERIAL_CONTAINED   │ #1976d2  │ #ffffff     │ none     │ 4px         │ 8px 22px         │
 * │ MATERIAL_OUTLINED    │ trans    │ #1976d2     │ 1px rgba │ 4px         │ 8px 22px         │
 * │ MATERIAL_TEXT        │ trans    │ #1976d2     │ none     │ 4px         │ 8px 16px         │
 * ├──────────────────────┼──────────┼─────────────┼──────────┼─────────────┼──────────────────┤
 * │ ANT_PRIMARY          │ #1677ff  │ #ffffff     │ 1px solid│ 6px         │ 6px 16px         │
 * │                      │          │             │ #1677ff  │             │                  │
 * │ ANT_DEFAULT          │ #ffffff  │ rgba(0,0,0  │ 1px solid│ 6px         │ 6px 16px         │
 * │                      │          │ ,0.88)      │ #d9d9d9  │             │                  │
 * │ ANT_DASHED           │ #ffffff  │ rgba(0,0,0  │ 1px      │ 6px         │ 6px 16px         │
 * │                      │          │ ,0.88)      │ dashed   │             │                  │
 * │ ANT_LINK             │ trans    │ #1677ff     │ none     │ 6px         │ 6px 16px         │
 * ├──────────────────────┼──────────┼─────────────┼──────────┼─────────────┼──────────────────┤
 * │ ELEMENT_PRIMARY      │ #409eff  │ #ffffff     │ 1px solid│ 4px         │ 12px 20px        │
 * │                      │          │             │ #409eff  │             │                  │
 * │ ELEMENT_SUCCESS      │ #67c23a  │ #ffffff     │ 1px solid│ 4px         │ 12px 20px        │
 * │                      │          │             │ #67c23a  │             │                  │
 * │ ELEMENT_WARNING      │ #e6a23c  │ #ffffff     │ 1px solid│ 4px         │ 12px 20px        │
 * │                      │          │             │ #e6a23c  │             │                  │
 * │ ELEMENT_DANGER       │ #f56c6c  │ #ffffff     │ 1px solid│ 4px         │ 12px 20px        │
 * │                      │          │             │ #f56c6c  │             │                  │
 * │ ELEMENT_ROUND        │ #409eff  │ #ffffff     │ 1px solid│ 20px        │ 12px 23px        │
 * │                      │          │             │ #409eff  │             │                  │
 * ├──────────────────────┼──────────┼─────────────┼──────────┼─────────────┼──────────────────┤
 * │ BOOTSTRAP_PRIMARY    │ #0d6efd  │ #ffffff     │ 1px solid│ 6px         │ 10px 20px        │
 * │                      │          │             │ #0d6efd  │             │                  │
 * │ BOOTSTRAP_SECONDARY  │ #6c757d  │ #ffffff     │ 1px solid│ 6px         │ 10px 20px        │
 * │                      │          │             │ #6c757d  │             │                  │
 * │ BOOTSTRAP_OUTLINE_   │ trans    │ #0d6efd     │ 1px solid│ 6px         │ 10px 20px        │
 * │ PRIMARY              │          │             │ #0d6efd  │             │                  │
 * ├──────────────────────┼──────────┼─────────────┼──────────┼─────────────┼──────────────────┤
 * │ ICON_BUTTON          │ trans    │ #666        │ none     │ 50%         │ 8px              │
 * │                      │          │             │          │ (40x40)     │                  │
 * │ FAB                  │ #1976d2  │ #ffffff     │ none     │ 50%         │ 16px             │
 * │                      │          │             │          │ (56x56)     │                  │
 * │ FAB_MINI             │ #1976d2  │ #ffffff     │ none     │ 50%         │ 12px             │
 * │                      │          │             │          │ (40x40)     │                  │
 * └──────────────────────┴──────────┴─────────────┴──────────┴─────────────┴──────────────────┘
 *
 * 【字重设置】:
 * - MATERIAL系列: W500 (500)
 * - 其他所有预设: NORMAL (400)
 *
 * 【阴影设置】:
 * - MATERIAL_CONTAINED: Material Design标准阴影
 * - FAB/FAB_MINI: 浮动按钮阴影
 * - 其他预设: 无阴影
 *
 * 【使用建议】:
 * 1. 主要操作: 使用 MATERIAL_CONTAINED / ANT_PRIMARY / ELEMENT_PRIMARY
 * 2. 次要操作: 使用 MATERIAL_OUTLINED / ANT_DEFAULT
 * 3. 文字链接: 使用 MATERIAL_TEXT / ANT_LINK
 * 4. 图标按钮: 使用 ICON_BUTTON
 * 5. 浮动操作: 使用 FAB / FAB_MINI
 * 6. 状态反馈: 使用 ELEMENT_SUCCESS/WARNING/DANGER
 *
 * 【与JS组件的关系】:
 * 当使用@ButtonStyle(preset = ButtonPreset.XXX)时:
 * 1. 首先应用本预设的getFont()/getLayout()/getBorder()返回值
 * 2. 然后应用@CSFont/@CSLayout/@CSBorder中的覆盖值
 * 3. 最后由JS组件渲染时应用运行时动态样式
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum ButtonPreset {

    /**
     * 不应用任何预设，使用JS组件默认样式
     * 所有属性返回空值或UNSET
     */
    UNSET,

    /**
     * Material Design实心按钮
     * 背景: #1976d2 (Material主色)
     * 文字: #ffffff (白色)
     * 边框: none
     * 圆角: 4px
     * 内边距: 8px 22px
     * 字重: W500
     * 阴影: Material标准阴影
     *
     * 【适用场景】
     * 主要操作按钮，如"提交"、"保存"、"确认"
     */
    MATERIAL_CONTAINED,

    /**
     * Material Design描边按钮
     * 背景: transparent (透明)
     * 文字: #1976d2 (Material主色)
     * 边框: 1px solid rgba(25,118,210,0.5) (半透明主色)
     * 圆角: 4px
     * 内边距: 8px 22px
     * 字重: W500
     *
     * 【适用场景】
     * 次要操作按钮，如"取消"、"返回"
     */
    MATERIAL_OUTLINED,

    /**
     * Material Design文字按钮
     * 背景: transparent (透明)
     * 文字: #1976d2 (Material主色)
     * 边框: none
     * 圆角: 4px
     * 内边距: 8px 16px (较小)
     * 字重: W500
     *
     * 【适用场景】
     * 低优先级操作，如"了解更多"、"查看详情"
     */
    MATERIAL_TEXT,

    /**
     * Ant Design主按钮
     * 背景: #1677ff (Ant Design主色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #1677ff
     * 圆角: 6px
     * 内边距: 6px 16px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 主要操作按钮，Ant Design风格
     */
    ANT_PRIMARY,

    /**
     * Ant Design默认按钮
     * 背景: #ffffff (白色)
     * 文字: rgba(0,0,0,0.88) (深灰)
     * 边框: 1px solid #d9d9d9 (浅灰)
     * 圆角: 6px
     * 内边距: 6px 16px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 次要操作按钮，Ant Design风格
     */
    ANT_DEFAULT,

    /**
     * Ant Design虚线按钮
     * 背景: #ffffff (白色)
     * 文字: rgba(0,0,0,0.88) (深灰)
     * 边框: 1px dashed #d9d9d9 (虚线浅灰)
     * 圆角: 6px
     * 内边距: 6px 16px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 添加、导入等操作，如"+ 添加"
     */
    ANT_DASHED,

    /**
     * Ant Design链接按钮
     * 背景: transparent (透明)
     * 文字: #1677ff (Ant Design主色)
     * 边框: none
     * 圆角: 6px
     * 内边距: 6px 16px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 链接样式按钮，如"忘记密码？"
     */
    ANT_LINK,

    /**
     * Element Plus主按钮
     * 背景: #409eff (Element主色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #409eff
     * 圆角: 4px
     * 内边距: 12px 20px (较大)
     * 字重: NORMAL
     *
     * 【适用场景】
     * 主要操作按钮，Element Plus风格
     */
    ELEMENT_PRIMARY,

    /**
     * Element Plus成功按钮
     * 背景: #67c23a (绿色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #67c23a
     * 圆角: 4px
     * 内边距: 12px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 成功状态操作，如"保存成功"、"通过"
     */
    ELEMENT_SUCCESS,

    /**
     * Element Plus警告按钮
     * 背景: #e6a23c (橙色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #e6a23c
     * 圆角: 4px
     * 内边距: 12px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 警告状态操作，如"警告"、"注意"
     */
    ELEMENT_WARNING,

    /**
     * Element Plus危险按钮
     * 背景: #f56c6c (红色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #f56c6c
     * 圆角: 4px
     * 内边距: 12px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 危险操作，如"删除"、"清空"
     */
    ELEMENT_DANGER,

    /**
     * Element Plus圆角按钮
     * 背景: #409eff (Element主色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #409eff
     * 圆角: 20px (大圆角)
     * 内边距: 12px 23px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 需要圆角风格的按钮
     */
    ELEMENT_ROUND,

    /**
     * Bootstrap主按钮
     * 背景: #0d6efd (Bootstrap主色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #0d6efd
     * 圆角: 6px
     * 内边距: 10px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * Bootstrap风格的主要操作
     */
    BOOTSTRAP_PRIMARY,

    /**
     * Bootstrap次要按钮
     * 背景: #6c757d (Bootstrap灰色)
     * 文字: #ffffff (白色)
     * 边框: 1px solid #6c757d
     * 圆角: 6px
     * 内边距: 10px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * Bootstrap风格的次要操作
     */
    BOOTSTRAP_SECONDARY,

    /**
     * Bootstrap描边主按钮
     * 背景: transparent (透明)
     * 文字: #0d6efd (Bootstrap主色)
     * 边框: 1px solid #0d6efd
     * 圆角: 6px
     * 内边距: 10px 20px
     * 字重: NORMAL
     *
     * 【适用场景】
     * Bootstrap风格的描边按钮
     */
    BOOTSTRAP_OUTLINE_PRIMARY,

    /**
     * 图标按钮
     * 背景: transparent (透明)
     * 文字: #666 (灰色)
     * 边框: none
     * 圆角: 50% (圆形)
     * 尺寸: 40px x 40px
     * 内边距: 8px
     * 字重: NORMAL
     *
     * 【适用场景】
     * 纯图标按钮，如"设置"、"更多"
     */
    ICON_BUTTON,

    /**
     * 浮动操作按钮 (Floating Action Button)
     * 背景: #1976d2 (Material主色)
     * 文字: #ffffff (白色)
     * 边框: none
     * 圆角: 50% (圆形)
     * 尺寸: 56px x 56px (大尺寸)
     * 内边距: 16px
     * 字重: NORMAL
     * 阴影: FAB专用阴影
     *
     * 【适用场景】
     * 页面主要操作的浮动按钮，如"添加"、"新建"
     */
    FAB,

    /**
     * 迷你浮动操作按钮
     * 背景: #1976d2 (Material主色)
     * 文字: #ffffff (白色)
     * 边框: none
     * 圆角: 50% (圆形)
     * 尺寸: 40px x 40px (小尺寸)
     * 内边距: 12px
     * 字重: NORMAL
     * 阴影: FAB专用阴影
     *
     * 【适用场景】
     * 空间受限时的浮动按钮
     */
    FAB_MINI;

    /**
     * 获取字体样式配置
     * 返回CSFont注解实例，包含当前预设的字体相关属性
     *
     * 【返回值说明】
     * - color(): 按钮文字颜色
     * - fontWeight(): 字重 (MATERIAL系列为W500，其他为NORMAL)
     * - 其他属性返回空值或UNSET
     */
    public CSFont getFont() {
        return new CSFont() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CSFont.class;
            }

            @Override
            public String color() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_CONTAINED: return "#ffffff";
                    case MATERIAL_OUTLINED: case MATERIAL_TEXT: return "#1976d2";
                    case ANT_PRIMARY: return "#ffffff";
                    case ANT_DEFAULT: case ANT_DASHED: return "rgba(0, 0, 0, 0.88)";
                    case ANT_LINK: return "#1677ff";
                    case ELEMENT_PRIMARY: case ELEMENT_SUCCESS: case ELEMENT_WARNING: case ELEMENT_DANGER: return "#ffffff";
                    case ELEMENT_ROUND: return "#ffffff";
                    case BOOTSTRAP_PRIMARY: case BOOTSTRAP_SECONDARY: return "#ffffff";
                    case BOOTSTRAP_OUTLINE_PRIMARY: return "#0d6efd";
                    case ICON_BUTTON: return "#666";
                    case FAB: case FAB_MINI: return "#ffffff";
                    default: return "";
                }
            }

            @Override
            public String fontSize() { return ""; }

            @Override
            public CSFontWeight fontWeight() {
                switch (ButtonPreset.this) {
                    case UNSET: return CSFontWeight.UNSET;
                    case MATERIAL_CONTAINED: case MATERIAL_OUTLINED: case MATERIAL_TEXT: return CSFontWeight.W500;
                    default: return CSFontWeight.NORMAL;
                }
            }

            @Override
            public String fontFamily() { return ""; }

            @Override
            public CSFontStyle fontStyle() { return CSFontStyle.UNSET; }

            @Override
            public String lineHeight() { return ""; }

            @Override
            public String letterSpacing() { return ""; }

            @Override
            public CSTextAlign textAlign() { return CSTextAlign.UNSET; }

            @Override
            public CSTextDecoration textDecoration() { return CSTextDecoration.UNSET; }

            @Override
            public CSTextTransform textTransform() { return CSTextTransform.UNSET; }

            @Override
            public CSWhiteSpace whiteSpace() { return CSWhiteSpace.UNSET; }

            @Override
            public String wordWrap() { return ""; }

            @Override
            public String textOverflow() { return ""; }

            @Override
            public String textShadow() { return ""; }

            @Override
            public CSVerticalAlign verticalAlign() { return CSVerticalAlign.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.this; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }

    /**
     * 获取布局样式配置
     * 返回CSLayout注解实例，包含当前预设的布局相关属性
     *
     * 【返回值说明】
     * - width/height: ICON_BUTTON/FAB/FAB_MINI有固定尺寸，其他为自适应
     * - padding(): 各预设的内边距值
     * - 其他属性返回空值或UNSET
     */
    public CSLayout getLayout() {
        return new CSLayout() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CSLayout.class;
            }

            @Override
            public CSDisplay display() { return CSDisplay.UNSET; }

            @Override
            public CSPosition position() { return CSPosition.UNSET; }

            @Override
            public String top() { return ""; }

            @Override
            public String left() { return ""; }

            @Override
            public String right() { return ""; }

            @Override
            public String bottom() { return ""; }

            @Override
            public String width() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case ICON_BUTTON: case FAB_MINI: return "40px";
                    case FAB: return "56px";
                    default: return "";
                }
            }

            @Override
            public String height() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case ICON_BUTTON: case FAB_MINI: return "40px";
                    case FAB: return "56px";
                    default: return "";
                }
            }

            @Override
            public String minWidth() { return ""; }

            @Override
            public String maxWidth() { return ""; }

            @Override
            public String minHeight() { return ""; }

            @Override
            public String maxHeight() { return ""; }

            @Override
            public String padding() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_CONTAINED: case MATERIAL_OUTLINED: return "8px 22px";
                    case MATERIAL_TEXT: return "8px 16px";
                    case ANT_PRIMARY: case ANT_DEFAULT: case ANT_DASHED: case ANT_LINK: return "6px 16px";
                    case ELEMENT_PRIMARY: case ELEMENT_SUCCESS: case ELEMENT_WARNING: case ELEMENT_DANGER: return "12px 20px";
                    case ELEMENT_ROUND: return "12px 23px";
                    case BOOTSTRAP_PRIMARY: case BOOTSTRAP_SECONDARY: case BOOTSTRAP_OUTLINE_PRIMARY: return "10px 20px";
                    default: return "";
                }
            }

            @Override
            public String paddingLeft() { return ""; }

            @Override
            public String paddingRight() { return ""; }

            @Override
            public String paddingTop() { return ""; }

            @Override
            public String paddingBottom() { return ""; }

            @Override
            public String margin() { return ""; }

            @Override
            public String marginLeft() { return ""; }

            @Override
            public String marginRight() { return ""; }

            @Override
            public String marginTop() { return ""; }

            @Override
            public String marginBottom() { return ""; }

            @Override
            public CSOverflow overflow() { return CSOverflow.UNSET; }

            @Override
            public CSOverflow overflowX() { return CSOverflow.UNSET; }

            @Override
            public CSOverflow overflowY() { return CSOverflow.UNSET; }

            @Override
            public String zIndex() { return ""; }

            @Override
            public String floatValue() { return ""; }

            @Override
            public String clear() { return ""; }

            @Override
            public CSVisibility visibility() { return CSVisibility.UNSET; }

            @Override
            public String opacity() { return ""; }

            @Override
            public CSCursor cursor() { return CSCursor.UNSET; }

            @Override
            public String pointerEvents() { return ""; }

            @Override
            public String userSelect() { return ""; }

            @Override
            public CSBoxSizing boxSizing() { return CSBoxSizing.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.this; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }

    /**
     * 获取边框样式配置
     * 返回CSBorder注解实例，包含当前预设的边框相关属性
     *
     * 【返回值说明】
     * - border(): 边框简写值
     * - borderStyle(): 边框样式 (DASHED预设为DASHED，其他为SOLID)
     * - borderRadius(): 圆角值 (4px/6px/20px/50%等)
     * - backgroundColor(): 背景色
     * - boxShadow(): 阴影 (仅MATERIAL_CONTAINED和FAB系列有阴影)
     * - 其他属性返回空值或UNSET
     */
    public CSBorder getBorder() {
        return new CSBorder() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CSBorder.class;
            }

            @Override
            public String border() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_OUTLINED: return "1px solid rgba(25, 118, 210, 0.5)";
                    case ANT_PRIMARY: return "1px solid #1677ff";
                    case ANT_DEFAULT: return "1px solid #d9d9d9";
                    case ANT_DASHED: return "1px dashed #d9d9d9";
                    case ELEMENT_PRIMARY: return "1px solid #409eff";
                    case ELEMENT_SUCCESS: return "1px solid #67c23a";
                    case ELEMENT_WARNING: return "1px solid #e6a23c";
                    case ELEMENT_DANGER: return "1px solid #f56c6c";
                    case ELEMENT_ROUND: return "1px solid #409eff";
                    case BOOTSTRAP_PRIMARY: return "1px solid #0d6efd";
                    case BOOTSTRAP_SECONDARY: return "1px solid #6c757d";
                    case BOOTSTRAP_OUTLINE_PRIMARY: return "1px solid #0d6efd";
                    case ICON_BUTTON: return "none";
                    default: return "";
                }
            }

            @Override
            public String borderWidth() { return ""; }

            @Override
            public CSBorderStyle borderStyle() {
                switch (ButtonPreset.this) {
                    case UNSET: return CSBorderStyle.UNSET;
                    case ANT_DASHED: return CSBorderStyle.DASHED;
                    default: return CSBorderStyle.SOLID;
                }
            }

            @Override
            public String borderColor() { return ""; }

            @Override
            public String borderRadius() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_CONTAINED: case MATERIAL_OUTLINED: case MATERIAL_TEXT: return "4px";
                    case ANT_PRIMARY: case ANT_DEFAULT: case ANT_DASHED: case ANT_LINK: return "6px";
                    case ELEMENT_PRIMARY: case ELEMENT_SUCCESS: case ELEMENT_WARNING: case ELEMENT_DANGER: return "4px";
                    case ELEMENT_ROUND: return "20px";
                    case BOOTSTRAP_PRIMARY: case BOOTSTRAP_SECONDARY: case BOOTSTRAP_OUTLINE_PRIMARY: return "6px";
                    case ICON_BUTTON: case FAB: case FAB_MINI: return "50%";
                    default: return "";
                }
            }

            @Override
            public String borderTop() { return ""; }

            @Override
            public String borderRight() { return ""; }

            @Override
            public String borderBottom() { return ""; }

            @Override
            public String borderLeft() { return ""; }

            @Override
            public String background() { return ""; }

            @Override
            public String backgroundColor() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_CONTAINED: return "#1976d2";
                    case MATERIAL_OUTLINED: case MATERIAL_TEXT: return "transparent";
                    case ANT_PRIMARY: return "#1677ff";
                    case ANT_DEFAULT: case ANT_DASHED: return "#ffffff";
                    case ANT_LINK: return "transparent";
                    case ELEMENT_PRIMARY: return "#409eff";
                    case ELEMENT_SUCCESS: return "#67c23a";
                    case ELEMENT_WARNING: return "#e6a23c";
                    case ELEMENT_DANGER: return "#f56c6c";
                    case ELEMENT_ROUND: return "#409eff";
                    case BOOTSTRAP_PRIMARY: return "#0d6efd";
                    case BOOTSTRAP_SECONDARY: return "#6c757d";
                    case BOOTSTRAP_OUTLINE_PRIMARY: return "transparent";
                    case ICON_BUTTON: return "transparent";
                    case FAB: case FAB_MINI: return "#1976d2";
                    default: return "";
                }
            }

            @Override
            public String backgroundImage() { return ""; }

            @Override
            public String backgroundSize() { return ""; }

            @Override
            public String backgroundPosition() { return ""; }

            @Override
            public String backgroundRepeat() { return ""; }

            @Override
            public String boxShadow() {
                switch (ButtonPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_CONTAINED: return "0 3px 1px -2px rgba(0,0,0,0.2), 0 2px 2px 0 rgba(0,0,0,0.14), 0 1px 5px 0 rgba(0,0,0,0.12)";
                    case FAB: case FAB_MINI: return "0 3px 5px -1px rgba(0,0,0,0.2), 0 6px 10px 0 rgba(0,0,0,0.14), 0 1px 18px 0 rgba(0,0,0,0.12)";
                    default: return "";
                }
            }

            @Override
            public String outline() { return ""; }

            @Override
            public String outlineColor() { return ""; }

            @Override
            public String outlineWidth() { return ""; }

            @Override
            public CSBorderStyle outlineStyle() { return CSBorderStyle.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.this; }

            @Override
            public InputPreset inputPreset() { return InputPreset.UNSET; }
        };
    }
}

package net.ooder.annotation.ui.css.preset;

import net.ooder.annotation.ui.css.CSBorder;
import net.ooder.annotation.ui.css.CSFont;
import net.ooder.annotation.ui.css.CSLayout;
import net.ooder.annotation.ui.css.enums.*;

/**
 * Input组件预设样式
 * 基于现代UI框架（Material Design, Ant Design, Element Plus, Bootstrap）的输入框样式方案
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【预设分类】
 * 本枚举定义了18种输入框预设，分为以下几类:
 *
 * 1. Material Design系列 (3种):
 *    - MATERIAL_FILLED: 填充样式，灰色背景，无边框
 *    - MATERIAL_OUTLINED: 描边样式，透明背景，完整边框
 *    - MATERIAL_STANDARD: 标准样式，仅底部边框
 *
 * 2. Ant Design系列 (4种):
 *    - ANT_DEFAULT: 标准输入框，32px高，6px圆角
 *    - ANT_LARGE: 大输入框，40px高
 *    - ANT_SMALL: 小输入框，24px高
 *    - ANT_TEXTAREA: 文本域，自适应高度
 *
 * 3. Element Plus系列 (3种):
 *    - ELEMENT_DEFAULT: 标准输入框，32px高
 *    - ELEMENT_DISABLED: 禁用状态，灰色背景
 *    - ELEMENT_READONLY: 只读状态
 *
 * 4. Bootstrap系列 (3种):
 *    - BOOTSTRAP_DEFAULT: 标准输入框，38px高
 *    - BOOTSTRAP_LARGE: 大输入框，48px高
 *    - BOOTSTRAP_SMALL: 小输入框，31px高
 *
 * 5. 特殊类型预设 (5种):
 *    - SEARCH: 搜索框，带搜索图标
 *    - PASSWORD: 密码框，带显示/隐藏切换
 *    - NUMBER: 数字输入框
 *    - DATE_PICKER: 日期选择器
 *    - UNSET: 不应用任何预设
 *
 * 【预设属性对照表】(完整CSS属性值):
 *
 * ┌──────────────────────┬──────────┬─────────────┬────────────┬──────────────────┐
 * │ 预设                 │ 高度     │ 内边距      │ 边框       │ 背景色           │
 * ├──────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ MATERIAL_FILLED      │ 48px     │ 12px 16px   │ none       │ rgba(0,0,0,0.06) │
 * │ MATERIAL_OUTLINED    │ 48px     │ 12px 16px   │ 1px rgba   │ transparent      │
 * │ MATERIAL_STANDARD    │ 32px     │ 8px 0       │ bottom only│ transparent      │
 * ├──────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ ANT_DEFAULT          │ 32px     │ 4px 11px    │ 1px solid  │ #ffffff          │
 * │ ANT_LARGE            │ 40px     │ 6px 11px    │ 1px solid  │ #ffffff          │
 * │ ANT_SMALL            │ 24px     │ 0px 7px     │ 1px solid  │ #ffffff          │
 * │ ANT_TEXTAREA         │ auto     │ 4px 11px    │ 1px solid  │ #ffffff          │
 * ├──────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ ELEMENT_DEFAULT      │ 32px     │ 0 15px      │ 1px solid  │ #ffffff          │
 * │ ELEMENT_DISABLED     │ 32px     │ 0 15px      │ 1px solid  │ #f5f7fa          │
 * │ ELEMENT_READONLY     │ 32px     │ 0 15px      │ 1px solid  │ #f5f7fa          │
 * ├──────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ BOOTSTRAP_DEFAULT    │ 38px     │ 6px 12px    │ 1px solid  │ #ffffff          │
 * │ BOOTSTRAP_LARGE      │ 48px     │ 12px 24px   │ 1px solid  │ #ffffff          │
 * │ BOOTSTRAP_SMALL      │ 31px     │ 4px 8px     │ 1px solid  │ #ffffff          │
 * ├──────────────────────┼──────────┼─────────────┼────────────┼──────────────────┤
 * │ SEARCH               │ 36px     │ 8px 12px    │ 1px solid  │ #ffffff          │
 * │ PASSWORD             │ 36px     │ 8px 12px    │ 1px solid  │ #ffffff          │
 * │ NUMBER               │ 36px     │ 8px 12px    │ 1px solid  │ #ffffff          │
 * │ DATE_PICKER          │ 36px     │ 8px 12px    │ 1px solid  │ #ffffff          │
 * └──────────────────────┴──────────┴─────────────┴────────────┴──────────────────┘
 *
 * 【圆角设置】:
 * - MATERIAL_FILLED: 4px 4px 0 0 (顶部圆角)
 * - MATERIAL_OUTLINED: 4px
 * - MATERIAL_STANDARD: 0
 * - ANT系列: 6px
 * - ELEMENT系列: 4px
 * - BOOTSTRAP系列: 6px
 * - 特殊类型: 4px
 *
 * 【文字颜色】:
 * - MATERIAL系列: rgba(0,0,0,0.87)
 * - ANT系列: rgba(0,0,0,0.88)
 * - ELEMENT_DEFAULT/READONLY: #606266
 * - ELEMENT_DISABLED: #a8abb2
 * - BOOTSTRAP系列: #212529
 * - 特殊类型: #333333
 *
 * 【使用建议】:
 * 1. 现代Material风格: 使用 MATERIAL_FILLED / MATERIAL_OUTLINED
 * 2. 标准企业应用: 使用 ANT_DEFAULT / ELEMENT_DEFAULT
 * 3. 响应式布局: 使用 BOOTSTRAP系列，配合栅格系统
 * 4. 搜索功能: 使用 SEARCH 预设，带搜索图标
 * 5. 表单密集排列: 使用 ANT_SMALL / BOOTSTRAP_SMALL
 * 6. 大表单输入: 使用 ANT_LARGE / BOOTSTRAP_LARGE
 *
 * 【与ButtonPreset的区别】:
 * ⚠️ 注意: InputPreset用于输入框，ButtonPreset用于按钮
 *          两者的预设值和默认样式不同
 *          输入框关注边框、背景、高度；按钮关注颜色、阴影
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum InputPreset {

    /**
     * 不应用任何预设，使用JS组件默认样式
     * 所有属性返回空值或UNSET
     */
    UNSET,

    /**
     * Material Design填充输入框
     * 高度: 48px
     * 内边距: 12px 16px
     * 边框: none
     * 背景: rgba(0,0,0,0.06) (浅灰)
     * 圆角: 4px 4px 0 0 (顶部圆角)
     * 文字色: rgba(0,0,0,0.87)
     *
     * 【适用场景】
     * Material Design风格的表单，需要明显的输入区域
     */
    MATERIAL_FILLED,

    /**
     * Material Design描边输入框
     * 高度: 48px
     * 内边距: 12px 16px
     * 边框: 1px solid rgba(0,0,0,0.23)
     * 背景: transparent
     * 圆角: 4px
     * 文字色: rgba(0,0,0,0.87)
     *
     * 【适用场景】
     * Material Design风格，需要清晰边界的输入框
     */
    MATERIAL_OUTLINED,

    /**
     * Material Design标准输入框
     * 高度: 32px
     * 内边距: 8px 0
     * 边框: 仅底部 1px solid rgba(0,0,0,0.42)
     * 背景: transparent
     * 圆角: 0
     * 文字色: rgba(0,0,0,0.87)
     *
     * 【适用场景】
     * Material Design风格，极简设计，仅底部边框
     */
    MATERIAL_STANDARD,

    /**
     * Ant Design标准输入框
     * 高度: 32px
     * 内边距: 4px 11px
     * 边框: 1px solid #d9d9d9
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: rgba(0,0,0,0.88)
     *
     * 【适用场景】
     * 企业级应用，Ant Design风格的标准输入框
     */
    ANT_DEFAULT,

    /**
     * Ant Design大输入框
     * 高度: 40px
     * 内边距: 6px 11px
     * 边框: 1px solid #d9d9d9
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: rgba(0,0,0,0.88)
     *
     * 【适用场景】
     * 需要更大输入区域的场景，如搜索框
     */
    ANT_LARGE,

    /**
     * Ant Design小输入框
     * 高度: 24px
     * 内边距: 0px 7px
     * 边框: 1px solid #d9d9d9
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: rgba(0,0,0,0.88)
     *
     * 【适用场景】
     * 空间受限的表单，如表格内编辑
     */
    ANT_SMALL,

    /**
     * Ant Design文本域
     * 高度: auto (自适应)
     * 内边距: 4px 11px
     * 边框: 1px solid #d9d9d9
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: rgba(0,0,0,0.88)
     *
     * 【适用场景】
     * 多行文本输入，如备注、描述
     */
    ANT_TEXTAREA,

    /**
     * Element Plus标准输入框
     * 高度: 32px
     * 内边距: 0 15px
     * 边框: 1px solid #dcdfe6
     * 背景: #ffffff
     * 圆角: 4px
     * 文字色: #606266
     *
     * 【适用场景】
     * Element Plus风格的表单
     */
    ELEMENT_DEFAULT,

    /**
     * Element Plus禁用状态
     * 高度: 32px
     * 内边距: 0 15px
     * 边框: 1px solid #e4e7ed
     * 背景: #f5f7fa (浅灰)
     * 圆角: 4px
     * 文字色: #a8abb2 (浅灰)
     *
     * 【适用场景】
     * 表单中的禁用输入框
     */
    ELEMENT_DISABLED,

    /**
     * Element Plus只读状态
     * 高度: 32px
     * 内边距: 0 15px
     * 边框: 1px solid #dcdfe6
     * 背景: #f5f7fa (浅灰)
     * 圆角: 4px
     * 文字色: #606266
     *
     * 【适用场景】
     * 表单中的只读输入框
     */
    ELEMENT_READONLY,

    /**
     * Bootstrap标准输入框
     * 高度: 38px
     * 内边距: 6px 12px
     * 边框: 1px solid #ced4da
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: #212529
     *
     * 【适用场景】
     * Bootstrap风格的表单
     */
    BOOTSTRAP_DEFAULT,

    /**
     * Bootstrap大输入框
     * 高度: 48px
     * 内边距: 12px 24px
     * 边框: 1px solid #ced4da
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: #212529
     *
     * 【适用场景】
     * Bootstrap风格的大输入框
     */
    BOOTSTRAP_LARGE,

    /**
     * Bootstrap小输入框
     * 高度: 31px
     * 内边距: 4px 8px
     * 边框: 1px solid #ced4da
     * 背景: #ffffff
     * 圆角: 6px
     * 文字色: #212529
     *
     * 【适用场景】
     * Bootstrap风格的小输入框
     */
    BOOTSTRAP_SMALL,

    /**
     * 搜索输入框
     * 高度: 36px
     * 内边距: 8px 12px
     * 边框: 1px solid #e0e0e0
     * 背景: #ffffff
     * 圆角: 4px
     * 文字色: #333333
     *
     * 【适用场景】
     * 搜索功能，通常配合搜索图标使用
     */
    SEARCH,

    /**
     * 密码输入框
     * 高度: 36px
     * 内边距: 8px 12px
     * 边框: 1px solid #e0e0e0
     * 背景: #ffffff
     * 圆角: 4px
     * 文字色: #333333
     *
     * 【适用场景】
     * 密码输入，通常带显示/隐藏切换按钮
     */
    PASSWORD,

    /**
     * 数字输入框
     * 高度: 36px
     * 内边距: 8px 12px
     * 边框: 1px solid #e0e0e0
     * 背景: #ffffff
     * 圆角: 4px
     * 文字色: #333333
     *
     * 【适用场景】
     * 数字输入，通常带增减按钮
     */
    NUMBER,

    /**
     * 日期选择器
     * 高度: 36px
     * 内边距: 8px 12px
     * 边框: 1px solid #e0e0e0
     * 背景: #ffffff
     * 圆角: 4px
     * 文字色: #333333
     *
     * 【适用场景】
     * 日期选择，通常带日历图标和下拉面板
     */
    DATE_PICKER;

    /**
     * 获取字体样式配置
     * 返回CSFont注解实例，包含当前预设的字体相关属性
     *
     * 【返回值说明】
     * - color(): 输入框文字颜色
     * - 其他字体属性返回空值或UNSET
     */
    public CSFont getFont() {
        return new CSFont() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CSFont.class;
            }

            @Override
            public String color() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: case MATERIAL_OUTLINED: case MATERIAL_STANDARD: return "rgba(0, 0, 0, 0.87)";
                    case ANT_DEFAULT: case ANT_LARGE: case ANT_SMALL: case ANT_TEXTAREA: return "rgba(0, 0, 0, 0.88)";
                    case ELEMENT_DEFAULT: return "#606266";
                    case ELEMENT_DISABLED: return "#a8abb2";
                    case ELEMENT_READONLY: return "#606266";
                    case BOOTSTRAP_DEFAULT: case BOOTSTRAP_LARGE: case BOOTSTRAP_SMALL: return "#212529";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "#333333";
                    default: return "";
                }
            }

            @Override
            public String fontSize() { return ""; }

            @Override
            public CSFontWeight fontWeight() { return CSFontWeight.UNSET; }

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
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.this; }
        };
    }

    /**
     * 获取布局样式配置
     * 返回CSLayout注解实例，包含当前预设的布局相关属性
     *
     * 【返回值说明】
     * - height(): 输入框高度
     * - padding(): 内边距
     * - 其他布局属性返回空值或UNSET
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
            public String width() { return ""; }

            @Override
            public String height() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: case MATERIAL_OUTLINED: return "48px";
                    case MATERIAL_STANDARD: return "32px";
                    case ANT_DEFAULT: return "32px";
                    case ANT_LARGE: return "40px";
                    case ANT_SMALL: return "24px";
                    case ANT_TEXTAREA: return "auto";
                    case ELEMENT_DEFAULT: return "32px";
                    case BOOTSTRAP_DEFAULT: return "38px";
                    case BOOTSTRAP_LARGE: return "48px";
                    case BOOTSTRAP_SMALL: return "31px";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "36px";
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
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: case MATERIAL_OUTLINED: return "12px 16px";
                    case MATERIAL_STANDARD: return "8px 0";
                    case ANT_DEFAULT: return "4px 11px";
                    case ANT_LARGE: return "6px 11px";
                    case ANT_SMALL: return "0px 7px";
                    case ANT_TEXTAREA: return "4px 11px";
                    case ELEMENT_DEFAULT: return "0 15px";
                    case BOOTSTRAP_DEFAULT: return "6px 12px";
                    case BOOTSTRAP_LARGE: return "12px 24px";
                    case BOOTSTRAP_SMALL: return "4px 8px";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "8px 12px";
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
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.this; }
        };
    }

    /**
     * 获取边框样式配置
     * 返回CSBorder注解实例，包含当前预设的边框相关属性
     *
     * 【返回值说明】
     * - border(): 边框简写值
     * - borderStyle(): 边框样式
     * - borderRadius(): 圆角值
     * - borderBottom(): 底部边框（仅MATERIAL_STANDARD）
     * - backgroundColor(): 背景色
     * - 其他边框属性返回空值或UNSET
     */
    public CSBorder getBorder() {
        return new CSBorder() {
            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return CSBorder.class;
            }

            @Override
            public String border() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: return "none";
                    case MATERIAL_OUTLINED: return "1px solid rgba(0, 0, 0, 0.23)";
                    case MATERIAL_STANDARD: return "none none solid none";
                    case ANT_DEFAULT: case ANT_LARGE: case ANT_SMALL: case ANT_TEXTAREA: return "1px solid #d9d9d9";
                    case ELEMENT_DEFAULT: return "1px solid #dcdfe6";
                    case ELEMENT_DISABLED: return "1px solid #e4e7ed";
                    case ELEMENT_READONLY: return "1px solid #dcdfe6";
                    case BOOTSTRAP_DEFAULT: case BOOTSTRAP_LARGE: case BOOTSTRAP_SMALL: return "1px solid #ced4da";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "1px solid #e0e0e0";
                    default: return "";
                }
            }

            @Override
            public String borderWidth() { return ""; }

            @Override
            public CSBorderStyle borderStyle() {
                switch (InputPreset.this) {
                    case UNSET: case MATERIAL_STANDARD: return CSBorderStyle.NONE;
                    default: return CSBorderStyle.SOLID;
                }
            }

            @Override
            public String borderColor() { return ""; }

            @Override
            public String borderRadius() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: return "4px 4px 0 0";
                    case MATERIAL_OUTLINED: return "4px";
                    case MATERIAL_STANDARD: return "0";
                    case ANT_DEFAULT: case ANT_LARGE: case ANT_SMALL: case ANT_TEXTAREA: return "6px";
                    case ELEMENT_DEFAULT: return "4px";
                    case BOOTSTRAP_DEFAULT: case BOOTSTRAP_LARGE: case BOOTSTRAP_SMALL: return "6px";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "4px";
                    default: return "";
                }
            }

            @Override
            public String borderTop() { return ""; }

            @Override
            public String borderRight() { return ""; }

            @Override
            public String borderBottom() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_STANDARD: return "1px solid rgba(0, 0, 0, 0.42)";
                    default: return "";
                }
            }

            @Override
            public String borderLeft() { return ""; }

            @Override
            public String background() { return ""; }

            @Override
            public String backgroundColor() {
                switch (InputPreset.this) {
                    case UNSET: return "";
                    case MATERIAL_FILLED: return "rgba(0, 0, 0, 0.06)";
                    case MATERIAL_OUTLINED: case MATERIAL_STANDARD: return "transparent";
                    case ANT_DEFAULT: case ANT_LARGE: case ANT_SMALL: case ANT_TEXTAREA: return "#ffffff";
                    case ELEMENT_DEFAULT: return "#ffffff";
                    case ELEMENT_DISABLED: return "#f5f7fa";
                    case ELEMENT_READONLY: return "#f5f7fa";
                    case BOOTSTRAP_DEFAULT: case BOOTSTRAP_LARGE: case BOOTSTRAP_SMALL: return "#ffffff";
                    case SEARCH: case PASSWORD: case NUMBER: case DATE_PICKER: return "#ffffff";
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
            public String boxShadow() { return ""; }

            @Override
            public String outline() { return ""; }

            @Override
            public String outlineColor() { return ""; }

            @Override
            public String outlineWidth() { return ""; }

            @Override
            public CSBorderStyle outlineStyle() { return CSBorderStyle.UNSET; }

            @Override
            public ButtonPreset buttonPreset() { return ButtonPreset.UNSET; }

            @Override
            public InputPreset inputPreset() { return InputPreset.this; }
        };
    }
}

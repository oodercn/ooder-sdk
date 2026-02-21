package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Card/Panel组件样式变体枚举
 * 定义卡片的视觉样式、语义类型、阴影、圆角、间距和布局变体
 *
 * <p><b>属性作用：</b>
 * 该枚举用于控制Card/Panel组件的视觉表现，包括：
 * <ul>
 *   <li>外观变体 - 卡片的基础样式风格</li>
 *   <li>阴影层级 - 控制卡片的立体感和层级关系</li>
 *   <li>圆角大小 - 控制卡片的边角弧度</li>
 *   <li>语义颜色 - 表达不同的业务语义和状态</li>
 *   <li>间距密度 - 控制卡片内部元素的紧凑程度</li>
 *   <li>布局模式 - 控制卡片的内容结构</li>
 * </ul>
 *
 * <p><b>取值对照表：</b>
 * <table border="1">
 *   <tr><th>类别</th><th>枚举值</th><th>JSON值</th><th>说明</th></tr>
 *   <tr><td rowspan="4">外观变体</td><td>UNSET</td><td>""</td><td>未设置，使用系统默认样式</td></tr>
 *   <tr><td>ELEVATED</td><td>"elevated"</td><td>浮起样式，带阴影的卡片</td></tr>
 *   <tr><td>OUTLINED</td><td>"outlined"</td><td>边框样式，带边框线的卡片</td></tr>
 *   <tr><td>FILLED</td><td>"filled"</td><td>填充样式，带背景色的卡片</td></tr>
 *   <tr><td></td><td>FLAT</td><td>"flat"</td><td>扁平样式，无阴影无边框</td></tr>
 *   <tr><td rowspan="5">阴影层级</td><td>SHADOW_0</td><td>"shadow-0"</td><td>无阴影</td></tr>
 *   <tr><td>SHADOW_1</td><td>"shadow-1"</td><td>轻微阴影，低层级</td></tr>
 *   <tr><td>SHADOW_2</td><td>"shadow-2"</td><td>中等阴影，默认层级</td></tr>
 *   <tr><td>SHADOW_3</td><td>"shadow-3"</td><td>明显阴影，高层级</td></tr>
 *   <tr><td>SHADOW_4</td><td>"shadow-4"</td><td>强烈阴影，最高层级</td></tr>
 *   <tr><td rowspan="6">圆角大小</td><td>ROUNDED_NONE</td><td>"rounded-none"</td><td>无圆角，直角</td></tr>
 *   <tr><td>ROUNDED_SM</td><td>"rounded-sm"</td><td>小圆角</td></tr>
 *   <tr><td>ROUNDED</td><td>"rounded"</td><td>默认圆角</td></tr>
 *   <tr><td>ROUNDED_LG</td><td>"rounded-lg"</td><td>大圆角</td></tr>
 *   <tr><td>ROUNDED_XL</td><td>"rounded-xl"</td><td>超大圆角</td></tr>
 *   <tr><td>ROUNDED_FULL</td><td>"rounded-full"</td><td>完全圆角，胶囊形</td></tr>
 *   <tr><td rowspan="6">语义颜色</td><td>PRIMARY</td><td>"primary"</td><td>主色，品牌色</td></tr>
 *   <tr><td>SECONDARY</td><td>"secondary"</td><td>次要色，辅助色</td></tr>
 *   <tr><td>SUCCESS</td><td>"success"</td><td>成功状态，绿色系</td></tr>
 *   <tr><td>WARNING</td><td>"warning"</td><td>警告状态，黄色/橙色系</td></tr>
 *   <tr><td>DANGER</td><td>"danger"</td><td>危险/错误状态，红色系</td></tr>
 *   <tr><td>INFO</td><td>"info"</td><td>信息提示，蓝色系</td></tr>
 *   <tr><td rowspan="3">间距密度</td><td>COMPACT</td><td>"compact"</td><td>紧凑间距，高密度内容</td></tr>
 *   <tr><td>COMFORTABLE</td><td>"comfortable"</td><td>舒适间距，平衡布局</td></tr>
 *   <tr><td>SPACIOUS</td><td>"spacious"</td><td>宽松间距，呼吸感强</td></tr>
 *   <tr><td rowspan="3">布局模式</td><td>HEADER_ONLY</td><td>"header-only"</td><td>仅头部模式</td></tr>
 *   <tr><td>MEDIA_TOP</td><td>"media-top"</td><td>媒体内容置顶</td></tr>
 *   <tr><td>ACTIONS_BOTTOM</td><td>"actions-bottom"</td><td>操作按钮置底</td></tr>
 * </table>
 *
 * <p><b>使用建议：</b>
 * <ul>
 *   <li><b>外观变体选择：</b>
 *     <ul>
 *       <li>ELEVATED - 用于需要突出显示的卡片，如仪表板卡片</li>
 *       <li>OUTLINED - 用于列表项或分组内容，视觉边界清晰</li>
 *       <li>FILLED - 用于强调区域或状态展示</li>
 *       <li>FLAT - 用于嵌套卡片或极简设计</li>
 *     </ul>
 *   </li>
 *   <li><b>阴影层级选择：</b>
 *     <ul>
 *       <li>SHADOW_0 - 模态框下的背景层</li>
 *       <li>SHADOW_1~2 - 普通卡片</li>
 *       <li>SHADOW_3~4 - 悬浮卡片、下拉菜单</li>
 *     </ul>
 *   </li>
 *   <li><b>语义颜色使用场景：</b>
 *     <ul>
 *       <li>PRIMARY - 主要操作卡片、品牌展示</li>
 *       <li>SUCCESS - 操作成功提示、完成状态</li>
 *       <li>WARNING - 注意事项、需要关注的内容</li>
 *       <li>DANGER - 错误提示、删除确认</li>
 *       <li>INFO - 帮助信息、提示说明</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><b>常见组合：</b>
 * <pre>
 * // 标准卡片 - 浮起外观 + 默认阴影 + 默认圆角
 * CSCardVariant.ELEVATED + CSCardVariant.SHADOW_2 + CSCardVariant.ROUNDED
 *
 * // 信息提示卡片 - 填充样式 + 信息色 + 中等圆角
 * CSCardVariant.FILLED + CSCardVariant.INFO + CSCardVariant.ROUNDED_LG
 *
 * // 警告卡片 - 边框样式 + 警告色 + 紧凑间距
 * CSCardVariant.OUTLINED + CSCardVariant.WARNING + CSCardVariant.COMPACT
 *
 * // 悬浮操作卡片 - 强烈阴影 + 主色 + 宽松间距
 * CSCardVariant.SHADOW_4 + CSCardVariant.PRIMARY + CSCardVariant.SPACIOUS
 *
 * // 媒体卡片 - 媒体置顶布局 + 大圆角 + 无阴影
 * CSCardVariant.MEDIA_TOP + CSCardVariant.ROUNDED_XL + CSCardVariant.SHADOW_0
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see net.ooder.annotation.ui.css.component.CardStyle
 * @see net.ooder.annotation.ui.css.preset.CardPreset
 */
public enum CSCardVariant {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "elevated")
    ELEVATED("elevated"),

    @JSONField(name = "outlined")
    OUTLINED("outlined"),

    @JSONField(name = "filled")
    FILLED("filled"),

    @JSONField(name = "flat")
    FLAT("flat"),

    @JSONField(name = "shadow-0")
    SHADOW_0("shadow-0"),

    @JSONField(name = "shadow-1")
    SHADOW_1("shadow-1"),

    @JSONField(name = "shadow-2")
    SHADOW_2("shadow-2"),

    @JSONField(name = "shadow-3")
    SHADOW_3("shadow-3"),

    @JSONField(name = "shadow-4")
    SHADOW_4("shadow-4"),

    @JSONField(name = "rounded-none")
    ROUNDED_NONE("rounded-none"),

    @JSONField(name = "rounded-sm")
    ROUNDED_SM("rounded-sm"),

    @JSONField(name = "rounded")
    ROUNDED("rounded"),

    @JSONField(name = "rounded-lg")
    ROUNDED_LG("rounded-lg"),

    @JSONField(name = "rounded-xl")
    ROUNDED_XL("rounded-xl"),

    @JSONField(name = "rounded-full")
    ROUNDED_FULL("rounded-full"),

    @JSONField(name = "primary")
    PRIMARY("primary"),

    @JSONField(name = "secondary")
    SECONDARY("secondary"),

    @JSONField(name = "success")
    SUCCESS("success"),

    @JSONField(name = "warning")
    WARNING("warning"),

    @JSONField(name = "danger")
    DANGER("danger"),

    @JSONField(name = "info")
    INFO("info"),

    @JSONField(name = "compact")
    COMPACT("compact"),

    @JSONField(name = "comfortable")
    COMFORTABLE("comfortable"),

    @JSONField(name = "spacious")
    SPACIOUS("spacious"),

    @JSONField(name = "header-only")
    HEADER_ONLY("header-only"),

    @JSONField(name = "media-top")
    MEDIA_TOP("media-top"),

    @JSONField(name = "actions-bottom")
    ACTIONS_BOTTOM("actions-bottom");

    private final String value;

    CSCardVariant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

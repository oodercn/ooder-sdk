package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS position属性枚举
 * 
 * 【大模型读取指南】
 * 本枚举定义CSS position属性的所有合法取值，用于控制元素在文档中的定位方式。
 * 使用场景：UI组件库、CSS生成器、布局配置等需要程序化操作CSS position的场景。
 * 序列化：使用fastjson的@JSONField注解，序列化时会输出对应的CSS字符串值。
 * 
 * 【属性作用】
 * position属性定义元素的定位方式，决定元素如何相对于其正常位置或包含块进行定位。
 * 它是CSS布局系统的核心属性，直接影响元素在页面中的渲染位置和层级关系。
 * 
 * 【取值对照表】
 * | 枚举值  | CSS值   | 说明                              |
 * |--------|---------|-----------------------------------|
 * | UNSET  | ""      | 未设置，使用默认值或继承值          |
 * | STATIC | "static"| 静态定位，正常文档流中的位置        |
 * | RELATIVE| "relative"| 相对定位，相对于正常位置的偏移     |
 * | ABSOLUTE| "absolute"| 绝对定位，相对于最近的定位祖先元素  |
 * | FIXED  | "fixed" | 固定定位，相对于视口固定位置        |
 * | STICKY | "sticky"| 粘性定位，基于滚动位置的相对/固定切换|
 * 
 * 【定位类型说明】
 * 1. 静态定位(STATIC)：
 *    - 元素按照正常文档流排列
 *    - top/right/bottom/left属性无效
 *    - 默认值
 * 
 * 2. 相对定位(RELATIVE)：
 *    - 元素先按正常文档流放置，再相对于该位置偏移
 *    - 不脱离文档流，原位置保留
 *    - 可作为子元素绝对定位的包含块
 * 
 * 3. 绝对定位(ABSOLUTE)：
 *    - 元素脱离文档流，不占据空间
 *    - 相对于最近的非static定位祖先元素定位
 *    - 若无定位祖先，则相对于初始包含块(通常是视口)
 * 
 * 4. 固定定位(FIXED)：
 *    - 元素脱离文档流
 *    - 相对于视口固定位置，不随滚动移动
 *    - 常用于导航栏、悬浮按钮等
 * 
 * 5. 粘性定位(STICKY)：
 *    - 混合了相对定位和固定定位的特性
 *    - 在阈值内表现为相对定位，超出阈值后表现为固定定位
 *    - 必须指定top/right/bottom/left之一
 * 
 * 【使用建议】
 * - 默认使用STATIC，除非需要特殊定位效果
 * - 需要微调位置但不影响布局时，使用RELATIVE
 * - 需要完全控制元素位置时，使用ABSOLUTE
 * - 需要元素始终可见(如导航栏)时，使用FIXED
 * - 需要元素在滚动时吸附到特定位置时，使用STICKY
 * - 使用ABSOLUTE/FIXED/STICKY时，注意z-index控制层级
 * 
 * 【常见组合】
 * - position: ABSOLUTE配合top/right/bottom/left实现精确定位
 *   示例：ABSOLUTE + top: 0 + left: 0 (定位到左上角)
 *   
 * - position: RELATIVE配合top/left实现微调偏移
 *   示例：RELATIVE + top: -5px (向上偏移5像素)
 *   
 * - position: FIXED配合bottom/right实现悬浮按钮
 *   示例：FIXED + bottom: 20px + right: 20px
 *   
 * - position: STICKY配合top实现吸顶效果
 *   示例：STICKY + top: 0 (滚动到顶部时吸附)
 *   
 * - position: ABSOLUTE配合transform: translate实现居中
 *   示例：ABSOLUTE + top: 50% + left: 50% + transform: translate(-50%, -50%)
 *
 * @author OODER Team
 * @version 2.0.0
 */
public enum CSPosition {

    @JSONField(name = "")
    UNSET(""),

    @JSONField(name = "static")
    STATIC("static"),
    
    @JSONField(name = "relative")
    RELATIVE("relative"),
    
    @JSONField(name = "absolute")
    ABSOLUTE("absolute"),
    
    @JSONField(name = "fixed")
    FIXED("fixed"),
    
    @JSONField(name = "sticky")
    STICKY("sticky");
    
    private final String value;
    
    CSPosition(String value) {
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

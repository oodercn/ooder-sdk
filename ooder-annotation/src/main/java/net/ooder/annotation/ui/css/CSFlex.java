package net.ooder.annotation.ui.css;

import java.lang.annotation.*;

/**
 * CSS Flex 布局样式注解
 * 对应虚拟DOM类型：flex, layout, container
 *
 * 【大模型读取指南】
 * ═══════════════════════════════════════════════════════════════
 *
 * 【Flexbox 核心概念】
 * Flexbox 是一种一维布局模型，用于在容器内分配空间和对齐项目。
 * - 主轴 (Main Axis): 由 flexDirection 决定，项目沿此轴排列
 * - 交叉轴 (Cross Axis): 垂直于主轴的轴
 *
 * 【样式覆盖优先级】(从高到低):
 * 1. 运行时动态样式 (ContainerMeta.update())
 * 2. 本注解显式设置的属性
 * 3. 父容器继承的样式
 * 4. 浏览器默认样式
 *
 * 【零注解默认值】(当没有任何注解时，浏览器/JS组件默认值):
 * - flexDirection: "row" (水平排列)
 * - flexWrap: "nowrap" (不换行)
 * - justifyContent: "flex-start" (左对齐)
 * - alignItems: "stretch" (拉伸填满)
 * - alignContent: "stretch" (多行时拉伸)
 * - gap: "0" (无间隙)
 *
 * 【常用布局模式】:
 * 模式1 - 水平居中: flexDirection="row" + justifyContent="center"
 * 模式2 - 垂直居中: flexDirection="column" + justifyContent="center" + alignItems="center"
 * 模式3 - 两端对齐: justifyContent="space-between"
 * 模式4 - 等分布局: flex="1" (子元素设置)
 * 模式5 - 自动换行: flexWrap="wrap"
 *
 * 【与CSLayout的关系】
 * ⚠️ 注意: CSFlex 专用于 Flexbox 布局属性
 *          CSLayout 用于通用布局属性 (width, height, padding等)
 *          两者通常配合使用
 *
 * @author OODER Team
 * @version 2.0.0
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CSFlex {

    /**
     * Flex 方向
     * 格式: CSS值，如 "row", "row-reverse", "column", "column-reverse"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "row")
     *
     * 【常用值】
     * - "row": 水平排列 (从左到右)
     * - "row-reverse": 水平排列 (从右到左)
     * - "column": 垂直排列 (从上到下)
     * - "column-reverse": 垂直排列 (从下到上)
     *
     * 【JS默认值】
     * - 水平布局容器: "row"
     * - 垂直布局容器: "column"
     */
    String flexDirection() default "";

    /**
     * Flex 换行
     * 格式: CSS值，如 "nowrap", "wrap", "wrap-reverse"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "nowrap")
     *
     * 【常用值】
     * - "nowrap": 不换行 (默认值，项目可能溢出)
     * - "wrap": 换行 (第一行在上方)
     * - "wrap-reverse": 换行 (第一行在下方)
     *
     * 【使用场景】
     * - 固定宽度项目列表: "wrap" (如标签云、图片列表)
     * - 单行按钮组: "nowrap"
     */
    String flexWrap() default "";

    /**
     * Flex 流（方向和换行的简写）
     * 格式: CSS值，如 "row nowrap", "column wrap"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【说明】
     * flexFlow 是 flexDirection 和 flexWrap 的简写
     * 如: "row wrap" = flexDirection="row" + flexWrap="wrap"
     *
     * 【建议】
     * 建议分别使用 flexDirection 和 flexWrap，代码更清晰
     */
    String flexFlow() default "";

    /**
     * 主轴对齐方式
     * 格式: CSS值，如 "flex-start", "center", "space-between", "space-around"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "flex-start")
     *
     * 【常用值】
     * - "flex-start": 左对齐 (默认值)
     * - "flex-end": 右对齐
     * - "center": 居中
     * - "space-between": 两端对齐，项目间等距
     * - "space-around": 项目两侧等距
     * - "space-evenly": 项目间和两端都等距
     *
     * 【使用场景】
     * - 按钮组居中: "center"
     * - 导航栏两端对齐: "space-between"
     * - 卡片等分布局: "space-around"
     */
    String justifyContent() default "";

    /**
     * 交叉轴对齐方式
     * 格式: CSS值，如 "stretch", "center", "flex-start", "baseline"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "stretch")
     *
     * 【常用值】
     * - "stretch": 拉伸填满容器 (默认值)
     * - "center": 居中对齐
     * - "flex-start": 顶部对齐
     * - "flex-end": 底部对齐
     * - "baseline": 基线对齐 (文本对齐)
     *
     * 【使用场景】
     * - 垂直居中: "center"
     * - 表单标签与输入框对齐: "center"
     * - 不同高度卡片顶部对齐: "flex-start"
     */
    String alignItems() default "";

    /**
     * 多行对齐方式
     * 格式: CSS值，如 "stretch", "center", "space-between"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "stretch")
     *
     * 【说明】
     * 仅在 flexWrap="wrap" 且有多行时生效
     * 控制行与行之间的对齐方式
     *
     * 【常用值】
     * - "stretch": 拉伸填满 (默认值)
     * - "center": 居中对齐
     * - "space-between": 两端对齐
     * - "space-around": 行两侧等距
     */
    String alignContent() default "";

    /**
     * 项目排序
     * 格式: 整数值，如 "0", "1", "-1"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "0")
     *
     * 【说明】
     * 数值越小，排列越靠前
     * 可以为负值
     *
     * 【使用场景】
     * - 动态调整项目顺序
     * - 将某个项目移到最前: "-1"
     */
    String order() default "";

    /**
     * 项目放大比例
     * 格式: 数值 (≥0)，如 "0", "1", "2"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "0")
     *
     * 【说明】
     * - 0: 不放大 (默认值)
     * - 1: 等分剩余空间
     * - 2: 占据两倍于1的空间
     *
     * 【使用场景】
     * - 等分布局: 所有子元素设置 "1"
     * - 侧边栏+主内容: 侧边栏 "0"，主内容 "1"
     */
    String flexGrow() default "";

    /**
     * 项目缩小比例
     * 格式: 数值 (≥0)，如 "1", "0"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "1")
     *
     * 【说明】
     * - 1: 空间不足时缩小 (默认值)
     * - 0: 不缩小
     *
     * 【使用场景】
     * - 固定宽度按钮: "0" (不缩小)
     * - 自适应输入框: "1" (允许缩小)
     */
    String flexShrink() default "";

    /**
     * 项目基础大小
     * 格式: CSS长度值，如 "auto", "200px", "50%"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "auto")
     *
     * 【说明】
     * 定义项目在分配剩余空间之前的初始大小
     *
     * 【常用值】
     * - "auto": 根据内容自动计算
     * - "200px": 固定宽度
     * - "0": 完全依赖flexGrow分配空间
     */
    String flexBasis() default "";

    /**
     * Flex 简写属性
     * 格式: CSS值，如 "0 1 auto", "1", "0 0 200px"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【说明】
     * flex 是 flexGrow + flexShrink + flexBasis 的简写
     * 如: "1 0 200px" = flexGrow="1" + flexShrink="0" + flexBasis="200px"
     *
     * 【常用简写】
     * - "auto" = "1 1 auto"
     * - "none" = "0 0 auto"
     * - "1" = "1 1 0%"
     *
     * 【建议】
     * 建议分别使用 flexGrow/flexShrink/flexBasis，代码更清晰
     */
    String flex() default "";

    /**
     * 项目自身对齐方式
     * 格式: CSS值，如 "auto", "center", "flex-start"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "auto")
     *
     * 【说明】
     * 覆盖父容器的 alignItems 设置
     * 仅对当前项目生效
     *
     * 【常用值】
     * - "auto": 继承父容器 alignItems (默认值)
     * - "center": 居中
     * - "flex-start": 顶部对齐
     * - "flex-end": 底部对齐
     */
    String alignSelf() default "";

    /**
     * 间隙（行列间距）
     * 格式: CSS长度值，如 "10px", "1rem", "16px"
     * 默认值: "" (空字符串表示不设置，使用浏览器默认 "0")
     *
     * 【说明】
     * 同时设置 rowGap 和 columnGap
     * 现代 Flexbox 推荐用法，替代 margin
     *
     * 【使用场景】
     * - 按钮组间距: "8px"
     * - 卡片列表间距: "16px"
     * - 表单项间距: "24px"
     *
     * 【JS默认值对照】
     * - 紧凑布局: "8px"
     * - 标准布局: "16px"
     * - 宽松布局: "24px"
     */
    String gap() default "";

    /**
     * 行间隙
     * 格式: CSS长度值，如 "10px", "1rem"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【说明】
     * 多行布局时，行与行之间的间距
     * 仅在 flexWrap="wrap" 时生效
     */
    String rowGap() default "";

    /**
     * 列间隙
     * 格式: CSS长度值，如 "10px", "1rem"
     * 默认值: "" (空字符串表示不设置)
     *
     * 【说明】
     * 项目之间的水平间距
     */
    String columnGap() default "";
}

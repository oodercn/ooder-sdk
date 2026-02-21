package net.ooder.annotation.ui.css.enums;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * CSS cursor属性枚举 - 定义鼠标指针样式
 *
 * <p>该枚举用于控制元素上的鼠标光标外观，提供视觉反馈以指示元素的可交互性。
 * 在UI组件开发中合理使用cursor属性可以显著提升用户体验。</p>
 *
 * <h2>取值对照表</h2>
 * <table border="1">
 *   <tr><th>枚举值</th><th>CSS值</th><th>光标样式</th><th>典型使用场景</th></tr>
 *   <tr><td>UNSET</td><td>""</td><td>继承/默认</td><td>不设置光标样式，继承父元素或使用浏览器默认</td></tr>
 *   <tr><td>AUTO</td><td>auto</td><td>自动</td><td>浏览器根据上下文自动选择合适的光标（默认行为）</td></tr>
 *   <tr><td>DEFAULT</td><td>default</td><td>箭头</td><td>普通静态元素，无特殊交互状态</td></tr>
 *   <tr><td>NONE</td><td>none</td><td>隐藏</td><td>需要隐藏光标的场景，如全屏游戏、视频播放</td></tr>
 *   <tr><td>POINTER</td><td>pointer</td><td>手型</td><td>可点击的链接、按钮、可交互元素</td></tr>
 *   <tr><td>TEXT</td><td>text</td><td>I型文本</td><td>可选中文本的区域、输入框</td></tr>
 *   <tr><td>MOVE</td><td>move</td><td>十字箭头</td><td>可拖动的元素、拖拽排序</td></tr>
 *   <tr><td>GRAB</td><td>grab</td><td>张开手</td><td>可抓取的内容，如地图拖拽、滑块</td></tr>
 *   <tr><td>GRABBING</td><td>grabbing</td><td>握拳</td><td>正在抓取/拖拽中的状态</td></tr>
 *   <tr><td>NOT_ALLOWED</td><td>not-allowed</td><td>禁止符号</td><td>禁用状态按钮、不可操作区域</td></tr>
 *   <tr><td>WAIT</td><td>wait</td><td>加载/等待</td><td>加载中、处理中的状态指示</td></tr>
 *   <tr><td>HELP</td><td>help</td><td>问号</td><td>帮助信息、提示说明区域</td></tr>
 *   <tr><td>CROSSHAIR</td><td>crosshair</td><td>十字准星</td><td>图像裁剪、精确选择、绘图工具</td></tr>
 *   <tr><td>COL_RESIZE</td><td>col-resize</td><td>左右箭头</td><td>表格列宽调整、水平分隔线拖拽</td></tr>
 *   <tr><td>ROW_RESIZE</td><td>row-resize</td><td>上下箭头</td><td>表格行高调整、垂直分隔线拖拽</td></tr>
 *   <tr><td>NS_RESIZE</td><td>ns-resize</td><td>上下双箭头</td><td>上下方向的边缘调整</td></tr>
 *   <tr><td>EW_RESIZE</td><td>ew-resize</td><td>左右双箭头</td><td>左右方向的边缘调整</td></tr>
 * </table>
 *
 * <h2>使用建议</h2>
 * <ul>
 *   <li><b>交互一致性</b>：相同功能的元素应使用相同的光标样式</li>
 *   <li><b>状态反馈</b>：配合:hover、:active等伪类提供动态光标变化</li>
 *   <li><b>性能考虑</b>：避免在大量元素上频繁切换光标样式</li>
 *   <li><b>兼容性</b>：grab/grabbing在IE11及以下不支持，需考虑降级方案</li>
 * </ul>
 *
 * <h2>常见组合模式</h2>
 * <pre>
 * // 按钮交互
 * button:hover { cursor: POINTER; }
 * button:disabled { cursor: NOT_ALLOWED; }
 *
 * // 拖拽交互
 * .draggable { cursor: GRAB; }
 * .draggable:active { cursor: GRABBING; }
 *
 * // 调整大小
 * .resizer-h { cursor: COL_RESIZE; }
 * .resizer-v { cursor: ROW_RESIZE; }
 *
 * // 加载状态
 * .loading { cursor: WAIT; }
 * </pre>
 *
 * @author OODER Team
 * @version 2.0.0
 * @see <a href="https://developer.mozilla.org/zh-CN/docs/Web/CSS/cursor">MDN Cursor文档</a>
 */
public enum CSCursor {

    /**
     * 未设置 - 继承父元素或使用浏览器默认光标
     */
    @JSONField(name = "")
    UNSET(""),

    /**
     * 自动 - 浏览器根据上下文自动选择合适的光标
     */
    @JSONField(name = "auto")
    AUTO("auto"),

    /**
     * 默认 - 标准箭头光标，用于普通静态元素
     */
    @JSONField(name = "default")
    DEFAULT("default"),

    /**
     * 无 - 隐藏光标
     * 适用于全屏应用、游戏、视频播放等场景
     */
    @JSONField(name = "none")
    NONE("none"),

    /**
     * 指针/手型 - 表示元素可点击
     * 用于链接、按钮等可交互元素
     */
    @JSONField(name = "pointer")
    POINTER("pointer"),

    /**
     * 文本 - I型光标，表示可选择文本
     * 用于输入框、文本区域等
     */
    @JSONField(name = "text")
    TEXT("text"),

    /**
     * 移动 - 十字箭头，表示元素可拖动
     * 用于拖拽排序、可移动面板等
     */
    @JSONField(name = "move")
    MOVE("move"),

    /**
     * 抓取 - 张开的手，表示可抓取内容
     * 常用于地图、画布拖拽
     */
    @JSONField(name = "grab")
    GRAB("grab"),

    /**
     * 抓取中 - 握拳，表示正在抓取
     * 配合GRAB使用，在:active状态下切换
     */
    @JSONField(name = "grabbing")
    GRABBING("grabbing"),

    /**
     * 禁止 - 圆圈加斜杠，表示操作不允许
     * 用于禁用状态的元素
     */
    @JSONField(name = "not-allowed")
    NOT_ALLOWED("not-allowed"),

    /**
     * 等待 - 加载动画光标，表示处理中
     * 用于异步操作、页面加载等场景
     */
    @JSONField(name = "wait")
    WAIT("wait"),

    /**
     * 帮助 - 问号光标，表示可获取帮助
     * 用于帮助按钮、提示区域
     */
    @JSONField(name = "help")
    HELP("help"),

    /**
     * 十字准星 - 精确选择光标
     * 用于图像处理、绘图、截图工具
     */
    @JSONField(name = "crosshair")
    CROSSHAIR("crosshair"),

    /**
     * 列调整 - 左右箭头，表示可调整列宽
     * 用于表格列分隔线
     */
    @JSONField(name = "col-resize")
    COL_RESIZE("col-resize"),

    /**
     * 行调整 - 上下箭头，表示可调整行高
     * 用于表格行分隔线
     */
    @JSONField(name = "row-resize")
    ROW_RESIZE("row-resize"),

    /**
     * 南北调整 - 上下双箭头
     * 用于上下方向的边缘调整
     */
    @JSONField(name = "ns-resize")
    NS_RESIZE("ns-resize"),

    /**
     * 东西调整 - 左右双箭头
     * 用于左右方向的边缘调整
     */
    @JSONField(name = "ew-resize")
    EW_RESIZE("ew-resize");

    private final String value;

    CSCursor(String value) {
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

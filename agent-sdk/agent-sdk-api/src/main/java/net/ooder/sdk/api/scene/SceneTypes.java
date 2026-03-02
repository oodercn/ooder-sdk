package net.ooder.sdk.api.scene;

/**
 * 标准场景类型常量
 *
 * <p>定义常用的场景类型，用于能力注册时声明支持的场景类型。</p>
 * <p>能力可以通过 {@link net.ooder.sdk.api.capability.Capability#getSupportedSceneTypes()}
 * 声明支持的场景类型，实现声明式场景自动匹配。</p>
 *
 * @author ooder
 * @since 2.3
 */
public final class SceneTypes {

    private SceneTypes() {
        // 私有构造函数，防止实例化
    }

    // ==================== 基础控制场景 ====================

    /**
     * 开关控制场景
     * 用于控制设备的开关状态
     */
    public static final String SWITCH = "switch";

    /**
     * 调光场景
     * 用于控制灯光的亮度
     */
    public static final String DIMMER = "dimmer";

    /**
     * 颜色控制场景
     * 用于控制灯光的颜色
     */
    public static final String COLOR = "color";

    /**
     * 温度控制场景
     * 用于控制环境温度
     */
    public static final String TEMPERATURE = "temperature";

    /**
     * 湿度控制场景
     * 用于控制环境湿度
     */
    public static final String HUMIDITY = "humidity";

    // ==================== 环境场景 ====================

    /**
     * 安防场景
     * 用于安防监控和报警
     */
    public static final String SECURITY = "security";

    /**
     * 监控场景
     * 用于视频监控
     */
    public static final String MONITOR = "monitor";

    /**
     * 窗帘控制场景
     * 用于控制窗帘开合
     */
    public static final String CURTAIN = "curtain";

    /**
     * 门锁控制场景
     * 用于控制智能门锁
     */
    public static final String LOCK = "lock";

    // ==================== 定时场景 ====================

    /**
     * 定时场景
     * 用于定时执行任务
     */
    public static final String SCHEDULE = "schedule";

    /**
     * 延时场景
     * 用于延时执行任务
     */
    public static final String DELAY = "delay";

    // ==================== 生活模式场景 ====================

    /**
     * 离家场景
     * 用户离开家时的场景模式
     */
    public static final String AWAY = "away";

    /**
     * 回家场景
     * 用户回家时的场景模式
     */
    public static final String HOME = "home";

    /**
     * 睡眠场景
     * 用户准备睡觉时的场景模式
     */
    public static final String SLEEP = "sleep";

    /**
     * 起床场景
     * 用户起床时的场景模式
     */
    public static final String WAKE_UP = "wake_up";

    /**
     * 影院场景
     * 观看电影时的场景模式
     */
    public static final String MOVIE = "movie";

    /**
     * 阅读场景
     * 阅读时的场景模式
     */
    public static final String READING = "reading";

    /**
     * 用餐场景
     * 用餐时的场景模式
     */
    public static final String DINING = "dining";

    /**
     * 会客场景
     * 接待客人时的场景模式
     */
    public static final String GUEST = "guest";

    // ==================== 娱乐场景 ====================

    /**
     * 音乐场景
     * 播放音乐时的场景模式
     */
    public static final String MUSIC = "music";

    /**
     * 游戏场景
     * 游戏时的场景模式
     */
    public static final String GAME = "game";

    /**
     * 派对场景
     * 聚会派对时的场景模式
     */
    public static final String PARTY = "party";

    // ==================== 节能场景 ====================

    /**
     * 节能场景
     * 节能模式
     */
    public static final String ENERGY_SAVING = "energy_saving";

    /**
     * 环保场景
     * 环保模式
     */
    public static final String ECO = "eco";

    // ==================== 通知场景 ====================

    /**
     * 通知场景
     * 用于发送通知
     */
    public static final String NOTIFICATION = "notification";

    /**
     * 告警场景
     * 用于处理告警
     */
    public static final String ALERT = "alert";

    /**
     * 获取所有预定义的场景类型
     *
     * @return 场景类型数组
     */
    public static String[] getAllSceneTypes() {
        return new String[]{
            SWITCH, DIMMER, COLOR, TEMPERATURE, HUMIDITY,
            SECURITY, MONITOR, CURTAIN, LOCK,
            SCHEDULE, DELAY,
            AWAY, HOME, SLEEP, WAKE_UP, MOVIE, READING, DINING, GUEST,
            MUSIC, GAME, PARTY,
            ENERGY_SAVING, ECO,
            NOTIFICATION, ALERT
        };
    }

    /**
     * 检查是否为有效的场景类型
     *
     * @param sceneType 场景类型
     * @return true表示有效
     */
    public static boolean isValidSceneType(String sceneType) {
        if (sceneType == null || sceneType.isEmpty()) {
            return false;
        }
        for (String type : getAllSceneTypes()) {
            if (type.equals(sceneType)) {
                return true;
            }
        }
        return false;
    }
}

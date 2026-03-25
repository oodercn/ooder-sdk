package net.ooder.scene.rad;

import java.util.Map;

/**
 * RAD 适配器接口
 *
 * <p>用于与低代码平台集成的适配器接口</p>
 *
 * @author Ooder Team
 * @since 3.0.0
 */
public interface RadAdapter {

    /**
     * 获取适配器名称
     *
     * @return 名称
     */
    String getName();

    /**
     * 获取适配器类型
     *
     * @return 类型: form, flow, page
     */
    String getType();

    /**
     * 触发场景
     *
     * @param eventType 事件类型
     * @param eventData 事件数据
     * @return 场景实例 ID
     */
    String triggerScene(String eventType, Map<String, Object> eventData);

    /**
     * 检查是否支持该事件
     *
     * @param eventType 事件类型
     * @return 是否支持
     */
    boolean supports(String eventType);
}

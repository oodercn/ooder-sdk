package net.ooder.sdk.api.capability;

import java.util.List;
import java.util.Map;

/**
 * 能力接口，定义Agent的能力基本信息
 * 包含能力ID、名称、类型、版本、状态等属性
 *
 * @author ooder
 * @since 2.3
 */
public interface Capability {

    /**
     * 获取能力ID
     * @return 能力唯一标识符
     */
    String getCapId();

    /**
     * 设置能力ID
     * @param capId 能力唯一标识符
     */
    void setCapId(String capId);

    /**
     * 获取能力名称
     * @return 能力名称
     */
    String getName();

    /**
     * 设置能力名称
     * @param name 能力名称
     */
    void setName(String name);

    /**
     * 获取能力类型
     * @return 能力类型
     */
    String getType();

    /**
     * 设置能力类型
     * @param type 能力类型
     */
    void setType(String type);

    /**
     * 获取能力版本
     * @return 能力版本号
     */
    String getVersion();

    /**
     * 设置能力版本
     * @param version 能力版本号
     */
    void setVersion(String version);

    /**
     * 获取能力描述
     * @return 能力描述信息
     */
    String getDescription();

    /**
     * 设置能力描述
     * @param description 能力描述信息
     */
    void setDescription(String description);

    /**
     * 获取能力状态
     * @return 能力状态
     */
    CapabilityStatus getStatus();

    /**
     * 设置能力状态
     * @param status 能力状态
     */
    void setStatus(CapabilityStatus status);

    /**
     * 设置能力状态（字符串形式）
     * @param status 能力状态字符串
     */
    void setStatus(String status);

    /**
     * 获取能力配置
     * @return 能力配置信息
     */
    Map<String, Object> getConfig();

    /**
     * 设置能力配置
     * @param config 能力配置信息
     */
    void setConfig(Map<String, Object> config);

    /**
     * 获取能力标识ID
     * @return 能力标识ID
     */
    String getCapabilityId();

    /**
     * 设置能力标识ID
     * @param capabilityId 能力标识ID
     */
    void setCapabilityId(String capabilityId);

    /**
     * 获取关联的技能ID
     * @return 技能ID
     */
    String getSkillId();

    /**
     * 设置关联的技能ID
     * @param skillId 技能ID
     */
    void setSkillId(String skillId);

    /**
     * 获取能力地址
     * @return 能力地址信息
     */
    CapAddress getAddress();

    /**
     * 设置能力地址
     * @param address 能力地址信息
     */
    void setAddress(CapAddress address);

    /**
     * 获取能力标签列表
     * @return 标签列表
     */
    List<String> getTags();

    /**
     * 设置能力标签列表
     * @param tags 标签列表
     */
    void setTags(List<String> tags);

    /**
     * 获取注册时间
     * @return 注册时间戳
     */
    long getRegisteredTime();

    /**
     * 设置注册时间
     * @param registeredTime 注册时间戳
     */
    void setRegisteredTime(long registeredTime);

    /**
     * 获取最后心跳时间
     * @return 最后心跳时间戳
     */
    long getLastHeartbeat();

    /**
     * 设置最后心跳时间
     * @param lastHeartbeat 最后心跳时间戳
     */
    void setLastHeartbeat(long lastHeartbeat);

    /**
     * 判断能力是否可用
     * @return true表示可用，false表示不可用
     */
    boolean isAvailable();

    /**
     * 设置能力可用状态
     * @param available 可用状态
     */
    void setAvailable(boolean available);

    // ==================== 新增方法：支持场景类型 ====================

    /**
     * 获取能力支持的场景类型列表
     *
     * <p>用于声明式场景自动匹配。能力注册时声明支持的场景类型，
     * 场景管理器根据此列表自动将能力加入匹配的场景。</p>
     *
     * <p>示例：灯泡能力可以声明支持 ["switch", "dimmer", "color"] 场景类型</p>
     *
     * @return 支持的场景类型列表，为空表示不支持自动匹配
     */
    List<String> getSupportedSceneTypes();

    /**
     * 设置能力支持的场景类型列表
     *
     * @param sceneTypes 场景类型列表
     */
    void setSupportedSceneTypes(List<String> sceneTypes);

    /**
     * 判断能力是否支持指定场景类型
     *
     * @param sceneType 场景类型
     * @return true表示支持
     */
    default boolean supportsSceneType(String sceneType) {
        List<String> types = getSupportedSceneTypes();
        return types != null && types.contains(sceneType);
    }

    /**
     * 判断能力是否支持声明式场景匹配
     *
     * @return true表示支持声明式场景
     */
    default boolean supportsDeclarativeScenes() {
        List<String> types = getSupportedSceneTypes();
        return types != null && !types.isEmpty();
    }
}

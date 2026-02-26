package net.ooder.sdk.a2a.capability;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.ooder.sdk.discovery.SkillDiscoveryService;
import net.ooder.sdk.plugin.SkillMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Skill卡片管理器
 *
 * <p>管理Skill卡片的注册、发现和查询。</p>
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class SkillCardManager {

    private static final Logger log = LoggerFactory.getLogger(SkillCardManager.class);

    /**
     * Skill卡片缓存
     */
    private final Map<String, SkillCard> skillCards;

    /**
     * Skill发现服务
     */
    private final SkillDiscoveryService discoveryService;

    public SkillCardManager(SkillDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
        this.skillCards = new ConcurrentHashMap<>();
    }

    // ==================== 注册管理 ====================

    /**
     * 注册Skill卡片
     *
     * @param skillCard Skill卡片
     */
    public void registerSkillCard(SkillCard skillCard) {
        if (skillCard == null || skillCard.getSkillId() == null) {
            throw new IllegalArgumentException("SkillCard and skillId cannot be null");
        }

        skillCards.put(skillCard.getSkillId(), skillCard);
        log.info("Registered SkillCard for skill: {}", skillCard.getSkillId());
    }

    /**
     * 从Skill元数据生成并注册卡片
     *
     * @param metadata Skill元数据
     */
    public void registerFromMetadata(SkillMetadata metadata) {
        SkillCard skillCard = convertFromMetadata(metadata);
        registerSkillCard(skillCard);
    }

    /**
     * 注销Skill卡片
     *
     * @param skillId Skill标识
     */
    public void unregisterSkillCard(String skillId) {
        skillCards.remove(skillId);
        log.info("Unregistered SkillCard for skill: {}", skillId);
    }

    // ==================== 查询方法 ====================

    /**
     * 获取Skill卡片
     *
     * @param skillId Skill标识
     * @return Skill卡片或null
     */
    public SkillCard getSkillCard(String skillId) {
        SkillCard card = skillCards.get(skillId);
        if (card == null) {
            // 尝试从发现服务获取
            SkillDiscoveryService.DiscoveredSkill discovered = discoveryService.getDiscoveredSkill(skillId);
            if (discovered != null && discovered.getMetadata() != null) {
                card = convertFromMetadata(discovered.getMetadata());
                skillCards.put(skillId, card);
            }
        }
        return card;
    }

    /**
     * 获取所有Skill卡片
     *
     * @return Skill卡片列表
     */
    public List<SkillCard> getAllSkillCards() {
        return new ArrayList<>(skillCards.values());
    }

    /**
     * 根据分类查询Skill卡片
     *
     * @param category 分类
     * @return Skill卡片列表
     */
    public List<SkillCard> getSkillCardsByCategory(String category) {
        return skillCards.values().stream()
                .filter(card -> category.equals(card.getCategory()))
                .collect(Collectors.toList());
    }

    /**
     * 根据标签查询Skill卡片
     *
     * @param tag 标签
     * @return Skill卡片列表
     */
    public List<SkillCard> getSkillCardsByTag(String tag) {
        return skillCards.values().stream()
                .filter(card -> card.getTags() != null && card.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    /**
     * 根据能力查询Skill卡片
     *
     * @param capabilityId 能力ID
     * @return Skill卡片列表
     */
    public List<SkillCard> getSkillCardsByCapability(String capabilityId) {
        return skillCards.values().stream()
                .filter(card -> card.getCapabilities() != null &&
                        card.getCapabilities().stream()
                                .anyMatch(cap -> capabilityId.equals(cap.getId())))
                .collect(Collectors.toList());
    }

    /**
     * 搜索Skill卡片
     *
     * @param keyword 关键词
     * @return Skill卡片列表
     */
    public List<SkillCard> searchSkillCards(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllSkillCards();
        }

        String lowerKeyword = keyword.toLowerCase();
        return skillCards.values().stream()
                .filter(card -> {
                    // 搜索ID
                    if (card.getSkillId() != null && card.getSkillId().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    // 搜索名称
                    if (card.getName() != null && card.getName().values().stream()
                            .anyMatch(name -> name.toLowerCase().contains(lowerKeyword))) {
                        return true;
                    }
                    // 搜索描述
                    if (card.getDescription() != null && card.getDescription().values().stream()
                            .anyMatch(desc -> desc.toLowerCase().contains(lowerKeyword))) {
                        return true;
                    }
                    // 搜索标签
                    if (card.getTags() != null && card.getTags().stream()
                            .anyMatch(tag -> tag.toLowerCase().contains(lowerKeyword))) {
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    // ==================== 序列化/反序列化 ====================

    /**
     * 将Skill卡片转换为JSON
     *
     * @param skillCard Skill卡片
     * @return JSON字符串
     */
    public String toJson(SkillCard skillCard) {
        return JSON.toJSONString(skillCard, SerializerFeature.PrettyFormat);
    }

    /**
     * 从JSON解析Skill卡片
     *
     * @param json JSON字符串
     * @return Skill卡片
     */
    public SkillCard fromJson(String json) {
        return JSON.parseObject(json, SkillCard.class);
    }

    /**
     * 获取所有Skill卡片的JSON表示
     *
     * @return JSON字符串
     */
    public String getAllSkillCardsJson() {
        return JSON.toJSONString(skillCards.values(), SerializerFeature.PrettyFormat);
    }

    // ==================== 转换方法 ====================

    /**
     * 从Skill元数据转换为Skill卡片
     *
     * @param metadata Skill元数据
     * @return Skill卡片
     */
    public SkillCard convertFromMetadata(SkillMetadata metadata) {
        SkillCard card = new SkillCard();
        card.setSkillId(metadata.getId());
        card.setVersion(metadata.getVersion());

        // 名称和描述
        if (metadata.getName() != null) {
            card.addName("zh_CN", metadata.getName());
        }
        if (metadata.getDescription() != null) {
            card.addDescription("zh_CN", metadata.getDescription());
        }

        // 作者
        if (metadata.getAuthor() != null) {
            card.setAuthor(new SkillCard.AuthorInfo(metadata.getAuthor(), null, null));
        }

        // 分类和标签
        card.setCategory(metadata.getCategory());
        if (metadata.getTags() != null) {
            card.setTags(metadata.getTags());
        }

        // 能力
        if (metadata.getCapabilities() != null) {
            for (Map<String, Object> capData : metadata.getCapabilities()) {
                SkillCard.Capability cap = new SkillCard.Capability();
                cap.setId((String) capData.get("id"));
                cap.setName((String) capData.get("name"));
                cap.setDescription((String) capData.get("description"));
                card.addCapability(cap);
            }
        }

        // UI配置
        if (metadata.getUi() != null) {
            SkillCard.UIConfig uiConfig = new SkillCard.UIConfig();
            uiConfig.setEnabled(true);
            uiConfig.setEntry((String) metadata.getUi().get("entry"));
            card.setUiConfig(uiConfig);
        }

        // 端点信息
        if (metadata.getRoutes() != null) {
            SkillCard.EndpointInfo endpointInfo = new SkillCard.EndpointInfo();
            for (Map<String, Object> route : metadata.getRoutes()) {
                String path = (String) route.get("path");
                String method = (String) route.get("method");
                String handler = (String) route.get("handler");
                if (path != null && method != null) {
                    endpointInfo.addEndpoint(path, method, handler);
                }
            }
            card.setEndpoint(endpointInfo);
        }

        return card;
    }

    // ==================== 统计信息 ====================

    /**
     * 获取注册的Skill卡片数量
     */
    public int getSkillCardCount() {
        return skillCards.size();
    }

    /**
     * 检查是否存在Skill卡片
     */
    public boolean hasSkillCard(String skillId) {
        return skillCards.containsKey(skillId);
    }

    /**
     * 清空所有Skill卡片
     */
    public void clear() {
        skillCards.clear();
        log.info("Cleared all SkillCards");
    }
}

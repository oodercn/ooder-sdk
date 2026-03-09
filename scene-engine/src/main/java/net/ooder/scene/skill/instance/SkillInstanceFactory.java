package net.ooder.scene.skill.instance;

import net.ooder.scene.skill.adapter.SkillSDKAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Skill实例工厂
 *
 * <p>负责创建SkillInstance实例</p>
 *
 * @author Ooder Team
 * @version 2.3
 * @since 2.3.0
 */
@Component
public class SkillInstanceFactory {

    private static final Logger log = LoggerFactory.getLogger(SkillInstanceFactory.class);

    @Autowired
    private SkillSDKAdapter sdkAdapter;

    /**
     * 创建Skill实例
     *
     * @param userId  用户ID
     * @param skillId Skill ID
     * @return SkillInstance
     */
    public SkillInstance create(String userId, String skillId) {
        log.debug("Creating SkillInstance for user={}, skill={}", userId, skillId);

        // 生成实例ID
        String instanceId = generateInstanceId(userId, skillId);

        // 创建实例
        SkillInstance instance = new SkillInstance(skillId, userId, instanceId, sdkAdapter);

        log.info("Created SkillInstance: user={}, skill={}, instanceId={}",
                userId, skillId, instanceId);

        return instance;
    }

    /**
     * 生成实例ID
     */
    private String generateInstanceId(String userId, String skillId) {
        return String.format("%s-%s-%s", userId, skillId, UUID.randomUUID().toString().substring(0, 8));
    }
}

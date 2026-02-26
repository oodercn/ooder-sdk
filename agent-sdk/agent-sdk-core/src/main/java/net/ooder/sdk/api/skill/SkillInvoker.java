package net.ooder.sdk.api.skill;

import java.util.Map;

/**
 * Skill 调用器接口
 *
 * <p>定义如何调用 Skill 的能力，由具体实现提供调用机制</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public interface SkillInvoker {

    /**
     * 调用 Skill 的能力
     *
     * @param skillId Skill ID
     * @param capabilityId 能力ID
     * @param params 参数
     * @return 调用结果
     * @throws SkillInvocationException 调用失败时抛出
     */
    Object invoke(String skillId, String capabilityId, Map<String, Object> params) throws SkillInvocationException;

    /**
     * 检查 Skill 是否可用
     *
     * @param skillId Skill ID
     * @return 是否可用
     */
    boolean isSkillAvailable(String skillId);

    /**
     * 获取 Skill 信息
     *
     * @param skillId Skill ID
     * @return Skill 信息
     */
    SkillInfo getSkillInfo(String skillId);

    /**
     * Skill 信息
     */
    class SkillInfo {
        private String skillId;
        private String name;
        private String version;
        private String status;

        public String getSkillId() { return skillId; }
        public void setSkillId(String skillId) { this.skillId = skillId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    /**
     * Skill 调用异常
     */
    class SkillInvocationException extends Exception {
        private final String skillId;
        private final String capabilityId;

        public SkillInvocationException(String message, String skillId, String capabilityId) {
            super(message);
            this.skillId = skillId;
            this.capabilityId = capabilityId;
        }

        public SkillInvocationException(String message, Throwable cause, String skillId, String capabilityId) {
            super(message, cause);
            this.skillId = skillId;
            this.capabilityId = capabilityId;
        }

        public String getSkillId() { return skillId; }
        public String getCapabilityId() { return capabilityId; }
    }
}

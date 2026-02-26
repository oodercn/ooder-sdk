package net.ooder.sdk.a2a.message;

import net.ooder.sdk.a2a.capability.SkillCard;

/**
 * Skill卡片消息
 *
 * @author Ooder Team
 * @version 1.0
 * @since 2.3.0
 */
public class SkillCardMessage extends A2AMessage {

    private SkillCard skillCard;

    public SkillCardMessage() {
        super(A2AMessageType.SKILL_CARD);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SkillCardMessage message = new SkillCardMessage();

        public Builder skillId(String skillId) {
            message.setSkillId(skillId);
            return this;
        }

        public Builder skillCard(SkillCard skillCard) {
            message.setSkillCard(skillCard);
            return this;
        }

        public SkillCardMessage build() {
            return message;
        }
    }

    public SkillCard getSkillCard() {
        return skillCard;
    }

    public void setSkillCard(SkillCard skillCard) {
        this.skillCard = skillCard;
    }
}

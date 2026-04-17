package net.ooder.spi.messaging.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class UnifiedMessage {

    private String messageId;

    private String conversationId;

    private String sceneGroupId;

    private MessageType messageType;

    private ConversationType conversationType;

    private Participant from;

    private Participant to;

    private List<Participant> cc;

    private Content content;

    private Map<String, Object> metadata;

    private int priority;

    private MessageStatus status;

    private long createTime;

    private long expireTime;

    private String threadId;

    private String parentMessageId;

    private List<MessageReaction> reactions;

    private boolean requiresAction;

    private List<MessageAction> availableActions;
}

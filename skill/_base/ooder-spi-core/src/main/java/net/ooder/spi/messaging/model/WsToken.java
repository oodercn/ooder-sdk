package net.ooder.spi.messaging.model;

import lombok.Data;

@Data
public class WsToken {

    private String tokenId;

    private String token;

    private String userId;

    private String sceneGroupId;

    private long createdAt;

    private long expireAt;
}

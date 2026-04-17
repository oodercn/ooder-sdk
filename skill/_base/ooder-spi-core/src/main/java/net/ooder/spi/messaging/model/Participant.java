package net.ooder.spi.messaging.model;

import lombok.Data;
import java.util.Map;

@Data
public class Participant {

    private String id;

    private String name;

    private ParticipantType type;

    private String role;

    private String avatar;

    private boolean online;

    private Map<String, Object> metadata;
}

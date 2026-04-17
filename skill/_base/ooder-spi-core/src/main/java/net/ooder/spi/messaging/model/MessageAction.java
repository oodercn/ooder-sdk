package net.ooder.spi.messaging.model;

import lombok.Data;
import java.util.Map;

@Data
public class MessageAction {

    private String actionId;

    private String type;

    private String label;

    private String icon;

    private Map<String, Object> params;
}

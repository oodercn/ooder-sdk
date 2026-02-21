package net.ooder.scene.drivers.msg;

import java.util.List;
import java.util.Map;

public interface Topic {

    String getTopicId();
    
    String getName();
    
    String getDescription();
    
    String getOwnerId();
    
    Long getCreateTime();
    
    List<String> getSubscriberIds();
    
    int getSubscriberCount();
    
    boolean isSubscribed(String userId);
    
    Map<String, Object> getMetadata();
}

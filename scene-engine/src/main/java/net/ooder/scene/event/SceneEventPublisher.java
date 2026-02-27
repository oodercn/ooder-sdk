package net.ooder.scene.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SceneEventPublisher {
    
    private final ApplicationEventPublisher publisher;
    
    public SceneEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }
    
    public void publish(SceneEvent event) {
        publisher.publishEvent(event);
    }
}

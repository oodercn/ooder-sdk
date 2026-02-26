package net.ooder.scene.core;

import net.ooder.scene.event.SceneEventPublisher;
import net.ooder.scene.event.capability.CapabilityEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRouter {
    private CapRegistry registry;
    private Map<String, CapHandler> handlers;
    private SceneEventPublisher eventPublisher;

    public CapRouter(CapRegistry registry) {
        this.registry = registry;
        this.handlers = new ConcurrentHashMap<>();
    }
    
    public void setEventPublisher(SceneEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void registerHandler(String capId, CapHandler handler) {
        handlers.put(capId, handler);
    }

    public CapResponse routeRequest(String capId, CapRequest request) {
        if (!registry.hasCapability(capId)) {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(this, capId, 
                request.getRequestId(), "Capability not found"));
            return CapResponse.failure(request.getRequestId(), capId, "Capability not found");
        }

        CapAddress address = new CapAddress(capId);
        if (!address.isValid()) {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(this, capId, 
                request.getRequestId(), "Invalid capability address"));
            return CapResponse.failure(request.getRequestId(), capId, "Invalid capability address");
        }

        CapHandler handler = handlers.get(capId);
        CapResponse response;
        if (handler != null) {
            response = handler.handle(request);
        } else {
            response = handleDefault(request);
        }
        
        if (response.isSuccess()) {
            CapabilityInfo info = registry.getCapability(capId);
            String capName = info != null ? info.getName() : capId;
            publishCapabilityEvent(CapabilityEvent.invoked(this, capId, capName, request.getRequestId()));
        } else {
            publishCapabilityEvent(CapabilityEvent.invocationFailed(this, capId, 
                request.getRequestId(), response.getErrorMessage()));
        }
        
        return response;
    }

    private CapResponse handleDefault(CapRequest request) {
        return CapResponse.success(request.getRequestId(), request.getCapId(), "Default handler executed");
    }
    
    private void publishCapabilityEvent(CapabilityEvent event) {
        if (eventPublisher != null) {
            eventPublisher.publish(event);
        }
    }

    public interface CapHandler {
        CapResponse handle(CapRequest request);
    }
}

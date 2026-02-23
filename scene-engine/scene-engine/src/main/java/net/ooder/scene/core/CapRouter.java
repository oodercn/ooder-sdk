package net.ooder.scene.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapRouter {
    private CapRegistry registry;
    private Map<String, CapHandler> handlers;

    public CapRouter(CapRegistry registry) {
        this.registry = registry;
        this.handlers = new ConcurrentHashMap<>();
    }

    public void registerHandler(String capId, CapHandler handler) {
        handlers.put(capId, handler);
    }

    public CapResponse routeRequest(String capId, CapRequest request) {
        // 检查能力是否存在
        if (!registry.hasCapability(capId)) {
            return CapResponse.failure(request.getRequestId(), capId, "Capability not found");
        }

        // 检查地址是否有效
        CapAddress address = new CapAddress(capId);
        if (!address.isValid()) {
            return CapResponse.failure(request.getRequestId(), capId, "Invalid capability address");
        }

        // 查找处理器
        CapHandler handler = handlers.get(capId);
        if (handler != null) {
            // 使用注册的处理器处理请求
            return handler.handle(request);
        } else {
            // 使用默认处理逻辑
            return handleDefault(request);
        }
    }

    private CapResponse handleDefault(CapRequest request) {
        // 默认处理逻辑
        // 这里可以添加通用的能力调用逻辑
        return CapResponse.success(request.getRequestId(), request.getCapId(), "Default handler executed");
    }

    public interface CapHandler {
        CapResponse handle(CapRequest request);
    }
}

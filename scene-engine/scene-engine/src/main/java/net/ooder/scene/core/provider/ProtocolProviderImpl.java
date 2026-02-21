package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.core.SceneEngine;
import net.ooder.scene.provider.ProtocolProvider;
import net.ooder.scene.provider.model.protocol.ProtocolCommandResult;
import net.ooder.scene.provider.model.protocol.ProtocolHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProtocolProviderImpl implements ProtocolProvider {

    private static final String PROVIDER_NAME = "protocol-provider";
    private static final String VERSION = "1.0.0";

    private boolean initialized = false;
    private boolean running = false;
    private SceneEngine engine;

    private final Map<String, ProtocolHandler> handlers = new ConcurrentHashMap<>();

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public void initialize(SceneEngine engine) {
        this.engine = engine;
        initializeDefaultHandlers();
        this.initialized = true;
    }

    @Override
    public void start() {
        if (!initialized) {
            throw new IllegalStateException("Provider not initialized");
        }
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public int getPriority() {
        return 100;
    }

    private void initializeDefaultHandlers() {
        registerHandler("http", "HTTP Protocol Handler", "internal://http", "1.0.0", true);
        registerHandler("https", "HTTPS Protocol Handler", "internal://https", "1.0.0", true);
        registerHandler("ws", "WebSocket Protocol Handler", "internal://ws", "1.0.0", true);
        registerHandler("wss", "WebSocket Secure Protocol Handler", "internal://wss", "1.0.0", true);
        registerHandler("tcp", "TCP Protocol Handler", "internal://tcp", "1.0.0", true);
        registerHandler("udp", "UDP Protocol Handler", "internal://udp", "1.0.0", true);
    }

    private void registerHandler(String type, String description, String endpoint, String version, boolean enabled) {
        ProtocolHandler handler = new ProtocolHandler();
        handler.setHandlerType(type);
        handler.setDescription(description);
        handler.setEndpoint(endpoint);
        handler.setVersion(version);
        handler.setEnabled(enabled);
        handlers.put(type, handler);
    }

    @Override
    public Result<List<ProtocolHandler>> getProtocolHandlers() {
        List<ProtocolHandler> handlerList = new ArrayList<>(handlers.values());
        return Result.success(handlerList);
    }

    @Override
    public Result<ProtocolHandler> registerProtocolHandler(Map<String, Object> handlerData) {
        if (handlerData == null) {
            return Result.badRequest("Handler data is required");
        }

        String handlerType = (String) handlerData.get("handlerType");
        if (handlerType == null || handlerType.isEmpty()) {
            return Result.badRequest("Handler type is required");
        }

        ProtocolHandler handler = new ProtocolHandler();
        handler.setHandlerType(handlerType);

        if (handlerData.containsKey("description")) {
            handler.setDescription((String) handlerData.get("description"));
        }
        if (handlerData.containsKey("endpoint")) {
            handler.setEndpoint((String) handlerData.get("endpoint"));
        }
        if (handlerData.containsKey("version")) {
            handler.setVersion((String) handlerData.get("version"));
        } else {
            handler.setVersion("1.0.0");
        }
        if (handlerData.containsKey("enabled")) {
            handler.setEnabled((Boolean) handlerData.get("enabled"));
        } else {
            handler.setEnabled(true);
        }
        if (handlerData.containsKey("config")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> config = (Map<String, Object>) handlerData.get("config");
            handler.setConfig(config);
        }

        handlers.put(handlerType, handler);

        return Result.success(handler);
    }

    @Override
    public Result<Boolean> removeProtocolHandler(String handlerType) {
        if (handlerType == null || handlerType.isEmpty()) {
            return Result.badRequest("Handler type is required");
        }

        ProtocolHandler removed = handlers.remove(handlerType);
        if (removed == null) {
            return Result.notFound("Handler not found: " + handlerType);
        }

        return Result.success(true);
    }

    @Override
    public Result<ProtocolCommandResult> handleProtocolCommand(Map<String, Object> commandData) {
        if (commandData == null) {
            return Result.badRequest("Command data is required");
        }

        String handlerType = (String) commandData.get("handlerType");
        if (handlerType == null || handlerType.isEmpty()) {
            return Result.badRequest("Handler type is required");
        }

        ProtocolHandler handler = handlers.get(handlerType);
        if (handler == null) {
            ProtocolCommandResult result = new ProtocolCommandResult();
            result.setSuccess(false);
            result.setErrorCode("HANDLER_NOT_FOUND");
            result.setErrorMessage("Protocol handler not found: " + handlerType);
            return Result.success(result);
        }

        if (!handler.isEnabled()) {
            ProtocolCommandResult result = new ProtocolCommandResult();
            result.setSuccess(false);
            result.setErrorCode("HANDLER_DISABLED");
            result.setErrorMessage("Protocol handler is disabled: " + handlerType);
            return Result.success(result);
        }

        long startTime = System.currentTimeMillis();
        ProtocolCommandResult result = new ProtocolCommandResult();

        try {
            String command = (String) commandData.get("command");
            String payload = (String) commandData.get("payload");

            String response = processCommand(handlerType, command, payload, commandData);
            result.setSuccess(true);
            result.setResponse(response);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorCode("EXECUTION_ERROR");
            result.setErrorMessage(e.getMessage());
        }

        result.setDuration(System.currentTimeMillis() - startTime);

        return Result.success(result);
    }

    private String processCommand(String handlerType, String command, String payload, Map<String, Object> context) {
        StringBuilder response = new StringBuilder();
        response.append("{");
        response.append("\"handler\":\"").append(handlerType).append("\",");
        response.append("\"command\":\"").append(command != null ? command : "").append("\",");
        response.append("\"status\":\"processed\",");
        response.append("\"timestamp\":").append(System.currentTimeMillis());
        response.append("}");
        return response.toString();
    }

    @Override
    public Result<Boolean> refreshProtocolHandlers() {
        handlers.clear();
        initializeDefaultHandlers();
        return Result.success(true);
    }
}

package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.protocol.ProtocolCommandResult;
import net.ooder.scene.provider.model.protocol.ProtocolHandler;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ProtocolProviderTest {

    private ProtocolProviderImpl protocolProvider;

    @BeforeEach
    public void setup() {
        protocolProvider = new ProtocolProviderImpl();
        protocolProvider.initialize(null);
        protocolProvider.start();
    }

    @AfterEach
    public void teardown() {
        protocolProvider.stop();
    }

    @Test
    public void testProviderInitialization() {
        assertTrue(protocolProvider.isInitialized());
        assertTrue(protocolProvider.isRunning());
        assertEquals("protocol-provider", protocolProvider.getProviderName());
        assertEquals("1.0.0", protocolProvider.getVersion());
        assertEquals(100, protocolProvider.getPriority());
    }

    @Test
    public void testGetProtocolHandlers() {
        Result<List<ProtocolHandler>> result = protocolProvider.getProtocolHandlers();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().size() >= 6);
    }

    @Test
    public void testRegisterProtocolHandler() {
        Map<String, Object> handlerData = new HashMap<>();
        handlerData.put("handlerType", "custom");
        handlerData.put("description", "Custom Protocol Handler");
        handlerData.put("endpoint", "internal://custom");
        handlerData.put("version", "1.0.0");
        handlerData.put("enabled", true);
        
        Result<ProtocolHandler> result = protocolProvider.registerProtocolHandler(handlerData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("custom", result.getData().getHandlerType());
        assertEquals("Custom Protocol Handler", result.getData().getDescription());
    }

    @Test
    public void testRegisterProtocolHandlerWithNullData() {
        Result<ProtocolHandler> result = protocolProvider.registerProtocolHandler(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testRegisterProtocolHandlerWithoutType() {
        Map<String, Object> handlerData = new HashMap<>();
        handlerData.put("description", "Test Handler");
        
        Result<ProtocolHandler> result = protocolProvider.registerProtocolHandler(handlerData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testRemoveProtocolHandler() {
        Map<String, Object> handlerData = new HashMap<>();
        handlerData.put("handlerType", "to-remove");
        protocolProvider.registerProtocolHandler(handlerData);
        
        Result<Boolean> result = protocolProvider.removeProtocolHandler("to-remove");
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
    }

    @Test
    public void testRemoveProtocolHandlerNotFound() {
        Result<Boolean> result = protocolProvider.removeProtocolHandler("non-existent");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testRemoveProtocolHandlerWithNullType() {
        Result<Boolean> result = protocolProvider.removeProtocolHandler(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testHandleProtocolCommand() {
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("handlerType", "http");
        commandData.put("command", "GET");
        commandData.put("payload", "{}");
        
        Result<ProtocolCommandResult> result = protocolProvider.handleProtocolCommand(commandData);
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isSuccess());
        assertNotNull(result.getData().getResponse());
    }

    @Test
    public void testHandleProtocolCommandWithNullData() {
        Result<ProtocolCommandResult> result = protocolProvider.handleProtocolCommand(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testHandleProtocolCommandWithoutHandlerType() {
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("command", "GET");
        
        Result<ProtocolCommandResult> result = protocolProvider.handleProtocolCommand(commandData);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testHandleProtocolCommandHandlerNotFound() {
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("handlerType", "non-existent");
        commandData.put("command", "GET");
        
        Result<ProtocolCommandResult> result = protocolProvider.handleProtocolCommand(commandData);
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isSuccess());
        assertEquals("HANDLER_NOT_FOUND", result.getData().getErrorCode());
    }

    @Test
    public void testRefreshProtocolHandlers() {
        Map<String, Object> handlerData = new HashMap<>();
        handlerData.put("handlerType", "temp-handler");
        protocolProvider.registerProtocolHandler(handlerData);
        
        Result<Boolean> result = protocolProvider.refreshProtocolHandlers();
        
        assertTrue(result.isSuccess());
        assertTrue(result.getData());
        
        Result<List<ProtocolHandler>> handlersResult = protocolProvider.getProtocolHandlers();
        boolean hasTempHandler = false;
        for (ProtocolHandler handler : handlersResult.getData()) {
            if ("temp-handler".equals(handler.getHandlerType())) {
                hasTempHandler = true;
                break;
            }
        }
        assertFalse(hasTempHandler);
    }

    @Test
    public void testProviderLifecycle() {
        ProtocolProviderImpl provider = new ProtocolProviderImpl();
        
        assertFalse(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.initialize(null);
        assertTrue(provider.isInitialized());
        assertFalse(provider.isRunning());
        
        provider.start();
        assertTrue(provider.isRunning());
        
        provider.stop();
        assertFalse(provider.isRunning());
    }

    @Test
    public void testStartWithoutInitialize() {
        ProtocolProviderImpl provider = new ProtocolProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }
}

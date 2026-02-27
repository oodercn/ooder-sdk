package net.ooder.scene.core.provider;

import net.ooder.scene.core.Result;
import net.ooder.scene.provider.model.agent.CommandStatsData;
import net.ooder.scene.provider.model.agent.EndAgent;
import net.ooder.scene.provider.model.agent.NetworkStatusData;
import net.ooder.scene.provider.model.agent.TestCommandResult;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AgentProviderTest {

    private AgentProviderImpl agentProvider;

    @BeforeEach
    public void setup() {
        agentProvider = new AgentProviderImpl();
        agentProvider.initialize(null);
        agentProvider.start();
    }

    @AfterEach
    public void teardown() {
        agentProvider.stop();
    }

    @Test
    public void testAgentProviderInitialization() {
        assertTrue(agentProvider.isInitialized());
        assertTrue(agentProvider.isRunning());
        assertEquals("agent-provider", agentProvider.getProviderName());
        assertEquals("1.0.0", agentProvider.getVersion());
        assertEquals(100, agentProvider.getPriority());
    }

    @Test
    public void testGetEndAgentsEmpty() {
        Result<List<EndAgent>> result = agentProvider.getEndAgents();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    public void testAddEndAgent() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "test-agent");
        agentData.put("type", "terminal");
        agentData.put("endpoint", "192.168.1.100:8080");
        agentData.put("description", "Test agent");
        agentData.put("version", "1.0.0");
        
        Result<EndAgent> result = agentProvider.addEndAgent(agentData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        EndAgent agent = result.getData();
        assertNotNull(agent.getAgentId());
        assertEquals("test-agent", agent.getName());
        assertEquals("terminal", agent.getType());
        assertEquals("active", agent.getStatus());
        assertEquals("192.168.1.100:8080", agent.getEndpoint());
        assertEquals("Test agent", agent.getDescription());
        assertEquals("1.0.0", agent.getVersion());
        assertNotNull(agent.getLastActiveAt());
    }

    @Test
    public void testAddEndAgentWithNullData() {
        Result<EndAgent> result = agentProvider.addEndAgent(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testGetEndAgentDetails() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "detail-test-agent");
        agentData.put("type", "sensor");
        
        Result<EndAgent> addResult = agentProvider.addEndAgent(agentData);
        String agentId = addResult.getData().getAgentId();
        
        Result<EndAgent> result = agentProvider.getEndAgentDetails(agentId);
        
        assertTrue(result.isSuccess());
        assertEquals("detail-test-agent", result.getData().getName());
    }

    @Test
    public void testGetEndAgentDetailsNotFound() {
        Result<EndAgent> result = agentProvider.getEndAgentDetails("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testGetEndAgentDetailsWithNullId() {
        Result<EndAgent> result = agentProvider.getEndAgentDetails(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testEditEndAgent() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "original-name");
        agentData.put("type", "original-type");
        
        Result<EndAgent> addResult = agentProvider.addEndAgent(agentData);
        String agentId = addResult.getData().getAgentId();
        
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "updated-name");
        updateData.put("status", "inactive");
        
        Result<EndAgent> result = agentProvider.editEndAgent(agentId, updateData);
        
        assertTrue(result.isSuccess());
        assertEquals("updated-name", result.getData().getName());
        assertEquals("inactive", result.getData().getStatus());
        assertEquals("original-type", result.getData().getType());
    }

    @Test
    public void testEditEndAgentNotFound() {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", "updated-name");
        
        Result<EndAgent> result = agentProvider.editEndAgent("non-existent-id", updateData);
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testEditEndAgentWithNullData() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "test-agent");
        
        Result<EndAgent> addResult = agentProvider.addEndAgent(agentData);
        String agentId = addResult.getData().getAgentId();
        
        Result<EndAgent> result = agentProvider.editEndAgent(agentId, null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testDeleteEndAgent() {
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "to-delete-agent");
        
        Result<EndAgent> addResult = agentProvider.addEndAgent(agentData);
        String agentId = addResult.getData().getAgentId();
        
        Result<EndAgent> deleteResult = agentProvider.deleteEndAgent(agentId);
        
        assertTrue(deleteResult.isSuccess());
        assertEquals("to-delete-agent", deleteResult.getData().getName());
        
        Result<EndAgent> getResult = agentProvider.getEndAgentDetails(agentId);
        assertFalse(getResult.isSuccess());
        assertEquals(404, getResult.getCode());
    }

    @Test
    public void testDeleteEndAgentNotFound() {
        Result<EndAgent> result = agentProvider.deleteEndAgent("non-existent-id");
        
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    public void testGetEndAgentsAfterAdd() {
        Map<String, Object> agentData1 = new HashMap<>();
        agentData1.put("name", "agent-1");
        agentData1.put("type", "type-a");
        
        Map<String, Object> agentData2 = new HashMap<>();
        agentData2.put("name", "agent-2");
        agentData2.put("type", "type-b");
        
        agentProvider.addEndAgent(agentData1);
        agentProvider.addEndAgent(agentData2);
        
        Result<List<EndAgent>> result = agentProvider.getEndAgents();
        
        assertTrue(result.isSuccess());
        assertEquals(2, result.getData().size());
    }

    @Test
    public void testGetNetworkStatus() {
        Result<NetworkStatusData> result = agentProvider.getNetworkStatus();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        NetworkStatusData status = result.getData();
        assertTrue(status.isOnline());
        assertNotNull(status.getConnectionType());
        assertNotNull(status.getIpAddress());
    }

    @Test
    public void testGetCommandStatsInitial() {
        Result<CommandStatsData> result = agentProvider.getCommandStats();
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        
        CommandStatsData stats = result.getData();
        assertEquals(0, stats.getTotalCommands());
        assertEquals(0, stats.getSuccessRate(), 0.01);
    }

    @Test
    public void testTestCommandWithNullData() {
        Result<TestCommandResult> result = agentProvider.testCommand(null);
        
        assertFalse(result.isSuccess());
        assertEquals(400, result.getCode());
    }

    @Test
    public void testTestCommandWithEmptyCommand() {
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("command", "");
        
        Result<TestCommandResult> result = agentProvider.testCommand(commandData);
        
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isSuccess());
        assertEquals("INVALID_COMMAND", result.getData().getErrorCode());
    }

    @Test
    public void testTestCommandEcho() {
        Map<String, Object> commandData = new HashMap<>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            commandData.put("command", "echo test");
        } else {
            commandData.put("command", "echo test");
        }
        
        Result<TestCommandResult> result = agentProvider.testCommand(commandData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertTrue(result.getData().isSuccess());
        assertTrue(result.getData().getOutput().contains("test"));
        assertTrue(result.getData().getDuration() >= 0);
    }

    @Test
    public void testCommandStatsAfterExecution() {
        Map<String, Object> commandData = new HashMap<>();
        commandData.put("command", "echo test");
        
        agentProvider.testCommand(commandData);
        
        Result<CommandStatsData> statsResult = agentProvider.getCommandStats();
        
        assertTrue(statsResult.isSuccess());
        assertTrue(statsResult.getData().getTotalCommands() > 0);
    }

    @Test
    public void testProviderLifecycle() {
        AgentProviderImpl provider = new AgentProviderImpl();
        
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
        AgentProviderImpl provider = new AgentProviderImpl();
        
        assertThrows(IllegalStateException.class, () -> provider.start());
    }

    @Test
    public void testAddAgentWithMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("location", "building-a");
        metadata.put("department", "engineering");
        
        Map<String, Object> agentData = new HashMap<>();
        agentData.put("name", "agent-with-metadata");
        agentData.put("metadata", metadata);
        
        Result<EndAgent> result = agentProvider.addEndAgent(agentData);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getData().getMetadata());
        assertEquals("building-a", result.getData().getMetadata().get("location"));
    }
}

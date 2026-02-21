package net.ooder.sdk.api.protocol;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandPacketTest {
    
    @Test
    public void testDefaultConstructor() {
        CommandPacket packet = new CommandPacket();
        
        assertNotNull(packet.getPacketId());
        assertTrue(packet.getPacketId().startsWith("pkt_"));
        assertNotNull(packet.getHeader());
        assertNotNull(packet.getChildCommandIds());
        assertTrue(packet.getChildCommandIds().isEmpty());
        assertFalse(packet.isRollbackable());
    }
    
    @Test
    public void testOfWithProtocolAndType() {
        CommandPacket packet = CommandPacket.of("discovery", "discover_peers");
        
        assertNotNull(packet);
        assertEquals("discovery", packet.getHeader().getProtocolType());
        assertEquals("discover_peers", packet.getHeader().getCommandType());
    }
    
    @Test
    public void testOfWithPayload() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("key", "value");
        
        CommandPacket packet = CommandPacket.of("discovery", "discover_peers", payload);
        
        assertNotNull(packet);
        assertEquals("discovery", packet.getHeader().getProtocolType());
        assertEquals("discover_peers", packet.getHeader().getCommandType());
        assertEquals(payload, packet.getPayload());
    }
    
    @Test
    public void testBuilder() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("param1", "value1");
        
        CommandPacket packet = CommandBuilder.create()
            .protocolType("collaboration")
            .commandType("send_message")
            .payload(payload)
            .source("agent-001")
            .target("agent-002")
            .priority(8)
            .timeout(60000)
            .requiresAck(true)
            .direction(CommandDirection.NORTHBOUND)
            .parentCommandId("parent-cmd-001")
            .rollbackable(true)
            .sceneId("scene-001")
            .domainId("domain-001")
            .build();
        
        assertEquals("collaboration", packet.getHeader().getProtocolType());
        assertEquals("send_message", packet.getHeader().getCommandType());
        assertEquals("agent-001", packet.getSource());
        assertEquals("agent-002", packet.getTarget());
        assertEquals(8, packet.getHeader().getPriority());
        assertEquals(60000, packet.getHeader().getTimeout());
        assertTrue(packet.getHeader().isRequiresAck());
        assertEquals(CommandDirection.NORTHBOUND, packet.getDirection());
        assertEquals("parent-cmd-001", packet.getParentCommandId());
        assertTrue(packet.isRollbackable());
        assertEquals("scene-001", packet.getSceneId());
        assertEquals("domain-001", packet.getDomainId());
    }
    
    @Test
    public void testBuilderNorthbound() {
        CommandPacket packet = CommandBuilder.create()
            .protocolType("test")
            .commandType("test")
            .northbound()
            .build();
        
        assertEquals(CommandDirection.NORTHBOUND, packet.getDirection());
    }
    
    @Test
    public void testBuilderSouthbound() {
        CommandPacket packet = CommandBuilder.create()
            .protocolType("test")
            .commandType("test")
            .southbound()
            .build();
        
        assertEquals(CommandDirection.SOUTHBOUND, packet.getDirection());
    }
    
    @Test
    public void testSettersAndGetters() {
        CommandPacket packet = new CommandPacket();
        
        packet.setPacketId("custom-pkt-id");
        assertEquals("custom-pkt-id", packet.getPacketId());
        
        packet.setSource("source-agent");
        assertEquals("source-agent", packet.getSource());
        
        packet.setTarget("target-agent");
        assertEquals("target-agent", packet.getTarget());
        
        packet.setDirection(CommandDirection.SOUTHBOUND);
        assertEquals(CommandDirection.SOUTHBOUND, packet.getDirection());
        
        packet.setParentCommandId("parent-id");
        assertEquals("parent-id", packet.getParentCommandId());
        
        packet.setRollbackable(true);
        assertTrue(packet.isRollbackable());
        
        packet.setSceneId("scene-id");
        assertEquals("scene-id", packet.getSceneId());
        
        packet.setDomainId("domain-id");
        assertEquals("domain-id", packet.getDomainId());
        
        packet.setRetryCount(3);
        assertEquals(3, packet.getRetryCount());
        
        packet.setCreatedTime(1234567890L);
        assertEquals(1234567890L, packet.getCreatedTime());
        
        packet.setExecutedTime(1234567900L);
        assertEquals(1234567900L, packet.getExecutedTime());
    }
    
    @Test
    public void testChildCommandIds() {
        CommandPacket packet = new CommandPacket();
        
        List<String> childIds = new ArrayList<String>();
        childIds.add("child-001");
        childIds.add("child-002");
        packet.setChildCommandIds(childIds);
        
        assertEquals(2, packet.getChildCommandIds().size());
        assertTrue(packet.getChildCommandIds().contains("child-001"));
        assertTrue(packet.getChildCommandIds().contains("child-002"));
        
        packet.addChildCommandId("child-003");
        assertEquals(3, packet.getChildCommandIds().size());
        assertTrue(packet.getChildCommandIds().contains("child-003"));
    }
    
    @Test
    public void testIsNorthbound() {
        CommandPacket packet = new CommandPacket();
        packet.setDirection(CommandDirection.NORTHBOUND);
        assertTrue(packet.isNorthbound());
        assertFalse(packet.isSouthbound());
    }
    
    @Test
    public void testIsSouthbound() {
        CommandPacket packet = new CommandPacket();
        packet.setDirection(CommandDirection.SOUTHBOUND);
        assertTrue(packet.isSouthbound());
        assertFalse(packet.isNorthbound());
    }
    
    @Test
    public void testHasParent() {
        CommandPacket packetWithParent = new CommandPacket();
        packetWithParent.setParentCommandId("parent-001");
        assertTrue(packetWithParent.hasParent());
        
        CommandPacket packetWithoutParent = new CommandPacket();
        packetWithoutParent.setParentCommandId(null);
        assertFalse(packetWithoutParent.hasParent());
        
        CommandPacket packetWithEmptyParent = new CommandPacket();
        packetWithEmptyParent.setParentCommandId("");
        assertFalse(packetWithEmptyParent.hasParent());
    }
    
    @Test
    public void testHasChildren() {
        CommandPacket packetWithChildren = new CommandPacket();
        packetWithChildren.addChildCommandId("child-001");
        assertTrue(packetWithChildren.hasChildren());
        
        CommandPacket packetWithoutChildren = new CommandPacket();
        assertFalse(packetWithoutChildren.hasChildren());
    }
    
    @Test
    public void testCommandHeader() {
        CommandPacket.CommandHeader header = new CommandPacket.CommandHeader();
        
        assertEquals("1.0", header.getVersion());
        assertEquals(5, header.getPriority());
        assertTrue(header.isRequiresAck());
        assertEquals(30000, header.getTimeout());
        
        header.setProtocolType("test-protocol");
        assertEquals("test-protocol", header.getProtocolType());
        
        header.setCommandType("test-command");
        assertEquals("test-command", header.getCommandType());
        
        header.setVersion("2.0");
        assertEquals("2.0", header.getVersion());
        
        header.setPriority(10);
        assertEquals(10, header.getPriority());
        
        header.setRequiresAck(false);
        assertFalse(header.isRequiresAck());
        
        header.setTimeout(60000);
        assertEquals(60000, header.getTimeout());
    }
    
    @Test
    public void testToString() {
        CommandPacket packet = CommandPacket.of("test-protocol", "test-command");
        packet.setDirection(CommandDirection.NORTHBOUND);
        
        String str = packet.toString();
        assertTrue(str.contains("test-protocol"));
        assertTrue(str.contains("test-command"));
        assertTrue(str.contains("NORTHBOUND"));
    }
}

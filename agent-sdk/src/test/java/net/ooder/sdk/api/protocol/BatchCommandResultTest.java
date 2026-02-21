package net.ooder.sdk.api.protocol;

import org.junit.Test;
import static org.junit.Assert.*;

public class BatchCommandResultTest {
    
    @Test
    public void testDefaultConstructor() {
        BatchCommandResult result = new BatchCommandResult();
        
        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertNotNull(result.getFailedPacketIds());
        assertTrue(result.getFailedPacketIds().isEmpty());
    }
    
    @Test
    public void testSettersAndGetters() {
        BatchCommandResult result = new BatchCommandResult();
        
        result.setTotalCount(10);
        assertEquals(10, result.getTotalCount());
        
        result.setSuccessCount(8);
        assertEquals(8, result.getSuccessCount());
        
        result.setFailedCount(2);
        assertEquals(2, result.getFailedCount());
        
        result.setProcessingTime(1234L);
        assertEquals(1234L, result.getProcessingTime());
    }
    
    @Test
    public void testIncrementSuccess() {
        BatchCommandResult result = new BatchCommandResult();
        result.setSuccessCount(5);
        
        result.incrementSuccess();
        assertEquals(6, result.getSuccessCount());
    }
    
    @Test
    public void testIncrementFailed() {
        BatchCommandResult result = new BatchCommandResult();
        result.setFailedCount(2);
        
        result.incrementFailed();
        assertEquals(3, result.getFailedCount());
    }
    
    @Test
    public void testAddFailedPacketId() {
        BatchCommandResult result = new BatchCommandResult();
        
        result.addFailedPacketId("pkt-001");
        result.addFailedPacketId("pkt-002");
        
        assertEquals(2, result.getFailedPacketIds().size());
        assertTrue(result.getFailedPacketIds().contains("pkt-001"));
        assertTrue(result.getFailedPacketIds().contains("pkt-002"));
    }
    
    @Test
    public void testIsAllSuccess() {
        BatchCommandResult allSuccess = new BatchCommandResult();
        allSuccess.setTotalCount(10);
        allSuccess.setSuccessCount(10);
        allSuccess.setFailedCount(0);
        assertTrue(allSuccess.isAllSuccess());
        
        BatchCommandResult partialSuccess = new BatchCommandResult();
        partialSuccess.setTotalCount(10);
        partialSuccess.setSuccessCount(8);
        partialSuccess.setFailedCount(2);
        assertFalse(partialSuccess.isAllSuccess());
    }
    
    @Test
    public void testGetSuccessRate() {
        BatchCommandResult result = new BatchCommandResult();
        result.setTotalCount(10);
        result.setSuccessCount(8);
        
        assertEquals(0.8, result.getSuccessRate(), 0.001);
    }
    
    @Test
    public void testGetSuccessRateZeroTotal() {
        BatchCommandResult result = new BatchCommandResult();
        result.setTotalCount(0);
        
        assertEquals(0.0, result.getSuccessRate(), 0.001);
    }
}

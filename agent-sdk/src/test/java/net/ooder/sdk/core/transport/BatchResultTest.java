package net.ooder.sdk.core.transport;

import org.junit.Test;
import static org.junit.Assert.*;

public class BatchResultTest {
    
    @Test
    public void testDefaultConstructor() {
        BatchResult result = new BatchResult();
        
        assertEquals(0, result.getTotalCount());
        assertEquals(0, result.getSuccessCount());
        assertEquals(0, result.getFailedCount());
        assertNotNull(result.getFailedMessageIds());
        assertTrue(result.getFailedMessageIds().isEmpty());
    }
    
    @Test
    public void testSettersAndGetters() {
        BatchResult result = new BatchResult();
        
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
        BatchResult result = new BatchResult();
        result.setSuccessCount(5);
        
        result.incrementSuccess();
        assertEquals(6, result.getSuccessCount());
    }
    
    @Test
    public void testIncrementFailed() {
        BatchResult result = new BatchResult();
        result.setFailedCount(2);
        
        result.incrementFailed();
        assertEquals(3, result.getFailedCount());
    }
    
    @Test
    public void testAddFailedMessageId() {
        BatchResult result = new BatchResult();
        
        result.addFailedMessageId("msg-001");
        result.addFailedMessageId("msg-002");
        
        assertEquals(2, result.getFailedMessageIds().size());
        assertTrue(result.getFailedMessageIds().contains("msg-001"));
        assertTrue(result.getFailedMessageIds().contains("msg-002"));
    }
    
    @Test
    public void testIsAllSuccess() {
        BatchResult allSuccess = new BatchResult();
        allSuccess.setTotalCount(10);
        allSuccess.setSuccessCount(10);
        allSuccess.setFailedCount(0);
        assertTrue(allSuccess.isAllSuccess());
        
        BatchResult partialSuccess = new BatchResult();
        partialSuccess.setTotalCount(10);
        partialSuccess.setSuccessCount(8);
        partialSuccess.setFailedCount(2);
        assertFalse(partialSuccess.isAllSuccess());
    }
    
    @Test
    public void testGetSuccessRate() {
        BatchResult result = new BatchResult();
        result.setTotalCount(10);
        result.setSuccessCount(8);
        
        assertEquals(0.8, result.getSuccessRate(), 0.001);
    }
    
    @Test
    public void testGetSuccessRateZeroTotal() {
        BatchResult result = new BatchResult();
        result.setTotalCount(0);
        
        assertEquals(0.0, result.getSuccessRate(), 0.001);
    }
}

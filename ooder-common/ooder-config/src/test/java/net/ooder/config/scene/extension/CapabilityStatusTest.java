package net.ooder.config.scene.extension;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CapabilityStatusTest {
    
    private CapabilityStatus status;
    
    @Before
    public void setUp() {
        status = new CapabilityStatus("org-query");
    }
    
    @Test
    public void testDefaultValues() {
        assertEquals("org-query", status.getCapabilityCode());
        assertEquals(CapabilityStatus.STATUS_INACTIVE, status.getStatus());
        assertEquals(0, status.getInvokeCount());
        assertEquals(0, status.getErrorCount());
        assertFalse(status.isActive());
        assertFalse(status.isError());
    }
    
    @Test
    public void testMarkActive() {
        status.markActive();
        
        assertEquals(CapabilityStatus.STATUS_ACTIVE, status.getStatus());
        assertTrue(status.isActive());
        assertNull(status.getErrorMessage());
    }
    
    @Test
    public void testMarkError() {
        status.markError("Connection failed");
        
        assertEquals(CapabilityStatus.STATUS_ERROR, status.getStatus());
        assertTrue(status.isError());
        assertEquals("Connection failed", status.getErrorMessage());
        assertEquals(1, status.getErrorCount());
    }
    
    @Test
    public void testMarkInactive() {
        status.markActive();
        status.markInactive();
        
        assertEquals(CapabilityStatus.STATUS_INACTIVE, status.getStatus());
        assertFalse(status.isActive());
    }
    
    @Test
    public void testIncrementInvokeCount() {
        status.incrementInvokeCount();
        status.incrementInvokeCount();
        
        assertEquals(2, status.getInvokeCount());
        assertTrue(status.getLastInvokeTime() > 0);
    }
    
    @Test
    public void testIncrementErrorCount() {
        status.incrementErrorCount();
        
        assertEquals(1, status.getErrorCount());
    }
    
    @Test
    public void testReset() {
        status.markActive();
        status.incrementInvokeCount();
        status.incrementErrorCount();
        
        status.reset();
        
        assertEquals(CapabilityStatus.STATUS_INACTIVE, status.getStatus());
        assertEquals(0, status.getInvokeCount());
        assertEquals(0, status.getErrorCount());
        assertNull(status.getErrorMessage());
    }
    
    @Test
    public void testStatusConstants() {
        assertEquals("ACTIVE", CapabilityStatus.STATUS_ACTIVE);
        assertEquals("INACTIVE", CapabilityStatus.STATUS_INACTIVE);
        assertEquals("ERROR", CapabilityStatus.STATUS_ERROR);
        assertEquals("INITIALIZING", CapabilityStatus.STATUS_INITIALIZING);
    }
    
    @Test
    public void testToString() {
        String str = status.toString();
        assertTrue(str.contains("org-query"));
        assertTrue(str.contains("INACTIVE"));
    }
}

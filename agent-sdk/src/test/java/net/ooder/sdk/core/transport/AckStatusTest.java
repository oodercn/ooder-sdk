package net.ooder.sdk.core.transport;

import org.junit.Test;
import static org.junit.Assert.*;

public class AckStatusTest {
    
    @Test
    public void testAckStatusValues() {
        AckStatus[] statuses = AckStatus.values();
        assertEquals(5, statuses.length);
    }
    
    @Test
    public void testAckStatusCodes() {
        assertEquals(1, AckStatus.DELIVERED.getCode());
        assertEquals(2, AckStatus.READ.getCode());
        assertEquals(3, AckStatus.FAILED.getCode());
        assertEquals(4, AckStatus.PENDING.getCode());
        assertEquals(5, AckStatus.TIMEOUT.getCode());
    }
    
    @Test
    public void testAckStatusDescriptions() {
        assertEquals("已送达", AckStatus.DELIVERED.getDescription());
        assertEquals("已读", AckStatus.READ.getDescription());
        assertEquals("发送失败", AckStatus.FAILED.getDescription());
        assertEquals("待确认", AckStatus.PENDING.getDescription());
        assertEquals("确认超时", AckStatus.TIMEOUT.getDescription());
    }
    
    @Test
    public void testFromCode() {
        assertEquals(AckStatus.DELIVERED, AckStatus.fromCode(1));
        assertEquals(AckStatus.READ, AckStatus.fromCode(2));
        assertEquals(AckStatus.FAILED, AckStatus.fromCode(3));
        assertEquals(AckStatus.PENDING, AckStatus.fromCode(4));
        assertEquals(AckStatus.TIMEOUT, AckStatus.fromCode(5));
    }
    
    @Test
    public void testFromCodeUnknown() {
        assertEquals(AckStatus.PENDING, AckStatus.fromCode(999));
        assertEquals(AckStatus.PENDING, AckStatus.fromCode(-1));
    }
}

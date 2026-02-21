package net.ooder.sdk.core.collaboration;

import org.junit.Test;
import static org.junit.Assert.*;

public class MsgStatusTest {
    
    @Test
    public void testMsgStatusValues() {
        MsgStatus[] statuses = MsgStatus.values();
        assertEquals(8, statuses.length);
    }
    
    @Test
    public void testMsgStatusCodes() {
        assertEquals(0, MsgStatus.NORMAL.getCode());
        assertEquals(1, MsgStatus.URGENT.getCode());
        assertEquals(2, MsgStatus.READ.getCode());
        assertEquals(3, MsgStatus.UNREAD.getCode());
        assertEquals(4, MsgStatus.DELIVERED.getCode());
        assertEquals(5, MsgStatus.FAILED.getCode());
        assertEquals(6, MsgStatus.PENDING.getCode());
        assertEquals(7, MsgStatus.SENDING.getCode());
    }
    
    @Test
    public void testMsgStatusDescriptions() {
        assertEquals("普通", MsgStatus.NORMAL.getDescription());
        assertEquals("紧急", MsgStatus.URGENT.getDescription());
        assertEquals("已读", MsgStatus.READ.getDescription());
        assertEquals("未读", MsgStatus.UNREAD.getDescription());
        assertEquals("已送达", MsgStatus.DELIVERED.getDescription());
        assertEquals("发送失败", MsgStatus.FAILED.getDescription());
        assertEquals("待发送", MsgStatus.PENDING.getDescription());
        assertEquals("发送中", MsgStatus.SENDING.getDescription());
    }
    
    @Test
    public void testFromCode() {
        assertEquals(MsgStatus.NORMAL, MsgStatus.fromCode(0));
        assertEquals(MsgStatus.URGENT, MsgStatus.fromCode(1));
        assertEquals(MsgStatus.READ, MsgStatus.fromCode(2));
        assertEquals(MsgStatus.UNREAD, MsgStatus.fromCode(3));
        assertEquals(MsgStatus.DELIVERED, MsgStatus.fromCode(4));
        assertEquals(MsgStatus.FAILED, MsgStatus.fromCode(5));
        assertEquals(MsgStatus.PENDING, MsgStatus.fromCode(6));
        assertEquals(MsgStatus.SENDING, MsgStatus.fromCode(7));
    }
    
    @Test
    public void testFromCodeUnknown() {
        assertEquals(MsgStatus.NORMAL, MsgStatus.fromCode(999));
        assertEquals(MsgStatus.NORMAL, MsgStatus.fromCode(-1));
    }
    
    @Test
    public void testValueOf() {
        assertEquals(MsgStatus.NORMAL, MsgStatus.valueOf("NORMAL"));
        assertEquals(MsgStatus.URGENT, MsgStatus.valueOf("URGENT"));
        assertEquals(MsgStatus.FAILED, MsgStatus.valueOf("FAILED"));
    }
}

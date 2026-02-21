package net.ooder.sdk.api.protocol;

import org.junit.Test;
import static org.junit.Assert.*;

public class CommandStatusTest {
    
    @Test
    public void testCommandStatusValues() {
        CommandStatus[] statuses = CommandStatus.values();
        assertEquals(8, statuses.length);
    }
    
    @Test
    public void testCommandStatusCodes() {
        assertEquals(1, CommandStatus.PENDING.getCode());
        assertEquals(2, CommandStatus.RUNNING.getCode());
        assertEquals(3, CommandStatus.SUCCESS.getCode());
        assertEquals(4, CommandStatus.FAILED.getCode());
        assertEquals(5, CommandStatus.TIMEOUT.getCode());
        assertEquals(6, CommandStatus.CANCELLED.getCode());
        assertEquals(7, CommandStatus.ROLLBACK.getCode());
        assertEquals(8, CommandStatus.RETRYING.getCode());
    }
    
    @Test
    public void testCommandStatusDescriptions() {
        assertEquals("待执行", CommandStatus.PENDING.getDescription());
        assertEquals("执行中", CommandStatus.RUNNING.getDescription());
        assertEquals("执行成功", CommandStatus.SUCCESS.getDescription());
        assertEquals("执行失败", CommandStatus.FAILED.getDescription());
        assertEquals("执行超时", CommandStatus.TIMEOUT.getDescription());
        assertEquals("已取消", CommandStatus.CANCELLED.getDescription());
        assertEquals("已回滚", CommandStatus.ROLLBACK.getDescription());
        assertEquals("重试中", CommandStatus.RETRYING.getDescription());
    }
    
    @Test
    public void testFromCode() {
        assertEquals(CommandStatus.PENDING, CommandStatus.fromCode(1));
        assertEquals(CommandStatus.RUNNING, CommandStatus.fromCode(2));
        assertEquals(CommandStatus.SUCCESS, CommandStatus.fromCode(3));
        assertEquals(CommandStatus.FAILED, CommandStatus.fromCode(4));
        assertEquals(CommandStatus.TIMEOUT, CommandStatus.fromCode(5));
        assertEquals(CommandStatus.CANCELLED, CommandStatus.fromCode(6));
        assertEquals(CommandStatus.ROLLBACK, CommandStatus.fromCode(7));
        assertEquals(CommandStatus.RETRYING, CommandStatus.fromCode(8));
    }
    
    @Test
    public void testFromCodeUnknown() {
        assertEquals(CommandStatus.PENDING, CommandStatus.fromCode(999));
        assertEquals(CommandStatus.PENDING, CommandStatus.fromCode(-1));
    }
    
    @Test
    public void testIsTerminal() {
        assertTrue(CommandStatus.SUCCESS.isTerminal());
        assertTrue(CommandStatus.FAILED.isTerminal());
        assertTrue(CommandStatus.TIMEOUT.isTerminal());
        assertTrue(CommandStatus.CANCELLED.isTerminal());
        assertTrue(CommandStatus.ROLLBACK.isTerminal());
        
        assertFalse(CommandStatus.PENDING.isTerminal());
        assertFalse(CommandStatus.RUNNING.isTerminal());
        assertFalse(CommandStatus.RETRYING.isTerminal());
    }
}

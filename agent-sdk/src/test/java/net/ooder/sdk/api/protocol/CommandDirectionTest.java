package net.ooder.sdk.api.protocol;

import org.junit.Test;
import static org.junit.Assert.*;

public class CommandDirectionTest {
    
    @Test
    public void testCommandDirectionValues() {
        CommandDirection[] directions = CommandDirection.values();
        assertEquals(3, directions.length);
    }
    
    @Test
    public void testCommandDirectionCodes() {
        assertEquals(1, CommandDirection.NORTHBOUND.getCode());
        assertEquals(2, CommandDirection.SOUTHBOUND.getCode());
        assertEquals(3, CommandDirection.INTERNAL.getCode());
    }
    
    @Test
    public void testCommandDirectionDescriptions() {
        assertEquals("北向命令", CommandDirection.NORTHBOUND.getDescription());
        assertEquals("南向命令", CommandDirection.SOUTHBOUND.getDescription());
        assertEquals("内部命令", CommandDirection.INTERNAL.getDescription());
    }
    
    @Test
    public void testFromCode() {
        assertEquals(CommandDirection.NORTHBOUND, CommandDirection.fromCode(1));
        assertEquals(CommandDirection.SOUTHBOUND, CommandDirection.fromCode(2));
        assertEquals(CommandDirection.INTERNAL, CommandDirection.fromCode(3));
    }
    
    @Test
    public void testFromCodeUnknown() {
        assertEquals(CommandDirection.INTERNAL, CommandDirection.fromCode(999));
        assertEquals(CommandDirection.INTERNAL, CommandDirection.fromCode(-1));
    }
    
    @Test
    public void testValueOf() {
        assertEquals(CommandDirection.NORTHBOUND, CommandDirection.valueOf("NORTHBOUND"));
        assertEquals(CommandDirection.SOUTHBOUND, CommandDirection.valueOf("SOUTHBOUND"));
        assertEquals(CommandDirection.INTERNAL, CommandDirection.valueOf("INTERNAL"));
    }
}

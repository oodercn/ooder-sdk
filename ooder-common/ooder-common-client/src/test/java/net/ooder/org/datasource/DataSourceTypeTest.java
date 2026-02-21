package net.ooder.org.datasource;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class DataSourceTypeTest {
    
    @Test
    public void testFromCode() {
        assertEquals(DataSourceType.JSON, DataSourceType.fromCode("json"));
        assertEquals(DataSourceType.DATABASE, DataSourceType.fromCode("database"));
        assertEquals(DataSourceType.DINGTALK, DataSourceType.fromCode("dingtalk"));
        assertEquals(DataSourceType.FEISHU, DataSourceType.fromCode("feishu"));
        assertEquals(DataSourceType.WECOM, DataSourceType.fromCode("wecom"));
    }
    
    @Test
    public void testFromCodeNull() {
        assertEquals(DataSourceType.JSON, DataSourceType.fromCode(null));
    }
    
    @Test
    public void testFromCodeUnknown() {
        assertEquals(DataSourceType.JSON, DataSourceType.fromCode("unknown"));
    }
    
    @Test
    public void testIsSkillsBased() {
        assertFalse(DataSourceType.JSON.isSkillsBased());
        assertFalse(DataSourceType.DATABASE.isSkillsBased());
        assertTrue(DataSourceType.DINGTALK.isSkillsBased());
        assertTrue(DataSourceType.FEISHU.isSkillsBased());
        assertTrue(DataSourceType.WECOM.isSkillsBased());
    }
    
    @Test
    public void testIsLocalMock() {
        assertTrue(DataSourceType.JSON.isLocalMock());
        assertFalse(DataSourceType.DATABASE.isLocalMock());
        assertFalse(DataSourceType.DINGTALK.isLocalMock());
    }
    
    @Test
    public void testGetCode() {
        assertEquals("json", DataSourceType.JSON.getCode());
        assertEquals("database", DataSourceType.DATABASE.getCode());
        assertEquals("dingtalk", DataSourceType.DINGTALK.getCode());
    }
    
    @Test
    public void testGetName() {
        assertNotNull(DataSourceType.JSON.getName());
        assertNotNull(DataSourceType.DATABASE.getName());
    }
    
    @Test
    public void testGetDescription() {
        assertNotNull(DataSourceType.JSON.getDescription());
    }
}

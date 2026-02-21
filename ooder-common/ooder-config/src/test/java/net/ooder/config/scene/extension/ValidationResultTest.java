package net.ooder.config.scene.extension;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationResultTest {
    
    private ValidationResult result;
    
    @Before
    public void setUp() {
        result = new ValidationResult("OrgSceneConfig");
    }
    
    @Test
    public void testDefaultValues() {
        assertTrue(result.isValid());
        assertEquals("OrgSceneConfig", result.getConfigType());
        assertTrue(result.getErrors().isEmpty());
        assertTrue(result.getWarnings().isEmpty());
        assertFalse(result.hasErrors());
        assertFalse(result.hasWarnings());
    }
    
    @Test
    public void testAddError() {
        result.addError("dbUrl is required");
        
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrorCount());
        assertEquals("dbUrl is required", result.getFirstError());
    }
    
    @Test
    public void testAddWarning() {
        result.addWarning("cacheExpireTime is using default value");
        
        assertTrue(result.isValid());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getWarningCount());
        assertEquals("cacheExpireTime is using default value", result.getFirstWarning());
    }
    
    @Test
    public void testMultipleErrors() {
        result.addError("Error 1");
        result.addError("Error 2");
        
        assertEquals(2, result.getErrorCount());
        assertEquals("Error 1", result.getFirstError());
    }
    
    @Test
    public void testMerge() {
        ValidationResult other = new ValidationResult();
        other.addError("Other error");
        other.addWarning("Other warning");
        
        result.merge(other);
        
        assertTrue(result.hasErrors());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getErrorCount());
        assertEquals(1, result.getWarningCount());
    }
    
    @Test
    public void testStaticFactoryMethods() {
        ValidationResult success = ValidationResult.success();
        assertTrue(success.isValid());
        
        ValidationResult successWithType = ValidationResult.success("TestConfig");
        assertTrue(successWithType.isValid());
        assertEquals("TestConfig", successWithType.getConfigType());
        
        ValidationResult error = ValidationResult.error("Validation failed");
        assertFalse(error.isValid());
        assertEquals("Validation failed", error.getFirstError());
        
        ValidationResult errorWithType = ValidationResult.error("TestConfig", "Field required");
        assertFalse(errorWithType.isValid());
        assertEquals("TestConfig", errorWithType.getConfigType());
        assertEquals("Field required", errorWithType.getFirstError());
    }
    
    @Test
    public void testToString() {
        result.addError("Test error");
        String str = result.toString();
        
        assertTrue(str.contains("valid=false"));
        assertTrue(str.contains("errors=1"));
        assertTrue(str.contains("OrgSceneConfig"));
    }
}

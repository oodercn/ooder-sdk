package net.ooder.scene.core.instance;

import net.ooder.scene.core.lifecycle.SceneSkillLifecycle.SkillLifecycleState;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlSceneInstanceRepository 单元测试
 */
public class SqlSceneInstanceRepositoryTest {

    private static final String TEST_DB_PATH = "target/test-scene-instances.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + TEST_DB_PATH;
    
    private SqlSceneInstanceRepository repository;

    @BeforeAll
    public static void setupClass() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @BeforeEach
    public void setUp() {
        repository = new SqlSceneInstanceRepository(JDBC_URL);
        repository.initialize();
    }

    @AfterEach
    public void tearDown() {
        if (repository != null) {
            repository.close();
        }
    }

    @AfterAll
    public static void cleanup() {
        File dbFile = new File(TEST_DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testInitialize() {
        assertTrue(repository.isInitialized());
    }

    @Test
    public void testSaveAndFindById() {
        SceneInstance instance = createTestInstance("inst-1", "scene-1", "template-1");
        
        SceneInstance saved = repository.save(instance);
        
        assertNotNull(saved);
        assertEquals("inst-1", saved.getInstanceId());
        
        Optional<SceneInstance> found = repository.findById("inst-1");
        assertTrue(found.isPresent());
        assertEquals("inst-1", found.get().getInstanceId());
        assertEquals("scene-1", found.get().getSceneId());
        assertEquals("template-1", found.get().getTemplateId());
    }

    @Test
    public void testFindBySceneId() {
        repository.save(createTestInstance("inst-2", "scene-A", "template-1"));
        repository.save(createTestInstance("inst-3", "scene-A", "template-2"));
        repository.save(createTestInstance("inst-4", "scene-B", "template-1"));
        
        List<SceneInstance> found = repository.findBySceneId("scene-A");
        
        assertEquals(2, found.size());
    }

    @Test
    public void testFindByTemplateId() {
        repository.save(createTestInstance("inst-5", "scene-1", "template-X"));
        repository.save(createTestInstance("inst-6", "scene-2", "template-X"));
        repository.save(createTestInstance("inst-7", "scene-3", "template-Y"));
        
        List<SceneInstance> found = repository.findByTemplateId("template-X");
        
        assertEquals(2, found.size());
    }

    @Test
    public void testFindByState() {
        SceneInstance inst1 = createTestInstance("inst-8", "scene-1", "template-1");
        inst1.setState(SkillLifecycleState.ACTIVATED);
        
        SceneInstance inst2 = createTestInstance("inst-9", "scene-2", "template-1");
        inst2.setState(SkillLifecycleState.INSTALLED);
        
        repository.save(inst1);
        repository.save(inst2);
        
        List<SceneInstance> found = repository.findByState("ACTIVATED");
        
        assertEquals(1, found.size());
        assertEquals("inst-8", found.get(0).getInstanceId());
    }

    @Test
    public void testUpdateState() {
        repository.save(createTestInstance("inst-10", "scene-1", "template-1"));
        
        boolean updated = repository.updateState("inst-10", "ACTIVATED");
        
        assertTrue(updated);
        
        Optional<SceneInstance> found = repository.findById("inst-10");
        assertTrue(found.isPresent());
        assertEquals(SkillLifecycleState.ACTIVATED, found.get().getState());
    }

    @Test
    public void testAddAndRemoveParticipant() {
        repository.save(createTestInstance("inst-11", "scene-1", "template-1"));
        
        SceneInstance.ParticipantInfo participant = new SceneInstance.ParticipantInfo();
        participant.setUserId("user-1");
        participant.setUserName("Test User");
        participant.setRoleId("role-1");
        participant.setRoleName("Admin");
        participant.setStatus(SceneInstance.ParticipantInfo.ParticipantStatus.ACTIVE);
        participant.setJoinedAt(System.currentTimeMillis());
        
        boolean added = repository.addParticipant("inst-11", participant);
        assertTrue(added);
        
        Optional<SceneInstance> found = repository.findById("inst-11");
        assertTrue(found.isPresent());
        assertTrue(found.get().getParticipants().containsKey("user-1"));
        
        boolean removed = repository.removeParticipant("inst-11", "user-1");
        assertTrue(removed);
        
        found = repository.findById("inst-11");
        assertTrue(found.isPresent());
        assertFalse(found.get().getParticipants().containsKey("user-1"));
    }

    @Test
    public void testFindByUserId() {
        SceneInstance inst1 = createTestInstance("inst-12", "scene-1", "template-1");
        SceneInstance.ParticipantInfo p1 = new SceneInstance.ParticipantInfo();
        p1.setUserId("user-A");
        inst1.addParticipant(p1);
        
        SceneInstance inst2 = createTestInstance("inst-13", "scene-2", "template-1");
        SceneInstance.ParticipantInfo p2 = new SceneInstance.ParticipantInfo();
        p2.setUserId("user-A");
        inst2.addParticipant(p2);
        
        SceneInstance inst3 = createTestInstance("inst-14", "scene-3", "template-1");
        SceneInstance.ParticipantInfo p3 = new SceneInstance.ParticipantInfo();
        p3.setUserId("user-B");
        inst3.addParticipant(p3);
        
        repository.save(inst1);
        repository.save(inst2);
        repository.save(inst3);
        
        List<SceneInstance> found = repository.findByUserId("user-A");
        
        assertEquals(2, found.size());
    }

    @Test
    public void testAddActivationRecord() {
        repository.save(createTestInstance("inst-15", "scene-1", "template-1"));
        
        SceneInstance.ActivationRecord record = new SceneInstance.ActivationRecord();
        record.setRecordId("rec-1");
        record.setUserId("user-1");
        record.setStepId("step-1");
        record.setStepName("Test Step");
        record.setAction("EXECUTE");
        record.setSuccess(true);
        record.setTimestamp(System.currentTimeMillis());
        
        boolean added = repository.addActivationRecord("inst-15", record);
        assertTrue(added);
        
        Optional<SceneInstance> found = repository.findById("inst-15");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getActivationHistory().size());
    }

    @Test
    public void testUpdateConfig() {
        repository.save(createTestInstance("inst-16", "scene-1", "template-1"));
        
        Map<String, Object> newConfig = new HashMap<>();
        newConfig.put("key1", "value1");
        newConfig.put("key2", 123);
        
        boolean updated = repository.updateConfig("inst-16", newConfig);
        assertTrue(updated);
        
        Optional<SceneInstance> found = repository.findById("inst-16");
        assertTrue(found.isPresent());
        assertEquals("value1", found.get().getConfig().get("key1"));
        assertEquals(123, found.get().getConfig().get("key2"));
    }

    @Test
    public void testDeleteById() {
        repository.save(createTestInstance("inst-17", "scene-1", "template-1"));
        
        assertTrue(repository.existsById("inst-17"));
        
        boolean deleted = repository.deleteById("inst-17");
        assertTrue(deleted);
        
        assertFalse(repository.existsById("inst-17"));
    }

    @Test
    public void testCount() {
        repository.save(createTestInstance("inst-18", "scene-1", "template-1"));
        repository.save(createTestInstance("inst-19", "scene-2", "template-1"));
        repository.save(createTestInstance("inst-20", "scene-1", "template-2"));
        
        assertEquals(3, repository.count());
        assertEquals(2, repository.countBySceneId("scene-1"));
    }

    @Test
    public void testFindAll() {
        repository.save(createTestInstance("inst-21", "scene-1", "template-1"));
        repository.save(createTestInstance("inst-22", "scene-2", "template-1"));
        
        List<SceneInstance> all = repository.findAll();
        
        assertEquals(2, all.size());
    }

    @Test
    public void testSaveWithParticipantsAndRecords() {
        SceneInstance instance = createTestInstance("inst-23", "scene-1", "template-1");
        
        SceneInstance.ParticipantInfo p = new SceneInstance.ParticipantInfo();
        p.setUserId("user-1");
        p.setUserName("Test User");
        p.setRoleId("admin");
        instance.addParticipant(p);
        
        SceneInstance.ActivationRecord r = new SceneInstance.ActivationRecord();
        r.setRecordId("rec-1");
        r.setStepId("step-1");
        r.setSuccess(true);
        instance.addActivationRecord(r);
        
        repository.save(instance);
        
        Optional<SceneInstance> found = repository.findById("inst-23");
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getParticipants().size());
        assertEquals(1, found.get().getActivationHistory().size());
    }

    private SceneInstance createTestInstance(String instanceId, String sceneId, String templateId) {
        SceneInstance instance = new SceneInstance();
        instance.setInstanceId(instanceId);
        instance.setSceneId(sceneId);
        instance.setTemplateId(templateId);
        instance.setTemplateName("Test Template");
        instance.setState(SkillLifecycleState.INSTALLED);
        instance.setCreatedAt(System.currentTimeMillis());
        instance.setUpdatedAt(System.currentTimeMillis());
        instance.setCreatedBy("test-user");
        return instance;
    }
}

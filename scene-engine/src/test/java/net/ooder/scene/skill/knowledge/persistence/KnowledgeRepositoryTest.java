package net.ooder.scene.skill.knowledge.persistence;

import net.ooder.scene.skill.knowledge.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * KnowledgeRepository 测试类
 *
 * <p>测试内存存储和 JSON 文件存储两种实现的功能一致性</p>
 *
 * @author ooder
 * @since 2.3
 */
public class KnowledgeRepositoryTest {

    @TempDir
    Path tempDir;

    private KnowledgeRepository inMemoryRepo;
    private KnowledgeRepository jsonRepo;

    @BeforeEach
    void setUp() {
        inMemoryRepo = new InMemoryKnowledgeRepository();
        inMemoryRepo.initialize();

        jsonRepo = new JsonKnowledgeRepository(tempDir.toString());
        jsonRepo.initialize();
    }

    @AfterEach
    void tearDown() {
        if (inMemoryRepo != null) {
            inMemoryRepo.close();
        }
        if (jsonRepo != null) {
            jsonRepo.close();
        }
    }

    // ========== 知识库 CRUD 测试 ==========

    @Test
    @DisplayName("测试知识库保存和查询 - 内存存储")
    void testKnowledgeBaseSaveAndFind_InMemory() {
        testKnowledgeBaseSaveAndFind(inMemoryRepo);
    }

    @Test
    @DisplayName("测试知识库保存和查询 - JSON存储")
    void testKnowledgeBaseSaveAndFind_Json() {
        testKnowledgeBaseSaveAndFind(jsonRepo);
    }

    private void testKnowledgeBaseSaveAndFind(KnowledgeRepository repo) {
        // 创建知识库
        KnowledgeBase kb = createTestKnowledgeBase("test-kb-1", "Test KB", "user-1");
        repo.saveKnowledgeBase(kb);

        // 验证保存成功
        assertTrue(repo.existsKnowledgeBase("test-kb-1"));

        // 验证查询
        KnowledgeBase found = repo.findKnowledgeBaseById("test-kb-1");
        assertNotNull(found);
        assertEquals("Test KB", found.getName());
        assertEquals("user-1", found.getOwnerId());

        // 验证列表查询
        List<KnowledgeBase> all = repo.findAllKnowledgeBases();
        assertEquals(1, all.size());

        // 验证按所有者查询
        List<KnowledgeBase> byOwner = repo.findKnowledgeBasesByOwner("user-1");
        assertEquals(1, byOwner.size());
    }

    @Test
    @DisplayName("测试知识库更新 - 内存存储")
    void testKnowledgeBaseUpdate_InMemory() {
        testKnowledgeBaseUpdate(inMemoryRepo);
    }

    @Test
    @DisplayName("测试知识库更新 - JSON存储")
    void testKnowledgeBaseUpdate_Json() {
        testKnowledgeBaseUpdate(jsonRepo);
    }

    private void testKnowledgeBaseUpdate(KnowledgeRepository repo) {
        KnowledgeBase kb = createTestKnowledgeBase("test-kb-2", "Original Name", "user-1");
        repo.saveKnowledgeBase(kb);

        // 更新
        kb.setName("Updated Name");
        kb.setDescription("Updated Description");
        repo.saveKnowledgeBase(kb);

        // 验证更新
        KnowledgeBase updated = repo.findKnowledgeBaseById("test-kb-2");
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    @DisplayName("测试知识库删除 - 内存存储")
    void testKnowledgeBaseDelete_InMemory() {
        testKnowledgeBaseDelete(inMemoryRepo);
    }

    @Test
    @DisplayName("测试知识库删除 - JSON存储")
    void testKnowledgeBaseDelete_Json() {
        testKnowledgeBaseDelete(jsonRepo);
    }

    private void testKnowledgeBaseDelete(KnowledgeRepository repo) {
        KnowledgeBase kb = createTestKnowledgeBase("test-kb-3", "To Delete", "user-1");
        repo.saveKnowledgeBase(kb);
        assertTrue(repo.existsKnowledgeBase("test-kb-3"));

        repo.deleteKnowledgeBase("test-kb-3");
        assertFalse(repo.existsKnowledgeBase("test-kb-3"));
        assertNull(repo.findKnowledgeBaseById("test-kb-3"));
    }

    // ========== 文档 CRUD 测试 ==========

    @Test
    @DisplayName("测试文档保存和查询 - 内存存储")
    void testDocumentSaveAndFind_InMemory() {
        testDocumentSaveAndFind(inMemoryRepo);
    }

    @Test
    @DisplayName("测试文档保存和查询 - JSON存储")
    void testDocumentSaveAndFind_Json() {
        testDocumentSaveAndFind(jsonRepo);
    }

    private void testDocumentSaveAndFind(KnowledgeRepository repo) {
        // 先创建知识库
        KnowledgeBase kb = createTestKnowledgeBase("kb-doc-test", "KB for Docs", "user-1");
        repo.saveKnowledgeBase(kb);

        // 创建文档
        Document doc = createTestDocument("doc-1", "kb-doc-test", "Test Doc", "Test Content");
        repo.saveDocument(doc);

        // 验证查询
        Document found = repo.findDocumentById("kb-doc-test", "doc-1");
        assertNotNull(found);
        assertEquals("Test Doc", found.getTitle());
        assertEquals("Test Content", found.getContent());

        // 验证列表查询
        List<Document> docs = repo.findDocumentsByKnowledgeBase("kb-doc-test");
        assertEquals(1, docs.size());
    }

    @Test
    @DisplayName("测试批量文档保存 - 内存存储")
    void testBatchDocumentSave_InMemory() {
        testBatchDocumentSave(inMemoryRepo);
    }

    @Test
    @DisplayName("测试批量文档保存 - JSON存储")
    void testBatchDocumentSave_Json() {
        testBatchDocumentSave(jsonRepo);
    }

    private void testBatchDocumentSave(KnowledgeRepository repo) {
        KnowledgeBase kb = createTestKnowledgeBase("kb-batch-test", "KB for Batch", "user-1");
        repo.saveKnowledgeBase(kb);

        List<Document> docs = Arrays.asList(
            createTestDocument("doc-1", "kb-batch-test", "Doc 1", "Content 1"),
            createTestDocument("doc-2", "kb-batch-test", "Doc 2", "Content 2"),
            createTestDocument("doc-3", "kb-batch-test", "Doc 3", "Content 3")
        );
        repo.saveDocuments(docs);

        List<Document> found = repo.findDocumentsByKnowledgeBase("kb-batch-test");
        assertEquals(3, found.size());
    }

    @Test
    @DisplayName("测试文档删除 - 内存存储")
    void testDocumentDelete_InMemory() {
        testDocumentDelete(inMemoryRepo);
    }

    @Test
    @DisplayName("测试文档删除 - JSON存储")
    void testDocumentDelete_Json() {
        testDocumentDelete(jsonRepo);
    }

    private void testDocumentDelete(KnowledgeRepository repo) {
        KnowledgeBase kb = createTestKnowledgeBase("kb-del-test", "KB for Delete", "user-1");
        repo.saveKnowledgeBase(kb);

        Document doc = createTestDocument("doc-del", "kb-del-test", "To Delete", "Content");
        repo.saveDocument(doc);
        assertNotNull(repo.findDocumentById("kb-del-test", "doc-del"));

        repo.deleteDocument("kb-del-test", "doc-del");
        assertNull(repo.findDocumentById("kb-del-test", "doc-del"));
    }

    // ========== 索引状态测试 ==========

    @Test
    @DisplayName("测试索引状态保存和查询 - 内存存储")
    void testIndexStatusSaveAndFind_InMemory() {
        testIndexStatusSaveAndFind(inMemoryRepo);
    }

    @Test
    @DisplayName("测试索引状态保存和查询 - JSON存储")
    void testIndexStatusSaveAndFind_Json() {
        testIndexStatusSaveAndFind(jsonRepo);
    }

    private void testIndexStatusSaveAndFind(KnowledgeRepository repo) {
        IndexStatus status = new IndexStatus("kb-status-test");
        status.start(10, 1000);
        repo.saveIndexStatus(status);

        IndexStatus found = repo.findIndexStatus("kb-status-test");
        assertNotNull(found);
        assertEquals(10, found.getTotalDocuments());
        assertEquals(1000, found.getTotalSize());
    }

    // ========== 权限管理测试 ==========

    @Test
    @DisplayName("测试权限保存和查询 - 内存存储")
    void testPermissionSaveAndFind_InMemory() {
        testPermissionSaveAndFind(inMemoryRepo);
    }

    @Test
    @DisplayName("测试权限保存和查询 - JSON存储")
    void testPermissionSaveAndFind_Json() {
        testPermissionSaveAndFind(jsonRepo);
    }

    private void testPermissionSaveAndFind(KnowledgeRepository repo) {
        repo.savePermission("kb-perm-test", "user-1", "admin");
        repo.savePermission("kb-perm-test", "user-2", "read");

        assertEquals("admin", repo.findPermission("kb-perm-test", "user-1"));
        assertEquals("read", repo.findPermission("kb-perm-test", "user-2"));

        Map<String, String> perms = repo.findPermissionsByKnowledgeBase("kb-perm-test");
        assertEquals(2, perms.size());
        assertTrue(perms.containsKey("user-1"));
        assertTrue(perms.containsKey("user-2"));
    }

    @Test
    @DisplayName("测试权限删除 - 内存存储")
    void testPermissionDelete_InMemory() {
        testPermissionDelete(inMemoryRepo);
    }

    @Test
    @DisplayName("测试权限删除 - JSON存储")
    void testPermissionDelete_Json() {
        testPermissionDelete(jsonRepo);
    }

    private void testPermissionDelete(KnowledgeRepository repo) {
        repo.savePermission("kb-perm-del", "user-1", "admin");
        assertNotNull(repo.findPermission("kb-perm-del", "user-1"));

        repo.deletePermission("kb-perm-del", "user-1");
        assertNull(repo.findPermission("kb-perm-del", "user-1"));
    }

    // ========== JSON 持久化专项测试 ==========

    @Test
    @DisplayName("测试 JSON 存储数据持久化 - 重启后数据保留")
    void testJsonPersistence() {
        // 第一次实例：保存数据
        JsonKnowledgeRepository repo1 = new JsonKnowledgeRepository(tempDir.toString());
        repo1.initialize();

        KnowledgeBase kb = createTestKnowledgeBase("persistent-kb", "Persistent KB", "user-1");
        kb.setDescription("This should persist");
        repo1.saveKnowledgeBase(kb);

        Document doc = createTestDocument("persistent-doc", "persistent-kb", "Persistent Doc", "Content");
        repo1.saveDocument(doc);

        repo1.close();

        // 第二次实例：验证数据仍然存在
        JsonKnowledgeRepository repo2 = new JsonKnowledgeRepository(tempDir.toString());
        repo2.initialize();

        KnowledgeBase foundKb = repo2.findKnowledgeBaseById("persistent-kb");
        assertNotNull(foundKb);
        assertEquals("Persistent KB", foundKb.getName());
        assertEquals("This should persist", foundKb.getDescription());

        Document foundDoc = repo2.findDocumentById("persistent-kb", "persistent-doc");
        assertNotNull(foundDoc);
        assertEquals("Persistent Doc", foundDoc.getTitle());

        repo2.close();
    }

    // ========== 工厂模式测试 ==========

    @Test
    @DisplayName("测试 RepositoryFactory 配置切换")
    void testRepositoryFactorySwitch() {
        // 重置工厂状态
        KnowledgeRepositoryFactory.reset();

        // 默认应该是内存存储
        KnowledgeRepository repo1 = KnowledgeRepositoryFactory.getRepository();
        assertEquals("in-memory", repo1.getStorageType());

        // 切换到 JSON 存储
        KnowledgeRepositoryFactory.switchStorageType(RepositoryConfig.jsonFile(tempDir.resolve("factory-test").toString()));
        KnowledgeRepository repo2 = KnowledgeRepositoryFactory.getRepository();
        assertEquals("json-file", repo2.getStorageType());

        // 验证数据操作
        KnowledgeBase kb = createTestKnowledgeBase("factory-kb", "Factory KB", "user-1");
        repo2.saveKnowledgeBase(kb);
        assertTrue(repo2.existsKnowledgeBase("factory-kb"));

        KnowledgeRepositoryFactory.reset();
    }

    // ========== 辅助方法 ==========

    private KnowledgeBase createTestKnowledgeBase(String kbId, String name, String ownerId) {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setKbId(kbId);
        kb.setName(name);
        kb.setOwnerId(ownerId);
        kb.setVisibility(KnowledgeBase.VISIBILITY_PRIVATE);
        kb.setChunkSize(500);
        kb.setChunkOverlap(50);
        kb.setCreatedAt(System.currentTimeMillis());
        kb.setUpdatedAt(System.currentTimeMillis());
        return kb;
    }

    private Document createTestDocument(String docId, String kbId, String title, String content) {
        Document doc = new Document();
        doc.setDocId(docId);
        doc.setKbId(kbId);
        doc.setTitle(title);
        doc.setContent(content);
        doc.setCreatedAt(System.currentTimeMillis());
        doc.setUpdatedAt(System.currentTimeMillis());
        return doc;
    }
}

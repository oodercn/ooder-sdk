package net.ooder.skills.core.discovery;

import net.ooder.skills.api.SkillDiscoverer;
import net.ooder.skills.api.SkillPackage;
import net.ooder.skills.common.enums.DiscoveryMethod;
import net.ooder.skills.exception.AuthenticationException;
import net.ooder.skills.exception.DiscoveryException;
import net.ooder.skills.exception.RepositoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class GitRepositoryDiscovererAdapterTest {

    private GitRepositoryDiscovererAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new GitRepositoryDiscovererAdapter("gitee");
    }

    @Test
    @DisplayName("测试构造函数 - 默认源")
    void testDefaultConstructor() {
        GitRepositoryDiscovererAdapter defaultAdapter = new GitRepositoryDiscovererAdapter();
        assertEquals("github", defaultAdapter.getSource());
    }

    @Test
    @DisplayName("测试构造函数 - 指定源")
    void testConstructorWithSource() {
        assertEquals("gitee", adapter.getSource());
        
        GitRepositoryDiscovererAdapter githubAdapter = new GitRepositoryDiscovererAdapter("github");
        assertEquals("github", githubAdapter.getSource());
    }

    @Test
    @DisplayName("测试 getMethod - Gitee")
    void testGetMethodGitee() {
        adapter.setSource("gitee");
        assertEquals(DiscoveryMethod.GITEE, adapter.getMethod());
    }

    @Test
    @DisplayName("测试 getMethod - GitHub")
    void testGetMethodGithub() {
        adapter.setSource("github");
        assertEquals(DiscoveryMethod.GITHUB, adapter.getMethod());
    }

    @Test
    @DisplayName("测试 getMethod - 默认")
    void testGetMethodDefault() {
        adapter.setSource("gitlab");
        assertEquals(DiscoveryMethod.GIT_REPOSITORY, adapter.getMethod());
    }

    @Test
    @DisplayName("测试 isAvailable - 未配置 token")
    void testIsAvailableWithoutToken() {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        assertFalse(adapter.isAvailable());
    }

    @Test
    @DisplayName("测试 isAvailable - 已配置 token")
    void testIsAvailableWithToken() {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        assertTrue(adapter.isAvailable());
    }

    @Test
    @DisplayName("测试 discover - 未配置 owner")
    void testDiscoverWithoutOwner() {
        adapter.setGiteeToken("test-token");
        adapter.setDefaultRepo("testRepo");
        
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            adapter.discover().join();
        });
        
        assertTrue(exception.getCause() instanceof DiscoveryException);
        assertTrue(exception.getCause().getMessage().contains("owner"));
    }

    @Test
    @DisplayName("测试 discover - 未配置 repo")
    void testDiscoverWithoutRepo() {
        adapter.setGiteeToken("test-token");
        adapter.setDefaultOwner("testOwner");
        
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            adapter.discover().join();
        });
        
        assertTrue(exception.getCause() instanceof DiscoveryException);
        assertTrue(exception.getCause().getMessage().contains("repository"));
    }

    @Test
    @DisplayName("测试 discover - 未配置 token")
    void testDiscoverWithoutToken() {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            adapter.discover().join();
        });
        
        assertTrue(exception.getCause() instanceof DiscoveryException);
        assertTrue(exception.getCause().getMessage().contains("Token"));
    }

    @Test
    @DisplayName("测试 discover(String) - 空 skillId")
    void testDiscoverBySkillIdWithEmptyId() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<SkillPackage> future = adapter.discover("");
        SkillPackage result = future.get();
        assertNull(result);
    }

    @Test
    @DisplayName("测试 discover(String) - null skillId")
    void testDiscoverBySkillIdWithNullId() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<SkillPackage> future = adapter.discover(null);
        SkillPackage result = future.get();
        assertNull(result);
    }

    @Test
    @DisplayName("测试 discoverByScene - 空 sceneId")
    void testDiscoverBySceneWithEmptySceneId() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<List<SkillPackage>> future = adapter.discoverByScene("");
        List<SkillPackage> result = future.get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 search - 空查询")
    void testSearchWithEmptyQuery() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<List<SkillPackage>> future = adapter.search("");
        List<SkillPackage> result = future.get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 searchByCapability - 空 capabilityId")
    void testSearchByCapabilityWithEmptyId() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<List<SkillPackage>> future = adapter.searchByCapability("");
        List<SkillPackage> result = future.get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 discoverByCategory - 空 category")
    void testDiscoverByCategoryWithEmptyCategory() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<List<SkillPackage>> future = adapter.discoverByCategory("");
        List<SkillPackage> result = future.get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 searchByTags - 空 tags")
    void testSearchByTagsWithEmptyTags() throws Exception {
        adapter.setDefaultOwner("testOwner");
        adapter.setDefaultRepo("testRepo");
        adapter.setGiteeToken("test-token");
        
        CompletableFuture<List<SkillPackage>> future = adapter.searchByTags(null);
        List<SkillPackage> result = future.get();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("测试 timeout 设置")
    void testTimeoutSetting() {
        adapter.setTimeout(30000);
        assertEquals(30000, adapter.getTimeout());
    }

    @Test
    @DisplayName("测试默认 timeout")
    void testDefaultTimeout() {
        assertEquals(60000, adapter.getTimeout());
    }

    @Test
    @DisplayName("测试默认分支")
    void testDefaultBranch() {
        assertEquals("main", adapter.getDefaultBranch());
    }

    @Test
    @DisplayName("测试分支设置")
    void testBranchSetting() {
        adapter.setDefaultBranch("master");
        assertEquals("master", adapter.getDefaultBranch());
    }

    @Test
    @DisplayName("测试缓存 TTL 设置")
    void testCacheTtlSetting() {
        adapter.setCacheTtlMs(60000);
        assertEquals(60000, adapter.getCacheTtlMs());
    }

    @Test
    @DisplayName("测试默认缓存 TTL")
    void testDefaultCacheTtl() {
        assertEquals(300000, adapter.getCacheTtlMs());
    }

    @Test
    @DisplayName("测试清除缓存")
    void testClearCache() {
        adapter.clearCache();
    }

    @Test
    @DisplayName("测试 owner 设置")
    void testOwnerSetting() {
        adapter.setDefaultOwner("ooderCN");
        assertEquals("ooderCN", adapter.getDefaultOwner());
    }

    @Test
    @DisplayName("测试 repo 设置")
    void testRepoSetting() {
        adapter.setDefaultRepo("skills");
        assertEquals("skills", adapter.getDefaultRepo());
    }

    @Test
    @DisplayName("测试 token 设置 - Gitee")
    void testGiteeTokenSetting() {
        adapter.setGiteeToken("gitee-test-token");
        assertEquals("gitee-test-token", adapter.getGiteeToken());
    }

    @Test
    @DisplayName("测试 token 设置 - GitHub")
    void testGithubTokenSetting() {
        adapter.setGithubToken("github-test-token");
        assertEquals("github-test-token", adapter.getGithubToken());
    }

    @Test
    @DisplayName("测试 GitRepositoryConfig")
    void testRepositoryConfig() {
        GitRepositoryDiscovererAdapter.GitRepositoryConfig config = 
            new GitRepositoryDiscovererAdapter.GitRepositoryConfig();
        config.setOwner("testOwner");
        config.setRepo("testRepo");
        config.setBranch("develop");
        config.setToken("test-token");
        config.setBaseUrl("https://custom.git.com");
        
        assertEquals("testOwner", config.getOwner());
        assertEquals("testRepo", config.getRepo());
        assertEquals("develop", config.getBranch());
        assertEquals("test-token", config.getToken());
        assertEquals("https://custom.git.com", config.getBaseUrl());
    }

    @Test
    @DisplayName("测试添加仓库配置")
    void testAddRepositoryConfig() {
        GitRepositoryDiscovererAdapter.GitRepositoryConfig config = 
            new GitRepositoryDiscovererAdapter.GitRepositoryConfig();
        config.setOwner("configOwner");
        config.setRepo("configRepo");
        
        adapter.addRepositoryConfig("custom", config);
        
        GitRepositoryDiscovererAdapter.GitRepositoryConfig retrieved = 
            adapter.getRepositoryConfig("custom");
        
        assertNotNull(retrieved);
        assertEquals("configOwner", retrieved.getOwner());
        assertEquals("configRepo", retrieved.getRepo());
    }

    @Test
    @DisplayName("测试 DiscoveryFilter 设置")
    void testFilterSetting() {
        SkillDiscoverer.DiscoveryFilter filter = new SkillDiscoverer.DiscoveryFilter();
        filter.setCategory("test-category");
        filter.setSceneId("test-scene");
        
        adapter.setFilter(filter);
        
        assertNotNull(adapter.getFilter());
        assertEquals("test-category", adapter.getFilter().getCategory());
        assertEquals("test-scene", adapter.getFilter().getSceneId());
    }

    @Test
    @DisplayName("测试源切换")
    void testSourceSwitching() {
        adapter.setSource("github");
        assertEquals("github", adapter.getSource());
        assertEquals(DiscoveryMethod.GITHUB, adapter.getMethod());
        
        adapter.setSource("gitee");
        assertEquals("gitee", adapter.getSource());
        assertEquals(DiscoveryMethod.GITEE, adapter.getMethod());
    }
}

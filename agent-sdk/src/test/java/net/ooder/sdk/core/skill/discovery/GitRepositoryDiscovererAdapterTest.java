package net.ooder.sdk.core.skill.discovery;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import net.ooder.sdk.api.skill.SkillPackage;
import net.ooder.sdk.common.enums.DiscoveryMethod;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GitRepositoryDiscovererAdapterTest {
    
    private GitRepositoryDiscovererAdapter adapter;
    
    @Before
    public void setUp() {
        adapter = new GitRepositoryDiscovererAdapter();
    }
    
    @Test
    public void testDefaultConstructor() {
        assertNotNull(adapter);
        assertTrue(adapter.isAvailable());
        assertEquals(60000, adapter.getTimeout());
    }
    
    @Test
    public void testConstructorWithSource() {
        GitRepositoryDiscovererAdapter githubAdapter = new GitRepositoryDiscovererAdapter("github");
        assertEquals("github", githubAdapter.getSource());
        assertEquals(DiscoveryMethod.GITHUB, githubAdapter.getMethod());
        
        GitRepositoryDiscovererAdapter giteeAdapter = new GitRepositoryDiscovererAdapter("gitee");
        assertEquals("gitee", giteeAdapter.getSource());
        assertEquals(DiscoveryMethod.GITEE, giteeAdapter.getMethod());
    }
    
    @Test
    public void testGetMethod() {
        adapter.setSource("github");
        assertEquals(DiscoveryMethod.GITHUB, adapter.getMethod());
        
        adapter.setSource("gitee");
        assertEquals(DiscoveryMethod.GITEE, adapter.getMethod());
        
        adapter.setSource("other");
        assertEquals(DiscoveryMethod.GIT_REPOSITORY, adapter.getMethod());
    }
    
    @Test
    public void testSetTimeout() {
        adapter.setTimeout(30000);
        assertEquals(30000, adapter.getTimeout());
    }
    
    @Test
    public void testSetDefaultOwner() {
        adapter.setDefaultOwner("ooderCN");
        assertEquals("ooderCN", adapter.getDefaultOwner());
    }
    
    @Test
    public void testSetDefaultRepo() {
        adapter.setDefaultRepo("skills");
        assertEquals("skills", adapter.getDefaultRepo());
    }
    
    @Test
    public void testSetDefaultBranch() {
        adapter.setDefaultBranch("develop");
        assertEquals("develop", adapter.getDefaultBranch());
    }
    
    @Test
    public void testSetGithubToken() {
        adapter.setGithubToken("ghp_test_token");
        assertEquals("ghp_test_token", adapter.getGithubToken());
    }
    
    @Test
    public void testSetGiteeToken() {
        adapter.setGiteeToken("gitee_test_token");
        assertEquals("gitee_test_token", adapter.getGiteeToken());
    }
    
    @Test
    public void testDiscover() throws Exception {
        adapter.setDefaultOwner("ooderCN");
        adapter.setDefaultRepo("skills");
        
        SkillPackage pkg = adapter.discover("skill-test").get(10, TimeUnit.SECONDS);
        
        assertNotNull(pkg);
        assertEquals("skill-test", pkg.getSkillId());
        assertEquals("skill-test", pkg.getName());
        assertEquals("1.0.0", pkg.getVersion());
    }
    
    @Test
    public void testDiscoverAll() throws Exception {
        List<SkillPackage> packages = adapter.discover().get(10, TimeUnit.SECONDS);
        
        assertNotNull(packages);
    }
    
    @Test
    public void testDiscoverByScene() throws Exception {
        List<SkillPackage> packages = adapter.discoverByScene("scene-001").get(10, TimeUnit.SECONDS);
        
        assertNotNull(packages);
    }
    
    @Test
    public void testSearch() throws Exception {
        List<SkillPackage> packages = adapter.search("test-query").get(10, TimeUnit.SECONDS);
        
        assertNotNull(packages);
    }
    
    @Test
    public void testSearchByCapability() throws Exception {
        List<SkillPackage> packages = adapter.searchByCapability("cap-001").get(10, TimeUnit.SECONDS);
        
        assertNotNull(packages);
    }
    
    @Test
    public void testRepositoryConfig() {
        GitRepositoryDiscovererAdapter.GitRepositoryConfig config = new GitRepositoryDiscovererAdapter.GitRepositoryConfig();
        config.setOwner("testOwner");
        config.setRepo("testRepo");
        config.setBranch("main");
        config.setToken("testToken");
        config.setBaseUrl("https://api.github.com");
        
        adapter.addRepositoryConfig("test", config);
        
        GitRepositoryDiscovererAdapter.GitRepositoryConfig retrieved = adapter.getRepositoryConfig("test");
        assertNotNull(retrieved);
        assertEquals("testOwner", retrieved.getOwner());
        assertEquals("testRepo", retrieved.getRepo());
        assertEquals("main", retrieved.getBranch());
        assertEquals("testToken", retrieved.getToken());
        assertEquals("https://api.github.com", retrieved.getBaseUrl());
    }
    
    @Test
    public void testIsAvailable() {
        assertTrue(adapter.isAvailable());
    }
}

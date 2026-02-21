package net.ooder.sdk.common.enums;

import org.junit.Test;
import static org.junit.Assert.*;

public class DiscoveryMethodTest {
    
    @Test
    public void testDiscoveryMethodValues() {
        DiscoveryMethod[] methods = DiscoveryMethod.values();
        assertEquals(10, methods.length);
    }
    
    @Test
    public void testDiscoveryMethodCodes() {
        assertEquals("udp_broadcast", DiscoveryMethod.UDP_BROADCAST.getCode());
        assertEquals("dht_kademlia", DiscoveryMethod.DHT_KADEMLIA.getCode());
        assertEquals("mdns_dns_sd", DiscoveryMethod.MDNS_DNS_SD.getCode());
        assertEquals("skill_center", DiscoveryMethod.SKILL_CENTER.getCode());
        assertEquals("local_fs", DiscoveryMethod.LOCAL_FS.getCode());
        assertEquals("github", DiscoveryMethod.GITHUB.getCode());
        assertEquals("gitee", DiscoveryMethod.GITEE.getCode());
        assertEquals("git_repository", DiscoveryMethod.GIT_REPOSITORY.getCode());
        assertEquals("auto", DiscoveryMethod.AUTO.getCode());
    }
    
    @Test
    public void testDiscoveryMethodDescriptions() {
        assertNotNull(DiscoveryMethod.UDP_BROADCAST.getDescription());
        assertNotNull(DiscoveryMethod.SKILL_CENTER.getDescription());
        assertNotNull(DiscoveryMethod.LOCAL_FS.getDescription());
        assertNotNull(DiscoveryMethod.GITHUB.getDescription());
        assertNotNull(DiscoveryMethod.GITEE.getDescription());
        assertNotNull(DiscoveryMethod.GIT_REPOSITORY.getDescription());
        assertNotNull(DiscoveryMethod.AUTO.getDescription());
    }
    
    @Test
    public void testFromCode() {
        assertEquals(DiscoveryMethod.UDP_BROADCAST, DiscoveryMethod.fromCode("udp_broadcast"));
        assertEquals(DiscoveryMethod.SKILL_CENTER, DiscoveryMethod.fromCode("skill_center"));
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.fromCode("local_fs"));
        assertEquals(DiscoveryMethod.GITHUB, DiscoveryMethod.fromCode("github"));
        assertEquals(DiscoveryMethod.GITEE, DiscoveryMethod.fromCode("gitee"));
        assertEquals(DiscoveryMethod.GIT_REPOSITORY, DiscoveryMethod.fromCode("git_repository"));
        assertEquals(DiscoveryMethod.AUTO, DiscoveryMethod.fromCode("auto"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFromCodeUnknown() {
        DiscoveryMethod.fromCode("unknown_method");
    }
    
    @Test
    public void testInferFromSourceGithub() {
        assertEquals(DiscoveryMethod.GITHUB, DiscoveryMethod.inferFromSource("github"));
        assertEquals(DiscoveryMethod.GITHUB, DiscoveryMethod.inferFromSource("GITHUB"));
        assertEquals(DiscoveryMethod.GITHUB, DiscoveryMethod.inferFromSource("GitHub"));
    }
    
    @Test
    public void testInferFromSourceGitee() {
        assertEquals(DiscoveryMethod.GITEE, DiscoveryMethod.inferFromSource("gitee"));
        assertEquals(DiscoveryMethod.GITEE, DiscoveryMethod.inferFromSource("GITEE"));
        assertEquals(DiscoveryMethod.GITEE, DiscoveryMethod.inferFromSource("Gitee"));
    }
    
    @Test
    public void testInferFromSourceSkillCenter() {
        assertEquals(DiscoveryMethod.SKILL_CENTER, DiscoveryMethod.inferFromSource("skill_center"));
        assertEquals(DiscoveryMethod.SKILL_CENTER, DiscoveryMethod.inferFromSource("skillcenter"));
        assertEquals(DiscoveryMethod.SKILL_CENTER, DiscoveryMethod.inferFromSource("SKILL_CENTER"));
    }
    
    @Test
    public void testInferFromSourceLocal() {
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource("local"));
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource("local_fs"));
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource("LOCAL"));
    }
    
    @Test
    public void testInferFromSourceNull() {
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource(null));
    }
    
    @Test
    public void testInferFromSourceEmpty() {
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource(""));
    }
    
    @Test
    public void testInferFromSourceUnknown() {
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource("unknown"));
        assertEquals(DiscoveryMethod.LOCAL_FS, DiscoveryMethod.inferFromSource("custom"));
    }
}

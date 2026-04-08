package net.ooder.skill.common.jpa;

import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SkillJpaConfigurationSupport 单元测试
 *
 * @author SDK Team
 * @version 1.0
 * @since 3.0.1
 */
class SkillJpaConfigurationSupportTest {

    /**
     * 测试配置类
     */
    static class TestJpaConfiguration extends SkillJpaConfigurationSupport {
        public TestJpaConfiguration() {
            super("test", "net.ooder.skill.test.entity");
        }
    }

    @Test
    void testConstructor() {
        SkillJpaConfigurationSupport config = new TestJpaConfiguration();

        assertEquals("test", config.getSkillName());
        assertEquals("net.ooder.skill.test.entity", config.getEntityPackage());
        assertEquals("test-persistence", config.getPersistenceUnitName());
    }

    @Test
    void testSkillDataSource() {
        SkillJpaConfigurationSupport config = new TestJpaConfiguration();
        DataSource dataSource = config.skillDataSource();

        assertNotNull(dataSource);
    }

    @Test
    void testSkillEntityManagerFactory() {
        SkillJpaConfigurationSupport config = new TestJpaConfiguration();
        DataSource dataSource = config.skillDataSource();
        LocalContainerEntityManagerFactoryBean emf = config.skillEntityManagerFactory(dataSource);

        assertNotNull(emf);
        assertEquals("test-persistence", emf.getPersistenceUnitName());
    }

    @Test
    void testSkillTransactionManager() {
        SkillJpaConfigurationSupport config = new TestJpaConfiguration();
        DataSource dataSource = config.skillDataSource();
        LocalContainerEntityManagerFactoryBean emf = config.skillEntityManagerFactory(dataSource);
        PlatformTransactionManager transactionManager = config.skillTransactionManager(emf);

        assertNotNull(transactionManager);
    }

    @Test
    void testCustomDbPath() {
        System.setProperty("skill.test.db.path", "./custom/test.db");
        try {
            SkillJpaConfigurationSupport config = new TestJpaConfiguration();
            DataSource dataSource = config.skillDataSource();
            assertNotNull(dataSource);
        } finally {
            System.clearProperty("skill.test.db.path");
        }
    }
}

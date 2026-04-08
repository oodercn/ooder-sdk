package net.ooder.skill.common.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Skill JPA 配置支持类
 *
 * <p>为 Skill 提供通用的 JPA 配置支持，包括：</p>
 * <ul>
 *   <li>数据源配置（SQLite）</li>
 *   <li>EntityManagerFactory 配置</li>
 *   <li>事务管理器配置</li>
 * </ul>
 *
 * <p>使用方法：</p>
 * <pre>
 * &#064;Configuration
 * &#064;EnableJpaRepositories(
 *     basePackages = "net.ooder.skill.xxx.repository",
 *     entityManagerFactoryRef = "xxxEntityManagerFactory",
 *     transactionManagerRef = "xxxTransactionManager"
 * )
 * public class XxxJpaConfiguration extends SkillJpaConfigurationSupport {
 *     public XxxJpaConfiguration() {
 *         super("xxx", "net.ooder.skill.xxx.entity");
 *     }
 * }
 * </pre>
 *
 * @author SDK Team
 * @version 1.0
 * @since 3.0.1
 */
public abstract class SkillJpaConfigurationSupport {

    protected final String skillName;
    protected final String entityPackage;
    protected final String persistenceUnitName;

    /**
     * 构造函数
     *
     * @param skillName     Skill 名称（用于生成 Bean 名称和数据库文件名）
     * @param entityPackage Entity 类所在的包路径
     */
    public SkillJpaConfigurationSupport(String skillName, String entityPackage) {
        this.skillName = skillName;
        this.entityPackage = entityPackage;
        this.persistenceUnitName = skillName + "-persistence";
    }

    /**
     * 创建数据源
     *
     * <p>默认使用 SQLite 数据库，数据库文件路径可通过系统属性配置：</p>
     * <pre>skill.{skillName}.db.path=./data/{skillName}.db</pre>
     *
     * <p>子类可以覆盖此方法以使用其他数据源。</p>
     *
     * @return 数据源
     */
    @Bean
    @ConditionalOnMissingBean(name = "skillDataSource")
    public DataSource skillDataSource() {
        String dbPath = System.getProperty("skill." + skillName + ".db.path",
                "./data/" + skillName + ".db");

        return DataSourceBuilder.create()
                .url("jdbc:sqlite:" + dbPath)
                .driverClassName("org.sqlite.JDBC")
                .build();
    }

    /**
     * 创建 EntityManagerFactory
     *
     * @param dataSource 数据源
     * @return EntityManagerFactory
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean skillEntityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(entityPackage);
        emf.setPersistenceUnitName(persistenceUnitName);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        emf.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        jpaProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        emf.setJpaProperties(jpaProperties);

        return emf;
    }

    /**
     * 创建事务管理器
     *
     * @param entityManagerFactory EntityManagerFactory
     * @return 事务管理器
     */
    @Bean
    public PlatformTransactionManager skillTransactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    /**
     * 获取 Skill 名称
     *
     * @return Skill 名称
     */
    public String getSkillName() {
        return skillName;
    }

    /**
     * 获取 Entity 包路径
     *
     * @return Entity 包路径
     */
    public String getEntityPackage() {
        return entityPackage;
    }

    /**
     * 获取持久化单元名称
     *
     * @return 持久化单元名称
     */
    public String getPersistenceUnitName() {
        return persistenceUnitName;
    }
}

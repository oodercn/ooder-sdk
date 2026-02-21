package net.ooder.scene.drivers.org;

import net.ooder.scene.core.driver.Driver;
import net.ooder.scene.core.driver.DriverContext;
import net.ooder.scene.core.driver.HealthStatus;
import net.ooder.scene.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgDriver implements Driver {
    
    private static final Logger logger = LoggerFactory.getLogger(OrgDriver.class);
    
    private static final String CATEGORY = "ORG";
    private static final String VERSION = "1.0.0";
    
    private DriverContext context;
    private OrgSkillImpl skillImpl;
    private OrgCapabilities capabilities;
    private OrgFallback fallback;
    private InterfaceDefinition interfaceDefinition;
    private HealthStatus healthStatus = HealthStatus.UNKNOWN;
    
    @Override
    public String getCategory() {
        return CATEGORY;
    }
    
    @Override
    public String getVersion() {
        return VERSION;
    }
    
    @Override
    public void initialize(DriverContext context) {
        logger.info("Initializing ORG Driver v{}", VERSION);
        
        this.context = context;
        this.interfaceDefinition = context.getInterfaceDefinition();
        
        if (interfaceDefinition != null) {
            this.capabilities = parseCapabilities(interfaceDefinition);
        } else {
            this.capabilities = OrgCapabilities.forJson();
        }
        
        this.fallback = new OrgFallback();
        this.fallback.initialize();
        
        this.skillImpl = new OrgSkillImpl();
        
        OrgManager remoteManager = context.getRemoteManager(OrgManager.class);
        if (remoteManager != null) {
            this.skillImpl.initialize(capabilities, remoteManager);
            logger.info("ORG Driver initialized with remote manager");
        } else {
            this.skillImpl.initialize(capabilities);
            logger.info("ORG Driver initialized with fallback only");
        }
        
        this.healthStatus = HealthStatus.UP;
        logger.info("ORG Driver initialization completed");
    }
    
    @Override
    public void shutdown() {
        logger.info("Shutting down ORG Driver");
        
        if (fallback != null) {
            fallback.shutdown();
        }
        
        this.healthStatus = HealthStatus.DOWN;
        logger.info("ORG Driver shutdown completed");
    }
    
    @Override
    public OrgSkill getSkill() {
        return skillImpl;
    }
    
    @Override
    public OrgCapabilities getCapabilities() {
        return capabilities;
    }
    
    @Override
    public Object getFallback() {
        return fallback;
    }
    
    @Override
    public boolean hasFallback() {
        return fallback != null;
    }
    
    @Override
    public InterfaceDefinition getInterfaceDefinition() {
        return interfaceDefinition;
    }
    
    @Override
    public HealthStatus getHealthStatus() {
        return healthStatus;
    }
    
    private OrgCapabilities parseCapabilities(InterfaceDefinition interfaceDef) {
        OrgCapabilities caps = new OrgCapabilities();
        
        if (interfaceDef.hasCapability("user-auth")) {
            caps.setSupportUserAuth(true);
        }
        if (interfaceDef.hasCapability("user-manage")) {
            caps.setSupportPersonQuery(true);
            caps.setSupportOrgAdmin(true);
        }
        if (interfaceDef.hasCapability("org-manage")) {
            caps.setSupportOrgQuery(true);
        }
        if (interfaceDef.hasCapability("role-manage")) {
            caps.setSupportPersonRole(true);
        }
        
        return caps;
    }
}

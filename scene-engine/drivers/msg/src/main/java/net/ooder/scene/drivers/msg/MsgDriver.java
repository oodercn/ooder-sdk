package net.ooder.scene.drivers.msg;

import net.ooder.scene.core.driver.Driver;
import net.ooder.scene.core.driver.DriverContext;
import net.ooder.scene.core.driver.HealthStatus;
import net.ooder.scene.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgDriver implements Driver {
    
    private static final Logger logger = LoggerFactory.getLogger(MsgDriver.class);
    
    private static final String CATEGORY = "MSG";
    private static final String VERSION = "1.0.0";
    
    private DriverContext context;
    private MsgSkillImpl skillImpl;
    private MsgCapabilities capabilities;
    private MsgFallback fallback;
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
        logger.info("Initializing MSG Driver v{}", VERSION);
        
        this.context = context;
        this.interfaceDefinition = context.getInterfaceDefinition();
        
        if (interfaceDefinition != null) {
            this.capabilities = parseCapabilities(interfaceDefinition);
        } else {
            this.capabilities = MsgCapabilities.forLocal();
        }
        
        this.fallback = new MsgFallback();
        this.fallback.initialize();
        
        this.skillImpl = new MsgSkillImpl();
        
        MsgManager remoteManager = context.getRemoteManager(MsgManager.class);
        if (remoteManager != null) {
            this.skillImpl.initialize(capabilities, remoteManager);
            logger.info("MSG Driver initialized with remote manager");
        } else {
            this.skillImpl.initialize(capabilities);
            logger.info("MSG Driver initialized with fallback only");
        }
        
        this.healthStatus = HealthStatus.UP;
        logger.info("MSG Driver initialization completed");
    }
    
    @Override
    public void shutdown() {
        logger.info("Shutting down MSG Driver");
        
        if (fallback != null) {
            fallback.shutdown();
        }
        
        this.healthStatus = HealthStatus.DOWN;
        logger.info("MSG Driver shutdown completed");
    }
    
    @Override
    public MsgSkill getSkill() {
        return skillImpl;
    }
    
    @Override
    public MsgCapabilities getCapabilities() {
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
    
    private MsgCapabilities parseCapabilities(InterfaceDefinition interfaceDef) {
        MsgCapabilities caps = new MsgCapabilities();
        
        if (interfaceDef.hasCapability("msg-push")) {
            caps.setSupportPush(true);
        }
        if (interfaceDef.hasCapability("msg-p2p")) {
            caps.setSupportP2P(true);
        }
        if (interfaceDef.hasCapability("msg-topic")) {
            caps.setSupportTopic(true);
        }
        if (interfaceDef.hasCapability("msg-broadcast")) {
            caps.setSupportBroadcast(true);
        }
        if (interfaceDef.hasCapability("msg-offline")) {
            caps.setSupportOffline(true);
        }
        if (interfaceDef.hasCapability("msg-attachment")) {
            caps.setSupportAttachment(true);
        }
        
        return caps;
    }
}

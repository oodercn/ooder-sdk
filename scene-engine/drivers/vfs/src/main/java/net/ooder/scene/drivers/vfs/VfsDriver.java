package net.ooder.scene.drivers.vfs;

import net.ooder.scene.core.driver.Driver;
import net.ooder.scene.core.driver.DriverContext;
import net.ooder.scene.core.driver.HealthStatus;
import net.ooder.scene.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VfsDriver implements Driver {
    
    private static final Logger logger = LoggerFactory.getLogger(VfsDriver.class);
    
    private static final String CATEGORY = "VFS";
    private static final String VERSION = "1.0.0";
    
    private DriverContext context;
    private VfsSkillImpl skillImpl;
    private VfsCapabilities capabilities;
    private VfsFallback fallback;
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
        logger.info("Initializing VFS Driver v{}", VERSION);
        
        this.context = context;
        this.interfaceDefinition = context.getInterfaceDefinition();
        
        if (interfaceDefinition != null) {
            this.capabilities = parseCapabilities(interfaceDefinition);
        } else {
            this.capabilities = VfsCapabilities.forLocal();
        }
        
        this.fallback = new VfsFallback();
        this.fallback.initialize();
        
        this.skillImpl = new VfsSkillImpl();
        
        VfsManager remoteManager = context.getRemoteManager(VfsManager.class);
        if (remoteManager != null) {
            this.skillImpl.initialize(capabilities, remoteManager);
            logger.info("VFS Driver initialized with remote manager");
        } else {
            this.skillImpl.initialize(capabilities);
            logger.info("VFS Driver initialized with fallback only");
        }
        
        this.healthStatus = HealthStatus.UP;
        logger.info("VFS Driver initialization completed");
    }
    
    @Override
    public void shutdown() {
        logger.info("Shutting down VFS Driver");
        
        if (fallback != null) {
            fallback.shutdown();
        }
        
        this.healthStatus = HealthStatus.DOWN;
        logger.info("VFS Driver shutdown completed");
    }
    
    @Override
    public VfsSkill getSkill() {
        return skillImpl;
    }
    
    @Override
    public VfsCapabilities getCapabilities() {
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
    
    private VfsCapabilities parseCapabilities(InterfaceDefinition interfaceDef) {
        VfsCapabilities caps = new VfsCapabilities();
        
        if (interfaceDef.hasCapability("file-read")) {
            caps.setSupportFileRead(true);
        }
        if (interfaceDef.hasCapability("file-write")) {
            caps.setSupportFileWrite(true);
        }
        if (interfaceDef.hasCapability("file-version")) {
            caps.setSupportFileVersion(true);
        }
        if (interfaceDef.hasCapability("file-share")) {
            caps.setSupportFileShare(true);
        }
        if (interfaceDef.hasCapability("file-compress")) {
            caps.setSupportFileCompress(true);
        }
        if (interfaceDef.hasCapability("stream-upload")) {
            caps.setSupportStreamUpload(true);
        }
        if (interfaceDef.hasCapability("acl-manage")) {
            caps.setSupportAclManage(true);
        }
        
        return caps;
    }
}

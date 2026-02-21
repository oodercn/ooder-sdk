package net.ooder.scene.drivers.mqtt;

import net.ooder.scene.core.driver.Driver;
import net.ooder.scene.core.driver.DriverContext;
import net.ooder.scene.core.driver.HealthStatus;
import net.ooder.scene.core.InterfaceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttDriver implements Driver {
    
    private static final Logger logger = LoggerFactory.getLogger(MqttDriver.class);
    
    private static final String CATEGORY = "MQTT";
    private static final String VERSION = "1.0.0";
    
    private DriverContext context;
    private MqttSkillImpl skillImpl;
    private MqttCapabilities capabilities;
    private MqttFallback fallback;
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
        logger.info("Initializing MQTT Driver v{}", VERSION);
        
        this.context = context;
        this.interfaceDefinition = context.getInterfaceDefinition();
        
        if (interfaceDefinition != null) {
            this.capabilities = parseCapabilities(interfaceDefinition);
        } else {
            this.capabilities = MqttCapabilities.forLightweight();
        }
        
        this.fallback = new MqttFallback();
        this.fallback.initialize();
        
        this.skillImpl = new MqttSkillImpl();
        this.skillImpl.initialize(capabilities);
        
        this.healthStatus = HealthStatus.UP;
        logger.info("MQTT Driver initialization completed");
    }
    
    @Override
    public void shutdown() {
        logger.info("Shutting down MQTT Driver");
        
        if (skillImpl != null) {
            skillImpl.stop();
        }
        if (fallback != null) {
            fallback.shutdown();
        }
        
        this.healthStatus = HealthStatus.DOWN;
        logger.info("MQTT Driver shutdown completed");
    }
    
    @Override
    public MqttSkill getSkill() {
        return skillImpl;
    }
    
    @Override
    public MqttCapabilities getCapabilities() {
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
    
    private MqttCapabilities parseCapabilities(InterfaceDefinition interfaceDef) {
        MqttCapabilities caps = new MqttCapabilities();
        
        if (interfaceDef.hasCapability("mqtt-broker")) {
            caps.setSupportBroker(true);
        }
        if (interfaceDef.hasCapability("mqtt-publish")) {
            caps.setSupportPublish(true);
        }
        if (interfaceDef.hasCapability("mqtt-subscribe")) {
            caps.setSupportSubscribe(true);
        }
        if (interfaceDef.hasCapability("mqtt-p2p")) {
            caps.setSupportP2P(true);
        }
        if (interfaceDef.hasCapability("mqtt-broadcast")) {
            caps.setSupportBroadcast(true);
        }
        if (interfaceDef.hasCapability("mqtt-qos2")) {
            caps.setSupportQos2(true);
        }
        if (interfaceDef.hasCapability("mqtt-tls")) {
            caps.setSupportTls(true);
        }
        if (interfaceDef.hasCapability("mqtt-cluster")) {
            caps.setSupportCluster(true);
        }
        
        return caps;
    }
}

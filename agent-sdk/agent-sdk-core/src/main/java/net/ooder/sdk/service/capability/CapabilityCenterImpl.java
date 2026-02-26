package net.ooder.sdk.service.capability;

import net.ooder.sdk.service.capability.spec.CapabilitySpecService;
import net.ooder.sdk.service.capability.dist.CapabilityDistService;
import net.ooder.sdk.service.capability.mgt.CapabilityMgtService;
import net.ooder.sdk.service.capability.mon.CapabilityMonService;
import net.ooder.sdk.service.capability.coop.CapabilityCoopService;
import net.ooder.sdk.core.capability.CapDefinitionRegistry;
import net.ooder.sdk.core.capability.impl.CapDefinitionRegistryImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapabilityCenterImpl implements CapabilityCenter {
    
    private static final Logger log = LoggerFactory.getLogger(CapabilityCenterImpl.class);
    
    private final CapabilitySpecService specService;
    private final CapabilityDistService distService;
    private final CapabilityMgtService mgtService;
    private final CapabilityMonService monService;
    private final CapabilityCoopService coopService;
    private final CapDefinitionRegistry capRegistry;
    
    private volatile boolean initialized = false;
    
    public CapabilityCenterImpl() {
        this.specService = new CapabilitySpecServiceImpl();
        this.distService = new CapabilityDistServiceImpl();
        this.mgtService = new CapabilityMgtServiceImpl();
        this.monService = new CapabilityMonServiceImpl();
        this.coopService = new CapabilityCoopServiceImpl();
        this.capRegistry = new CapDefinitionRegistryImpl();
        log.info("CapabilityCenterImpl created");
    }
    
    public CapDefinitionRegistry getCapRegistry() {
        return capRegistry;
    }
    
    @Override
    public CapabilitySpecService getSpecService() {
        return specService;
    }
    
    @Override
    public CapabilityDistService getDistService() {
        return distService;
    }
    
    @Override
    public CapabilityMgtService getMgtService() {
        return mgtService;
    }
    
    @Override
    public CapabilityMonService getMonService() {
        return monService;
    }
    
    @Override
    public CapabilityCoopService getCoopService() {
        return coopService;
    }
    
    @Override
    public void initialize() {
        log.info("Initializing CapabilityCenter");
        capRegistry.loadFromClasspath("cap/cap-index.yaml");
        initialized = true;
        log.info("CapabilityCenter initialized with {} CAPs", capRegistry.size());
    }
    
    @Override
    public void shutdown() {
        log.info("Shutting down CapabilityCenter");
        initialized = false;
        log.info("CapabilityCenter shutdown complete");
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }
}

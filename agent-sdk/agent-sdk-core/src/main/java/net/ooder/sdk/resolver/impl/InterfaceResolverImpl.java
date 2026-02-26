package net.ooder.sdk.resolver.impl;

import net.ooder.sdk.api.skill.InterfaceDefinition;
import net.ooder.sdk.api.skill.InterfaceDependency;
import net.ooder.sdk.core.driver.loader.InterfaceDriverLoader;
import net.ooder.sdk.core.registry.InterfaceRegistry;
import net.ooder.sdk.resolver.InterfaceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class InterfaceResolverImpl implements InterfaceResolver {
    
    private static final Logger log = LoggerFactory.getLogger(InterfaceResolverImpl.class);
    
    private final InterfaceRegistry interfaceRegistry;
    private final InterfaceDriverLoader driverLoader;
    private volatile FallbackStrategy fallbackStrategy = FallbackStrategy.AUTO;
    
    public InterfaceResolverImpl(InterfaceRegistry interfaceRegistry, InterfaceDriverLoader driverLoader) {
        this.interfaceRegistry = interfaceRegistry;
        this.driverLoader = driverLoader;
    }
    
    @Override
    public ResolvedInterface resolve(String interfaceId) {
        if (interfaceId == null) {
            return createUnresolved(interfaceId, "Interface ID cannot be null");
        }
        
        Optional<InterfaceDefinition> defOpt = interfaceRegistry.getInterface(interfaceId);
        if (!defOpt.isPresent()) {
            return createUnresolved(interfaceId, "Interface not found: " + interfaceId);
        }
        
        InterfaceDefinition definition = defOpt.get();
        String preferredSkillId = interfaceRegistry.getPreferredImplementation(interfaceId);
        
        if (preferredSkillId != null) {
            ResolvedInterface resolved = tryResolveWithSkill(definition, preferredSkillId);
            if (resolved.isResolved()) {
                return resolved;
            }
        }
        
        List<String> implementations = interfaceRegistry.getImplementations(interfaceId);
        for (String skillId : implementations) {
            ResolvedInterface resolved = tryResolveWithSkill(definition, skillId);
            if (resolved.isResolved()) {
                return resolved;
            }
        }
        
        return resolveWithFallbackInternal(definition);
    }
    
    @Override
    public ResolvedInterface resolve(String interfaceId, String preferredSkillId) {
        if (interfaceId == null) {
            return createUnresolved(interfaceId, "Interface ID cannot be null");
        }
        
        Optional<InterfaceDefinition> defOpt = interfaceRegistry.getInterface(interfaceId);
        if (!defOpt.isPresent()) {
            return createUnresolved(interfaceId, "Interface not found: " + interfaceId);
        }
        
        InterfaceDefinition definition = defOpt.get();
        
        if (preferredSkillId != null) {
            ResolvedInterface resolved = tryResolveWithSkill(definition, preferredSkillId);
            if (resolved.isResolved()) {
                return resolved;
            }
        }
        
        return resolve(interfaceId);
    }
    
    @Override
    public ResolvedInterface resolveWithFallback(String interfaceId) {
        if (interfaceId == null) {
            return createUnresolved(interfaceId, "Interface ID cannot be null");
        }
        
        Optional<InterfaceDefinition> defOpt = interfaceRegistry.getInterface(interfaceId);
        if (!defOpt.isPresent()) {
            return createUnresolved(interfaceId, "Interface not found: " + interfaceId);
        }
        
        InterfaceDefinition definition = defOpt.get();
        return resolveWithFallbackInternal(definition);
    }
    
    @Override
    public List<ResolvedInterface> resolveAll(List<InterfaceDependency> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<ResolvedInterface> results = new ArrayList<>();
        
        for (InterfaceDependency dep : dependencies) {
            ResolvedInterface resolved = resolve(dep.getInterfaceId());
            results.add(resolved);
            
            if (!resolved.isResolved() && dep.isRequired()) {
                log.error("Failed to resolve required interface: {}", dep.getInterfaceId());
            }
        }
        
        return results;
    }
    
    @Override
    public boolean canResolve(String interfaceId) {
        if (interfaceId == null) {
            return false;
        }
        
        if (!interfaceRegistry.hasInterface(interfaceId)) {
            return false;
        }
        
        return driverLoader.hasDriver(interfaceId);
    }
    
    @Override
    public boolean canResolveWithFallback(String interfaceId) {
        if (!canResolve(interfaceId)) {
            Optional<InterfaceDefinition> defOpt = interfaceRegistry.getInterface(interfaceId);
            if (defOpt.isPresent()) {
                InterfaceDefinition.DriverConfig driverConfig = defOpt.get().getDriver();
                if (driverConfig != null && driverConfig.getFallback() != null) {
                    return driverLoader.hasDriver(interfaceId, driverConfig.getFallback());
                }
            }
            return false;
        }
        return true;
    }
    
    @Override
    public ValidationResult validate(String interfaceId) {
        if (interfaceId == null) {
            return ValidationResult.failure("Interface ID cannot be null");
        }
        
        ValidationResult result = new ValidationResult(true);
        
        Optional<InterfaceDefinition> defOpt = interfaceRegistry.getInterface(interfaceId);
        if (!defOpt.isPresent()) {
            return ValidationResult.failure("Interface not found: " + interfaceId);
        }
        
        InterfaceDefinition definition = defOpt.get();
        result.addDetail("interfaceId", interfaceId);
        result.addDetail("version", definition.getVersion());
        
        List<String> implementations = interfaceRegistry.getImplementations(interfaceId);
        result.addDetail("implementationCount", String.valueOf(implementations.size()));
        
        if (implementations.isEmpty()) {
            result.addWarning("No implementations registered");
            
            InterfaceDefinition.DriverConfig driverConfig = definition.getDriver();
            if (driverConfig == null || driverConfig.getFallback() == null) {
                result.addError("No implementations and no fallback defined");
                return result;
            }
            
            result.addDetail("fallback", driverConfig.getFallback());
        }
        
        if (!driverLoader.hasDriver(interfaceId)) {
            result.addWarning("No driver loaded");
        }
        
        return result;
    }
    
    @Override
    public ValidationResult validateAll(List<InterfaceDependency> dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            return ValidationResult.success();
        }
        
        ValidationResult result = new ValidationResult(true);
        
        for (InterfaceDependency dep : dependencies) {
            ValidationResult singleResult = validate(dep.getInterfaceId());
            
            if (!singleResult.isValid() && dep.isRequired()) {
                result.addError("Required interface validation failed: " + dep.getInterfaceId());
            }
            
            for (String warning : singleResult.getWarnings()) {
                result.addWarning(dep.getInterfaceId() + ": " + warning);
            }
        }
        
        return result;
    }
    
    @Override
    public void setFallbackStrategy(FallbackStrategy strategy) {
        this.fallbackStrategy = strategy != null ? strategy : FallbackStrategy.AUTO;
        log.info("Fallback strategy set to: {}", this.fallbackStrategy);
    }
    
    @Override
    public FallbackStrategy getFallbackStrategy() {
        return fallbackStrategy;
    }
    
    private ResolvedInterface tryResolveWithSkill(InterfaceDefinition definition, String skillId) {
        String interfaceId = definition.getInterfaceId();
        
        Optional<?> driverOpt = driverLoader.load(interfaceId, skillId, Object.class);
        if (driverOpt.isPresent()) {
            ResolvedInterface resolved = new ResolvedInterface(
                interfaceId, definition, skillId, driverOpt.get(), false);
            
            List<String> allImpls = interfaceRegistry.getImplementations(interfaceId);
            for (String impl : allImpls) {
                if (!impl.equals(skillId)) {
                    resolved.addAlternativeSkill(impl);
                }
            }
            
            log.debug("Interface resolved: {} -> {}", interfaceId, skillId);
            return resolved;
        }
        
        return createUnresolved(interfaceId, "Driver not found for skill: " + skillId);
    }
    
    private ResolvedInterface resolveWithFallbackInternal(InterfaceDefinition definition) {
        String interfaceId = definition.getInterfaceId();
        InterfaceDefinition.DriverConfig driverConfig = definition.getDriver();
        
        if (driverConfig != null && driverConfig.getFallback() != null) {
            String fallbackSkillId = driverConfig.getFallback();
            Optional<?> driverOpt = driverLoader.loadFallback(interfaceId, Object.class);
            
            if (driverOpt.isPresent()) {
                log.info("Using fallback driver for interface: {} -> {}", interfaceId, fallbackSkillId);
                return new ResolvedInterface(
                    interfaceId, definition, fallbackSkillId, driverOpt.get(), true);
            }
        }
        
        return createUnresolved(interfaceId, "No driver or fallback available");
    }
    
    private ResolvedInterface createUnresolved(String interfaceId, String reason) {
        log.warn("Interface unresolved: {} - {}", interfaceId, reason);
        ResolvedInterface unresolved = new ResolvedInterface(interfaceId, null, null, null, false);
        return unresolved;
    }
}

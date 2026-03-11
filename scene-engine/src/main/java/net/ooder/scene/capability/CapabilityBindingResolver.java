package net.ooder.scene.capability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CapabilityBindingResolver {

    private static final Logger log = LoggerFactory.getLogger(CapabilityBindingResolver.class);

    private final CapabilityRegistry registry;
    private final CapabilityBinding userBinding;
    private final CapabilityBinding sceneBinding;
    private final CapabilityBinding systemBinding;

    public CapabilityBindingResolver(CapabilityRegistry registry) {
        this.registry = registry;
        this.userBinding = new CapabilityBindingImpl();
        this.sceneBinding = new CapabilityBindingImpl();
        this.systemBinding = new CapabilityBindingImpl();
    }

    public String resolve(int address) {
        return resolve(address, null);
    }

    public String resolve(int address, String sceneId) {
        log.debug("Resolving capability binding for address: 0x{}", Integer.toHexString(address));

        if (userBinding.hasBinding(address)) {
            String provider = userBinding.getBinding(address);
            log.debug("Found user binding: {}", provider);
            return provider;
        }

        if (sceneId != null && sceneBinding.hasBinding(address)) {
            String provider = sceneBinding.getBinding(address);
            log.debug("Found scene binding: {}", provider);
            return provider;
        }

        if (systemBinding.hasBinding(address)) {
            String provider = systemBinding.getBinding(address);
            log.debug("Found system binding: {}", provider);
            return provider;
        }

        String fallback = registry.getFallbackProvider(address);
        if (fallback != null) {
            log.debug("Using fallback provider: {}", fallback);
            return fallback;
        }

        CapabilitySegment segment = CapabilitySegment.fromAddress(address);
        if (segment != null && segment.getFallbackProvider() != null) {
            log.debug("Using enum fallback: {}", segment.getFallbackProvider());
            return segment.getFallbackProvider();
        }

        log.warn("No provider found for address: 0x{}", Integer.toHexString(address));
        return null;
    }

    public ProviderInfo resolveProvider(int address) {
        String providerId = resolve(address);
        if (providerId == null) {
            return null;
        }

        java.util.List<ProviderInfo> providers = registry.getProviders(address);
        return providers.stream()
            .filter(p -> p.getSkillId().equals(providerId))
            .findFirst()
            .orElse(null);
    }

    public void bindUser(int address, String provider) {
        userBinding.bind(address, provider);
        log.info("User bound capability 0x{} to {}", Integer.toHexString(address), provider);
    }

    public void unbindUser(int address) {
        userBinding.unbind(address);
        log.info("User unbound capability 0x{}", Integer.toHexString(address));
    }

    public void bindScene(int address, String provider) {
        sceneBinding.bind(address, provider);
        log.info("Scene bound capability 0x{} to {}", Integer.toHexString(address), provider);
    }

    public void unbindScene(int address) {
        sceneBinding.unbind(address);
    }

    public void bindSystem(int address, String provider) {
        systemBinding.bind(address, provider);
        log.info("System bound capability 0x{} to {}", Integer.toHexString(address), provider);
    }

    public void unbindSystem(int address) {
        systemBinding.unbind(address);
    }

    public void loadUserBindings(Map<Integer, String> bindings) {
        if (bindings != null) {
            ((CapabilityBindingImpl) userBinding).loadFromMap(bindings);
            log.info("Loaded {} user bindings", bindings.size());
        }
    }

    public void loadSceneBindings(Map<Integer, String> bindings) {
        if (bindings != null) {
            ((CapabilityBindingImpl) sceneBinding).loadFromMap(bindings);
            log.info("Loaded {} scene bindings", bindings.size());
        }
    }

    public void loadSystemBindings(Map<Integer, String> bindings) {
        if (bindings != null) {
            ((CapabilityBindingImpl) systemBinding).loadFromMap(bindings);
            log.info("Loaded {} system bindings", bindings.size());
        }
    }

    public Map<Integer, String> getUserBindings() {
        return ((CapabilityBindingImpl) userBinding).getAllBindings();
    }

    public Map<Integer, String> getSceneBindings() {
        return ((CapabilityBindingImpl) sceneBinding).getAllBindings();
    }

    public Map<Integer, String> getSystemBindings() {
        return ((CapabilityBindingImpl) systemBinding).getAllBindings();
    }

    public CapabilityRegistry getRegistry() {
        return registry;
    }
}

package net.ooder.scene.capability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapabilityBindingImpl implements CapabilityBinding {

    private final Map<Integer, String> bindings = new ConcurrentHashMap<>();

    @Override
    public void bind(int address, String provider) {
        if (provider != null && !provider.isEmpty()) {
            bindings.put(address, provider);
        }
    }

    @Override
    public void unbind(int address) {
        bindings.remove(address);
    }

    @Override
    public String getBinding(int address) {
        return bindings.get(address);
    }

    @Override
    public boolean hasBinding(int address) {
        return bindings.containsKey(address);
    }

    public void clear() {
        bindings.clear();
    }

    public Map<Integer, String> getAllBindings() {
        return new ConcurrentHashMap<>(bindings);
    }

    public void loadFromMap(Map<Integer, String> map) {
        if (map != null) {
            bindings.putAll(map);
        }
    }
}

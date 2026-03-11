package net.ooder.scene.capability;

public interface CapabilityBinding {

    void bind(int address, String provider);

    void unbind(int address);

    String getBinding(int address);

    boolean hasBinding(int address);
}

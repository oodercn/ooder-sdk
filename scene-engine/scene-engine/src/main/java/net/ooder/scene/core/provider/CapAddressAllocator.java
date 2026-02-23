package net.ooder.scene.core.provider;

import java.util.BitSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CapAddressAllocator {
    private BitSet systemAddresses; // 00-3F
    private BitSet commonAddresses; // 40-9F
    private BitSet extensionAddresses; // A0-FF
    private Map<String, String> addressMap; // capId -> address
    private Map<String, String> reverseMap; // address -> capId

    public CapAddressAllocator() {
        this.systemAddresses = new BitSet(64); // 00-3F
        this.commonAddresses = new BitSet(96); // 40-9F
        this.extensionAddresses = new BitSet(64); // A0-FF
        this.addressMap = new ConcurrentHashMap<>();
        this.reverseMap = new ConcurrentHashMap<>();

        // 预分配系统地址
        preallocateSystemAddresses();
    }

    private void preallocateSystemAddresses() {
        // 预分配一些系统能力地址
        allocateSystemAddress("01", "MessagePush");
        allocateSystemAddress("02", "CommandService");
        allocateSystemAddress("10", "Audit");
        allocateSystemAddress("11", "AccessControl");
        allocateSystemAddress("20", "RemoteTerminal");
    }

    private void allocateSystemAddress(String address, String capabilityName) {
        int addr = Integer.parseInt(address, 16);
        systemAddresses.set(addr);
        String capId = address;
        addressMap.put(capId, address);
        reverseMap.put(address, capId);
    }

    public String allocateAddress(String capId, String category) {
        // 检查是否已分配
        if (addressMap.containsKey(capId)) {
            return addressMap.get(capId);
        }

        String address = null;

        switch (category.toUpperCase()) {
            case "SYSTEM":
                address = allocateFromRange(systemAddresses, 0x00, 0x3F);
                break;
            case "COMMON":
                address = allocateFromRange(commonAddresses, 0x40, 0x9F);
                break;
            case "EXTENSION":
                address = allocateFromRange(extensionAddresses, 0xA0, 0xFF);
                break;
            default:
                address = allocateFromRange(commonAddresses, 0x40, 0x9F);
                break;
        }

        if (address != null) {
            addressMap.put(capId, address);
            reverseMap.put(address, capId);
        }

        return address;
    }

    private String allocateFromRange(BitSet bitSet, int start, int end) {
        for (int i = start; i <= end; i++) {
            if (!bitSet.get(i - start)) {
                bitSet.set(i - start);
                return String.format("%02X", i);
            }
        }
        return null; // 没有可用地址
    }

    public void releaseAddress(String capId) {
        String address = addressMap.remove(capId);
        if (address != null) {
            reverseMap.remove(address);
            int addr = Integer.parseInt(address, 16);
            if (addr >= 0x00 && addr <= 0x3F) {
                systemAddresses.clear(addr);
            } else if (addr >= 0x40 && addr <= 0x9F) {
                commonAddresses.clear(addr - 0x40);
            } else if (addr >= 0xA0 && addr <= 0xFF) {
                extensionAddresses.clear(addr - 0xA0);
            }
        }
    }

    public String getAddress(String capId) {
        return addressMap.get(capId);
    }

    public String getCapabilityId(String address) {
        return reverseMap.get(address);
    }

    public boolean isAddressAllocated(String address) {
        return reverseMap.containsKey(address);
    }

    public int getAvailableAddresses(String category) {
        switch (category.toUpperCase()) {
            case "SYSTEM":
                return 64 - systemAddresses.cardinality();
            case "COMMON":
                return 96 - commonAddresses.cardinality();
            case "EXTENSION":
                return 64 - extensionAddresses.cardinality();
            default:
                return 0;
        }
    }
}

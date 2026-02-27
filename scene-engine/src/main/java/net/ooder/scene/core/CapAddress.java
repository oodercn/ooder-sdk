package net.ooder.scene.core;

public class CapAddress {
    private String address;
    private String category;

    public CapAddress(String address) {
        this.address = address;
        this.category = determineCategory(address);
    }

    public String getAddress() {
        return address;
    }

    public String getCategory() {
        return category;
    }

    private String determineCategory(String address) {
        try {
            int addr = Integer.parseInt(address, 16);
            if (addr >= 0x00 && addr <= 0x3F) {
                return "SYSTEM";
            } else if (addr >= 0x40 && addr <= 0x9F) {
                return "COMMON";
            } else if (addr >= 0xA0 && addr <= 0xFF) {
                return "EXTENSION";
            }
        } catch (NumberFormatException e) {
            // 忽略格式错误
        }
        return "UNKNOWN";
    }

    public boolean isValid() {
        try {
            int addr = Integer.parseInt(address, 16);
            return addr >= 0x00 && addr <= 0xFF;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return address + " (" + category + ")";
    }
}

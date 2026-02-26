package net.ooder.sdk.api.capability;

/**
 * CAP 能力地址
 *
 * <p>遵循 v0.8.0 架构,使用 00-FF 地址空间标识能力</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class CapAddress {

    private final int address;

    private final String domainId;

    private CapAddress(int address, String domainId) {
        if (address < 0 || address > 255) {
            throw new IllegalArgumentException("Address must be between 00 and FF (0-255)");
        }
        this.address = address;
        this.domainId = domainId != null ? domainId : "default";
    }

    public static CapAddress of(int address) {
        return new CapAddress(address, "default");
    }

    public static CapAddress of(int address, String domainId) {
        return new CapAddress(address, domainId);
    }

    public static CapAddress fromHex(String hex) {
        int address = Integer.parseInt(hex, 16);
        return new CapAddress(address, "default");
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.length() != 2) {
            return false;
        }
        try {
            int addr = Integer.parseInt(address, 16);
            return addr >= 0x00 && addr <= 0xFF;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int getAddress() {
        return address;
    }

    public String getDomainId() {
        return domainId;
    }

    public String toHex() {
        return String.format("%02X", address);
    }

    public String toFullString() {
        return domainId + ":" + toHex();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CapAddress that = (CapAddress) o;
        return address == that.address && domainId.equals(that.domainId);
    }

    @Override
    public int hashCode() {
        return 31 * address + domainId.hashCode();
    }

    @Override
    public String toString() {
        return toFullString();
    }
}

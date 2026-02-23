package net.ooder.sdk.cap;

public class CapAddress {
    
    private final String address;
    private final CapCategory category;
    
    public CapAddress(String address) {
        if (!isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid CAP address: " + address);
        }
        this.address = address.toUpperCase();
        this.category = CapCategory.fromAddress(this.address);
    }
    
    public String getAddress() {
        return address;
    }
    
    public CapCategory getCategory() {
        return category;
    }
    
    public int getAddressAsInt() {
        return Integer.parseInt(address, 16);
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
    
    public static CapAddress fromString(String address) {
        return new CapAddress(address);
    }
    
    @Override
    public String toString() {
        return address;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CapAddress other = (CapAddress) obj;
        return address.equals(other.address);
    }
    
    @Override
    public int hashCode() {
        return address.hashCode();
    }
}

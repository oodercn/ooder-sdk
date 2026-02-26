package net.ooder.sdk.core.capability.model;

public enum CapCategory {
    
    SYSTEM("system", "00", "3F", "系统区 - 核心系统能力，由 Ooder 官方定义"),
    COMMON("common", "40", "9F", "通用区 - 通用业务能力，由 Ooder 官方定义"),
    EXTENSION("extension", "A0", "FF", "扩展区 - 扩展能力，可由社区申请定义");
    
    private final String code;
    private final String startAddress;
    private final String endAddress;
    private final String description;
    
    CapCategory(String code, String startAddress, String endAddress, String description) {
        this.code = code;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getStartAddress() {
        return startAddress;
    }
    
    public String getEndAddress() {
        return endAddress;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static CapCategory fromAddress(String address) {
        int addr = Integer.parseInt(address, 16);
        if (addr >= 0x00 && addr <= 0x3F) {
            return SYSTEM;
        } else if (addr >= 0x40 && addr <= 0x9F) {
            return COMMON;
        } else if (addr >= 0xA0 && addr <= 0xFF) {
            return EXTENSION;
        }
        throw new IllegalArgumentException("Invalid CAP address: " + address);
    }
    
    public boolean contains(String address) {
        int addr = Integer.parseInt(address, 16);
        int start = Integer.parseInt(startAddress, 16);
        int end = Integer.parseInt(endAddress, 16);
        return addr >= start && addr <= end;
    }
}

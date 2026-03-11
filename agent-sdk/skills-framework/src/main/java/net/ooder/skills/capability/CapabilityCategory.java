package net.ooder.skills.capability;

/**
 * 能力分类枚举
 * 
 * <p>共16个分类，每分类8个地址</p>
 * 
 * <h3>地址分配：</h3>
 * <ul>
 *   <li>SYS (0x00-0x07): 系统核心</li>
 *   <li>ORG (0x08-0x0F): 组织服务</li>
 *   <li>AUTH (0x10-0x17): 认证服务</li>
 *   <li>VFS (0x18-0x1F): 文件存储</li>
 *   <li>DB (0x20-0x27): 数据库</li>
 *   <li>LLM (0x28-0x2F): 大语言模型</li>
 *   <li>KNOW (0x30-0x37): 知识库</li>
 *   <li>PAY (0x38-0x3F): 支付服务</li>
 *   <li>MEDIA (0x40-0x47): 媒体服务</li>
 *   <li>COMM (0x48-0x4F): 通讯服务</li>
 *   <li>MON (0x50-0x57): 监控服务</li>
 *   <li>IOT (0x58-0x5F): 物联网</li>
 *   <li>SEARCH (0x60-0x67): 搜索服务</li>
 *   <li>SCHED (0x68-0x6F): 调度服务</li>
 *   <li>SEC (0x70-0x77): 安全服务</li>
 *   <li>NET (0x78-0x7F): 网络服务</li>
 * </ul>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public enum CapabilityCategory {
    
    SYS("SYS", "系统核心", 0x00, 0x07),
    ORG("ORG", "组织服务", 0x08, 0x0F),
    AUTH("AUTH", "认证服务", 0x10, 0x17),
    VFS("VFS", "文件存储", 0x18, 0x1F),
    DB("DB", "数据库", 0x20, 0x27),
    LLM("LLM", "大语言模型", 0x28, 0x2F),
    KNOW("KNOW", "知识库", 0x30, 0x37),
    PAY("PAY", "支付服务", 0x38, 0x3F),
    MEDIA("MEDIA", "媒体服务", 0x40, 0x47),
    COMM("COMM", "通讯服务", 0x48, 0x4F),
    MON("MON", "监控服务", 0x50, 0x57),
    IOT("IOT", "物联网", 0x58, 0x5F),
    SEARCH("SEARCH", "搜索服务", 0x60, 0x67),
    SCHED("SCHED", "调度服务", 0x68, 0x6F),
    SEC("SEC", "安全服务", 0x70, 0x77),
    NET("NET", "网络服务", 0x78, 0x7F);
    
    private final String code;
    private final String name;
    private final int startAddress;
    private final int endAddress;
    
    CapabilityCategory(String code, String name, int startAddress, int endAddress) {
        this.code = code;
        this.name = name;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }
    
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getStartAddress() { return startAddress; }
    public int getEndAddress() { return endAddress; }
    
    /**
     * 检查地址是否属于本分类
     */
    public boolean contains(int address) {
        return address >= startAddress && address <= endAddress;
    }
    
    /**
     * 根据地址获取分类
     */
    public static CapabilityCategory fromAddress(int address) {
        for (CapabilityCategory category : values()) {
            if (category.contains(address)) {
                return category;
            }
        }
        return null;
    }
    
    /**
     * 根据代码获取分类
     */
    public static CapabilityCategory fromCode(String code) {
        if (code == null) return null;
        for (CapabilityCategory category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        return null;
    }
}

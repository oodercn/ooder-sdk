package net.ooder.scene.capability;

import java.util.HashMap;
import java.util.Map;

public enum CapabilitySegment {

    SYSTEM(0x0000, "system", "系统核心", SelectionMode.NONE, SwitchScope.NONE, null),
    SYSTEM_CONFIG(0x0005, "system-config", "系统配置", SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-system-config"),

    VFS(0x0100, "vfs", "文件存储", SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-vfs-local"),
    VFS_CACHE(0x0102, "vfs-cache", "存储缓存", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-vfs-local"),
    DATABASE(0x0105, "database", "数据库", SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-db-sqlite"),
    NETWORK(0x0110, "network", "网络服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-network"),
    PROTOCOL(0x0115, "protocol", "协议服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-protocol"),

    LLM(0x0200, "llm", "大语言模型", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-llm-ollama"),
    LLM_EMBEDDING(0x0205, "llm-embedding", "向量嵌入", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-llm-ollama"),
    LLM_RAG(0x0210, "llm-rag", "知识检索", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-rag"),
    AGENT(0x0220, "agent", "智能代理", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-agent"),

    ORG(0x0300, "org", "组织服务", SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-org-base"),
    ORG_AUTH(0x0305, "org-auth", "认证服务", SelectionMode.SINGLE, SwitchScope.SYSTEM, "skill-user-auth"),
    ORG_SYNC(0x0310, "org-sync", "组织同步", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-org-base"),
    MSG(0x0320, "msg", "消息服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-msg"),
    NOTIFY(0x0325, "notify", "通知服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-notify"),

    PAYMENT(0x0400, "payment", "支付服务", SelectionMode.MULTI, SwitchScope.RUNTIME, "skill-payment-mock"),
    MEDIA(0x0410, "media", "媒体服务", SelectionMode.MULTI, SwitchScope.RUNTIME, "skill-media-wechat"),
    SEARCH(0x0420, "search", "搜索服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-search"),
    REPORT(0x0430, "report", "报表服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-report"),
    SCHEDULER(0x0440, "scheduler", "调度服务", SelectionMode.SINGLE, SwitchScope.RUNTIME, "skill-scheduler-quartz");

    private static final Map<Integer, CapabilitySegment> ADDRESS_MAP = new HashMap<>();
    private static final Map<String, CapabilitySegment> CODE_MAP = new HashMap<>();

    static {
        for (CapabilitySegment segment : values()) {
            ADDRESS_MAP.put(segment.baseAddress, segment);
            CODE_MAP.put(segment.code, segment);
        }
    }

    private final int baseAddress;
    private final String code;
    private final String name;
    private final SelectionMode selectionMode;
    private final SwitchScope switchScope;
    private final String fallbackProvider;

    CapabilitySegment(int baseAddress, String code, String name,
                      SelectionMode selectionMode, SwitchScope switchScope,
                      String fallbackProvider) {
        this.baseAddress = baseAddress;
        this.code = code;
        this.name = name;
        this.selectionMode = selectionMode;
        this.switchScope = switchScope;
        this.fallbackProvider = fallbackProvider;
    }

    public int getBaseAddress() {
        return baseAddress;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public SwitchScope getSwitchScope() {
        return switchScope;
    }

    public String getFallbackProvider() {
        return fallbackProvider;
    }

    public int getAddress(SegmentSlot slot) {
        return baseAddress + slot.getOffset();
    }

    public boolean isAddressInRange(int address) {
        return address >= baseAddress && address < baseAddress + 5;
    }

    public static CapabilitySegment fromAddress(int address) {
        int baseAddress = (address / 5) * 5;
        return ADDRESS_MAP.get(baseAddress);
    }

    public static CapabilitySegment fromCode(String code) {
        return CODE_MAP.get(code);
    }

    public static boolean isValidAddress(int address) {
        return fromAddress(address) != null;
    }
}

package net.ooder.skills.capability;

/**
 * 能力地址枚举
 *
 * <p>共128个固定地址，16个分类，每分类8个地址</p>
 *
 * <h3>地址范围：</h3>
 * <ul>
 *   <li>0x00-0x7F: 固定地址区（128个）</li>
 *   <li>0x80-0xFF: 扩展地址区（128个）</li>
 * </ul>
 *
 * @author Agent-SDK Team
 * @version 2.4.0
 * @since 2.4.0
 */
public enum CapabilityAddress {

    // ========== SYS - 系统核心 (0x00-0x07) ==========
    SYS_CORE(0x00, "SYS_CORE", "系统核心", CapabilityCategory.SYS),
    SYS_CONFIG(0x01, "SYS_CONFIG", "系统配置", CapabilityCategory.SYS),
    SYS_LICENSE(0x02, "SYS_LICENSE", "许可证", CapabilityCategory.SYS),
    SYS_TENANT(0x03, "SYS_TENANT", "租户管理", CapabilityCategory.SYS),
    SYS_CACHE(0x04, "SYS_CACHE", "系统缓存", CapabilityCategory.SYS),
    SYS_LOCK(0x05, "SYS_LOCK", "分布式锁", CapabilityCategory.SYS),
    SYS_ID(0x06, "SYS_ID", "ID生成", CapabilityCategory.SYS),
    SYS_RESERVED(0x07, "SYS_RESERVED", "系统预留", CapabilityCategory.SYS),

    // ========== ORG - 组织服务 (0x08-0x0F) ==========
    ORG_LOCAL(0x08, "ORG_LOCAL", "本地组织", CapabilityCategory.ORG),
    ORG_DINGDING(0x09, "ORG_DINGDING", "钉钉组织", CapabilityCategory.ORG),
    ORG_FEISHU(0x0A, "ORG_FEISHU", "飞书组织", CapabilityCategory.ORG),
    ORG_WECOM(0x0B, "ORG_WECOM", "企业微信", CapabilityCategory.ORG),
    ORG_LDAP(0x0C, "ORG_LDAP", "LDAP", CapabilityCategory.ORG),
    ORG_AD(0x0D, "ORG_AD", "Active Directory", CapabilityCategory.ORG),
    ORG_CUSTOM(0x0E, "ORG_CUSTOM", "自定义组织", CapabilityCategory.ORG),
    ORG_RESERVED(0x0F, "ORG_RESERVED", "组织预留", CapabilityCategory.ORG),

    // ========== AUTH - 认证服务 (0x10-0x17) ==========
    AUTH_LOCAL(0x10, "AUTH_LOCAL", "本地认证", CapabilityCategory.AUTH),
    AUTH_OAUTH2(0x11, "AUTH_OAUTH2", "OAuth2", CapabilityCategory.AUTH),
    AUTH_SAML(0x12, "AUTH_SAML", "SAML", CapabilityCategory.AUTH),
    AUTH_JWT(0x13, "AUTH_JWT", "JWT", CapabilityCategory.AUTH),
    AUTH_SSO(0x14, "AUTH_SSO", "单点登录", CapabilityCategory.AUTH),
    AUTH_MFA(0x15, "AUTH_MFA", "多因素认证", CapabilityCategory.AUTH),
    AUTH_BIOMETRIC(0x16, "AUTH_BIOMETRIC", "生物认证", CapabilityCategory.AUTH),
    AUTH_RESERVED(0x17, "AUTH_RESERVED", "认证预留", CapabilityCategory.AUTH),

    // ========== VFS - 文件存储 (0x18-0x1F) ==========
    VFS_LOCAL(0x18, "VFS_LOCAL", "本地存储", CapabilityCategory.VFS),
    VFS_DATABASE(0x19, "VFS_DATABASE", "数据库存储", CapabilityCategory.VFS),
    VFS_MINIO(0x1A, "VFS_MINIO", "MinIO存储", CapabilityCategory.VFS),
    VFS_OSS(0x1B, "VFS_OSS", "阿里云OSS", CapabilityCategory.VFS),
    VFS_S3(0x1C, "VFS_S3", "AWS S3", CapabilityCategory.VFS),
    VFS_COS(0x1D, "VFS_COS", "腾讯云COS", CapabilityCategory.VFS),
    VFS_NAS(0x1E, "VFS_NAS", "NAS存储", CapabilityCategory.VFS),
    VFS_RESERVED(0x1F, "VFS_RESERVED", "存储预留", CapabilityCategory.VFS),

    // ========== DB - 数据库 (0x20-0x27) ==========
    DB_SQLITE(0x20, "DB_SQLITE", "SQLite", CapabilityCategory.DB),
    DB_MYSQL(0x21, "DB_MYSQL", "MySQL", CapabilityCategory.DB),
    DB_POSTGRESQL(0x22, "DB_POSTGRESQL", "PostgreSQL", CapabilityCategory.DB),
    DB_MONGODB(0x23, "DB_MONGODB", "MongoDB", CapabilityCategory.DB),
    DB_REDIS(0x24, "DB_REDIS", "Redis", CapabilityCategory.DB),
    DB_ELASTICSEARCH(0x25, "DB_ELASTICSEARCH", "Elasticsearch", CapabilityCategory.DB),
    DB_CLICKHOUSE(0x26, "DB_CLICKHOUSE", "ClickHouse", CapabilityCategory.DB),
    DB_RESERVED(0x27, "DB_RESERVED", "数据库预留", CapabilityCategory.DB),

    // ========== LLM - 大语言模型 (0x28-0x2F) ==========
    LLM_OLLAMA(0x28, "LLM_OLLAMA", "Ollama", CapabilityCategory.LLM),
    LLM_OPENAI(0x29, "LLM_OPENAI", "OpenAI", CapabilityCategory.LLM),
    LLM_QIANWEN(0x2A, "LLM_QIANWEN", "通义千问", CapabilityCategory.LLM),
    LLM_DEEPSEEK(0x2B, "LLM_DEEPSEEK", "DeepSeek", CapabilityCategory.LLM),
    LLM_VOLCENGINE(0x2C, "LLM_VOLCENGINE", "火山引擎", CapabilityCategory.LLM),
    LLM_ZHIPU(0x2D, "LLM_ZHIPU", "智谱AI", CapabilityCategory.LLM),
    LLM_BAIDU(0x2E, "LLM_BAIDU", "文心一言", CapabilityCategory.LLM),
    LLM_RESERVED(0x2F, "LLM_RESERVED", "LLM预留", CapabilityCategory.LLM),

    // ========== KNOW - 知识库 (0x30-0x37) ==========
    KNOW_VECTOR(0x30, "KNOW_VECTOR", "向量知识库", CapabilityCategory.KNOW),
    KNOW_DOCUMENT(0x31, "KNOW_DOCUMENT", "文档知识库", CapabilityCategory.KNOW),
    KNOW_GRAPH(0x32, "KNOW_GRAPH", "图谱知识库", CapabilityCategory.KNOW),
    KNOW_RAG(0x33, "KNOW_RAG", "RAG服务", CapabilityCategory.KNOW),
    KNOW_EMBEDDING(0x34, "KNOW_EMBEDDING", "嵌入服务", CapabilityCategory.KNOW),
    KNOW_CHUNK(0x35, "KNOW_CHUNK", "分块服务", CapabilityCategory.KNOW),
    KNOW_EXTRACT(0x36, "KNOW_EXTRACT", "提取服务", CapabilityCategory.KNOW),
    KNOW_RESERVED(0x37, "KNOW_RESERVED", "知识库预留", CapabilityCategory.KNOW),

    // ========== PAY - 支付服务 (0x38-0x3F) ==========
    PAY_MOCK(0x38, "PAY_MOCK", "模拟支付", CapabilityCategory.PAY),
    PAY_ALIPAY(0x39, "PAY_ALIPAY", "支付宝", CapabilityCategory.PAY),
    PAY_WECHAT(0x3A, "PAY_WECHAT", "微信支付", CapabilityCategory.PAY),
    PAY_UNIONPAY(0x3B, "PAY_UNIONPAY", "银联支付", CapabilityCategory.PAY),
    PAY_STRIPE(0x3C, "PAY_STRIPE", "Stripe", CapabilityCategory.PAY),
    PAY_PAYPAL(0x3D, "PAY_PAYPAL", "PayPal", CapabilityCategory.PAY),
    PAY_CUSTOM(0x3E, "PAY_CUSTOM", "自定义支付", CapabilityCategory.PAY),
    PAY_RESERVED(0x3F, "PAY_RESERVED", "支付预留", CapabilityCategory.PAY),

    // ========== MEDIA - 媒体服务 (0x40-0x47) ==========
    MEDIA_WECHAT_MP(0x40, "MEDIA_WECHAT_MP", "微信公众号", CapabilityCategory.MEDIA),
    MEDIA_WEIBO(0x41, "MEDIA_WEIBO", "微博", CapabilityCategory.MEDIA),
    MEDIA_XIAOHONGSHU(0x42, "MEDIA_XIAOHONGSHU", "小红书", CapabilityCategory.MEDIA),
    MEDIA_ZHIHU(0x43, "MEDIA_ZHIHU", "知乎", CapabilityCategory.MEDIA),
    MEDIA_TOUTIAO(0x44, "MEDIA_TOUTIAO", "今日头条", CapabilityCategory.MEDIA),
    MEDIA_DOUYIN(0x45, "MEDIA_DOUYIN", "抖音", CapabilityCategory.MEDIA),
    MEDIA_BILIBILI(0x46, "MEDIA_BILIBILI", "B站", CapabilityCategory.MEDIA),
    MEDIA_RESERVED(0x47, "MEDIA_RESERVED", "媒体预留", CapabilityCategory.MEDIA),

    // ========== COMM - 通讯服务 (0x48-0x4F) ==========
    COMM_MSG(0x48, "COMM_MSG", "消息服务", CapabilityCategory.COMM),
    COMM_NOTIFY(0x49, "COMM_NOTIFY", "通知服务", CapabilityCategory.COMM),
    COMM_EMAIL(0x4A, "COMM_EMAIL", "邮件服务", CapabilityCategory.COMM),
    COMM_SMS(0x4B, "COMM_SMS", "短信服务", CapabilityCategory.COMM),
    COMM_VOICE(0x4C, "COMM_VOICE", "语音服务", CapabilityCategory.COMM),
    COMM_VIDEO(0x4D, "COMM_VIDEO", "视频服务", CapabilityCategory.COMM),
    COMM_IM(0x4E, "COMM_IM", "即时通讯", CapabilityCategory.COMM),
    COMM_RESERVED(0x4F, "COMM_RESERVED", "通讯预留", CapabilityCategory.COMM),

    // ========== MON - 监控服务 (0x50-0x57) ==========
    MON_METRICS(0x50, "MON_METRICS", "指标监控", CapabilityCategory.MON),
    MON_LOG(0x51, "MON_LOG", "日志服务", CapabilityCategory.MON),
    MON_TRACE(0x52, "MON_TRACE", "链路追踪", CapabilityCategory.MON),
    MON_ALERT(0x53, "MON_ALERT", "告警服务", CapabilityCategory.MON),
    MON_DASHBOARD(0x54, "MON_DASHBOARD", "仪表盘", CapabilityCategory.MON),
    MON_REPORT(0x55, "MON_REPORT", "报表服务", CapabilityCategory.MON),
    MON_ANALYSIS(0x56, "MON_ANALYSIS", "分析服务", CapabilityCategory.MON),
    MON_RESERVED(0x57, "MON_RESERVED", "监控预留", CapabilityCategory.MON),

    // ========== IOT - 物联网 (0x58-0x5F) ==========
    IOT_DEVICE(0x58, "IOT_DEVICE", "设备管理", CapabilityCategory.IOT),
    IOT_GATEWAY(0x59, "IOT_GATEWAY", "网关服务", CapabilityCategory.IOT),
    IOT_DATA(0x5A, "IOT_DATA", "数据采集", CapabilityCategory.IOT),
    IOT_RULE(0x5B, "IOT_RULE", "规则引擎", CapabilityCategory.IOT),
    IOT_SHADOW(0x5C, "IOT_SHADOW", "设备影子", CapabilityCategory.IOT),
    IOT_OTA(0x5D, "IOT_OTA", "OTA升级", CapabilityCategory.IOT),
    IOT_EDGE(0x5E, "IOT_EDGE", "边缘计算", CapabilityCategory.IOT),
    IOT_RESERVED(0x5F, "IOT_RESERVED", "IoT预留", CapabilityCategory.IOT),

    // ========== SEARCH - 搜索服务 (0x60-0x67) ==========
    SEARCH_FULLTEXT(0x60, "SEARCH_FULLTEXT", "全文搜索", CapabilityCategory.SEARCH),
    SEARCH_VECTOR(0x61, "SEARCH_VECTOR", "向量搜索", CapabilityCategory.SEARCH),
    SEARCH_HYBRID(0x62, "SEARCH_HYBRID", "混合搜索", CapabilityCategory.SEARCH),
    SEARCH_SUGGEST(0x63, "SEARCH_SUGGEST", "搜索建议", CapabilityCategory.SEARCH),
    SEARCH_AGGREGATE(0x64, "SEARCH_AGGREGATE", "聚合分析", CapabilityCategory.SEARCH),
    SEARCH_RANK(0x65, "SEARCH_RANK", "排序服务", CapabilityCategory.SEARCH),
    SEARCH_INDEX(0x66, "SEARCH_INDEX", "索引服务", CapabilityCategory.SEARCH),
    SEARCH_RESERVED(0x67, "SEARCH_RESERVED", "搜索预留", CapabilityCategory.SEARCH),

    // ========== SCHED - 调度服务 (0x68-0x6F) ==========
    SCHED_QUARTZ(0x68, "SCHED_QUARTZ", "Quartz调度", CapabilityCategory.SCHED),
    SCHED_XXLJOB(0x69, "SCHED_XXLJOB", "XXL-JOB", CapabilityCategory.SCHED),
    SCHED_DELAY(0x6A, "SCHED_DELAY", "延迟队列", CapabilityCategory.SCHED),
    SCHED_CRON(0x6B, "SCHED_CRON", "Cron服务", CapabilityCategory.SCHED),
    SCHED_WORKFLOW(0x6C, "SCHED_WORKFLOW", "工作流调度", CapabilityCategory.SCHED),
    SCHED_BATCH(0x6D, "SCHED_BATCH", "批处理", CapabilityCategory.SCHED),
    SCHED_DAG(0x6E, "SCHED_DAG", "DAG调度", CapabilityCategory.SCHED),
    SCHED_RESERVED(0x6F, "SCHED_RESERVED", "调度预留", CapabilityCategory.SCHED),

    // ========== SEC - 安全服务 (0x70-0x77) ==========
    SEC_ENCRYPT(0x70, "SEC_ENCRYPT", "加密服务", CapabilityCategory.SEC),
    SEC_DECRYPT(0x71, "SEC_DECRYPT", "解密服务", CapabilityCategory.SEC),
    SEC_SIGN(0x72, "SEC_SIGN", "签名服务", CapabilityCategory.SEC),
    SEC_VERIFY(0x73, "SEC_VERIFY", "验签服务", CapabilityCategory.SEC),
    SEC_KEY(0x74, "SEC_KEY", "密钥管理", CapabilityCategory.SEC),
    SEC_CERT(0x75, "SEC_CERT", "证书管理", CapabilityCategory.SEC),
    SEC_AUDIT(0x76, "SEC_AUDIT", "审计服务", CapabilityCategory.SEC),
    SEC_RESERVED(0x77, "SEC_RESERVED", "安全预留", CapabilityCategory.SEC),

    // ========== NET - 网络服务 (0x78-0x7F) ==========
    NET_PROXY(0x78, "NET_PROXY", "代理服务", CapabilityCategory.NET),
    NET_GATEWAY(0x79, "NET_GATEWAY", "网关服务", CapabilityCategory.NET),
    NET_DNS(0x7A, "NET_DNS", "DNS服务", CapabilityCategory.NET),
    NET_LB(0x7B, "NET_LB", "负载均衡", CapabilityCategory.NET),
    NET_TUNNEL(0x7C, "NET_TUNNEL", "隧道服务", CapabilityCategory.NET),
    NET_VPN(0x7D, "NET_VPN", "VPN服务", CapabilityCategory.NET),
    NET_FIREWALL(0x7E, "NET_FIREWALL", "防火墙", CapabilityCategory.NET),
    NET_RESERVED(0x7F, "NET_RESERVED", "网络预留", CapabilityCategory.NET);

    private final int address;
    private final String code;
    private final String name;
    private final CapabilityCategory category;

    CapabilityAddress(int address, String code, String name, CapabilityCategory category) {
        this.address = address;
        this.code = code;
        this.name = name;
        this.category = category;
    }

    public int getAddress() { return address; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public CapabilityCategory getCategory() { return category; }

    /**
     * 获取十六进制地址字符串
     */
    public String getHexAddress() {
        return String.format("0x%02X", address);
    }

    /**
     * 根据地址获取枚举
     */
    public static CapabilityAddress fromAddress(int address) {
        for (CapabilityAddress addr : values()) {
            if (addr.address == address) {
                return addr;
            }
        }
        return null;
    }

    /**
     * 根据代码获取枚举
     */
    public static CapabilityAddress fromCode(String code) {
        if (code == null) return null;
        for (CapabilityAddress addr : values()) {
            if (addr.code.equalsIgnoreCase(code)) {
                return addr;
            }
        }
        return null;
    }

    /**
     * 根据分类获取所有地址
     */
    public static CapabilityAddress[] byCategory(CapabilityCategory category) {
        return java.util.Arrays.stream(values())
                .filter(addr -> addr.category == category)
                .toArray(CapabilityAddress[]::new);
    }
}

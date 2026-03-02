package net.ooder.sdk.api.capability;

/**
 * CAP 能力地址
 *
 * <p>遵循 v0.8.0 架构,使用 00-FF 地址空间标识能力</p>
 * <p>支持区域划分：系统区(00-3F)、通用区(40-9F)、扩展区(A0-FF)</p>
 *
 * @author Ooder Team
 * @version 2.3
 */
public class CapAddress {

    private final int address;

    private final String domainId;

    /**
     * 地址区域枚举
     */
    public enum AddressZone {
        /**
         * 系统区：00-3F (0-63)
         * 用途：核心系统能力
         * 权限：全局可访问
         */
        SYSTEM(0x00, 0x3F, "系统区"),

        /**
         * 通用区：40-9F (64-159)
         * 用途：通用业务能力
         * 权限：场景内可访问
         */
        GENERAL(0x40, 0x9F, "通用区"),

        /**
         * 扩展区：A0-FF (160-255)
         * 用途：扩展能力（私有域）
         * 权限：同域可访问
         */
        EXTENSION(0xA0, 0xFF, "扩展区");

        private final int start;
        private final int end;
        private final String name;

        AddressZone(int start, int end, String name) {
            this.start = start;
            this.end = end;
            this.name = name;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public String getName() {
            return name;
        }

        /**
         * 根据地址获取所属区域
         *
         * @param address 地址 (0-255)
         * @return 地址区域
         */
        public static AddressZone fromAddress(int address) {
            if (address < 0 || address > 255) {
                throw new IllegalArgumentException("Address must be between 0 and 255");
            }
            if (address <= SYSTEM.end) {
                return SYSTEM;
            } else if (address <= GENERAL.end) {
                return GENERAL;
            } else {
                return EXTENSION;
            }
        }

        /**
         * 获取区域的随机可用地址
         *
         * @return 区域内随机地址
         */
        public int getRandomAddress() {
            return start + (int) (Math.random() * (end - start + 1));
        }
    }

    private CapAddress(int address, String domainId) {
        if (address < 0 || address > 255) {
            throw new IllegalArgumentException("Address must be between 00 and FF (0-255)");
        }
        this.address = address;
        this.domainId = domainId != null ? domainId : "default";
    }

    /**
     * 创建CAP地址（默认域）
     *
     * @param address 地址 (0-255)
     * @return CAP地址
     */
    public static CapAddress of(int address) {
        return new CapAddress(address, "default");
    }

    /**
     * 创建CAP地址（指定域）
     *
     * @param address 地址 (0-255)
     * @param domainId 域ID
     * @return CAP地址
     */
    public static CapAddress of(int address, String domainId) {
        return new CapAddress(address, domainId);
    }

    /**
     * 从十六进制字符串创建CAP地址
     *
     * @param hex 十六进制字符串（如"01"）
     * @return CAP地址
     */
    public static CapAddress fromHex(String hex) {
        int address = Integer.parseInt(hex, 16);
        return new CapAddress(address, "default");
    }

    /**
     * 验证地址是否有效
     *
     * @param address 地址字符串
     * @return true表示有效
     */
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

    /**
     * 在指定区域创建地址
     *
     * @param zone 地址区域
     * @param domainId 域ID
     * @return CAP地址
     */
    public static CapAddress ofZone(AddressZone zone, String domainId) {
        int address = zone.getRandomAddress();
        return new CapAddress(address, domainId);
    }

    /**
     * 在指定区域创建地址（默认域）
     *
     * @param zone 地址区域
     * @return CAP地址
     */
    public static CapAddress ofZone(AddressZone zone) {
        return ofZone(zone, "default");
    }

    /**
     * 获取地址所属区域
     *
     * @return 地址区域
     */
    public AddressZone getZone() {
        return AddressZone.fromAddress(address);
    }

    /**
     * 检查地址是否在指定区域
     *
     * @param zone 区域
     * @return true表示在该区域
     */
    public boolean isInZone(AddressZone zone) {
        return getZone() == zone;
    }

    /**
     * 检查访问权限
     *
     * @param sourceDomain 源域ID
     * @return true表示有权限访问
     */
    public boolean isAccessibleFrom(String sourceDomain) {
        AddressZone zone = getZone();
        switch (zone) {
            case SYSTEM:
                return true;
            case GENERAL:
                return true;
            case EXTENSION:
                return domainId.equals(sourceDomain);
            default:
                return false;
        }
    }

    /**
     * 检查是否为系统区地址
     *
     * @return true表示系统区
     */
    public boolean isSystemZone() {
        return getZone() == AddressZone.SYSTEM;
    }

    /**
     * 检查是否为通用区地址
     *
     * @return true表示通用区
     */
    public boolean isGeneralZone() {
        return getZone() == AddressZone.GENERAL;
    }

    /**
     * 检查是否为扩展区地址
     *
     * @return true表示扩展区
     */
    public boolean isExtensionZone() {
        return getZone() == AddressZone.EXTENSION;
    }

    public int getAddress() {
        return address;
    }

    public String getDomainId() {
        return domainId;
    }

    /**
     * 转换为十六进制字符串
     *
     * @return 十六进制字符串（如"01"）
     */
    public String toHex() {
        return String.format("%02X", address);
    }

    /**
     * 转换为完整字符串（域ID:地址）
     *
     * @return 完整字符串
     */
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
        return toFullString() + " [" + getZone().getName() + "]";
    }
}

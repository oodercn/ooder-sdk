# CapAddress 区域划分扩展方案

## 背景

根据 `CAP-REGISTRY-SPEC.md` 规范，CAP地址空间应有明确的区域划分，但当前 `CapAddress` 实现缺少此功能。

## 当前问题

```java
// 当前 CapAddress 只有基本功能
public class CapAddress {
    private final int address;      // 0-255
    private final String domainId;  // 域ID
    // 缺少区域划分
}
```

## 扩展方案

### 方案1：添加 AddressZone 枚举（推荐）

```java
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
        SYSTEM(0x00, 0x3F, "系统区", AccessLevel.GLOBAL),

        /**
         * 通用区：40-9F (64-159)
         * 用途：通用业务能力
         * 权限：场景内可访问
         */
        GENERAL(0x40, 0x9F, "通用区", AccessLevel.SCENE),

        /**
         * 扩展区：A0-FF (160-255)
         * 用途：扩展能力（私有域）
         * 权限：同域可访问
         */
        EXTENSION(0xA0, 0xFF, "扩展区", AccessLevel.DOMAIN);

        private final int start;
        private final int end;
        private final String name;
        private final AccessLevel defaultAccessLevel;

        AddressZone(int start, int end, String name, AccessLevel accessLevel) {
            this.start = start;
            this.end = end;
            this.name = name;
            this.defaultAccessLevel = accessLevel;
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

        public AccessLevel getDefaultAccessLevel() {
            return defaultAccessLevel;
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

    /**
     * 访问级别枚举
     */
    public enum AccessLevel {
        GLOBAL,     // 全局可访问
        SCENE,      // 场景内可访问
        DOMAIN      // 同域可访问
    }

    private CapAddress(int address, String domainId) {
        if (address < 0 || address > 255) {
            throw new IllegalArgumentException("Address must be between 00 and FF (0-255)");
        }
        this.address = address;
        this.domainId = domainId != null ? domainId : "default";
    }

    // ==================== 现有方法 ====================

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

    // ==================== 新增方法 ====================

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
        switch (zone.getDefaultAccessLevel()) {
            case GLOBAL:
                return true;
            case SCENE:
                // 场景内可访问，需要额外判断
                return true;
            case DOMAIN:
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

    // ==================== Getter方法 ====================

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
        return toFullString() + " [" + getZone().getName() + "]";
    }
}
```

### 使用示例

```java
// 创建系统区地址
CapAddress systemAddr = CapAddress.of(0x01);
System.out.println(systemAddr.getZone());  // SYSTEM
System.out.println(systemAddr.isSystemZone());  // true

// 创建通用区地址
CapAddress generalAddr = CapAddress.of(0x50);
System.out.println(generalAddr.getZone());  // GENERAL

// 创建扩展区地址
CapAddress extAddr = CapAddress.ofZone(CapAddress.AddressZone.EXTENSION, "user-domain");
System.out.println(extAddr.isExtensionZone());  // true

// 检查访问权限
boolean accessible = extAddr.isAccessibleFrom("user-domain");  // true
boolean notAccessible = extAddr.isAccessibleFrom("other-domain");  // false
```

## 影响范围

| 组件 | 影响 | 修改内容 |
|------|------|----------|
| CapAddress | 新增枚举和方法 | 添加 AddressZone 枚举和相关方法 |
| CapRegistry | 可能需要适配 | 按区域分配地址 |
| Capability | 无影响 | 使用 CapAddress 即可 |

## 兼容性

- **向后兼容**：现有代码无需修改
- **新增功能**：新代码可以使用区域划分功能

---

**建议**：采用方案1，在 `CapAddress` 中添加 `AddressZone` 枚举和相关方法。

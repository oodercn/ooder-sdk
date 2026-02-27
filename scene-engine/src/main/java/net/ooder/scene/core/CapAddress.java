package net.ooder.scene.core;

/**
 * CAP 能力地址
 * 
 * <p>封装能力地址（00-FF），提供地址解析和分类功能。</p>
 * 
 * <h3>地址空间划分：</h3>
 * <ul>
 *   <li>00-3F (0-63) - 系统能力（System）：框架核心功能</li>
 *   <li>40-9F (64-159) - 通用能力（Common）：业务通用功能</li>
 *   <li>A0-FF (160-255) - 扩展能力（Extension）：第三方扩展</li>
 * </ul>
 * 
 * <h3>使用示例：</h3>
 * <pre>
 * // 创建地址
 * CapAddress address = new CapAddress("40");
 * 
 * // 获取分类
 * String category = address.getCategory(); // "COMMON"
 * 
 * // 验证有效性
 * boolean valid = address.isValid(); // true
 * </pre>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 0.7.0
 * @see CapRouter
 * @see CapRequest
 */
public class CapAddress {

    /** 地址值（00-FF） */
    private final String address;

    /** 地址分类（SYSTEM/COMMON/EXTENSION/UNKNOWN） */
    private final String category;

    /**
     * 构造器
     * 
     * @param address 地址值（00-FF）
     */
    public CapAddress(String address) {
        this.address = address;
        this.category = determineCategory(address);
    }

    /**
     * 获取地址值
     * 
     * @return 地址值（00-FF）
     */
    public String getAddress() {
        return address;
    }

    /**
     * 获取地址分类
     * 
     * @return 分类（SYSTEM/COMMON/EXTENSION/UNKNOWN）
     */
    public String getCategory() {
        return category;
    }

    /**
     * 判断是否为系统能力
     * 
     * @return 是否为系统能力
     */
    public boolean isSystem() {
        return "SYSTEM".equals(category);
    }

    /**
     * 判断是否为通用能力
     * 
     * @return 是否为通用能力
     */
    public boolean isCommon() {
        return "COMMON".equals(category);
    }

    /**
     * 判断是否为扩展能力
     * 
     * @return 是否为扩展能力
     */
    public boolean isExtension() {
        return "EXTENSION".equals(category);
    }

    /**
     * 确定地址分类
     * 
     * <p>根据地址值判断所属分类：</p>
     * <ul>
     *   <li>00-3F: SYSTEM</li>
     *   <li>40-9F: COMMON</li>
     *   <li>A0-FF: EXTENSION</li>
     * </ul>
     * 
     * @param address 地址值
     * @return 分类
     */
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

    /**
     * 验证地址是否有效
     * 
     * <p>有效地址范围：00-FF（十六进制）</p>
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        try {
            int addr = Integer.parseInt(address, 16);
            return addr >= 0x00 && addr <= 0xFF;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取地址的整数值
     * 
     * @return 整数值（0-255）
     */
    public int toInt() {
        try {
            return Integer.parseInt(address, 16);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String toString() {
        return address + " (" + category + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CapAddress that = (CapAddress) obj;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}

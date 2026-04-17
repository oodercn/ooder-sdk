package net.ooder.spi.dict;

import lombok.Data;
import java.util.Map;

/**
 * 字典项
 */
@Data
public class DictItem {

    /**
     * 字典项编码
     */
    private String code;

    /**
     * 字典项名称
     */
    private String name;

    /**
     * 所属分类
     */
    private String category;

    /**
     * 排序号
     */
    private int sortOrder;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
}

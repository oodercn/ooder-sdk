package net.ooder.spi.dict;

import lombok.Data;
import java.util.List;

/**
 * 字典
 */
@Data
public class Dict {

    /**
     * 字典编码
     */
    private String code;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典描述
     */
    private String description;

    /**
     * 字典项列表
     */
    private List<DictItem> items;

    /**
     * 是否系统字典
     */
    private boolean system;

    /**
     * 是否启用
     */
    private boolean enabled;
}

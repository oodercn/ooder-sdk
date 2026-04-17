package net.ooder.spi.dict;

import java.util.List;

/**
 * 字典服务 SPI
 * 提供字典数据管理功能
 */
public interface DictService {

    /**
     * 根据分类获取字典项
     *
     * @param category 分类编码
     * @return 字典项列表
     */
    List<DictItem> getDictItems(String category);

    /**
     * 获取所有字典
     *
     * @return 字典列表
     */
    List<Dict> getAllDicts();

    /**
     * 添加字典项
     *
     * @param category 分类编码
     * @param code     字典项编码
     * @param name     字典项名称
     */
    void addDictItem(String category, String code, String name);

    /**
     * 根据编码获取字典项
     *
     * @param category 分类编码
     * @param code     字典项编码
     * @return 字典项
     */
    DictItem getDictItemByCode(String category, String code);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 获取字典分类列表
     *
     * @return 分类列表
     */
    List<String> getCategories();
}

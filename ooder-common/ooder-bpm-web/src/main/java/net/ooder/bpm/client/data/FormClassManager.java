package net.ooder.bpm.client.data;

/**
 * time 06-01-01
 * @author wenzhang
 */

import java.util.List;

public interface FormClassManager {
    /**
     * 指定应用指定ID查找
     * 
     * @param app
     * @param id
     * @return
     */
    public FormClassBean getFormClassBeanInst(String app, String id);

    /**
     * 根据表单名称查询实例
     * 
     * @param name
     * @return
     */
    public List<FormClassBean> getFormClassByName(String app, String name);

    /**
     * 取得所有表单列表
     * 
     * @return
     */
    public List<FormClassBean> getAllFormClassList(String app);

}
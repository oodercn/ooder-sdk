
package net.ooder.bpm.client.event;

import net.ooder.bpm.engine.BPMException;
import net.ooder.common.JDSListener;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 活动事件监听器接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2019
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang
 * @version 1.0
 */
public interface BPDListener extends JDSListener {


    /**
     * 创建流程完毕
     */
    public void ProcessDefCreaded(BPDEvent event) throws BPMException;

    /**
     * 流程删除完毕
     */
    public void ProcessDefDeleted(BPDEvent event) throws BPMException;


    /**
     * 冻结流程完毕
     */
    public void ProcessDefFreezed(BPDEvent event) throws BPMException;

    /**
     * 流程更新
     */
    public void ProcessDefUpdated(BPDEvent event) throws BPMException;


    /**
     * 激活流程完毕
     */
    public void ProcessDefActivaed(BPDEvent event) throws BPMException;


}

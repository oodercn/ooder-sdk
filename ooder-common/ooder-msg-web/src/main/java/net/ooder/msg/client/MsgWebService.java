/**
 * $RCSfile: MsgWebService.java,v $
 * $Revision: 1.0 $
 * $Date: 2025/08/25 $
 * <p>
 * Copyright (c) 2025 ooder.net
 * </p>
 * <p>
 * Company: ooder.net
 * </p>
 * <p>
 * License: MIT License
 * </p>
 */
package net.ooder.msg.client;

import net.ooder.annotation.JLuceneIndex;
import net.ooder.common.Condition;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.msg.RMsg;
import net.ooder.org.query.MsgConditionKey;


import java.util.List;

public interface MsgWebService {

    public ListResultModel<List<RMsg>> loadMsgs(String[] msgId);

    public ResultModel<RMsg> getMsgById(String msgId);

    public ListResultModel<List<String>> findMsgIds(Condition<MsgConditionKey, JLuceneIndex> condition) ;

    public ResultModel<Boolean>  updateMsg(RMsg msg);

    public ResultModel<Boolean>  deleteMsg(String msgId);

    public ResultModel<Boolean>  deleteMsgs(String[] msgIds);
}



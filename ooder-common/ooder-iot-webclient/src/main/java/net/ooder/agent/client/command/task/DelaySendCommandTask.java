package net.ooder.agent.client.command.task;

import  net.ooder.context.JDSActionContext;
import  net.ooder.context.JDSContext;
import  net.ooder.msg.CommandMsg;
import  net.ooder.msg.Msg;
import  net.ooder.msg.MsgFactroy;
import  net.ooder.org.Person;
import  net.ooder.server.context.MinServerActionContextImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class DelaySendCommandTask implements Callable<Msg> {

    private Person currPerson;

    private Msg msg;

    private MinServerActionContextImpl autoruncontext;

    public DelaySendCommandTask(Person currPerson, Msg msg) {
	JDSContext context = JDSActionContext.getActionContext();

	this.autoruncontext = new MinServerActionContextImpl(context.getHttpRequest(), context.getOgnlContext());
	autoruncontext.setParamMap(context.getContext());
	if (context.getSessionId() != null) {
	    autoruncontext.setSessionId(context.getSessionId());
	    autoruncontext.getSession().put("sessionHandle", context.getSession().get("sessionHandle"));
	}
	autoruncontext.setSessionMap(context.getSession());
	this.currPerson = currPerson;
	this.msg = msg;
    }

    public Msg call() throws Exception {
	JDSActionContext.setContext(autoruncontext);




	List<String> personIds = new ArrayList<String>();
	personIds.add(currPerson.getID());

	MsgFactroy.getInstance().getClient(currPerson.getID(),CommandMsg.class).sendMassMsg(msg,personIds);

//		PersonMsgGroup msgGroup = MsgFactroy.getInstance().getClient(currPerson.getID(),CommandMsg.class).getPersonMassMsgGroupByType();
//
//	List<Msg> msgs = msgGroup.massSendMsg(msg, personIds);
//	if (msgs != null && msgs.size() > 0) {
//	    msg = msgs.get(0);
//	}

	return msg;

    }

}

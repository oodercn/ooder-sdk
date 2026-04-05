package net.ooder.bpm.client.ct;

import net.ooder.bpm.client.ActivityDef;
import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ProcessInst;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.right.RightCtx;
import net.ooder.common.JDSException;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.context.JDSActionContext;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esb.config.manager.JDSExpressionParserManager;
import net.ooder.esd.manager.formula.ParticipantSelect;

import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.conf.OrgConstants;
import net.ooder.server.JDSClientService;
import net.ooder.server.OrgManagerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtRightEngine {

    static CtRightEngine rightEngine;

    public static CtRightEngine getInstance() {

        if (rightEngine == null) {
            rightEngine = new CtRightEngine();
        }
        return rightEngine;
    }


    private final CtBPMCacheManager cacheManager;

    private static final Log log = LogFactory.getLog(OrgConstants.CONFIG_KEY.getType(), CtBPMCacheManager.class);


    public CtRightEngine() {

        this.cacheManager = CtBPMCacheManager.getInstance();
    }

    /**
     * 取得活动办理人的候选人， 解析活动定义的公式来确定候选人！
     *
     * @param activityDefId 指定的活动ID
     * @param ctx           上下文环境，包括
     * @return
     * @throws JDSException
     */
    public List getPerformerCandidate(String activityDefId, Map<RightCtx, Object> ctx) throws JDSException {
        ActivityDef activityDef = cacheManager.getActivityDef(activityDefId);
        ActivityDefRight rightDef = activityDef.getRightAttribute();
        // 支持新的公式摸板
        ctx = this.fillInUserID(ctx);
        List<ParticipantSelect> participantSelects = rightDef.getPerformerSelectedAtt();
        return getParticipant(participantSelects, ctx);
    }

    /**
     * @param activityInstId
     * @param group
     * @return
     * @throws JDSException
     */
    public List<Person> getActivityInstPerson(String activityInstId, ActivityInstRightAtt group) throws JDSException {
        List<Person> persons = new ArrayList<Person>();
        List<String> personIds = CtBPMCacheManager.getInstance().getActivityInstRightAttribute(activityInstId, group);
        for (String personId : personIds) {
            try {
                persons.add(OrgManagerFactory.getOrgManager().getPersonByID(personId));
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        }
        return persons;
    }

    ;

    /**
     * @param activityInstId
     * @param group
     * @return
     * @throws JDSException
     */
    public List<Person> getActivityInstHistoryPerson(String activityInstId, ActivityInstHistoryAtt group) throws JDSException {
        List<Person> persons = new ArrayList<Person>();

        List<String> personIds = CtBPMCacheManager.getInstance().getActivityHistoryRightAttribute(activityInstId, group);

        for (String personId : personIds) {
            try {
                persons.add(OrgManagerFactory.getOrgManager().getPersonByID(personId));
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        }
        return persons;
    }

    ;


    /**
     * 支持新的公式摸板，可以有多个公式组合在一起，每个公式可以设置参数
     *
     * @param participantSelects 公式定义的扩展属性
     * @param ctx
     * @return
     * @throws JDSException
     */
    public List<Person> getParticipant(List<ParticipantSelect> participantSelects, Map<RightCtx, Object> ctx) throws JDSException {
        if (participantSelects == null) {
            return new ArrayList();
        }
        ctx = this.fillInUserID(ctx);
        ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
        List result = new ArrayList();
        for (ParticipantSelect participantSelect : participantSelects) {
            String formulaId = participantSelect.getParticipantSelectId();
            Object o = executeExpression(participantSelect, ctx);
            if (o == null) {
                continue;
            }

            if ((o instanceof Person) || o instanceof Org) {
                result.add(o);
            } else if (o instanceof Person[]) {
                Person[] persons = (Person[]) o;
                for (int j = 0; j < persons.length; j++) {
                    result.add(persons[j]);
                }
            } else if (o instanceof Org[]) {
                Org[] orgs = (Org[]) o;
                for (int j = 0; j < orgs.length; j++) {
                    result.add(orgs[j]);
                }
            }
        }
        result = combineParticipant(result);

        return result;
    }

    /**
     * @param src
     * @return
     */
    private List combineParticipant(List src) {
        List result = new ArrayList();
        for (int i = 0; i < src.size(); i++) {
            if (!result.contains(src.get(i))) {
                result.add(src.get(i));
            }
        }
        return result;
    }


    private boolean isUUID(String uuid) {
        if (uuid.length() == 36) {
            if (uuid.charAt(8) == '-' && uuid.charAt(13) == '-') {
                return true;
            }
        }
        return false;
    }


    private Object executeExpression(ParticipantSelect participantSelect, Map<RightCtx, Object> ctx) {
        ctx = this.fillInUserID(ctx);
        ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
        addCurrentActivityInst(parser, ctx);
        addCurrentProcessInst(parser, ctx);
        List<ExpressionParameter> parameters = participantSelect.getParameterList();
        for (ExpressionParameter parameter : parameters) {
            String parameterCode = parameter.getParameterCode();
            String parameterValue = parameter.getParameterValue();
            if (parameterValue != null) {
                parser.addVariableAsObject(parameterCode, parameterValue);
            } else if (ctx != null && ctx.containsKey(parameterCode)) {
                parser.addVariableAsObject(parameterCode, ctx.get(parameterCode));
            }
        }


        boolean result = parser.parseExpression(participantSelect.getFormula());
        if (result == false) {
            log.warn("expression parse error: " + parser.getErrorInfo());
            return null;
        } else {
            Object o = parser.getValueAsObject();
            if (parser.hasError() == true) {
                log.error(parser.getErrorInfo());
            }
            return o;
        }
    }

    /**
     * 向公式解析器加入当前活动实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentActivityInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {
        ctx = this.fillInUserID(ctx);
        String activityInstId = (String) ctx.get(RightCtx.ACTIVITYINST_ID);
        if (activityInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), null);
            return;
        }

        try {
            ActivityInst activityInst = cacheManager.getActivityInst(activityInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), activityInst);
        } catch (JDSException e) {
            log.warn("load activiytInst failed!", e);
        }
    }

    /**
     * 向公式解析器加入当前流程实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentProcessInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {
        ctx = this.fillInUserID(ctx);
        String processInstId = (String) ctx.get(RightCtx.PROCESSINST_ID);
        if (processInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), null);
            return;
        }

        try {
            ProcessInst processInst = (ProcessInst) cacheManager.getProcessInst(processInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), processInst);
        } catch (JDSException e) {
            log.warn("load processInst failed!", e);
        }
    }


    private Map<RightCtx, Object> fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }
        //添加当前环境变量
        result.putAll(JDSActionContext.getActionContext().getContext());

        result.put(RightCtx.USERID, this.getClient().getConnectInfo().getUserID());
        return result;
    }

    public JDSClientService getClient() {

        JDSClientService client = ((JDSClientService) EsbUtil.parExpression("$JDSC"));

        return client;
    }

}

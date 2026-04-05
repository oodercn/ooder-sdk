
package net.ooder.bpm.client;


import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.annotation.Attributetype;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.enums.process.ProcessDefAccess;
import net.ooder.bpm.enums.process.ProcessDefVersionStatus;
import net.ooder.annotation.DurationUnit;
import net.ooder.annotation.MethodChinaName;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义版本客户端接口
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public interface ProcessDefVersion extends java.io.Serializable {

    /**
     * 返回流程ID 此属性为流程基本属性，与版本无关，
     *
     * @return 流程的UUID
     */
    @MethodChinaName(cname = "流程的UUID")
    public String getProcessDefId();

    /**
     * 返回当前版本的UUID
     *
     * @return 版本的UUID
     */
    @MethodChinaName(cname = "版本的UUID")
    public String getProcessDefVersionId();

    /**
     * 返回当前流程定义的版本号
     *
     * @return
     */
    @MethodChinaName(cname = "版本号")
    public int getVersion();

    /**
     * 返回当前版本的状态： 此属性为流程基本属性，与版本无关，
     *
     * @return 返回的状态有三个值：
     * <li>VERSION_UNDER_REVISION - 修订中
     * <li>VERSION_RELEASED - 已发布
     * <li>VERSION_UNDER_TEST - 测试中
     */
    @MethodChinaName(cname = "当前版本的状态", display = false)
    public ProcessDefVersionStatus getPublicationStatus();

    /**
     * 流程定义的名称， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的名称
     */
    @MethodChinaName(cname = "流程定义名称")
    public String getProcessDefName();

    /**
     * 流程定义的描述， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的描述
     */
    @MethodChinaName(cname = "流程定义描述")
    public String getDescription();

    /**
     * 流程定义的分类， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的分类
     */
    @MethodChinaName(cname = "流程定义分类")
    public String getClassification();

    public String getDefDescription();

    /**
     * 流程所属的应用系统，如：OA,CMS等 此属性为流程基本属性，与版本无关，
     *
     * @return 字符串，应用系统的代码，如："SP","CMS"
     */
    @MethodChinaName(cname = "所属应用系统", display = false)
    public String getSystemCode();

    /**
     * 流程的访问级别，也就对应着流程的类型 此属性为流程基本属性，与版本无关，
     *
     * @return 两种返回值：
     * <li>ACCESS_PUBLIC: 可以独立启动
     * <li>ACCESS_PRIVATE: 不可以独立启动，只能作为Subflow
     */
    @MethodChinaName(cname = "当前版本的状态")

    public ProcessDefAccess getAccessLevel();

    /**
     * 取得流程版本的激活时间，如果在冻结状态，则返回null
     *
     * @return
     */
    @MethodChinaName(cname = "流程版本的激活时间", display = false)
    public Date getActiveTime();

    /**
     * 取得流程版本的冻结时间，如果在激活状态，则返回null
     *
     * @return
     */
    @MethodChinaName(cname = "流程版本的冻结时间", display = false)
    public Date getFreezeTime();

    /**
     * 取得流程版本创建人的Id
     *
     * @return
     */
    @MethodChinaName(cname = "版本创建人Id", display = false)
    public String getCreatorId();

    /**
     * 取得流程版本创建人的姓名
     *
     * @return
     */
    @MethodChinaName(cname = "创建人姓名", display = false)
    public String getCreatorName();

    /**
     * 取得流程版本的创建时间
     *
     * @return
     */
    @MethodChinaName(cname = "流程版本创建时间", display = false)
    public Date getCreated();

    /**
     * 取得流程版本最后修改人的Id
     *
     * @return
     */
    @MethodChinaName(cname = "最后修改人Id", display = false)
    public String getModifierId();

    /**
     * 取得流程版本最后修改人的姓名
     *
     * @return
     */
    @MethodChinaName(cname = "最后修改人姓名", display = false)
    public String getModifierName();

    /**
     * 取得流程版本的最后修改
     *
     * @return
     */
    @MethodChinaName(cname = "流程版本最后修改日期", display = false)
    public Date getModifyTime();

    /**
     * 取得流程版本的完成期限 单位从getDurationUnit()取得
     *
     * @return
     */
    @MethodChinaName(cname = "流程版本完成期限 ", display = false)
    public int getLimit();

    /**
     * 取得时间的单位
     *
     * @return <li>Y:年
     * <li>M:月
     * <li>D:日
     * <li>H:时
     * <li>m:分
     * <li>s:秒
     * <li>W:工作日
     */
    @MethodChinaName(cname = "取得时间的单位 ")

    public DurationUnit getDurationUnit();

    /**
     * 取得当前版本中包含所有活动的对象
     *
     * @return 返回的List是只读
     */
    @MethodChinaName(cname = "当前版本中包含所有活动的对象")
    public List<ActivityDef> getAllActivityDefs();

    /**
     * 取得当前版本中包含所有路由的对象
     *
     * @return 返回的List是只读
     */
    @MethodChinaName(cname = "当前版本中包含所有路由的对象")
    public List<RouteDef> getAllRouteDefs() throws BPMException;

    /**
     * 取得工作流扩展属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "工作流扩展属性值", returnStr = "getWorkflowAttribute($R('name'))", display = false)
    public Object getWorkflowAttribute(String name);

    /**
     *
     * @param attributetype
     * @param name
     * @return
     */
    @MethodChinaName(cname = "工作流扩展属性值", returnStr = "getWorkflowAttribute($R('attributetype'),$R('name'))", display = false)
    public Object getAttribute(Attributetype attributetype, String name);

    /**
     * 取得权限扩展属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "权限扩展属性值", returnStr = "getRightAttribute($R('name'))", display = false)
    public Object getRightAttribute(String name);


    /**
     * 取得权限扩展属性值
     * <p>
     * 属性名称
     *
     * @return 属性值
     */
    @MethodChinaName(cname = "获取所有属性", returnStr = "getRightAttribute()", display = false)
    public List getAllAttribute();


    /**
     * 取得应用扩展属性值
     *
     * @param name 属性名称
     * @return 属性值
     */
    @MethodChinaName(cname = "取得应用扩展属性值", returnStr = "getAppAttribute($R('attName'))", display = false)
    public Object getAppAttribute(String name);

    /**
     * 取得自定义扩展属性值
     *
     * @param name
     * @return
     */
    @MethodChinaName(cname = "取得自定义扩展属性值", returnStr = "getAttribute($R('attName'))", display = false)
    public String getAttribute(String name);

    /**
     * 取得所有的流程监听器
     *
     * @return
     */
    @MethodChinaName(cname = "取得所有的流程监听器", display = false)
    public List<Listener> getListeners();

    /**
     * @return
     */
    @MethodChinaName(cname = "活动流程表单定义", display = false)
    public ProcessDefForm getFormDef() throws BPMException;

    /**
     * @return
     */
    List<String> getActivityDefIds();

    /**
     * @return
     */
    List<String> getRouteDefIds();
}

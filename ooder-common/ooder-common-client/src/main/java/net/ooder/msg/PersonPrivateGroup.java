/**
 * $RCSfile: PersonPrivateGroup.java,v $
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
package net.ooder.msg;

import java.util.List;

import net.ooder.annotation.MethodChinaName;
import net.ooder.org.Person;

/**
 * 人员个人好友组接口类
 * <p>
 * Title: ooder组织机构中间件
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025-2008
 * </p>
 * <p>
 * Company: raddev.cn
 * </p>
 * 
 * @author  ooder
 * @version 2.0
 */
public interface PersonPrivateGroup {

    public static final String OFFICE = "OFFICE", PRIVATE = "PRIVATE", SYSTEM = "SYSTEM";

    /**
     * 取得在该个人组中的所有人员
     * 
     * @return 人员数组
     */

    @MethodChinaName(cname = "取得在该个人组中的所有人员")
    public List<Person> getPersonList();

    @MethodChinaName(cname = "取得在该个人组中的所有人员的标识", display = false)
    public List<String> getPersonIdList();

    /**
     * 取得人员个人组的名称
     * 
     * @return 人员个人组的名称
     */
    @MethodChinaName(cname = "取得人员个人组的名称")
    public String getName();

    @MethodChinaName(cname = "取得人员个人组的类别")
    public String getType();

    /**
     * 取得人员个人组的标识
     * 
     * @return 人员个人组的标识
     */
    @MethodChinaName(cname = "取得人员个人组的标识")
    public String getID();

    @MethodChinaName(cname = "取得人员ID")
    public String getPersonId();
}

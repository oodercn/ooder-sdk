/**
 * $RCSfile: ReturnType.java,v $
 * $Revision: 1.1 $
 * $Date: 2025/08/25 00:26:06 $
 * <p>
 * Copyright (C) 2025 ooder. All rights reserved.
 * <p>
 * This software is the proprietary information of ooder.
 * Use is subject to license terms.
 */
package net.ooder.common;

import net.ooder.annotation.ReturnTypeEnums;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: 调用返回值状态类
 * </p>
 * <p>
 * Copyright: Copyright (c) 2025
 * </p>
 * <p>
 * Company: ooder
 * </p>
 *
 * @author ooder
 * @version 3.0
 */
public class ReturnType {


    public static ReturnTypeEnums MAINCODE_SUCCESS = ReturnTypeEnums.MAINCODE_SUCCESS;
    public static ReturnTypeEnums MAINCODE_FAIL = ReturnTypeEnums.MAINCODE_FAIL;

    private ReturnTypeEnums mainCode;

    private ReturnTypeEnums subCode;

    private String message;

    public ReturnType() {

    }

    public ReturnType(ReturnTypeEnums mainCode) {
        this.mainCode = mainCode;
    }

    public ReturnType(ReturnTypeEnums mainCode, ReturnTypeEnums subCode) {
        this.mainCode = mainCode;
        this.subCode = subCode;
    }

    public ReturnType(ReturnTypeEnums mainCode, String message) {
        this.mainCode = mainCode;
        this.message = message;
    }

    public ReturnTypeEnums getMainCode() {
        return mainCode;
    }

    public void setMainCode(ReturnTypeEnums mainCode) {
        this.mainCode = mainCode;
    }

    public ReturnTypeEnums getSubCode() {
        return subCode;
    }

    public void setSubCode(ReturnTypeEnums subCode) {
        this.subCode = subCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ReturnType(ReturnTypeEnums mainCode, ReturnTypeEnums subCode, String message) {
        this.mainCode = mainCode;
        this.subCode = subCode;
        this.message = message;
    }

    /**
     * 取得主代码，此代码可以确定主要的执行情况
     *
     * @return 取值范围：
     * <li> MAINCODE_SUCCESS - 执行成功
     * <li> MAINCODE_FAIL - 执行失败
     */
    public ReturnTypeEnums mainCode() {
        return mainCode;
    }

    /**
     * 取得执行情况子代码.<br>
     * 此代码将具体解释主代码情况发生的原因等
     *
     * @return 此值根据主代码不同而不同，见错误代码文档
     */
    public ReturnTypeEnums subCode() {
        return subCode;
    }

    public String toString() {
        return message;
    }

    /**
     * 判断此次执行是否成功
     *
     * @return true - 成功<br>
     * false - 失败
     */
    public boolean isSucess() {
        return mainCode == ReturnTypeEnums.MAINCODE_SUCCESS;
    }
}

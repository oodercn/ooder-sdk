/**
 * $RCSfile: ExpressionParser.java,v $
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
package net.ooder.common.expression;

import net.ooder.common.expression.function.*;
import java.util.*;
/**
 * <p>Title: ETCL</p>
 * <p>Description: 数据更新工具（抽取，转换，清洗，装载）</p>
 * 表达式解析器接口
 * <p>Copyright: Copyright spk  (c) 2003</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public interface ExpressionParser
{
    /**
     * 加入标准函数
     */
    public void addStandardFunctions();

    /**
     * 加入常数
     */
    public void addStandardConstants();

    /**
     * 加入函数
     * @param functionName 函数的名称
     * @param function 要加入的函数
     */
    public void addFunction(String functionName,
                            Function function);

    /**
     * 加入函数。
     *
     */
    public void addFuntionTable(FunctionTable fuctionTable);
    
    /**
     * 加入变量。
     * @param name 变量名
     * @param object 变量的值
     */
    public void addVariableAsObject(String name, Object object);

    /**
     * 加入变量名，用于解析表达式用。
     * @param names
     */
    public void addVariableNames(String[] names);

    /**
     * 取得表达式的值
     * @return 表达式的值
     */
    public Object getValueAsObject();

    /**
     * 解析表达式
     * @param expression 要解析的表达式
     * @return true-解析成功, false-解析失败
     */
    public boolean parseExpression(String expression);

    /**
     * 将变量从当前的变量表中移除
     * @param name 变量的名称
     * @return 如果该变量存在，返回它的值；否则返回null
     */
    public Object removeVariable(String name);

    /**
     * 将函数从当前可用函数表中移去
     * @param name 函数的名称
     * @return 如果该函数存在，返回该函数的实例；否则返回null
     */
    public Object removeFunction(String name);

    /**
     * 返回变量名称及其值的Map
     * @return 变量名称及其值的Map
     */
    public HashMap getSymbolTable();

    public Hashtable getFunctionTable();

    public void setAllowUndeclared(boolean value);

    /**
     * 取得Parser中当前的表达式
     * @return Parser中当前的表达式
     */
    public String getExpression();

    /**
     * 取得错误信息
     * @return 错误信息
     */
    public String getErrorInfo();

    /**
     * 是否在上次getValueAsObject有错误发生
     * @return true-是 false-否
     */
    public boolean hasError();

}
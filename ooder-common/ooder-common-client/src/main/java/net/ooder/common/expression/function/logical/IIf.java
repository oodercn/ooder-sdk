/**
 * $RCSfile: IIf.java,v $
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
package net.ooder.common.expression.function.logical;

import java.util.*;

import net.ooder.common.expression.*;
import net.ooder.common.expression.function.*;

/**
 * <p>Title: ETCL</p>
 * <p>Description: 数据更新工具（抽取，转换，清洗，装载）</p>
 * 类似java String 的 substring方法, 而且能够对List中的每个元素执行substring操作。
 * 如果beginIndex小于0，则会被自动置为0。如果endIndex超出源字符串的长度，endIndex
 * 将被置为源字符串的长度。
 * <p>Copyright: Copyright spk  (c) 2025</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class IIf
    extends Function
{

    public IIf()
    {
        numberOfParameters = 3;
    }

    /**
     * Calculates the result of applying the "+" operator to the arguments from
     * the stack and pushes it back on the stack.
     */
    public void run(Stack stack) throws ParseException
    {
        checkStack(stack); // check the stack

        // get the parameter from the stack
        Object falseVal = stack.pop();
        Object trueVal = stack.pop();
        Object param = stack.pop();

        param = iif(param, trueVal, falseVal);

        stack.push(param);

        return;
    }

    public Object iif(Object src, Object trueVal, Object falseVal)
        throws ParseException
    {
        if (src == null){
            return null;
        }

        if (src instanceof Boolean)
        {
            boolean b = ((Boolean)src).booleanValue();
            return b ? trueVal : falseVal;
        }

        //List
        if (src instanceof List)
        {
            List list = (List) src;
            int  n=list.size();
            List temp = new ArrayList(n);
            for (int i = 0; i < n; i++)
            {
                temp.add(iif(list.get(i), trueVal, falseVal));
            }
            return temp;
        }

        throw new ParseException("Invalid parameter type");
    }

}
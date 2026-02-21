/**
 * $RCSfile: Not.java,v $
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

import net.ooder.common.expression.*;
import net.ooder.common.expression.function.*;
import java.util.*;

/**
 * <p>Title: ETCL</p>
 * <p>Description: 数据更新工具（抽取，转换，清洗，装载）</p>
 * 取非操作
 * <p>Copyright: Copyright spk  (c) 2025</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class Not extends Function
{
	public Not()
	{
		numberOfParameters = 1;

	}

	public void run(Stack stack)
		throws ParseException
	{
		checkStack(stack);// check the stack
        stack.push(not(stack.pop())); //push the result on the stack
		return;
	}

    protected Object not(Object x) throws ParseException
    {
        if (x == null){
            return null;
        }
        if (x instanceof Boolean){
            return new Boolean(!((Boolean)x).booleanValue());
        }
        List result = new ArrayList();
        if (x instanceof List){
            List list = (List)x;
            for (int i=0, n=list.size(); i<n ; i++) {
                Object o = list.get(i);
                if (o instanceof Boolean){
                    result.add(new Boolean(!((Boolean)o).booleanValue()));
                }else{
                    throw new ParseException("Invalid parameter type");
                }
            }
            return result;
        }
        throw new ParseException("Invalid parameter type");
   }
}

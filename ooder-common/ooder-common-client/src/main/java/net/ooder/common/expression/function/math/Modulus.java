/**
 * $RCSfile: Modulus.java,v $
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
package net.ooder.common.expression.function.math;

import net.ooder.common.expression.*;
import net.ooder.common.expression.function.*;
import java.util.*;

/**
 * <p>Title: ETCL</p>
 * <p>Description: 数据更新工具（抽取，转换，清洗，装载）</p>
 * <p>Copyright: Copyright spk  (c) 2025</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class Modulus extends Function
{
	public Modulus()
	{
		numberOfParameters = 2;
	}

	public void run(Stack stack)
		throws ParseException
	{
		checkStack(stack);// check the stack
		Object param2 = stack.pop();
		Object param1 = stack.pop();

        stack.push(mod(param1, param2));

		return;
	}

    protected Object mod(Object param1, Object param2) throws ParseException
    {
        if (param1 == null || param2 == null){
            return null;
        }

        if ((param1 instanceof Number) && (param2 instanceof Number))
        {
            double divisor = ((Number)param2).doubleValue();
            double dividend = ((Number)param1).doubleValue();
            double result = dividend % divisor;
            return new Double(result);
        }

        //List && Number
        if ( (param1 instanceof List) && param2 instanceof Number)
        {
            List list = (List) param1;
            List result = new ArrayList();
            for (int i = 0, n = list.size(); i < n; i++)
            {
                result.add(mod(list.get(i), param2));
            }
            return result;
        }

        //Number && List
        if ((param1 instanceof Number) && (param2 instanceof List))
        {
            List list = (List) param2;
            List result = new ArrayList();
            for (int i = 0, n = list.size(); i < n; i++)
            {
                result.add(mod(param1, list.get(i)));
            }
            return result;
        }

        //List && List
        if ( (param1 instanceof List) && (param2 instanceof List))
        {
            List list1 = (List) param1;
            List list2 = (List) param2;
            int n = list1.size();
            List temp = new ArrayList(n);
            if (n != list2.size())
            {
                throw new ParseException("Unmatched List parameter size");
            }
            for (int i = 0; i < n; i++)
            {
                temp.add(mod(list1.get(i), list2.get(i)));
            }
            return temp;
        }

        throw new ParseException("Invalid parameter type");
    }
}

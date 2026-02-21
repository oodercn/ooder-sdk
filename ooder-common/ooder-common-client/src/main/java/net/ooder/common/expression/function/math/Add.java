/**
 * $RCSfile: Add.java,v $
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

import java.util.*;

import net.ooder.common.expression.*;
import net.ooder.common.expression.function.*;

/**
 * <p>Title: 常用代码打包</p>
 * <p>Description: 表达式解析器</p>
 * 字符串，数字相加函数, 能够对String与String, String与Number, Number与String,
 * Number与Number, List与String, String与List, List与List(长度要相等)
 * , Number与List, List与Number等类型的对象进行相加。
 * <p>Copyright: Copyright spk  (c) 2003</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class Add extends Function
{

	public Add()
	{
		numberOfParameters = -1;
	}

	/**
	 * Calculates the result of applying the "+" operator to the arguments from
	 * the stack and pushes it back on the stack.
	 */
	public void run(Stack stack) throws ParseException {
		checkStack(stack);// check the stack

		Object sum = stack.pop();
		Object param;
        int i = 1;

        // repeat summation for each one of the current parameters
        while (i < curNumberOfParameters) {
        	// get the parameter from the stack
            param = stack.pop();

            // add it to the sum (order is important for String arguments)
            sum = add(param, sum);

            i++;
        }

		stack.push(sum);

		return;
	}

	protected Object add(Object param1, Object param2) throws ParseException {
        if (param1 == null){
            param1 = new String("");
        }
        if (param2 == null){
            param2 = new String("");
        }

        //Number && Number
		if (param1 instanceof Number) {
			if (param2 instanceof Number) {
				return new Double(((Number)param1).doubleValue()+((Number)param2).doubleValue());
			}
		}

        //String && String || String && Number || Number && String
        if ((param1 instanceof String) && (param2 instanceof String)
            || (param1 instanceof String && param2 instanceof Number)
            || (param1 instanceof Number && param2 instanceof String))
        {
            return new String(param1.toString().concat(param2.toString()));
        }

        //(String||Number) && List
        if ( (param1 instanceof String || param1 instanceof Number) && (param2 instanceof List))
        {
            List list = (List) param2;
            List result = new ArrayList();
            for (int i = 0, n = list.size(); i < n; i++)
            {
                result.add(add(param1, list.get(i)));
            }
            return result;
        }

        //List && (String || Number)
        if ( (param1 instanceof List) && (param2 instanceof String || param2 instanceof Number))
        {
            List list = (List) param1;
            List result = new ArrayList();
            for (int i = 0, n = list.size(); i < n; i++)
            {
                result.add(add(list.get(i), param2));
            }
            return result;
        }

        //List && List
        if ( (param1 instanceof List) && (param2 instanceof List))
        {
            List list1 = (List) param1;
            List list2 = (List) param2;
            int n = list1.size();
            List result = new ArrayList(n);
            if (n != list2.size())
            {
                throw new ParseException("Unmatched List parameter size");
            }
            for (int i = 0; i < n; i++)
            {
                result.add(add(list1.get(i), list2.get(i)));
            }
            return result;
        }

		throw new ParseException("Invalid parameter type");
	}
}

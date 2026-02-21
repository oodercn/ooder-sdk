/**
 * $RCSfile: Subtract.java,v $
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

public class Subtract extends Function
{
	public Subtract()
	{
		numberOfParameters = 2;
	}

	public void run(Stack stack)
		throws ParseException
	{
		checkStack(stack); // check the stack

		Object param2 = stack.pop();
		Object param1 = stack.pop();

		stack.push(sub(param1, param2));

		return;
	}

	public Object sub(Object param1, Object param2)
		throws ParseException
	{
        if (param1 == null){
            param1 = new Integer(0);
        }
        if (param2 == null){
            param2 = new Integer(0);
        }

		if (param1 instanceof Number)
		{
			if (param2 instanceof Number)
			{
				return sub((Number)param1, (Number)param2);
			}
            if (param2 instanceof List){
                return sub((Number)param1, (List)param2);
            }
		}

        if (param1 instanceof List)
        {
            if (param2 instanceof Number)
            {
                return sub((List)param1, (Number)param2);
            }
            if (param2 instanceof List){
                return sub((List)param1, (List)param2);
            }
        }

		throw new ParseException("Invalid parameter type");
	}


	protected Double sub(Number d1, Number d2)
	{
		return new Double(d1.doubleValue() - d2.doubleValue());
	}

    protected List sub(List v, Number d)
    {
        List result = new ArrayList();

        for (int i=0; i<v.size(); i++)
            result.add(sub((Number)v.get(i), d));

        return result;
    }

    protected List sub(Number d, List v)
    {
        List result = new ArrayList();

        for (int i=0; i<v.size(); i++)
            result.add(sub(d, (Number)v.get(i)));

        return result;
    }

    protected List sub(List v1, List v2) throws ParseException
    {
        List list1 = (List) v1;
        List list2 = (List) v2;
        int n = list1.size();
        List result = new ArrayList(n);
        if (n != list2.size())
        {
            throw new ParseException("Unmatched List parameter size");
        }
        for (int i = 0; i < n; i++)
        {
            result.add(sub((Number)list1.get(i), (Number)list2.get(i)));
        }
        return result;
    }
}

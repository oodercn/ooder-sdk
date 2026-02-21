/**
 * $RCSfile: Multiply.java,v $
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

public class Multiply extends Function
{

	public Multiply() {
		numberOfParameters = -1;
	}

	public void run(Stack stack) throws ParseException
	{
		checkStack(stack); // check the stack

		Object product = stack.pop();
		Object param;
        int i = 1;

        // repeat summation for each one of the current parameters
        while (i < curNumberOfParameters) {
        	// get the parameter from the stack
            param = stack.pop();

            // multiply it with the product
            product = mul(product, param);

            i++;
        }

		stack.push(product);

		return;
	}

	public Object mul(Object param1, Object param2)
		throws ParseException
	{
        if (param1 == null || param2 == null){
            return null;
        }

		if (param1 instanceof Number)
		{
			if (param2 instanceof Number)
				return mul((Number)param1, (Number)param2);
			if (param2 instanceof List)
				return mul((List)param2, (Number)param1);
		}

        if (param1 instanceof List)
		{
			if (param2 instanceof Number)
				return mul((List)param1, (Number)param2);
            if (param2 instanceof List)
                return mul((List)param1, (List)param2);
		}

		throw new ParseException("Invalid parameter type");
	}

	protected Double mul(Number d1, Number d2)
	{
		return new Double(d1.doubleValue()*d2.doubleValue());
	}

	protected List mul(List v, Number d)
	{
		List result = new ArrayList();

		for (int i=0; i<v.size(); i++)
			result.add(mul((Number)v.get(i), d));

		return result;
	}

	protected List mul(List v1, List v2) throws ParseException
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
            result.add(mul((Number)list1.get(i), (Number)list2.get(i)));
        }
        return result;
    }

}

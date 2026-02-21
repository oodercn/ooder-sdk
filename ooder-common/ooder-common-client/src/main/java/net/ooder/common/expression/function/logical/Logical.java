/**
 * $RCSfile: Logical.java,v $
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
 * 逻辑或，逻辑与
 * <p>Copyright: Copyright spk  (c) 2025</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class Logical extends Function
{
	int id;

	public Logical(int id_in)
	{
		id = id_in;
		numberOfParameters = 2;
	}

	public void run(Stack stack)
		throws ParseException
	{
		checkStack(stack);// check the stack

		Object param2 = stack.pop();
		Object param1 = stack.pop();

        stack.push(logical(param1, param2)); // push the result on the stack
		return;
	}

    protected Object logical(Object x, Object y) throws ParseException
    {
        if (x == null || y == null){
            return null;
        }
        if ((x instanceof Boolean) && (y instanceof Boolean))
        {
            return logical(((Boolean)x).booleanValue(), ((Boolean)y).booleanValue());
        }

        if ((x instanceof Boolean) && (y instanceof List))
        {
            return logical(((Boolean)x).booleanValue(), (List)y);
        }
        if ((x instanceof List) && (y instanceof Boolean))
        {
            return logical(((Boolean)y).booleanValue(), (List)x);
        }

        throw new ParseException("Invalid parameter type");
    }

    protected Boolean logical(boolean x, boolean y) throws ParseException
    {
        boolean b;

        switch (id)
        {
            case 0:
                // AND
                b = ((x!=false) && (y!=false)) ? true : false;
                break;
            case 1:
                // OR
                b = ((x!=false) || (y!=false)) ? true : false;
                break;
            default:
                b = false;
        }
        return new Boolean(b);
    }

    protected List logical(boolean x, List y) throws ParseException
    {
        List result = new ArrayList();
        for (int i = 0, n=y.size(); i < n; i++) {
            Object o = y.get(i);
            if (o instanceof Boolean){
                result.add(logical(x, ((Boolean)o).booleanValue()));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }

    protected List logical(List x, List y) throws ParseException
    {
        if(x.size() != y.size()){
            throw new ParseException("Unmatched List parameter size");
        }
        List result = new ArrayList();
        for (int i = 0, n=x.size(); i < n; i++) {
            Object o = x.get(i);
            Object oo = y.get(i);
            if (o instanceof Boolean && oo instanceof Boolean){
                result.add(logical(((Boolean)o).booleanValue(), ((Boolean)oo).booleanValue()));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }
}

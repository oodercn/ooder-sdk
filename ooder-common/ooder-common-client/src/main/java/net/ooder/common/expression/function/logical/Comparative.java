/**
 * $RCSfile: Comparative.java,v $
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
 * 比较函数。比较数值与数值，字符串与字符串的大小，返回true或false。
 * 同时对参数为列表的情况也列表适用。
 * <p>Copyright: Copyright spk  (c) 2025</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class Comparative extends Function
{
	int id;
	double tolerance;

	public Comparative(int id_in)
	{
		id = id_in;
		numberOfParameters = 2;
	}

	public void run(Stack stack)
		throws ParseException
	{
		checkStack(stack); // check the stack

		Object param2 = stack.pop();
		Object param1 = stack.pop();

         //push the result on the stack
        stack.push(cmp(param1, param2));
        return;

	}

    protected Object cmp(Object param1, Object param2) throws ParseException
    {
        if (param1 == null || param2 == null){
            return null;
        }
        //Number与Number比较
        if ((param1 instanceof Number) && (param2 instanceof Number))
        {
            double x = ((Number)param1).doubleValue();
            double y = ((Number)param2).doubleValue();
            return cmp(x, y);
        }

        //String与String比较
        if ((param1 instanceof String) && (param2 instanceof String))
        {
            return cmp((String)param1, (String)param2);
        }

        //Number与List比较
        if ((param1 instanceof Number) && (param2 instanceof List))
        {
            return cmp(((Number)param1).doubleValue(), (List)param2);
        }
        //List与Number比较
        if ((param1 instanceof List) && (param2 instanceof Number))
        {
            return cmp((List)param1, ((Number)param2).doubleValue());
        }

        //String与List比较
        if ((param1 instanceof String) && (param2 instanceof List))
        {
            return cmp((String)param1, (List)param2);
        }
        //List与String比较
        if ((param1 instanceof List) && (param2 instanceof String))
        {
            return cmp((List)param1, (String)param2);
        }

        //List与List比较
        if ((param1 instanceof List) && (param2 instanceof List))
        {
            return cmp((List)param1, (List)param2);
        }

        throw new ParseException("Invalid parameter type");
    }

    protected Object cmp(double x, double y) throws ParseException
    {
        boolean b;
        switch (id)
        {
            case 0: // "<"
                b = (x<y) ? true : false;
                break;
            case 1: // ">"
                b = (x>y) ? true : false;
                break;
            case 2: // "<="
                b = (x<=y) ? true : false;
                break;
            case 3: // ">="
                b = (x>=y) ? true : false;
                break;
            case 4: // "!="
                b = (x!=y) ? true : false;
                break;
            case 5: // "=="
                b = (x==y) ? true : false;
                break;
            default:
                throw new ParseException("Unknown relational operator");
        }

        return new Boolean(b);
    }

    protected Object cmp(String x, String y) throws ParseException
    {
        boolean b;
        switch (id)
        {
            case 0: // "<"
                b = x.compareTo(y)<0 ? true : false;
                break;
            case 1: // ">"
                b = (x).compareTo(y)>0 ? true : false;
                break;
            case 2: // "<="
                b = (x).compareTo(y)<=0 ? true : false;
                break;
            case 3: // ">="
                b = (x).compareTo(y)>=0 ? true : false;
                break;
            case 4: //!=
                b = (x).equals(y) ? false : true;
                break;
            case 5: //==
                b = (x).equals(y) ? true : false;
                break;
            default:
                throw new ParseException("Relational operator type error");
        }

        return new Boolean(b);
    }

    protected List cmp(List x, String y) throws ParseException
    {
        List result = new ArrayList();
        for (int i = 0, n=x.size(); i < n; i++) {
            Object o = x.get(i);
            if (o instanceof String){
                result.add(cmp((String)o, y));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }

    protected List cmp(String x, List y) throws ParseException
    {
        List result = new ArrayList();
        for (int i = 0, n=y.size(); i < n; i++) {
            Object o = y.get(i);
            if (o instanceof String){
                result.add(cmp(x, (String)o));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }

    protected List cmp(List x, double y) throws ParseException
    {
        List result = new ArrayList();
        for (int i = 0, n=x.size(); i < n; i++) {
            Object o = x.get(i);
            if (o instanceof Number){
                result.add(cmp(((Number)o).doubleValue(), y));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }

    protected List cmp(double x, List y) throws ParseException
    {
        List result = new ArrayList();
        for (int i = 0, n=y.size(); i < n; i++) {
            Object o = y.get(i);
            if (o instanceof Number){
                result.add(cmp(x, ((Number)o).doubleValue()));
            }else{
                throw new ParseException("Invalid parameter type");
            }
        }
        return result;
    }

    protected List cmp(List x, List y) throws ParseException
    {
        if(x.size() != y.size()){
            throw new ParseException("Unmatched List parameter size");
        }
        List result = new ArrayList();
        for (int i=0, n=x.size(); i < n; i++) {
            Object o = x.get(i);
            Object oo = y.get(i);
            result.add(cmp(o, oo));
        }
        return result;
    }
}

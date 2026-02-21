/**
 * $RCSfile: SymbolTable.java,v $
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
import java.util.HashMap;


/**
 * <p>Title: ETCL</p>
 * <p>Description: 数据更新工具（抽取，转换，清洗，装载）</p>
 * 当key为String类型时，不区分大小写。
 * <p>Copyright: Copyright spk  (c) 2003</p>
 * <p>Company: spk</p>
 * @author chenjie
 * @version 1.0
 */
public class SymbolTable extends HashMap
{
	public SymbolTable()
	{

	}
    public Object get(Object key){
      if(key instanceof String){
        return super.get(((String)key).toLowerCase());
      }else
        return super.get(key);
    }

    public Object put(Object key, Object value){
      if(key instanceof String){
        return super.put(((String)key).toLowerCase(), value);
      }else
        return super.put(key, value);
    }

    public boolean containsKey(Object key){
      if(key instanceof String){
        return super.containsKey(((String)key).toLowerCase());
      }else
        return super.containsKey(key);
    }

    public Object remove(Object key){
      if(key instanceof String){
        return super.remove(((String)key).toLowerCase());
      }else
        return super.remove(key);
    }

}

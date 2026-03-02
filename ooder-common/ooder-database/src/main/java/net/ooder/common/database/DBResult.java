/**
 * $RCSfile: DBResult.java,v $
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
package net.ooder.common.database;

// Import system packages and classes
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.Constants;
import net.ooder.annotation.MethodChinaName;

/**
 * This class store the data which fetch from database
 * Creation date: (2001-7-9 9:14:40)
 * @author: Liang Yong
 * modify by lxl 2003-07
 */

public class DBResult extends Object implements Cloneable {
    /**
     * Commons Logging instance.
     */
    protected static Log log = LogFactory.getLog(Constants.COMMON_CONFIGKEY,DBResult.class);

	/**
	*
	*/
	protected HashMap mapName = new HashMap();

    /**
     * Result
     */
    private List result = null;

    /**
     * Row of result
     */
    private int resultRow = 0;

    /**
     * Column of result
     */
    private int resultColumn = 0;

    /**
     * DBResult constructor.
     */
    public DBResult() {
        result = new ArrayList();
        resultRow = 0;
        resultColumn = 0;
    }

    /**
     * DBResult constructor.
     */
    public DBResult(List v, int row, int column) {
        this.result = v;
        this.resultRow = row;
        this.resultColumn = column;
    }

    /**
     * Set column of result
     */
    @MethodChinaName("设置列数")
    public void setColumns(int value) {
        resultColumn = value;
    }

    /**
     * Set result
     */
    @MethodChinaName("设置结果集")
    public void setResult(List value) {
        result = value;
    }

    /**
     * Set row of result
     */
    @MethodChinaName("设置行数")
    public void setRows(int value) {
        resultRow = value;
    }

    /**
     * Update specified item in result.
     * @param row int
     * @param column int
     */
    @MethodChinaName("更新指定位置的数据")
    public void updateItem(Object value, int row, int column) {
        result.set( row * resultColumn + column, value);
    }

    /**
     * Insert the method's description here.
     * Creation date: (01-8-21 9:23:46)
     */
    @MethodChinaName("清空结果集")
    public void clearResult() {
        result = new ArrayList();
        resultRow = 0;
        resultColumn = 0;
    }

    /**
     * Clone a DBResult.
     * @return java.lang.Object
     */
    @MethodChinaName("克隆结果集")
    public Object clone() {
        return new DBResult(result, resultRow, resultColumn);
    }


    /**
     * Get column of result
     */
    @MethodChinaName("获取列数")
    public int getColumns() {
        return resultColumn;
    }

    /**
     * Get result item
     */
    @MethodChinaName("根据行和列获取数据")
    public Object getItem(int row, int col) {
	Object o = result.get(getColumns() * row + col);
        if (o != null) {
            return o;
        } else {
            return "";
        }
    }

    /**
     * Get result item
     */
    @MethodChinaName("获取第一行指定列的数据")
    public Object getItem(int i) {
	return getItem(0, i);
    }

    /**
     * Get result item
	 * strCol为所需要的列的标题
     */
    @MethodChinaName("根据行和列名获取数据")
    public Object getItem(int row, String strCol) {
        strCol = strCol.trim().toUpperCase();
	Integer iCol = (Integer) mapName.get(strCol);
	if(iCol != null)
	{
		return getItem(row, iCol.intValue());
	}
	else
	{
		log.error("Error: Can't found Column Name " + strCol);
		return null;
	}
    }

	@MethodChinaName("获取第一行指定列名的数据")
	public Object getItem(String strCol)
	{
		return getItem(0, strCol);
	}

    /**
     * Get result item
     */
    @MethodChinaName("获取第一行指定列的字符串数据")
    public String getString(int i) {
	return getString(0, i);
    }

    /**
     * Get result item
     */
    @MethodChinaName("根据行和列获取字符串数据")
    public String getString(int row, int col) {
      Object o = result.get(getColumns() * row + col);
      if (o != null) {
        if (o instanceof Clob) {
          return getClob( (Clob) o);
        }
        else if (o instanceof Blob) {
          byte[] bs = getBlob( (Blob) o);
          if (bs != null) {
            try {
              String str = new String(bs, "GBK");
              return str;
            }
            catch (Exception e) {
              return "";
            }
          }
          else {
            return "";
          }
        }
        else {
          return o.toString();
        }
      }
      else {
        return "";
      }
    }

    /**
     * Get result item
     * strCol为所需要的列的标题
     */
    @MethodChinaName("根据行和列名获取字符串数据")
    public String getString(int row, String strCol) {
	strCol = strCol.trim().toUpperCase();
	Integer iCol = (Integer) mapName.get(strCol);
	if(iCol != null)
	{
                    return getString(row, iCol.intValue());
	}
	else
	{
		log.error("Error: Can't found Column Name " + strCol);
		return null;
	}
    }

	@MethodChinaName("获取第一行指定列名的字符串数据")
	public String getString(String strCol)
	{
		return getString(0, strCol);
	}

    /**
     * Get result item
     */
    @MethodChinaName("获取第一行指定列的整数数据")
    public int getInt(int i) {
        return getInt(0, i);
    }

    /**
     * Get result item
     */
    @MethodChinaName("根据行和列获取整数数据")
    public int getInt(int row, int col) {
        Object o = result.get(getColumns() * row + col);
        if ( o != null)
        {
            if(o instanceof String)
            {
                String str = (String) o;
                str = str.trim();
                try
                {
                    return Integer.parseInt(str);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return -9999999;
                }
            }
            else if(o instanceof Integer)
            {
                Integer iInt = (Integer) o;
                return iInt.intValue();
            }
            else if(o instanceof java.math.BigDecimal)
            {
                java.math.BigDecimal oBD = (java.math.BigDecimal) o;
                return oBD.intValue();
            }
            else
            {
                return -9999999;
            }
        }
        else
        {
            return -9999999;
        }
    }

    /**
     * Get result item
     * strCol为所需要的列的标题
     */
    @MethodChinaName("根据行和列名获取整数数据")
    public int getInt(int row, String strCol) {
        strCol = strCol.trim().toUpperCase();
        Integer iCol = (Integer) mapName.get(strCol);
        if(iCol != null)
        {
            return getInt(row, iCol.intValue());
        }
        else
        {
            log.error("Error: Can't found Column Name " + strCol);
            return -9999999;
        }
    }

    @MethodChinaName("获取第一行指定列名的整数数据")
    public int getInt(String strCol)
    {
        return getInt(0, strCol);
    }
    /**
     * Get result item
     */
    @MethodChinaName("获取第一行指定列的长整数数据")
    public long getLong(int i) {
    	return getLong(0, i);
    }

    /**
     * Get result item
     */
    @MethodChinaName("根据行和列获取长整数数据")
    public long getLong(int row, int col) {
    	Object o = result.get(getColumns() * row + col);
    	if ( o != null)
    	{
    		if(o instanceof String)
    		{
    			String str = (String) o;
    			str = str.trim();
    			try
    			{
    				return Long.parseLong(str);
    			}
    			catch(Exception e)
    			{
    				e.printStackTrace();
    				return -9999999;
    			}
    		}
    		else if(o instanceof Integer)
    		{
    			Integer iInt = (Integer) o;
    			return iInt.longValue();
    		}
    		else if(o instanceof Long)
    		{
    			Long lLong = (Long) o;
    			return lLong.longValue();
    		}
    		else if(o instanceof java.math.BigDecimal)
    		{
    			java.math.BigDecimal oBD = (java.math.BigDecimal) o;
    			return oBD.longValue();
    		}
    		else
    		{
    			return -99999999999L;
    		}
    	}
    	else
    	{
    		return -999999999999L;
    	}
    }

    /**
     * Get result item
     * strCol为所需要的列的标题
     */
    @MethodChinaName("根据行和列名获取长整数数据")
    public long getLong(int row, String strCol) {
    	strCol = strCol.trim().toUpperCase();
    	Integer iCol = (Integer) mapName.get(strCol);
    	if(iCol != null)
    	{
    		return getLong(row, iCol.intValue());
    	}
    	else
    	{
    		log.error("Error: Can't found Column Name " + strCol);
    		return -999999999999L;
    	}
    }

    @MethodChinaName("获取第一行指定列名的长整数数据")
    public long getLong(String strCol)
    {
    	return getLong(0, strCol);
    }

	/**
    	* Return a vector as result
    	*/
    @MethodChinaName("获取结果集")
    public List getResult() {
        return result;
    }

    /**
     * Get row of result
     */
    @MethodChinaName("获取行数")
    public int getRows() {
        return resultRow;
    }

    /**
     * Get number of items in the result
     */
    @MethodChinaName("获取结果集大小")
    public int getSize() {
        return getRows() * getColumns();
    }

    /**
	* 设置第i列的标题
	*/
	@MethodChinaName("设置列名")
	public void setColumnName(int i, String strName)
	{
		strName = strName.toUpperCase();
		mapName.put(strName, new Integer(i));
	}

	@MethodChinaName("获取Clob对象的字符串数据")
public String getClob(Clob c)
	{
	    	StringBuffer sb = new StringBuffer();
		try{
			Reader in = c.getCharacterStream();

			char[] chars = new char[100];

			// length of characters read
			int length = 0;

			// fetch data
			while ((length = in.read(chars)) != -1)
			{
				sb.append(chars, 0, length);
			}
			// Close input stream
			in.close();
		}catch(Exception e) {e.printStackTrace();}

		return sb.toString();
	}

	@MethodChinaName("获取Blob对象的字节数据")
public byte[] getBlob(Blob b)
	{
		try{
			InputStream in = b.getBinaryStream();

			byte[] bs = new byte[(int) b.length()];

			// fetch data
			in.read(bs);
			// Close input stream
			in.close();
			return bs;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

     /**
     * Just for self test
     * @param args java.lang.String[]
     */
    public static void main(String[] args) {
	}

    /**
     * Just for debug
     * Print data in result.
     */
    @MethodChinaName("打印结果集")
    public void printResult() {
        System.out.println("===== Begin Result =====");
        for (int i = 0; i < resultRow; i++) {
            System.out.print("[" + i + "]:");
            for (int j = 0; j < resultColumn; j++) {
                System.out.print("[" + j + "]" + getItem(i, j) + " ");
            }
            System.out.println("");
        }
        System.out.println("===== End Result =====");
    }
}



package net.ooder.org.conf;

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

import java.io.Serializable;
import java.util.Map;

/**
 * Query 类用于表示查询信息，实现了 Cacheable 和 Serializable 接口。
 */
public class Query implements Cacheable, Serializable {
	private String type;
	private Map sqlClauses;

	public Query() {
	}

	public Query(String type, Map sqlClauses) {
		this.type = type;
		this.sqlClauses = sqlClauses;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map getSqlClauses() {
		return sqlClauses;
	}
	public void setSqlClauses(Map sqlClauses) {
		this.sqlClauses = sqlClauses;
	}
	public SqlClause getSqlClause(String name) {
		return (SqlClause) sqlClauses.get(name);
	}

	//SQL语句
	public class SqlClause implements Cacheable,Serializable {
		private String type;
		private String tableName;


		private String mainClause;
		private String topIds;
		private String whereClause;
		private String updataClause;
		private String insertClause;
		private String deleteClause;
		private String orderClause;
		private Map columnMappings;

		public SqlClause() {
		}

		public SqlClause(String type, String mainClause, Map columnMappings) {
			this.type = type;
			this.mainClause = mainClause;
			this.columnMappings = columnMappings;
		}


		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getMainClause() {
			return mainClause;
		}
		public void setMainClause(String mainClause) {
			this.mainClause = mainClause;
		}
		public String getWhereClause() {
			return whereClause;
		}
		public void setWhereClause(String whereClause) {
			this.whereClause = whereClause;
		}
		public String getOrderClause() {
			return orderClause;
		}
		public void setOrderClause(String orderClause) {
			this.orderClause = orderClause;
		}
		public Map getColumnMappings() {
			return columnMappings;
		}
		public void setColumnMappings(Map columnMappings) {
			this.columnMappings = columnMappings;
		}
		public String getUpdataClause() {
			return updataClause;
		}

		public void setUpdataClause(String updataClause) {
			this.updataClause = updataClause;
		}

		public String getInsertClause() {
			return insertClause;
		}

		public void setInsertClause(String insertClause) {
			this.insertClause = insertClause;
		}

		public int getCachedSize() {
			int size = 0;
		    size += CacheSizes.sizeOfString(type);
			size += CacheSizes.sizeOfString(tableName);
		    size += CacheSizes.sizeOfString(topIds);
		    size += CacheSizes.sizeOfString(mainClause);
		    size += CacheSizes.sizeOfString(whereClause);
		    size += CacheSizes.sizeOfString(orderClause);
		    size += CacheSizes.sizeOfString(updataClause);
		    size += CacheSizes.sizeOfString(insertClause);
		    size += CacheSizes.sizeOfString(deleteClause);
		    size += CacheSizes.sizeOfMap(columnMappings);
			return size;
		}

		public String getDeleteClause() {
			return deleteClause;
		}

		public void setDeleteClause(String deleteClause) {
			this.deleteClause = deleteClause;
		}

		public String getTopIds() {
			return topIds;
		}

		public void setTopIds(String topIds) {
			this.topIds = topIds;
		}

		
	}

	//字段映射类
	public class ColumnMapping implements Cacheable,Serializable{
		//属性，与Org, Person等类的属性
		private String property;
		//字段的别名，用于ResultSet的getString()方法取数据时
		private String columnAlias;
		//字段名称，用于拼Where语句时用
		private String column;

		public String getProperty() {
			return property;
		}
		public void setProperty(String property) {
			this.property = property;
		}
		public String getColumnAlias() {
			return columnAlias;
		}
		public void setColumnAlias(String columnAlias) {
			this.columnAlias = columnAlias;
		}
		public String getColumn() {
			return column;
		}
		public void setColumn(String column) {
			this.column = column;
		}
		
		public String toString() {
			return getColumn();
		}
		public int getCachedSize() {
			int size = 0;	
		    size += CacheSizes.sizeOfString(property);
		    size += CacheSizes.sizeOfString(columnAlias);
		    size += CacheSizes.sizeOfString(column);
		return size;
		}
	}

	public int getCachedSize() {
		int size = 0;
	    size += CacheSizes.sizeOfString(type);
	    size += CacheSizes.sizeOfMap(sqlClauses);
		return size;
	}
}
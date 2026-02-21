/*
 *@(#)LogLevel.java 2025-08-25
 *
 *Copyright (c) ooder. All rights reserved. 
 */
package net.ooder.annotation;

/**
 * 日志等级
 * 
 * 
 * @author ooder
 * @since 2025-08-25
 * @version <1.0>
 * 
 */
public enum LogLevel {

	DEBUG("调试"), INFO("信息"), WARN("警告"), ERROR("错误");

	/**
	 * 定义枚举类型
	 * 
	 * @param desc
	 */
	private LogLevel(String desc) {
		this.desc = desc;
	}

	/**
	 * 显示
	 */
	private String desc;

	/**
	 * 数据域访问方法
	 * 
	 * @return 描述信息
	 */
	public String getDesc() {
		return desc;
	}

}

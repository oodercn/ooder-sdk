
package net.ooder.bpm.client.attribute;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhang
 * @version 2.0
 */
public interface AttributeInterpreter<T> {

	/**
	 * 解释扩展属性的值
	 * 
	 * @param value
	 * @return
	 */
    public T interpret(String value);

	/**
	 * 将值实例化
	 * 
	 * @param obj
	 * @return
	 */
	public String instantiate(Object obj);

	/**
	 * 从实例中解释扩展属性的值，和instantiate方法相对
	 * 
	 * @param value
	 * @return
	 */
	public T interpretFromInstance(String value);

}

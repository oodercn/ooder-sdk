package net.ooder.common.org.listener;

import net.ooder.common.org.event.OrgEvent;
import net.ooder.org.Org;
import net.ooder.org.OrgNotFoundException;
import net.ooder.vfs.VFSException;

/**
 *
 */
public interface OrgListener<T extends Org> extends java.util.EventListener {

	/**
	 * orgCreate
	 * @param event
	 * @throws VFSException
	 */
	public void orgCreate(OrgEvent<T> event) throws OrgNotFoundException;


	/**
	 * orgSave
	 * @param event
	 * @throws VFSException
	 */
	public void orgSave(OrgEvent<T> event) throws OrgNotFoundException;


	/**
	 * 已经被添加
	 */
	public void personAdded(OrgEvent<T> event) throws OrgNotFoundException;


	/**
	 * 已经被添加
	 */
	public void orgAdded(OrgEvent<T> event) throws OrgNotFoundException;



	/**
	 * 正在被删除
	 */
	public void orgDelete(OrgEvent<T> event) throws OrgNotFoundException;


	
	/**
	 * 文件夹从命名
	 */
	public void orgRename(OrgEvent<T> event) throws OrgNotFoundException;


	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode();

}

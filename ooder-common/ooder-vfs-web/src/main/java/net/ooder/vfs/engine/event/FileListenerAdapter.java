/*
 *@(#)FileAdapter.java 2013-7-8
 *
 *Copyright (c) CSS-CA  LTD. All rights reserved. 
 */
package net.ooder.vfs.engine.event;

import  net.ooder.vfs.VFSException;

/**
 * 文件是适配器
 * 
 * @Author zhang xu
 * @Date <2013-7-8>
 * @version <1.0>
 * 
 */
public abstract class FileListenerAdapter implements FileListener {

	/**
	 * fileBeforeUpload
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileBeforeUpload(FileEvent event) throws VFSException {

	}

	/**
	 * fileUploading
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileUploading(FileEvent event) throws VFSException {

	}

	/**
	 * fileUploadError
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileUploadError(FileEvent event) throws VFSException {

	}

	/**
	 * fileAddVersion
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileAddVersion(FileEvent event) throws VFSException {
	}

	/**
	 * fileCreate
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileCreate(FileEvent event) throws VFSException {

	}

	/**
	 * fileCopy
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileCopy(FileEvent event) throws VFSException {

	}

	/**
	 * 文件创建
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileOpen(FileEvent event) throws VFSException {

	}

	/**
	 * fileMove
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileMove(FileEvent event) throws VFSException {

	}

	/**
	 * fileUploaded
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileDown(FileEvent event) throws VFSException {

	}

	/**
	 * fileSave
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileSave(FileEvent event) throws VFSException {

	}

	/**
	 * fileUpdate
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileUpdate(FileEvent event) throws VFSException {

	}

	/**
	 * fileUploaded
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileUploaded(FileEvent event) throws VFSException {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileSend(FileEvent event) throws VFSException {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileShare(FileEvent event) throws VFSException {

	}

	/**
	 * 文件删除
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileDeleted(FileEvent event) {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileRename(FileEvent event) {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileShareOpen(FileEvent event) {

	}

	/**
	 * 
	 */
	public void fileLink(FileEvent event) {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileShareDown(FileEvent event) {

	}

	/**
	 * 文件发送
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileSharePrint(FileEvent event) {

	}

	/**
	 * 分享文件另存云盘
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileShareSave(FileEvent event) {
	}

	/**
	 * 订阅文件
	 */
	public void fileSubScribe(FileEvent event) {
	}

	/**
	 * 取消订阅文件
	 */
	public void fileUnSubScribe(FileEvent event) {
	}

	/**
	 * 文件评论
	 */
	public void fileComment(FileEvent event) {
	}

	/**
	 * 取消评论
	 */
	public void fileUnComment(FileEvent event) {
	}

	/**
	 * 文件结束协作
	 */
	public void fileUnLink(FileEvent event) {
	}

	/**
	 * 完成协作
	 */
	public void fileFinishLink(FileEvent event) {
	}

	/**
	 * 文件增加备注
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileAddRemark(FileEvent event) {
	}

	/**
	 * 文件删除备注
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileDelRemark(FileEvent event) {
	}

	/**
	 * 文件彻底删除
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileClean(FileEvent event) {
	}

	/**
	 * 文件彻底还原
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileRestore(FileEvent event) {

	}

	/**
	 * 文件彻底还原
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileLocked(FileEvent event) {

	}

	/**
	 * 文件彻底还原
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileAuthor(FileEvent event) {

	}

	/**
	 * 文件解锁
	 * 
	 * @param event
	 * @throws VFSException
	 */
	public void fileUnLocked(FileEvent event) {

	}

	/**
	 * 云盘选文件发邮件
	 * 
	 * @param event
	 */
	public void fileSendMail(FileEvent event) {

	}

	/**
	 * 得到系统Code
	 * 
	 * @return
	 */
	public String getSystemCode() {
		return "";
	}

}

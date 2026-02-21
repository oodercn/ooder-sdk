package net.ooder.common.org.service;

import net.ooder.config.ResultModel;
import net.ooder.jds.core.User;

public interface UserService {
	/**
	 * 登录
	 * @param user
	 * @return
	 */

	public ResultModel<User> login(User user) ;

	/**
	 * 注册
	 * @param user
	 * @return
	 */

	public ResultModel<User> register(User user);

	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	public ResultModel<User> updateUserInfo(User user);

	/**
	 * 发送验证码
	 * @param mobile
	 * @return
	 */
	public 	ResultModel<User> sendCode(String mobile);


	/**
	 * 修改密码

	 * @param oldpassword
	 * @param newpassword
	 * @param userId
	 * @return
	 */
	public ResultModel<User> updatePassword(String oldpassword, String newpassword, String userId);

	/**
	 * 退出
	 * @return
	 */
	public ResultModel<User> logout();

	/**
	 * 重设密码
	 * @param account
	 * @param newpassword
	 * @param code 手机验证码
	 * @return
	 */
	public ResultModel<User> restPw(String account, String newpassword, String code);


}

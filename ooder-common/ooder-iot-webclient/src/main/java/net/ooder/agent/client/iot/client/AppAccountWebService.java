package net.ooder.agent.client.iot.client;

import  net.ooder.config.ResultModel;
import  net.ooder.engine.ConnectInfo;
import  net.ooder.jds.core.User;

public interface AppAccountWebService {
	ResultModel<Boolean> logout() ;
		
	
	/**
	 * 注册新用户
	 * @param user
	 * @return
	 * @
	 */
	ResultModel<User> register(User user) ;
	
	/**
	 * 登录验证
	 * @param userName
	 * @param password
	 * @return
	 * @
	 */
	ResultModel<ConnectInfo> login(String userName, String password, String imei) ;
//		
//	/**
//	 * 根据网关返回绑定用户
//	 * @param gatewayId
//	 * @return
//	 * @
//	 */
//	ResultModel<User> getAppUserByGwId(String gatewayId) ;

	/**
	 * 修改密码
	 * @param user
	 * @
	 */
	ResultModel<Boolean> modifyPwd(User user) ;
	
	/**
	 * 发送验证码
	 * @param userData
	 * @return
	 * @
	 */
	ResultModel<String> sendCode(User userData) ;
	
	/**
	 * 发送指定号码验证码
	 * @param phonenum
	 * @return
	 * @
	 */
	ResultModel<String> sendCodeByPhonenum(String phonenum) ;
		
	/**
	 * 保存用户信息
	 * @param user
	 * @return
	 * @
	 */
	ResultModel<User> saveUser(User user) ;
	
	/**
	 * 根据账户取得用户对象
	 * @param account
	 * @return
	 * @
	 */
	ResultModel<User> getUserByAccount(String account) ;
	
	
	/**
	 * 根据ID取得用户对象
	 * @param Id
	 * @return
	 * @
	 */
	ResultModel<User> getUserById(String Id) ;
	
	
	

}
